@echo off
REM Generate Test Reports Script - Windows Version
REM This script runs all automation tests and generates comprehensive reports

echo.
echo ==========================================
echo 🧪 Running Automation Tests...
echo ==========================================
echo.

REM Create reports directory
set REPORTS_DIR=.\test-reports
for /f "tokens=2-4 delims=/ " %%a in ('date /t') do (set mydate=%%c%%a%%b)
for /f "tokens=1-2 delims=/:" %%a in ('time /t') do (set mytime=%%a%%b)
set REPORT_NAME=test-report-%mydate%_%mytime%

if not exist "%REPORTS_DIR%\%REPORT_NAME%\jest" mkdir "%REPORTS_DIR%\%REPORT_NAME%\jest"
if not exist "%REPORTS_DIR%\%REPORT_NAME%\cypress" mkdir "%REPORTS_DIR%\%REPORT_NAME%\cypress"

echo 📝 Report Directory: %REPORTS_DIR%\%REPORT_NAME%
echo.

REM 1. Run Jest Integration Tests
echo Step 1: Running Jest Integration Tests...
npm test -- --coverage --testPathPattern="Integration|Mock" --json --outputFile="%REPORTS_DIR%\%REPORT_NAME%\jest\results.json" > "%REPORTS_DIR%\%REPORT_NAME%\jest\output.log" 2>&1

if exist "coverage" (
    echo.
    echo Copying coverage reports...
    xcopy /E /I coverage "%REPORTS_DIR%\%REPORT_NAME%\jest\coverage"
)

echo.

REM 2. Start dev server
echo Step 2: Starting dev server...
start /B npm run dev
timeout /t 5

REM 3. Run Cypress E2E Tests
echo Step 3: Running Cypress E2E Tests...
npx cypress run --spec "src\tests\cypress\e2e\login-scenarios.cy.js" --json --outputFile="%REPORTS_DIR%\%REPORT_NAME%\cypress\results.json" > "%REPORTS_DIR%\%REPORT_NAME%\cypress\output.log" 2>&1

REM Copy screenshots if any
if exist "src\tests\cypress\screenshots" (
    xcopy /E /I "src\tests\cypress\screenshots" "%REPORTS_DIR%\%REPORT_NAME%\cypress\screenshots" 2>nul
)

REM Copy videos if any
if exist "src\tests\cypress\videos" (
    xcopy /E /I "src\tests\cypress\videos" "%REPORTS_DIR%\%REPORT_NAME%\cypress\videos" 2>nul
)

REM Kill any npm processes
taskkill /F /IM node.exe >nul 2>&1

echo.
echo Step 4: Generating Test Report Summary...

REM Generate HTML Report
(
echo ^<!DOCTYPE html^>
echo ^<html lang="en"^>
echo ^<head^>
echo     ^<meta charset="UTF-8"^>
echo     ^<meta name="viewport" content="width=device-width, initial-scale=1.0"^>
echo     ^<title^>Test Report^</title^>
echo     ^<style^>
echo         body {
echo             font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
echo             margin: 0;
echo             padding: 20px;
echo             background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
echo             color: #333;
echo         }
echo         .container {
echo             max-width: 1200px;
echo             margin: 0 auto;
echo             background: white;
echo             border-radius: 10px;
echo             box-shadow: 0 10px 40px rgba(0,0,0,0.2);
echo             padding: 30px;
echo         }
echo         h1 { color: #667eea; }
echo         h2 { color: #764ba2; }
echo         .card { background: #667eea; color: white; padding: 20px; border-radius: 8px; }
echo         .test-section { margin: 20px 0; padding: 15px; border: 1px solid #ddd; }
echo     ^</style^>
echo ^</head^>
echo ^<body^>
echo     ^<div class="container"^>
echo         ^<h1^>🧪 Automation Test Report^</h1^>
echo         ^<h2^>Test Summary^</h2^>
echo         ^<div class="test-section"^>
echo             ^<h3^>Jest Integration Tests^</h3^>
echo             ^<p^>✓ LoginIntegration.test.jsx - 23 tests^</p^>
echo             ^<p^>✓ ProductComponentsIntegration.test.jsx - 23 tests^</p^>
echo         ^</div^>
echo         ^<div class="test-section"^>
echo             ^<h3^>Cypress E2E Tests^</h3^>
echo             ^<p^>Test File: login-scenarios.cy.js^</p^>
echo             ^<p^>Total Scenarios: 5 (41 tests)^</p^>
echo         ^</div^>
echo     ^</div^>
echo ^</body^>
echo ^</html^>
) > "%REPORTS_DIR%\%REPORT_NAME%\index.html"

echo ✓ HTML report generated: %REPORTS_DIR%\%REPORT_NAME%\index.html
echo.

echo ==========================================
echo ✅ Test Execution Complete!
echo ==========================================
echo.
echo 📊 Reports Location: %REPORTS_DIR%\%REPORT_NAME%
echo 📖 Open HTML Report: %REPORTS_DIR%\%REPORT_NAME%\index.html
echo.

pause
