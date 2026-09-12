@echo off
REM ============================================================
REM  Deploy pocetna.mk
REM  1) commits + pushes your changes to GitHub
REM  2) updates the live server (git pull + rebuild)
REM  Just double-click this file.
REM ============================================================
setlocal
cd /d "%~dp0"

REM --- Find the SSH key on THIS computer ---------------------------
REM  %USERPROFILE% is whatever account is logged in, so this keeps
REM  working if the project moves to another machine or user.
set "SSH_KEY="
for %%K in (id_ed26809 id_ed25519 id_ecdsa id_rsa) do (
  if not defined SSH_KEY if exist "%USERPROFILE%\.ssh\%%K" set "SSH_KEY=%USERPROFILE%\.ssh\%%K"
)

if defined SSH_KEY (
  set SSH_OPTS=-i "%SSH_KEY%"
  echo Using SSH key: %SSH_KEY%
) else (
  set "SSH_OPTS="
  echo No key found in "%USERPROFILE%\.ssh" - relying on ssh-agent or ssh config.
)
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

REM --- 2) Push to GitHub ---
REM  Local work and the server checkout are both on "master" - push that.
echo.
echo Pushing to GitHub...
git push origin master
if errorlevel 1 goto :error

REM --- 3) Update the server: pull latest + rebuild containers ---
echo.
echo Updating the server (this rebuilds, ~5-8 min)...
ssh %SSH_OPTS% %SERVER% "cd pocetna && git pull && docker compose up -d --build"
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
