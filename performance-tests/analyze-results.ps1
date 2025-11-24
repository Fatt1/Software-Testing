# ========================================
# Analyze k6 Test Results
# ========================================

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  k6 Performance Test Analysis  " -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

Set-Location $PSScriptRoot

# Check if results exist
if (!(Test-Path "results")) {
    Write-Host "❌ No results directory found!" -ForegroundColor Red
    Write-Host "Please run tests first: .\run-complete-tests.ps1" -ForegroundColor Yellow
    exit 1
}

$resultFiles = Get-ChildItem "results\*-summary.json" -ErrorAction SilentlyContinue

if ($resultFiles.Count -eq 0) {
    Write-Host "❌ No summary files found!" -ForegroundColor Red
    Write-Host "k6 summary files should end with -summary.json" -ForegroundColor Yellow
    exit 1
}

Write-Host "Found $($resultFiles.Count) test results" -ForegroundColor Green
Write-Host ""

# Function to parse summary
function Parse-Summary {
    param([string]$FilePath)
    
    try {
        $json = Get-Content $FilePath -Raw | ConvertFrom-Json
        
        $metrics = $json.metrics
        
        $result = @{
            TotalRequests = [math]::Round($metrics.http_reqs.values.count, 0)
            AvgDuration = [math]::Round($metrics.http_req_duration.values.avg, 2)
            P95Duration = [math]::Round($metrics.http_req_duration.values.'p(95)', 2)
            P99Duration = [math]::Round($metrics.http_req_duration.values.'p(99)', 2)
            MaxDuration = [math]::Round($metrics.http_req_duration.values.max, 2)
            MinDuration = [math]::Round($metrics.http_req_duration.values.min, 2)
            ErrorRate = [math]::Round($metrics.http_req_failed.values.rate * 100, 2)
            SuccessRate = [math]::Round((1 - $metrics.http_req_failed.values.rate) * 100, 2)
            VUs = $metrics.vus.values.value
            Iterations = [math]::Round($metrics.iterations.values.count, 0)
        }
        
        return $result
    } catch {
        Write-Host "⚠️  Error parsing $FilePath" -ForegroundColor Yellow
        return $null
    }
}

# Analyze each test
$allResults = @()

foreach ($file in $resultFiles) {
    Write-Host "Analyzing: $($file.Name)" -ForegroundColor Cyan
    
    $result = Parse-Summary -FilePath $file.FullName
    
    if ($result) {
        $testName = $file.Name -replace '-summary\.json$', ''
        
        $allResults += [PSCustomObject]@{
            Test = $testName
            TotalRequests = $result.TotalRequests
            AvgDuration = $result.AvgDuration
            P95 = $result.P95Duration
            P99 = $result.P99Duration
            SuccessRate = $result.SuccessRate
            ErrorRate = $result.ErrorRate
        }
        
        Write-Host "  Total Requests: $($result.TotalRequests)" -ForegroundColor White
        Write-Host "  Avg Duration: $($result.AvgDuration) ms" -ForegroundColor White
        Write-Host "  P95: $($result.P95Duration) ms" -ForegroundColor White
        Write-Host "  P99: $($result.P99Duration) ms" -ForegroundColor White
        Write-Host "  Success Rate: $($result.SuccessRate)%" -ForegroundColor $(if ($result.SuccessRate -ge 99) { "Green" } elseif ($result.SuccessRate -ge 95) { "Yellow" } else { "Red" })
        Write-Host "  Error Rate: $($result.ErrorRate)%" -ForegroundColor $(if ($result.ErrorRate -le 1) { "Green" } elseif ($result.ErrorRate -le 5) { "Yellow" } else { "Red" })
        Write-Host ""
    }
}

# Export summary table
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Summary Table" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

$allResults | Format-Table -AutoSize

# Export to CSV
$csvPath = "results\summary-all-tests.csv"
$allResults | Export-Csv -Path $csvPath -NoTypeInformation
Write-Host "✅ Summary exported to: $csvPath" -ForegroundColor Green
Write-Host ""

# Create markdown report
Write-Host "Generating markdown report..." -ForegroundColor Yellow

$mdContent = @"
# Performance Test Results Report

**Generated:** $(Get-Date -Format "yyyy-MM-dd HH:mm:ss")

## Test Summary

| Test Name | Total Requests | Avg Duration (ms) | P95 (ms) | P99 (ms) | Success Rate | Error Rate |
|-----------|----------------|-------------------|----------|----------|--------------|------------|
"@

foreach ($result in $allResults) {
    $statusIcon = if ($result.SuccessRate -ge 99) { "✅" } elseif ($result.SuccessRate -ge 95) { "⚠️" } else { "❌" }
    $mdContent += "`n| $statusIcon $($result.Test) | $($result.TotalRequests) | $($result.AvgDuration) | $($result.P95) | $($result.P99) | $($result.SuccessRate)% | $($result.ErrorRate)% |"
}

$mdContent += @"


## Analysis by API

### Login API

"@

$loginTests = $allResults | Where-Object { $_.Test -like "login-*" }
foreach ($test in $loginTests) {
    $mdContent += @"

#### $($test.Test)
- **Total Requests:** $($test.TotalRequests)
- **Average Response Time:** $($test.AvgDuration) ms
- **95th Percentile:** $($test.P95) ms
- **99th Percentile:** $($test.P99) ms
- **Success Rate:** $($test.SuccessRate)%
- **Error Rate:** $($test.ErrorRate)%

"@
}

