@echo off
setlocal EnableExtensions
cd /d "%~dp0"

if not defined JAVA_HOME (
  for /f "tokens=2*" %%A in ('reg query "HKLM\SOFTWARE\JavaSoft\JDK\17" /v JavaHome 2^>nul ^| find "JavaHome"') do set "JAVA_HOME=%%B"
)
if defined JAVA_HOME if exist "%JAVA_HOME%\bin\java.exe" set "PATH=%JAVA_HOME%\bin;%PATH%"

where java >nul 2>nul
if errorlevel 1 (
  echo [ERROR] java not found. Install JDK 17 and set PATH.
  exit /b 1
)

if not exist ".\application.properties" (
  echo [ERROR] application.properties is missing.
  exit /b 1
)

if not exist ".\xdm-tx-distributor.jar" (
  echo [ERROR] xdm-tx-distributor.jar is missing.
  exit /b 1
)

:: Start tx distributor and redirect log
java --add-opens java.base/sun.security.util=ALL-UNNAMED --add-opens java.base/sun.security.x509=ALL-UNNAMED --add-opens java.base/sun.security.pkcs=ALL-UNNAMED -jar xdm-tx-distributor.jar > tx.log 2>&1
