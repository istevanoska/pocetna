@echo off
REM ============================================================
REM  Deploy pocetna.mk
REM  1) commits + pushes your changes to GitHub
REM  2) updates the live server (git pull + rebuild)
REM  Just double-click this file.
REM ============================================================
setlocal
cd /d "%~dp0"

set "SSH_KEY=C:\Users\Ilina\.ssh\id_ed26809"
set "SERVER=root@178.105.182.242"

echo.
echo ============================================
echo   Deploy pocetna.mk
echo ============================================
echo.

REM --- 1) Commit local changes (optional) ---
git add -A
set "MSG="
set /p MSG="Commit message (Enter to skip committing): "
if not "%MSG%"=="" (
  git commit -m "%MSG%"
)

REM --- 2) Push to GitHub (both branches) ---
echo.
echo Pushing to GitHub...
git push origin main
git push origin main:master
if errorlevel 1 goto :error

REM --- 3) Update the server: pull latest + rebuild containers ---
echo.
echo Updating the server (this rebuilds, ~5-8 min)...
ssh -i "%SSH_KEY%" %SERVER% "cd pocetna && git pull && docker compose up -d --build"
if errorlevel 1 goto :error

echo.
echo ============================================
echo   Done! Your changes are live.
echo   http://178.105.182.242   (and https://pocetna.mk once DNS is set)
echo ============================================
goto :end

:error
echo.
echo !!! Something failed above. Read the message and try again.

:end
echo.
pause
