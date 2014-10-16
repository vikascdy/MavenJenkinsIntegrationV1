@echo off
if not defined JAVA_HOME goto invalid_JAVA_HOME

"%JAVA_HOME%\bin\java" -Djava.net.preferIPv4Stack=true -classpath .;"%~dp0*" %*

goto exit
:invalid_JAVA_HOME
    java -Djava.net.preferIPv4Stack=true -classpath .;"%~dp0*" %*
    exit /B 0
:exit
exit /B 0