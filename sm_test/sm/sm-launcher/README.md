# Launching SM

There are several ways to configure and run SM based on the deployment architecture or development needs.

JRE/JDK Version

  * 1.7 or 1.8 required

Launch Service Manager Manually

  * Run start.bat found in: <service-manager-dir>\bin\start.bat

The Agent is not required to be running, it job is to simply monitor the running SM nodes that contain services, and restart/kill the JVM's
as necessary. It is just the monitoring component and single launch point for a set of processes.

This means that most of the time, only the nodes need to be run.

By default, there is a `Core` node, that needs to be running. This runs the core services:

  * ESM
  * Web Server
  * Content Repository
  * Flex Field Service
  * Application Bar Service

These configurations can be found in the Core-cConfiguration.xml, and can be removed.

This means that if you are manually running nodes, you always need to make sure that the CORE node is running. This can be launched once
and forgotten about as you start and stop other nodes as needed.

To start a node easily from a command line, just run:

    <service-manager-dir>\bin\startNode.bat -nodeName=Core

For Remote Debugging Configurations

* [Configuring SM for remote Debugging](docs/RemoteDebugging.md)

For Integration into a Developers IDE:

* [Development in Eclipse](docs/EclipseDevelopment.md)
* [Development in Intellij](docs/IntellijDevelopment.md)



## Using Bat or Shell scripts

Launch the SM Agent and all configured Nodes for that server

    startAgent.bat

Starting a node by nodeName defined in the Configuration.xml file

    startNode.bat -nodeName=Core

Starting a node from commandline without the Configuration.xml file

    startNode.bat -nodeConfig="{name:Core, services:[{name:esm-service, serviceType:esm-service, version:1.0, properties:[{name:password.max.attemps, value:5}], resources:[{typeName:'Security Database',properties:[{name:'URL',value:'jdbc:h2:file:securityDB'},{name:'Driver',value:org.h2.Driver},{name:Dialect,value:org.hibernate.dialect.H2Dialect},{name:Username,value:sa},{name:Password,value:''},{name:AutoCreate,value:true}]}]}]}" -cluster.name=TestCluster

## Launching Directly VIA Java

The launcher jar files are located in ServiceManager/bin/system so your classpath needs to add this directory.

The working directory needs to be ServiceManager/bin

