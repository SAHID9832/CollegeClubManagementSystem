# ==============================================================================
# College Club Management System - Automated Smoke Test Suite
# Course: 26MCA1L211 - Java and Web Programming Practical
# Phase: 4 - Server-Side Integration & Execution Verification
# ==============================================================================

param (
    [string]$BaseUrl = "http://localhost:8080/collegeclub"
)

Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host " Running College Club Management System Smoke Test Suite " -ForegroundColor Cyan
Write-Host " Target URL: $BaseUrl" -ForegroundColor Cyan
Write-Host "==========================================================" -ForegroundColor Cyan

$passed = 0
$failed = 0

function Assert-Step {
    param (
        [string]$Name,
        [scriptblock]$Test
    )
    Write-Host -NoNewline "[TEST] $Name ... "
    try {
        $result = & $Test
        if ($result -eq $true) {
            Write-Host "PASS" -ForegroundColor Green
            $script:passed++
        } else {
            Write-Host "FAIL" -ForegroundColor Red
            $script:failed++
        }
    } catch {
        Write-Host "ERROR: $_" -ForegroundColor Red
        $script:failed++
    }
}

# 1. Health Check
Assert-Step "1. System Health Check (/api/health)" {
    $r = Invoke-RestMethod -Uri "$BaseUrl/api/health" -Method Get -TimeoutSec 10
    return ($r.status -eq "UP" -and $r.database -eq "CONNECTED")
}

# 2. Registration (Unique timestamped user)
$testUser = "testuser_$(Get-Random -Minimum 1000 -Maximum 9999)"
$testPass = "Password@123"

Assert-Step "2. Student Registration (/api/auth/register)" {
    $payload = @{
        username   = $testUser
        password   = $testPass
        email      = "$testUser@college.edu"
        fullName   = "Test Student"
        rollNumber = "MCA26-$(Get-Random -Minimum 100 -Maximum 999)"
        department = "Computer Applications"
        course     = "MCA"
        year       = 1
    } | ConvertTo-Json

    $r = Invoke-RestMethod -Uri "$BaseUrl/api/auth/register" -Method Post -Body $payload -ContentType "application/json"
    return ($r.success -eq $true)
}

# 3. Duplicate Registration Rejection
Assert-Step "3. Duplicate Registration Rejection" {
    $payload = @{
        username   = $testUser
        password   = $testPass
        email      = "dup@college.edu"
        fullName   = "Duplicate Student"
        rollNumber = "MCA26-000"
        department = "MCA"
        course     = "MCA"
        year       = 1
    } | ConvertTo-Json

    try {
        $r = Invoke-RestMethod -Uri "$BaseUrl/api/auth/register" -Method Post -Body $payload -ContentType "application/json"
        return $false
    } catch {
        return ($_.Exception.Response.StatusCode.value__ -eq 400)
    }
}

# 4. Login & Session Establishment
$sessionCookieJar = New-Object Microsoft.PowerShell.Commands.WebRequestSession

Assert-Step "4. Student Authentication & Session Establishment (/api/auth/login)" {
    $payload = @{
        username = $testUser
        password = $testPass
    } | ConvertTo-Json

    $r = Invoke-RestMethod -Uri "$BaseUrl/api/auth/login" -Method Post -Body $payload -ContentType "application/json" -WebSession $sessionCookieJar
    return ($r.success -eq $true -and $r.data.username -eq $testUser)
}

# 5. Clubs Catalog Discovery
Assert-Step "5. Clubs Catalog Retrieval (/api/clubs)" {
    $r = Invoke-RestMethod -Uri "$BaseUrl/api/clubs" -Method Get -WebSession $sessionCookieJar
    return ($r.success -eq $true -and $r.data.Count -gt 0)
}

# 6. Events Catalog Discovery
Assert-Step "6. Events Catalog Retrieval (/api/events)" {
    $r = Invoke-RestMethod -Uri "$BaseUrl/api/events" -Method Get -WebSession $sessionCookieJar
    return ($r.success -eq $true -and $r.data.Count -gt 0)
}

# 7. XML Faculty Slide Form Navigation Submission
Assert-Step "7. Faculty Student XML Form Navigation (/api/xml/student)" {
    $xmlPayload = "<?xml version=""1.0"" encoding=""UTF-8""?><student><name>Arun Kumar</name><rollno>26MCA001</rollno><course>MCA</course><age>22</age></student>"
    $r = Invoke-RestMethod -Uri "$BaseUrl/api/xml/student" -Method Post -Body $xmlPayload -ContentType "application/xml"
    return ($r.success -eq $true -and $r.data.name -eq "Arun Kumar")
}

# 8. XML Clubs Catalog Export
Assert-Step "8. XML Clubs Catalog Export (/api/xml/clubs)" {
    $xml = Invoke-RestMethod -Uri "$BaseUrl/api/xml/clubs" -Method Get
    return ($xml -match "<clubs>" -and $xml -match "<club id=")
}

# 9. User Logout & Session Invalidation
Assert-Step "9. User Logout & Invalidation (/api/auth/logout)" {
    $r = Invoke-RestMethod -Uri "$BaseUrl/api/auth/logout" -Method Post -WebSession $sessionCookieJar
    return ($r.success -eq $true)
}

Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host " Test Execution Completed: $passed Passed, $failed Failed " -ForegroundColor $(if ($failed -eq 0) { "Green" } else { "Red" })
Write-Host "==========================================================" -ForegroundColor Cyan
