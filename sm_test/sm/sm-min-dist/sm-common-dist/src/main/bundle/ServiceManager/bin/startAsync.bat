@echo off

chdir %~dp0
pushd ..
SETX EDIFECS_SM_DIST %CD%
popd
wscript.exe "invis.vbs" "start.bat"

echo "Edifecs Agent Started"