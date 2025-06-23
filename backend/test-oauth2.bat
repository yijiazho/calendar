@echo off
echo Testing OAuth2 Integration with Real Credentials...
echo.

echo Checking environment variables...
if "%GOOGLE_CLIENT_ID%"=="" (
    echo ERROR: GOOGLE_CLIENT_ID environment variable is not set
    exit /b 1
)
if "%GOOGLE_CLIENT_SECRET%"=="" (
    echo ERROR: GOOGLE_CLIENT_SECRET environment variable is not set
    exit /b 1
)
if "%OUTLOOK_CLIENT_ID%"=="" (
    echo ERROR: OUTLOOK_CLIENT_ID environment variable is not set
    exit /b 1
)
if "%OUTLOOK_CLIENT_SECRET%"=="" (
    echo ERROR: OUTLOOK_CLIENT_SECRET environment variable is not set
    exit /b 1
)

echo Environment variables are set correctly.
echo.

echo Running OAuth2 integration tests...
mvn test -Dtest=OAuth2IntegrationTest -Dspring.profiles.active=test

echo.
echo OAuth2 integration tests completed.
pause 