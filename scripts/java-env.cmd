@echo off
rem Se llama desde un script con setlocal: configura solo ese proceso.
rem Respeta JAVA_HOME explicito; si falta, busca un JDK 21 local o usa PATH.
if defined JAVA_HOME goto validate_home
for /d %%J in ("%~dp0..\.tools\jdk\jdk-21*") do if not defined JAVA_HOME if exist "%%~fJ\bin\java.exe" if exist "%%~fJ\bin\javac.exe" set "JAVA_HOME=%%~fJ"
if defined JAVA_HOME goto validate_home
where java >nul 2>nul
if errorlevel 1 goto missing_java
where javac >nul 2>nul
if errorlevel 1 goto missing_java
exit /b 0

:validate_home
if not exist "%JAVA_HOME%\bin\java.exe" goto invalid_home
if not exist "%JAVA_HOME%\bin\javac.exe" goto invalid_home
set "PATH=%JAVA_HOME%\bin;%PATH%"
exit /b 0

:invalid_home
echo ERROR: JAVA_HOME debe apuntar a un JDK valido con java y javac. 1>&2
exit /b 1

:missing_java
echo ERROR: Instala JDK 21 y configura JAVA_HOME o agrega java y javac al PATH. 1>&2
exit /b 1