$mdContent += @"

### Product API

"@

$productTests = $allResults | Where-Object { $_.Test -like "product-*" }
foreach ($test in $productTests) {
    $mdContent += @"

#### $($test.Test)
- **Total Requests:** $($test.TotalRequests)
- **Average Response Time:** $($test.AvgDuration) ms
- **95th Percentile:** $($test.P95) ms
- **99th Percentile:** $($test.P99) ms
- **Success Rate:** $($test.SuccessRate)%
- **Error Rate:** $($test.ErrorRate)%

"@
}

$mdContent += @"

## Performance Thresholds

| Metric | Threshold | Status |
|--------|-----------|--------|
| Success Rate | > 99% | $(if (($allResults | Measure-Object -Property SuccessRate -Minimum).Minimum -ge 99) { "✅ PASS" } else { "❌ FAIL" }) |
| Error Rate | < 1% | $(if (($allResults | Measure-Object -Property ErrorRate -Maximum).Maximum -le 1) { "✅ PASS" } else { "❌ FAIL" }) |
| P95 Response Time | < 2000ms | $(if (($allResults | Measure-Object -Property P95 -Maximum).Maximum -le 2000) { "✅ PASS" } else { "❌ FAIL" }) |
| P99 Response Time | < 3000ms | $(if (($allResults | Measure-Object -Property P99 -Maximum).Maximum -le 3000) { "✅ PASS" } else { "❌ FAIL" }) |

## Recommendations

### 🔴 Critical Issues
"@

$criticalIssues = @()
$highPriorityIssues = @()
$mediumPriorityIssues = @()

foreach ($result in $allResults) {
    if ($result.ErrorRate -gt 10) {
        $criticalIssues += "- **$($result.Test)**: Error rate is $($result.ErrorRate)% (> 10%)"
    } elseif ($result.ErrorRate -gt 5) {
        $highPriorityIssues += "- **$($result.Test)**: Error rate is $($result.ErrorRate)% (> 5%)"
    }
    
    if ($result.P99 -gt 5000) {
        $criticalIssues += "- **$($result.Test)**: P99 response time is $($result.P99)ms (> 5s)"
    } elseif ($result.P99 -gt 3000) {
        $highPriorityIssues += "- **$($result.Test)**: P99 response time is $($result.P99)ms (> 3s)"
    }
    
    if ($result.SuccessRate -lt 90) {
        $criticalIssues += "- **$($result.Test)**: Success rate is only $($result.SuccessRate)% (< 90%)"
    } elseif ($result.SuccessRate -lt 99) {
        $mediumPriorityIssues += "- **$($result.Test)**: Success rate is $($result.SuccessRate)% (< 99%)"
    }
}

if ($criticalIssues.Count -eq 0) {
    $mdContent += "`n`nNo critical issues found! ✅"
} else {
    $mdContent += "`n`n" + ($criticalIssues -join "`n")
}

$mdContent += @"


### 🟡 High Priority

"@

if ($highPriorityIssues.Count -eq 0) {
    $mdContent += "`nNo high priority issues."
} else {
    $mdContent += "`n" + ($highPriorityIssues -join "`n")
}

$mdContent += @"


### 🟢 Medium Priority

"@

if ($mediumPriorityIssues.Count -eq 0) {
    $mdContent += "`nNo medium priority issues."
} else {
    $mdContent += "`n" + ($mediumPriorityIssues -join "`n")
}

$mdContent += @"


## Optimization Recommendations

1. **Database Optimization**
   - Add indexes on frequently queried columns
   - Optimize connection pool settings
   - Enable query caching

2. **Application Level**
   - Implement response caching (Redis)
   - Add circuit breakers
   - Implement rate limiting

3. **Infrastructure**
   - Increase thread pool size
   - Enable HTTP/2
   - Enable response compression

4. **Scaling**
   - Consider horizontal scaling for loads > 500 users
   - Implement load balancing
   - Database replication

## Next Steps

- [ ] Review detailed metrics in individual JSON files
- [ ] Implement high-priority optimizations
- [ ] Re-run tests to measure improvements
- [ ] Monitor production metrics
"@

# Save markdown report
$mdPath = "results\TEST_REPORT.md"
$mdContent | Out-File -FilePath $mdPath -Encoding UTF8
Write-Host "✅ Markdown report generated: $mdPath" -ForegroundColor Green
Write-Host ""

# Display summary
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Final Summary" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Total Tests: $($allResults.Count)" -ForegroundColor White
Write-Host "Average Success Rate: $([math]::Round(($allResults | Measure-Object -Property SuccessRate -Average).Average, 2))%" -ForegroundColor White
Write-Host "Average Error Rate: $([math]::Round(($allResults | Measure-Object -Property ErrorRate -Average).Average, 2))%" -ForegroundColor White
Write-Host "Average P95: $([math]::Round(($allResults | Measure-Object -Property P95 -Average).Average, 2)) ms" -ForegroundColor White
Write-Host "Average P99: $([math]::Round(($allResults | Measure-Object -Property P99 -Average).Average, 2)) ms" -ForegroundColor White
Write-Host ""
Write-Host "📊 Reports generated:" -ForegroundColor Cyan
Write-Host "  - CSV: $csvPath" -ForegroundColor White
Write-Host "  - Markdown: $mdPath" -ForegroundColor White
Write-Host ""
