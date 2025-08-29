@echo off
cls
echo.
echo ========================================
echo         Social Media Manager           
echo ========================================
echo.
echo Features included:
echo - Role-based access (Admin/Creator/Analyst)
echo - Post scheduling with analytics
echo - JFreeChart data visualization
echo - CSV-based persistence
echo - Performance optimization
echo.
echo Instructions:
echo 1. Enter username (any name)
echo 2. For new users: password + role (1=Admin, 2=Creator, 3=Analyst)
echo 3. For existing users: just password
echo.
pause
echo.
echo Starting application...
echo ========================================

cd /d "%~dp0"

rem Compile from src directory (this works!)
cd src
javac -cp ".;../lib/*" socialmedia/*.java
if %errorlevel% neq 0 (
    echo.
    echo ERROR: Compilation failed!
    echo Please check your Java files for syntax errors.
    pause
    exit /b 1
)

rem Run the application from src directory
echo.
echo Application compiled successfully!
echo ========================================
echo.
java -cp ".;../lib/*" socialmedia.SocialMediaManager

echo.
echo ========================================
echo      Application finished             
echo ========================================
pause
