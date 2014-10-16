# Initialization

Include the isc-api project.

Maven Dependency:

    <dependency>
        <groupId>com.edifecs.epp</groupId>
        <artifactId>isc-api</artifactId>
        <version>1.6.0.0-SNAPSHOT</version>
    </dependency>


## HOW TO CONNECT TO CLUSTER
  
ClusterConnectionBuilder class is used to connect to a cluster. It uses the Builder pattern to create and configure the
connections to the cluster.
  

    CommandCommunicatorBuilder builder = new CommandCommunicatorBuilder();
    builder.setClusterName(CLUSTER_NAME);
    builder.setAddress(node1);

    ICommandCommunicator commandCommunicator = builder.initialize();
    commandCommunicator.connect();
    commandCommunicator.registerCommandHandler(node1, new TestMessageHandler(), logger);


To create a very simple connection for test cases:

    ICommandCommunicator commandCommunicator = new CommandCommunicatorBuilder.initializeTestMode();


There are many ways to create a connection to the cluster based on the environment. Here is a small set of possible options:

To Define How Content is transferred:

  * TCPTransportProtocol
  * UDPTransportProtocol

To Define its Cluster Discovery:

  * MPingDiscoveryProtocol
  * TCPPingDiscoveryProtocol

If you need to communicate with other nodes that have security enabled, you must login before being able to send a command.

For an automated system account loading a security certificate:

    commandCommunicator.getSecurityManager().login(domain, organization, certificateBytes);

For a human user account:

    commandCommunicator.getSecurityManager().login(domain, organization, “admin”, “admin”);

The users are configured through the security service.