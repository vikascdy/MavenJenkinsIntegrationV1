@echo off
::---------------------------------------------
::- Author: Abhijit Singh, William Clements
::- Date: 1/10/2014
::- http://www.edifecs.com
::---------------------------------------------

NET FILE 1>NUL 2>NUL & IF ERRORLEVEL 1 (ECHO You must right-click and select & ECHO "RUN AS ADMINISTRATOR"  to run this batch. Exiting... & ECHO. & PAUSE & EXIT /D)
REM


if "%OS%" == "Windows_NT" setlocal

set SERVICE_NAME=%~2
set APPLICATION_SERVICE_HOME=%~dp0..\..

::---------------------------------------------
:: -- Update this section to match your needs
::---------------------------------------------

::-- 1. This name should match the name you gave to the prunsrv executable
set SERVICE_ID=EdifecsAgentService
set EXECUTABLE_NAME=service.exe
set EXECUTABLE=%APPLICATION_SERVICE_HOME%\bin\system\%EXECUTABLE_NAME%
set START_IMAGE=%APPLICATION_SERVICE_HOME%\bin\start.bat
set STOP_IMAGE=%APPLICATION_SERVICE_HOME%\bin\stop.bat

set CG_START_PATH=%APPLICATION_SERVICE_HOME%\bin
set CG_STOP_PATH=%CG_START_PATH%

::-- 6. Set to auto if you want the service to startup automatically. The default is manual
set CG_STARTUP_TYPE=auto

::---- Set other options via environment variables, just as an example -------
set PR_STARTUP=auto
set PR_DESCRIPTION=Edifecs Agent Management Service
set PR_INSTALL=%EXECUTABLE%

echo.

if [%1] == [] goto displayUsage
if [%1] == [remove] goto remove
if [%1] == [install] goto install

:displayUsage
echo Usage: Install-Remove-Serice.bat install/remove [service_name] [autostart (yes/no)]
goto end

:installDisplayUsage
echo Install Usage: Install-Remove-Serice.bat install service_name [autostart (yes/no)]
goto end

:removeDisplayUsage
echo Remove Usage: Install-Remove-Serice.bat remove
goto end

:remove

set EXECUTABLE=%APPLICATION_SERVICE_HOME%\bin\system\%EXECUTABLE_NAME%

::---- Remove the service -------
"%EXECUTABLE%" "//DS//%SERVICE_ID%"
IF ERRORLEVEL 1 GOTO NOT-THERE
	echo The service with ID '%SERVICE_ID%' has been removed
:NOT-THERE
	echo.

goto end

:install
if [%2] == [] goto installDisplayUsage

::---- Install the Service -------
echo Installing service '%SERVICE_ID%'
echo.

set PR_LOGPATH=%APPLICATION_SERVICE_HOME%\log

set EXECUTE_STRING=%EXECUTABLE% //IS//%SERVICE_ID%  --Startup="%CG_STARTUP_TYPE%"  --DisplayName="%SERVICE_NAME%" --Install="%EXECUTABLE%" --StartMode=exe --StartImage="%START_IMAGE%" --StopMode=exe --StopImage="%STOP_IMAGE%" ++StopParams="-f" --StdOutput="%PR_LOGPATH%\out.log, console" --StdError="%PR_LOGPATH%\err.log" --StartPath="%CG_STOP_PATH%" --StopPath="%CG_STOP_PATH%"
call:executeAndPrint %EXECUTE_STRING%

if [%3] == [] goto end
if %3 == yes goto auto
if %3 == no goto end

goto end

:auto
:: -------- start the service -----------

echo Starting Service '%SERVICE_NAME%'
net start "%SERVICE_ID%"

goto end

::--------
::- Functions
::-------
:executeAndPrint
%*

goto:eof

:end
echo.

