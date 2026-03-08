@echo off
setlocal EnableExtensions
cd /d "%~dp0"

set "PG13_HOME=C:\Program Files\PostgreSQL\13"
set "PG13_DATA=C:\Program Files\PostgreSQL\13\data"

if not exist "%PG13_HOME%\bin\pg_ctl.exe" (
  echo [ERROR] pg_ctl.exe not found in %PG13_HOME%\bin
  exit /b 1
)

if not exist "%PG13_DATA%\postgresql.conf" (
  echo [ERROR] PG13 data directory not found: %PG13_DATA%
  exit /b 1
)

"%PG13_HOME%\bin\pg_ctl.exe" -D "%PG13_DATA%" stop -m fast
if errorlevel 1 (
  echo [ERROR] failed to stop PG13.
  exit /b 1
)

echo [INFO] PG13 stopped.
