@echo off

chdir %~dp0
pushd ..
SETX EDIFECS_SM_DIST %CD%
popd
@call "%~dp0\system\exec.bat" com.edifecs.agent.launcher.AgentStarter "-launchNodes=false"