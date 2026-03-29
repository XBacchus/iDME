@echo off
setlocal EnableExtensions EnableDelayedExpansion
cd /d "%~dp0"

set "FORCE_MODE=0"
if /I "%~1"=="-force" set "FORCE_MODE=1"
if /I "%~1"=="/force" set "FORCE_MODE=1"
if /I "%~1"=="--force" set "FORCE_MODE=1"

echo [INFO] one-click startup begin...
if "%FORCE_MODE%"=="1" (
  echo [INFO] force mode enabled: stop then start.
  call :force_restart
  if errorlevel 1 exit /b 1
)

call :is_port_listening 5433
if not errorlevel 1 (
  echo [INFO] PG13 already listening on 5433, skip start.
) else (
  echo [INFO] starting PG13...
  call ".\startpg13.bat"
  if errorlevel 1 (
    echo [ERROR] failed to start PG13.
    exit /b 1
  )
  call :wait_port 5433 40 PG13
  if errorlevel 1 exit /b 1
)

call :is_port_listening 6001
if not errorlevel 1 (
  echo [INFO] tx-distributor already listening on 6001, skip start.
) else (
  echo [INFO] starting tx-distributor...
  start "tx-distributor" cmd /c "cd /d ""%~dp0xdm-tx-distributor"" && call starttx.bat"
  call :wait_port 6001 120 tx-distributor
  if errorlevel 1 exit /b 1
)

call :is_port_listening 8003
if not errorlevel 1 (
  echo [INFO] sdk runtime already listening on 8003, skip start.
) else (
  echo [INFO] starting sdk runtime...
  start "sdk-runtime" cmd /c "cd /d ""%~dp0"" && call startxdm.bat"
  call :wait_port 8003 240 sdk-runtime
  if errorlevel 1 exit /b 1
)

call :is_port_listening 8080
if not errorlevel 1 (
  call :wait_http_ready http://127.0.0.1:8080/api/health 30 miniapp-backend
  if errorlevel 1 (
    echo [ERROR] port 8080 is occupied but miniapp-backend health endpoint is not ready.
    call :print_file_tail "%~dp0miniapp\backend\logs\backend.err.log" 60
    call :print_file_tail "%~dp0miniapp\backend\logs\backend.out.log" 60
    exit /b 1
  )
  echo [INFO] miniapp-backend already listening on 8080, skip start.
) else (
  echo [INFO] starting miniapp-backend...
  start "miniapp-backend" cmd /c "cd /d ""%~dp0miniapp\backend"" && call mvn spring-boot:run"
  call :wait_http_ready http://127.0.0.1:8080/api/health 240 miniapp-backend
  if errorlevel 1 (
    call :print_file_tail "%~dp0miniapp\backend\logs\backend.err.log" 60
    call :print_file_tail "%~dp0miniapp\backend\logs\backend.out.log" 60
    exit /b 1
  )
)

call :is_port_listening 5173
if not errorlevel 1 (
  echo [INFO] miniapp-frontend already listening on 5173, skip start.
) else (
  pushd "%~dp0miniapp\frontend"
  if errorlevel 1 (
    echo [ERROR] failed to open miniapp-frontend directory.
    exit /b 1
  )
  if not exist "node_modules" (
    echo [INFO] installing frontend dependencies...
    call npm install
    if errorlevel 1 (
      popd
      echo [ERROR] failed to install frontend dependencies.
      exit /b 1
    )
  )
  popd
  echo [INFO] starting miniapp-frontend...
  start "miniapp-frontend" cmd /c "cd /d ""%~dp0miniapp\frontend"" && call npm run dev"
  call :wait_port 5173 180 miniapp-frontend
  if errorlevel 1 exit /b 1
)

echo [INFO] startup completed.
echo [INFO] ports: PG13=5433, tx-distributor=6001, sdk-runtime=8003, miniapp-backend=8080, miniapp-frontend=5173
exit /b 0

:force_restart
call :stop_by_port 5173 miniapp-frontend
call :wait_port_down 5173 30 miniapp-frontend
if errorlevel 1 exit /b 1

call :stop_by_port 8080 miniapp-backend
call :wait_port_down 8080 30 miniapp-backend
if errorlevel 1 exit /b 1

call :stop_by_port 8003 sdk-runtime
call :wait_port_down 8003 30 sdk-runtime
if errorlevel 1 exit /b 1

