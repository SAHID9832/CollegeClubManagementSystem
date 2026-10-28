Write-Host "====================================================================" -ForegroundColor Cyan
Write-Host "  College Club Management System - MCA Semester 1" -ForegroundColor Yellow
Write-Host "  Starting Integrated Server (Frontend + Java Backend + Database)" -ForegroundColor Green
Write-Host "====================================================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "  Server URL : http://localhost:8080/collegeclub/" -ForegroundColor White
Write-Host "  Database   : Embedded H2 Database (Auto-Connected)" -ForegroundColor Gray
Write-Host ""
Write-Host "Opening browser in 6 seconds..." -ForegroundColor DarkGray
Write-Host "Press Ctrl+C to stop the server.`n" -ForegroundColor DarkGray

Start-Job -ScriptBlock {
    Start-Sleep -Seconds 6
    Start-Process "http://localhost:8080/collegeclub/"
} | Out-Null

mvn cargo:run
