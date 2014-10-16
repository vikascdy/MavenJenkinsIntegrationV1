# Remote Debugging

First you need to specify the remote debugging option in exce.bat under system directory 

    "%JAVA_HOME%\bin\java" -Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=4000,suspend=n -Djava.net.preferIPv4Stack=true -classpath .;"%~dp0*" %*


This can be set in several places.

  * Command arguments on startup through command line.

    Example: `startNode.bat -nodeName=Core`
  
  * Within Configuration Files
    * conf/config.properties: `default.node.jvm.opts=-Djava.net.preferIPv4Stack=true;-Xms2024M;-Xmx4096M;-XX:MaxPermSize=256M -Xdebug -Xrunjdwp:transport=dt_socket,address=8998,server=y`
    * conf/Configuration.xml

        ...
        <Node>        
          <JVMOptions>
            <Property>
              <Value>-Xdebug</Value>
            </Property>
            <Property>
              <Value>-Xrunjdwp:transport=dt_socket,address=8998,server=y</Value>
            </Property>
          </JVMOptions>
          ...

Then you configure your IDE or debugger of choice to attach itself to debug the JVM running with the specified port.