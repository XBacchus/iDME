@echo off
setlocal EnableExtensions
cd /d "%~dp0"

set "PG13_HOME=C:\Program Files\PostgreSQL\13"
set "PG13_DATA=C:\Program Files\PostgreSQL\13\data"
set "PG13_LOG=D:\workspace\iDME\pg13\pg13-runtime.log"

if not exist "%PG13_HOME%\bin\pg_ctl.exe" (
  echo [ERROR] pg_ctl.exe not found in %PG13_HOME%\bin
  exit /b 1
)

if not exist "%PG13_DATA%\postgresql.conf" (
  echo [ERROR] PG13 data directory not found: %PG13_DATA%
  exit /b 1
)

"%PG13_HOME%\bin\pg_ctl.exe" -D "%PG13_DATA%" -l "%PG13_LOG%" -o "-p 5433" start
if errorlevel 1 (
  echo [ERROR] failed to start PG13.
  exit /b 1
)

echo [INFO] PG13 started on port 5433.
