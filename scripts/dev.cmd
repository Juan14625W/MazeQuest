@echo off
rem Abre VS Code con Java 21 y Maven locales disponibles para sus extensiones.
setlocal
cd /d "%~dp0.."
if exist ".tools\jdk" for /d %%J in (".tools\jdk\*") do set "JAVA_HOME=%%~fJ"
if defined JAVA_HOME set "PATH=%JAVA_HOME%\bin;%PATH%"
set "PATH=%CD%\.tools\apache-maven-3.9.11\bin;%PATH%"
call code .
