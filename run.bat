@echo off
cd /d "%~dp0"

rem find the JDK: JAVA_HOME, then PATH, then the usual install folders
set "JDK="
if defined JAVA_HOME if exist "%JAVA_HOME%\bin\javac.exe" set "JDK=%JAVA_HOME%\bin"
if not defined JDK for /f "delims=" %%i in ('where javac 2^>nul') do if not defined JDK set "JDK=%%~dpi"
if not defined JDK for /d %%d in ("%USERPROFILE%\.jdks\*") do if exist "%%~d\bin\javac.exe" set "JDK=%%~d\bin"
if not defined JDK for /d %%d in ("C:\Program Files\Java\*" "C:\Program Files\Eclipse Adoptium\*" "C:\Program Files\Microsoft\*" "C:\Program Files\Zulu\*") do if exist "%%~d\bin\javac.exe" set "JDK=%%~d\bin"
if not defined JDK (
    echo Could not find a JDK. Install a JDK or set JAVA_HOME.
    pause
    exit /b 1
)

rem delete the old class files and compile everything again
if exist bin rmdir /s /q bin
"%JDK%\javac" -d bin -sourcepath src src\com\auca\main\Main.java
if errorlevel 1 (
    pause
    exit /b 1
)
"%JDK%\java" -cp bin com.auca.main.Main %*
