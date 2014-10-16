Edifecs ISC Code Generation Documentation
=====================================

Code generation provides a type-safe and autocomplete-friendly way to send messages between services.

Service/Command Handler Interfaces
----------------------------------

In order for a service or command handler to be compatible with code generation, it must be separated into an annotated interface and an unannotated implementation class. The interfaces must be a part of the service's public API; clients will use the interfaces to communicate with the service.

A service interface is any interface annotated with `@Service`; it may also have zero or more getter methods annotated with `@Handler`. The `@Handler` methods' return types must be command handler interfaces.

    @Service(
      name="Code Generation Example Service",
      version="1.0",
      description="This is an example.")
    public interface IExampleService {
      @Handler
      public IExampleCommandHandler commands();
    }

A command handler interface is any interface annotated with `@CommandHandler`; it must contain one or more method(s) annotated with either `@SyncCommand` or `@AsyncCommand`. `@SyncCommand` methods may have any return type, while `@AsyncCommand` methods must return a `MessageFuture`, which represents the result of a delayed, nonblocking computation. All command method arguments must be annotated with `@Arg` (or, in special cases, `@StreamArg` or `@Sender`).

    @CommandHandler
    public interface IExampleCommandHandler {
      @SyncCommand
      public String syncCommand(
        @Arg(name="firstArgument") String firstArgument,
        @Arg(name="secondArgument", required=false) String secondArgument
      );
      
      @AsyncCommand
      public MessageFuture<String> asyncCommand();
    }

Proxy Classes
-------------

The code generator produces proxy classes: implementations of service or command handler interfaces which delegate all command method calls to an actual running instance of a service via the messaging system.

Proxy classes are generated as Scala source files in the same package as the interfaces they implement. The name of a proxy class is the name of the original interface, prefixed with `__GeneratedProxy__`. All proxy classes (for both services and command handlers) have a two-argument constructor, which takes an `Isc` instance and an `Address`.

Service proxy classes provide implementations for `@Handler` methods which return a proxy for the given handler type, and command handler proxy classes provide implementations for `@SyncCommand` and `@AsyncCommand` methods which send a message with the given command and arguments to a running instance of the service. All interface methods which do not fall into any of these categories will be given stub implementations that throw `UnsupportedOperationException`s.

In almost all use cases, there should be no reason to instantiate or refer to a proxy class directly; they are created via reflection, as instances of their interfaces.

Client-side Usage
-----------------

Every `AbstractService` and `AbstractCommandHandler` has a protected `isc` method, which returns an instance of `Isc`. This object can be used to communicate with other services, either via its `send` and `sendSync` methods, or via generated proxy classes.

`Isc` defines a `getService` method, which takes a `Class` instance that represents a service interface and then returns an instance of the service interface. The returned object is an instance of the interface's corresponding proxy class, if one exists. `Isc.getService` searches for any running instance of the given service in the cluster, and the returned proxy forwards command messages to its address. An `IllegalArgumentException` will be thrown if the given `Class` does not represent a service interface, or if no corresponding proxy class exists; a `ServiceNotFoundException` will be thrown if there is no running instance of the service in the cluster.

Some examples of sending messages using this API:

    // A synchronous command to get the current user.
    isc().getService(ISecurityService.class).users().getCurrentUser();

    // An example of a made-up asynchronous command, followed by a chained callback,
    // written using a Java 8 lambda expression.
    isc().getService(IExampleService.class).commandHandler().asyncCommand().thenReturn(result ->
        MessageFuture.of(doSomethingElseWith(result)));
    
    // The same example as above, written in Scala.
    isc.getService(classOf[IExampleService]).commandHandler.asyncCommand().flatMap(result =>
        MessageFuture.of(doSomethingElseWith(result)))

SBT Code Generation
-------------------

The code-generation tool is included in the `platform` project, under `project/CodeGen.scala`. It provides a new SBT setting (`CodeGen.serviceInterfaces`), a `Seq` of `String`s which should contain the fully-qualified names of any service interfaces in a project. When a project with a non-empty `serviceInterfaces` setting is compiled, proxy classes will be automatically generated for all specified service interfaces, as well as for any command handler interfaces that the service interfaces refer to.

In order for SBT code generation to work, a project using code generation must also depend on the local project `sm-codegen`.

Command-line Code Generation
----------------------------

If a project requires code generation, but is outside of the `platform` project tree or does not use SBT, the command-line code generation tool can be used. It is the `com.edifecs.servicemanager.codegen.CodeGenTool` class, located in the `sm-codegen` project.

`CodeGenTool` takes a list of fully-qualified service interface names as arguments, and outputs proxy source files into `src/main/scala`. In order for the tool to work, the service interfaces must be on the Java classpath. The output directory can be changed with the `-o` flag---for example, to use the output directory `somedir`, include `-o somedir` in `CodeGenTool`'s arguments before the list of service interface names.