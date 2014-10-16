@echo off
if not defined JAVA_HOME goto invalid_JAVA_HOME

"%JAVA_HOME%\bin\java" -Djava.net.preferIPv4Stack=true -Djava.library.path="%~dp0..\..\platform\core\lib\native" -classpath .;"%~dp0..\..\platform\core\lib\common\*";"%~dp0..\..\platform\core\lib\sm-container\*" %*

goto exit
:invalid_JAVA_HOME
    java -Djava.net.preferIPv4Stack=true -Djava.library.path="%~dp0..\..\platform\core\lib\native" -classpath .;"%~dp0..\..\platform\core\lib\common\*";"%~dp0..\..\platform\core\lib\sm-container\*" %*
    exit /B 0
:exit
exit /B 0