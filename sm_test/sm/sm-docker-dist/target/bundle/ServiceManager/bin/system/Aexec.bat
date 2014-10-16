@echo off
if not defined JAVA_HOME goto invalid_JAVA_HOME

"%JAVA_HOME%\bin\java" -Djava.net.preferIPv4Stack=true -classpath .;"%~dp0*" %*

goto exit
:invalid_JAVA_HOME
    echo.
    echo Error: The environment variable JAVA_HOME must be set.
    echo.
    exit /B 1
:exit
exit /B 0