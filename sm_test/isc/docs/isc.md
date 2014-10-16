# ISC Message API

Handles all Sync (blocking) and ASync (non-blocking) message processing for the cluster. It supplies both a way to send
commands, and to register commands to be executed. It also integrates into the Service Registry to identify the cluster
topography.

This is a detailed description of how the message API works as a stand along module. If you wish to see how to use it in
the context of an application or module, please see the app documentation found here:
  
[/examples/](../../apps/README.md)
  
## Definitions

  * *Address* – Like an IP Address, it identifies a machine within the cluster.

  * *Message* – A package of information that is sent through to another machine. There are multiple types.

  * *CommandCommunicator/ISC* – A reference to the cluster, that allows the connection to a cluster and ability to send messages 
  
  * *AddressRegistry* – Contains references to all the machines within the cluster and provides ways to lookup Addresses.
  
  * *CommandHandler* – Handles the incoming messages to a cluster connection and implements how to handle incoming messages.
  
  * *SecurityManager* – Convenience methods that send out commands to the Security Service in the cluster.
  
  * *CommandCommunicatorBuilder* – optional API to assist in configuration of the communication API.

## Usage

  * [Initialization](isc/initialization.md)
  
  * [Creation of Command Handler](isc/command_handler.md)

  * [Usage](isc/message_usage.md)

  * [Command Specifications](isc/command_specifications.md) (dynamically-generated documentation)

  * [Security Integration](../../esm/docs/api/commands.md)

## Design

To optimize the performance and resource utilization in messaging, the goal of both the message API and what the product
teams build, is to structure the processing in a non blocking way to allow the underlying system to optimize execution.
  
New in version 1.5 are:
  
  * *Support for Futures in command handlers*
  
    Futures/Callbacks are an asynchronous non-blocking pattern that lets a developer execute a command knowing that the
    response will take time, registering a separate method for handling the response separably.

    [Scala Futures](http://docs.scala-lang.org/overviews/core/futures.html) - [Java Futures](http://docs.oracle.com/javase/7/docs/api/java/util/concurrent/Future.html)

    The concept of futures is built into Scala as well as Java 8. The ISC also has a layer to support Java 7 interaction.
    
  * *Scala Support for commands*
  
    Allow the creation of a CommandHandler written and annotated in Scala or Java.
 
  * *Client code generation from the Service and CommandHandler Interfaces*
  
## Resources

  * [Akka](http://akka.io/)
  
  * [Wikipedia - Akka](http://en.wikipedia.org/wiki/Akka_\(toolkit\))
  
