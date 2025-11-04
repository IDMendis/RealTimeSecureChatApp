@echo off
REM Build script for Windows

echo ==================================
echo Building Real-Time Chat Application
echo ==================================

REM Check if Maven is installed
where mvn >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo Maven is not installed!
    echo Please install Maven from: https://maven.apache.org/download.cgi
    exit /b 1
)

REM Check Java version
echo Checking Java version...
java -version 2>&1 | findstr /R "version" >nul
if %ERRORLEVEL% NEQ 0 (
    echo Java is not installed!
    echo Download from: https://adoptium.net/temurin/releases/?version=17
    exit /b 1
)

echo Java version OK

REM Clean previous builds
echo Cleaning previous builds...
call mvn clean

REM Compile and package
echo Compiling and packaging...
call mvn package -DskipTests

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ==================================
    echo Build successful!
    echo ==================================
    echo.
    echo To run the server:
    echo   java -cp target\RealTimeSecureChatApp-1.0.0-server.jar com.securechat.server.ChatServer
    echo.
    echo To run the client:
    echo   java -cp target\RealTimeSecureChatApp-1.0.0-client.jar com.securechat.client.ChatClient
    echo.
) else (
    echo.
    echo ==================================
    echo Build failed!
    echo ==================================
    exit /b 1
)
