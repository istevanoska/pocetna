@echo off
REM ============================================================
REM  Почетна.мк — стартува сѐ и дава јавен линк за споделување
REM  Двоен клик на овој фајл. Остави го прозорецот отворен
REM  додека сакаш линкот да работи.
REM ============================================================
cd /d "%~dp0"

echo.
echo [1/3] Building the app...
call gradlew.bat bootJar -q
if errorlevel 1 (
  echo Build failed. Fix errors above and try again.
  pause
  exit /b 1
)

echo [2/3] Starting the server on port 8081...
start "pocetna-backend" /min java -jar build\libs\pocetna-0.0.1-SNAPSHOT.jar --server.port=8081

echo     Waiting for the server to come up...
timeout /t 8 /nobreak >nul

echo [3/3] Opening public link (Cloudflare tunnel)...
echo.
echo ============================================================
echo  Look for a line like:
echo    https://SOMETHING.trycloudflare.com
echo  Copy that link and send it to whoever you want.
echo  Keep this window open. Press Ctrl+C here to stop sharing.
echo ============================================================
echo.
cloudflared.exe tunnel --url http://localhost:8081
