@echo off
setlocal
title Emergency Contact Assistant - Launcher

echo ===================================================
echo     EMERGENCY CONTACT ASSISTANT (SWING APP)
echo ===================================================

REM 1. Priority: Check installed Android Studio OpenJDK 21
if exist "C:\Program Files\Android\Android Studio\jbr\bin\javac.exe" (
    set "JAVA_BIN=C:\Program Files\Android\Android Studio\jbr\bin"
    goto :FOUND
)

REM 2. Check JAVA_HOME
if defined JAVA_HOME (
    if exist "%JAVA_HOME%\bin\javac.exe" (
        set "JAVA_BIN=%JAVA_HOME%\bin"
        goto :FOUND
    )
)

REM 3. Check if javac is in PATH
where javac >nul 2>&1
if %ERRORLEVEL% equ 0 (
    goto :FOUND_PATH
)

echo [ERROR] Java Development Kit 17 or higher was not found.
echo Please ensure JDK 17+ is installed.
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
echo [1/2] Compiling Java source files...
if not exist "bin" mkdir bin
"%JAVAC_CMD%" -d bin -sourcepath src src\emergency\*.java
if %ERRORLEVEL% neq 0 (
    echo [ERROR] Compilation failed.
    pause
    exit /b %ERRORLEVEL%
)

echo [2/2] Launching Emergency Contact Assistant GUI...
start "" "%JAVA_CMD%" -cp bin emergency.Main
echo Application started successfully!
