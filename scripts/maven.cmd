@echo off
rem Usa herramientas locales verificadas o las instaladas en el sistema.
setlocal
cd /d "%~dp0.."
if exist ".tools\jdk" for /d %%J in (".tools\jdk\*") do set "JAVA_HOME=%%~fJ"
if defined JAVA_HOME set "PATH=%JAVA_HOME%\bin;%PATH%"
if exist ".tools\apache-maven-3.9.11\bin\mvn.cmd" (
  call ".tools\apache-maven-3.9.11\bin\mvn.cmd" %*
) else (
  call mvn %*
)
exit /b %errorlevel%
