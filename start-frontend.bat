@echo off
echo ========================================
echo   Avvio Frontend Ruota della Fortuna
echo ========================================
echo.

cd frontend

echo Installazione dipendenze (se necessario)...
call npm install

echo.
echo Avvio del server frontend...
echo Il frontend sarà disponibile su: http://localhost:4200
echo.

call npm start

pause
