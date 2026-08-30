@echo off
echo ========================================
echo   Avvio Backend Ruota della Fortuna
echo ========================================
echo.

cd backend

echo Avvio del server backend...
echo Il backend sarà disponibile su: http://localhost:8083/api/ruota
echo.

call mvn spring-boot:run

pause
