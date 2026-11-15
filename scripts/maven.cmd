@echo off
rem Usa herramientas locales verificadas o las instaladas en el sistema.
setlocal
cd /d "%~dp0.."
call "%~dp0java-env.cmd"
if errorlevel 1 exit /b 1
if exist ".tools\apache-maven-3.9.11\bin\mvn.cmd" (
  call ".tools\apache-maven-3.9.11\bin\mvn.cmd" %*
) else (
  call mvn %*
)
exit /b %errorlevel%
