@echo off

echo Starting Calendar Aggregator Services in DEBUG mode...

:: Start the backend service in debug mode (port 5005)
start cmd /k "cd backend && mvn spring-boot:run -Dspring-boot.run.jvmArguments='-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005'"

:: Wait for 10 seconds to let the backend start
timeout /t 10

:: Start the frontend service
start cmd /k "cd frontend && npm start"

echo Services are starting in DEBUG mode...
echo Backend (debug) will be available at: http://localhost:8080 (debug port: 5005)
echo Frontend will be available at: http://localhost:3000 