call :stop_by_port 6001 tx-distributor
call :wait_port_down 6001 30 tx-distributor
if errorlevel 1 exit /b 1

call :stop_pg13
call :wait_port_down 5433 30 PG13
if errorlevel 1 exit /b 1
exit /b 0

:stop_pg13
call :is_port_listening 5433
if errorlevel 1 (
  echo [INFO] PG13 is not running, skip stop.
  exit /b 0
)

if exist ".\stoppg13.bat" (
  echo [INFO] stopping PG13 by stoppg13.bat...
  call ".\stoppg13.bat" >nul 2>nul
  if not errorlevel 1 (
    echo [INFO] PG13 stop command completed.
    exit /b 0
  )
)

echo [WARN] stoppg13.bat failed, fallback to port kill.
call :stop_by_port 5433 PG13
exit /b 0

:stop_by_port
set "PORT=%~1"
set "SERVICE_NAME=%~2"
set "TARGET_PID="
for /f "tokens=5" %%p in ('netstat -ano ^| findstr /r /c:":%PORT% .*LISTENING"') do (
  set "TARGET_PID=%%p"
  goto :stop_by_port_found
)
:stop_by_port_found
if not defined TARGET_PID (
  echo [INFO] %SERVICE_NAME% is not running on port %PORT%, skip stop.
  exit /b 0
)

echo [INFO] stopping %SERVICE_NAME% (PID !TARGET_PID!) on port %PORT%...
taskkill /PID !TARGET_PID! /F >nul 2>nul
if errorlevel 1 (
  echo [WARN] failed to stop PID !TARGET_PID!, continue checking.
) else (
  echo [INFO] stop signal sent to %SERVICE_NAME%.
)
exit /b 0

:is_port_listening
set "PORT=%~1"
netstat -ano | findstr /r /c:":%PORT% .*LISTENING" >nul
exit /b %errorlevel%

:wait_port_down
set "PORT=%~1"
set "MAX_RETRY=%~2"
set "SERVICE_NAME=%~3"
for /l %%i in (1,1,%MAX_RETRY%) do (
  call :is_port_listening %PORT%
  if errorlevel 1 (
    echo [INFO] %SERVICE_NAME% port %PORT% is closed.
    exit /b 0
  )
  >nul ping -n 2 127.0.0.1
)
echo [ERROR] timeout waiting %SERVICE_NAME% port %PORT% to close.
exit /b 1

:wait_port
set "PORT=%~1"
set "MAX_RETRY=%~2"
set "SERVICE_NAME=%~3"
for /l %%i in (1,1,%MAX_RETRY%) do (
  call :is_port_listening %PORT%
  if not errorlevel 1 (
    echo [INFO] %SERVICE_NAME% is ready on port %PORT%.
    exit /b 0
  )
  >nul ping -n 2 127.0.0.1
)
echo [ERROR] timeout waiting for %SERVICE_NAME% on port %PORT%.
exit /b 1

:wait_http_ready
set "URL=%~1"
set "MAX_RETRY=%~2"
set "SERVICE_NAME=%~3"
for /l %%i in (1,1,%MAX_RETRY%) do (
  powershell -NoProfile -Command "$ProgressPreference='SilentlyContinue'; try { $resp = Invoke-WebRequest -UseBasicParsing -Uri '%URL%' -TimeoutSec 2; if ($resp.StatusCode -ge 200 -and $resp.StatusCode -lt 300) { exit 0 } else { exit 1 } } catch { exit 1 }" >nul 2>nul
  if not errorlevel 1 (
    echo [INFO] %SERVICE_NAME% health check passed: %URL%
    exit /b 0
  )
  >nul ping -n 2 127.0.0.1
)
echo [ERROR] timeout waiting for %SERVICE_NAME% health endpoint: %URL%
exit /b 1

:print_file_tail
set "FILE_PATH=%~1"
set "TAIL_COUNT=%~2"
if not exist "%FILE_PATH%" (
  echo [WARN] log file not found: %FILE_PATH%
  exit /b 0
)
echo [INFO] showing last %TAIL_COUNT% lines of %FILE_PATH%
powershell -NoProfile -Command "Get-Content -Path '%FILE_PATH%' -Tail %TAIL_COUNT%"
exit /b 0
