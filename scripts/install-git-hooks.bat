@echo off
setlocal

set ROOT_DIR=%~dp0..
set HOOK_DIR=%ROOT_DIR%\.git\hooks

if not exist "%HOOK_DIR%" (
  echo ERROR: .git\hooks not found
  exit /b 1
)

echo Installing git hooks...

for %%H in (commit-msg pre-push post-merge) do (
  if exist "%ROOT_DIR%\scripts\%%H" (
    copy /Y "%ROOT_DIR%\scripts\%%H" "%HOOK_DIR%\%%H" >nul
    echo OK: installed %%H
  ) else (
    echo WARN: missing scripts\%%H
  )
)

echo DONE: git hooks installed
exit /b 0