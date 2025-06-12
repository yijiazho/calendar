@echo off
echo Starting Calendar Aggregator Services...

:: Start the backend service
start cmd /k "cd backend && mvn spring-boot:run"

:: Wait for 10 seconds to let the backend start
timeout /t 10

:: Start the frontend service
start cmd /k "cd frontend && npm start"

echo Services are starting...
echo Backend will be available at: http://localhost:8080
echo Frontend will be available at: http://localhost:3000 