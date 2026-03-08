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

echo [INFO] startup completed.
echo [INFO] ports: PG13=5433, tx-distributor=6001, sdk-runtime=8003
exit /b 0

:force_restart
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
