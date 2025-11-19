# PowerShell script để chạy tất cả performance tests
# Run as: .\run-all-tests.ps1

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "   Performance Testing - All Scenarios   " -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Kiểm tra k6 đã được cài đặt
Write-Host "Checking k6 installation..." -ForegroundColor Yellow
$k6Version = k6 version 2>&1
if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: k6 is not installed!" -ForegroundColor Red
    Write-Host "Please install k6 from: https://k6.io/docs/get-started/installation/" -ForegroundColor Red
    exit 1
}
Write-Host "k6 is installed: $k6Version" -ForegroundColor Green
Write-Host ""

# Tạo thư mục results nếu chưa có
if (!(Test-Path -Path "results")) {
    New-Item -ItemType Directory -Path "results" | Out-Null
    Write-Host "Created results directory" -ForegroundColor Green
}

# Function để chạy test
function Run-PerformanceTest {
    param(
        [string]$TestName,
        [string]$ScriptFile,
        [string]$Scenario,
        [string]$OutputFile
    )
    
    Write-Host "========================================" -ForegroundColor Cyan
    Write-Host "Running: $TestName" -ForegroundColor Yellow
    Write-Host "Scenario: $Scenario" -ForegroundColor Yellow
    Write-Host "========================================" -ForegroundColor Cyan
    Write-Host ""
    
    $startTime = Get-Date
    
    # Chạy k6 test
    k6 run "scripts\$ScriptFile" --out "json=results\$OutputFile"
    
    $endTime = Get-Date
    $duration = $endTime - $startTime
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host ""
        Write-Host "✅ Test completed successfully!" -ForegroundColor Green
        Write-Host "Duration: $($duration.ToString('mm\:ss'))" -ForegroundColor Green
        Write-Host "Results saved to: results\$OutputFile" -ForegroundColor Green
    } else {
        Write-Host ""
        Write-Host "❌ Test failed!" -ForegroundColor Red
    }
    
    Write-Host ""
    Write-Host "Press any key to continue to next test..." -ForegroundColor Yellow
    $null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
    Write-Host ""
}

# Menu
Write-Host "Select test scenario:" -ForegroundColor Yellow
Write-Host "1. Login API - All scenarios (100, 500, 1000 users + Stress test)" -ForegroundColor White
Write-Host "2. Login API - 100 users only" -ForegroundColor White
Write-Host "3. Login API - 500 users only" -ForegroundColor White
Write-Host "4. Login API - 1000 users only" -ForegroundColor White
Write-Host "5. Login API - Stress test only" -ForegroundColor White
Write-Host "6. Product API - All scenarios" -ForegroundColor White
Write-Host "7. Product API - 100 users only" -ForegroundColor White
Write-Host "8. Product API - 500 users only" -ForegroundColor White
Write-Host "9. Product API - 1000 users only" -ForegroundColor White
Write-Host "10. Product API - Stress test only" -ForegroundColor White
Write-Host "11. Run ALL tests (Login + Product)" -ForegroundColor White
Write-Host "0. Exit" -ForegroundColor White
Write-Host ""

$choice = Read-Host "Enter your choice (0-11)"

