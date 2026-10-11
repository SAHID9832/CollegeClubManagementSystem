# ==============================================================================
# College Club Management System - Run Tomcat 10.1 Development Server
# Course: 26MCA1L211 - Java and Web Programming Practical
# Launches Apache Tomcat 10.1 via Cargo Maven Plugin at http://localhost:8080/collegeclub
# ==============================================================================

$projectRoot = Split-Path -Parent $PSScriptRoot

Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host " Starting Apache Tomcat 10.1 (Cargo Maven Plugin)         " -ForegroundColor Cyan
Write-Host " Context URL: http://localhost:8080/collegeclub           " -ForegroundColor Cyan
Write-Host " Press Ctrl+C in terminal to stop server                  " -ForegroundColor Cyan
Write-Host "==========================================================" -ForegroundColor Cyan

mvn -f "$projectRoot\pom.xml" cargo:run
