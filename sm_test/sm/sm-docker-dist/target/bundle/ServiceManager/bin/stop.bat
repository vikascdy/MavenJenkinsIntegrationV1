@echo off

chdir %~dp0
pushd ..
SETX EDIFECS_SM_DIST %CD%
popd
@call "%~dp0\system\exec-stopper.bat" com.edifecs.agent.stopper.AgentStopper %*