switch ($choice) {
    "1" {
        Write-Host ""
        Write-Host "⚠️  NOTE: Before running, make sure to uncomment the appropriate scenario in login-test.js" -ForegroundColor Yellow
        Write-Host "Press any key to start..." -ForegroundColor Yellow
        $null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
        
        Run-PerformanceTest -TestName "Login API - 100 Users" -ScriptFile "login-test.js" -Scenario "load_100" -OutputFile "login-100-users.json"
        Run-PerformanceTest -TestName "Login API - 500 Users" -ScriptFile "login-test.js" -Scenario "load_500" -OutputFile "login-500-users.json"
        Run-PerformanceTest -TestName "Login API - 1000 Users" -ScriptFile "login-test.js" -Scenario "load_1000" -OutputFile "login-1000-users.json"
        Run-PerformanceTest -TestName "Login API - Stress Test" -ScriptFile "login-test.js" -Scenario "stress_test" -OutputFile "login-stress-test.json"
    }
    "2" {
        Run-PerformanceTest -TestName "Login API - 100 Users" -ScriptFile "login-test.js" -Scenario "load_100" -OutputFile "login-100-users.json"
    }
    "3" {
        Run-PerformanceTest -TestName "Login API - 500 Users" -ScriptFile "login-test.js" -Scenario "load_500" -OutputFile "login-500-users.json"
    }
    "4" {
        Run-PerformanceTest -TestName "Login API - 1000 Users" -ScriptFile "login-test.js" -Scenario "load_1000" -OutputFile "login-1000-users.json"
    }
    "5" {
        Run-PerformanceTest -TestName "Login API - Stress Test" -ScriptFile "login-test.js" -Scenario "stress_test" -OutputFile "login-stress-test.json"
    }
    "6" {
        Run-PerformanceTest -TestName "Product API - 100 Users" -ScriptFile "product-test.js" -Scenario "load_100" -OutputFile "product-100-users.json"
        Run-PerformanceTest -TestName "Product API - 500 Users" -ScriptFile "product-test.js" -Scenario "load_500" -OutputFile "product-500-users.json"
        Run-PerformanceTest -TestName "Product API - 1000 Users" -ScriptFile "product-test.js" -Scenario "load_1000" -OutputFile "product-1000-users.json"
        Run-PerformanceTest -TestName "Product API - Stress Test" -ScriptFile "product-test.js" -Scenario "stress_test" -OutputFile "product-stress-test.json"
    }
    "7" {
        Run-PerformanceTest -TestName "Product API - 100 Users" -ScriptFile "product-test.js" -Scenario "load_100" -OutputFile "product-100-users.json"
    }
    "8" {
        Run-PerformanceTest -TestName "Product API - 500 Users" -ScriptFile "product-test.js" -Scenario "load_500" -OutputFile "product-500-users.json"
    }
    "9" {
        Run-PerformanceTest -TestName "Product API - 1000 Users" -ScriptFile "product-test.js" -Scenario "load_1000" -OutputFile "product-1000-users.json"
    }
    "10" {
        Run-PerformanceTest -TestName "Product API - Stress Test" -ScriptFile "product-test.js" -Scenario "stress_test" -OutputFile "product-stress-test.json"
    }
    "11" {
        Write-Host ""
        Write-Host "Running ALL tests - This will take a long time!" -ForegroundColor Red
        Write-Host "Press any key to start or Ctrl+C to cancel..." -ForegroundColor Yellow
        $null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
        
        # Login tests
        Run-PerformanceTest -TestName "Login API - 100 Users" -ScriptFile "login-test.js" -Scenario "load_100" -OutputFile "login-100-users.json"
        Run-PerformanceTest -TestName "Login API - 500 Users" -ScriptFile "login-test.js" -Scenario "load_500" -OutputFile "login-500-users.json"
        Run-PerformanceTest -TestName "Login API - 1000 Users" -ScriptFile "login-test.js" -Scenario "load_1000" -OutputFile "login-1000-users.json"
        Run-PerformanceTest -TestName "Login API - Stress Test" -ScriptFile "login-test.js" -Scenario "stress_test" -OutputFile "login-stress-test.json"
        
        # Product tests
        Run-PerformanceTest -TestName "Product API - 100 Users" -ScriptFile "product-test.js" -Scenario "load_100" -OutputFile "product-100-users.json"
        Run-PerformanceTest -TestName "Product API - 500 Users" -ScriptFile "product-test.js" -Scenario "load_500" -OutputFile "product-500-users.json"
        Run-PerformanceTest -TestName "Product API - 1000 Users" -ScriptFile "product-test.js" -Scenario "load_1000" -OutputFile "product-1000-users.json"
        Run-PerformanceTest -TestName "Product API - Stress Test" -ScriptFile "product-test.js" -Scenario "stress_test" -OutputFile "product-stress-test.json"
    }
    "0" {
        Write-Host "Exiting..." -ForegroundColor Yellow
        exit 0
    }
    default {
        Write-Host "Invalid choice!" -ForegroundColor Red
        exit 1
    }
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "   All tests completed!   " -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Results are saved in the 'results' directory" -ForegroundColor Green
Write-Host ""
