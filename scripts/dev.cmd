@echo off
rem Abre VS Code respetando el JDK configurado y priorizando JDK 21 local si falta.
setlocal
cd /d "%~dp0.."
call "%~dp0java-env.cmd"
if errorlevel 1 exit /b 1
set "PATH=%CD%\.tools\apache-maven-3.9.11\bin;%PATH%"
call code .
