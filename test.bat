@echo off
setlocal
title Emergency Contact Assistant - Automated Test Runner

echo ===================================================
echo     EMERGENCY CONTACT ASSISTANT - TEST SUITE
echo ===================================================

if exist "C:\Program Files\Android\Android Studio\jbr\bin\javac.exe" (
    set "JAVA_BIN=C:\Program Files\Android\Android Studio\jbr\bin"
    goto :FOUND
)

if defined JAVA_HOME (
    if exist "%JAVA_HOME%\bin\javac.exe" (
        set "JAVA_BIN=%JAVA_HOME%\bin"
        goto :FOUND
    )
)

where javac >nul 2>&1
if %ERRORLEVEL% equ 0 (
    goto :FOUND_PATH
)

echo [ERROR] Java Development Kit 17 or higher was not found.
pause
exit /b 1

:FOUND
set "JAVA_CMD=%JAVA_BIN%\java.exe"
set "JAVAC_CMD=%JAVA_BIN%\javac.exe"
goto :COMPILE

:FOUND_PATH
set "JAVA_CMD=java"
set "JAVAC_CMD=javac"

:COMPILE
echo [1/2] Compiling sources and tests...
if not exist "bin" mkdir bin
"%JAVAC_CMD%" -d bin -sourcepath src src\emergency\*.java
if %ERRORLEVEL% neq 0 (
    echo [ERROR] Compilation failed.
    pause
    exit /b %ERRORLEVEL%
)

echo [2/2] Running automated test cases...
echo.
"%JAVA_CMD%" -cp bin emergency.ContactManagerTest
echo.
"%JAVA_CMD%" -cp bin emergency.GUISmokeTest

echo.
echo All tests completed.
pause
