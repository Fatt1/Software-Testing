@echo off
REM Script to run Spring Boot application with proper Java settings

echo ====================================
echo Starting Spring Boot Application
echo ====================================

REM Check if Java is installed
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Java is not installed or not in PATH
    echo Please install Java 21 or higher
    pause
    exit /b 1
)

echo.
echo Java version detected:
java -version

echo.
echo ====================================
echo Building and running application...
echo ====================================

REM Navigate to backend directory
cd /d "%~dp0"

REM Clean and compile (skip tests for faster startup)
echo.
echo [1/3] Cleaning and compiling...
call mvnw.cmd clean compile -q
if %errorlevel% neq 0 (
    echo ERROR: Compilation failed!
    pause
    exit /b 1
)

echo [1/3] Compilation successful!

REM Run Spring Boot application
echo.
echo [2/3] Starting Spring Boot application...
echo.
echo ====================================
echo Application is starting...
echo Watch for Hibernate DDL statements
echo ====================================
echo.

call mvnw.cmd spring-boot:run

pause
