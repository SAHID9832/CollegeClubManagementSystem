# ==============================================================================
# College Club Management System - Build & Deployment Script
# Course: 26MCA1L211 - Java and Web Programming Practical
# Packages WAR and deploys to Tomcat webapps directory
# ==============================================================================

Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host " Building College Club Management System WAR Package     " -ForegroundColor Cyan
Write-Host "==========================================================" -ForegroundColor Cyan

# Ensure workspace root
$projectRoot = Split-Path -Parent $PSScriptRoot

# Run Maven clean package
mvn -f "$projectRoot\pom.xml" clean package -DskipTests

if ($LASTEXITCODE -ne 0) {
    Write-Host "[ERROR] Maven build failed with exit code $LASTEXITCODE" -ForegroundColor Red
    exit 1
}

$warPath = "$projectRoot\target\collegeclub.war"
if (-not (Test-Path $warPath)) {
    Write-Host "[ERROR] WAR file not generated at $warPath" -ForegroundColor Red
    exit 1
}

$distDir = "$projectRoot\dist"
if (-not (Test-Path $distDir)) {
    New-Item -ItemType Directory -Path $distDir -Force | Out-Null
}

Copy-Item $warPath "$distDir\collegeclub.war" -Force
Write-Host "[SUCCESS] WAR package created: $distDir\collegeclub.war" -ForegroundColor Green

# If CATALINA_HOME is set, deploy to Tomcat webapps
if ($env:CATALINA_HOME -and (Test-Path "$env:CATALINA_HOME\webapps")) {
    $targetApp = "$env:CATALINA_HOME\webapps\collegeclub.war"
    Copy-Item $warPath $targetApp -Force
    Write-Host "[SUCCESS] Deployed to Tomcat: $targetApp" -ForegroundColor Green
} else {
    Write-Host "[INFO] CATALINA_HOME environment variable not detected." -ForegroundColor Yellow
    Write-Host "[INFO] Run development server using: .\scripts\run-tomcat.ps1" -ForegroundColor Yellow
}
