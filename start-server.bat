@echo off
title College Club Management System - Server
echo ====================================================================
echo   College Club Management System - MCA Semester 1
echo   Starting Integrated Server (Frontend + Java Backend + Database)
echo ====================================================================
echo.
echo   Server Port : 8080
echo   Context URL : http://localhost:8080/collegeclub/
echo   Database    : Embedded H2 Database (Auto-Connected)
echo.
echo   Opening browser in 6 seconds...
start /b cmd /c "timeout /t 6 /nobreak >nul & start http://localhost:8080/collegeclub/"
echo.
echo   Starting Apache Tomcat 10.1 via Maven Cargo...
echo   (Press Ctrl+C to stop the server at any time)
echo.
mvn cargo:run
pause
