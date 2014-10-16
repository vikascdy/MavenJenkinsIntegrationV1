# Command Handler User Guide


## Service Definition of Command Handler

To enable a command handler for a service, a getter method with a `@Handler` annotation must be added to the service
interface, and it must a command handler object, which is both an instance of an interface with a `@CommandHandler`
annotation and a subclass of `AbstractCommandHandler`

The `@Handler` methods are called and initialized *BEFORE* the service start method is called.

Example Interface:

    package com.edifecs.helloexample.service;
    
    import com.edifecs.helloexample.handler.IHelloExampleHandler;
    import com.edifecs.servicemanager.annotations.Handler;
    import com.edifecs.servicemanager.annotations.Property;
    import com.edifecs.servicemanager.annotations.Property.PropertyType;
    import com.edifecs.servicemanager.annotations.Service;
    
    @Service(
    	name = "hello-example-service", 
    	version = "1.0", 
    	description = "Hello Example Service", 
    	properties = {@Property(name = "name", propertyType = PropertyType.STRING, description = "" , defaultValue = "world", required = true)}
    )
    public interface IHelloExampleService {
    
        @Handler
        IHelloExampleHandler getHelloExampleCommandHandler();
    }
    
Example Implementation:

    package com.edifecs.helloexample.service;
    
    import com.edifecs.helloexample.handler.HelloExampleHandler;
    import com.edifecs.helloexample.handler.IHelloExampleHandler;
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
    
    import com.edifecs.servicemanager.annotations.Property;
    import com.edifecs.servicemanager.annotations.Property.PropertyType;
    import com.edifecs.servicemanager.annotations.Service;
    import com.edifecs.servicemanager.api.AbstractService;
    
    @Service(
    	name = "hello-example-service",
    	version = "1.0", 
    	description = "Hello Example Service", 
    	properties = {
                @Property(name = "name", propertyType = PropertyType.STRING, description = "" , defaultValue = "world", required = true)}
    )
    public class HelloExampleService extends AbstractService implements IHelloExampleService {
    	
    	private Logger logger = LoggerFactory.getLogger(HelloExampleService.class);
    
        String greetee;
    
    	@Override
    	public void start() throws Exception {
    
            getCommandCommunicator().getSecurityManager();
    
            getSecurityManager().getSubjectManager().getUser();
    
    		String greetee = getProperties().getProperty("name");
    
    		logger.info("hello example service started.");
    	}
    
    	@Override
    	public void stop() throws Exception {
    		//TODO
    		logger.info("hello example service stopped.");
    	}
    
        @Override
        public IHelloExampleHandler getHelloExampleCommandHandler() {
            return new HelloExampleHandler(greetee);
        }
    }

    
For detailed specifications on service annotations, see: [Service Documentation](../../../sm/service-api/README.md)

## Command Handler Annotations

All annotations get placed into a interface class. this interface is used not only by the server, but also by the ISC
client code generation and documentation.

`@CommandHandler` - Must be placed at the top of a command handler interface. If this is not here, the commands will not
be registered. 

`@Rest` - Allows to explicitly enable or disable a command access through RESTful requests. This can be specified at the
interface, or at the method level.

`@Akka` - Allows to explicitly enable or disable a command access through Akka messages. This can be specified at the
interface, or at the method level.

`@Command` - *Deprecated* Replaced with `@SyncCommand`

`@AsyncCommand` - A command method which uses the new non-blocking asynchronous API. Any method annotated with this
annotation must return a `MessageFuture` object. This is the preferred way to write a command.

`@SyncCommand` - A blocking command method that allows any return type. Usually, `@AsyncCommand` should be used instead;
however, there are very rare cases where blocking is required.

`@Arg` - Required annotation for all arguments to a command method. Specifies the name, validation checks, etc. for a
command argument.

`@StreamArg` - *Deprecated* Streams can now be sent as normal arguments, so long as they are instances of the
`MessageStream` class. Use `MessageStream.fromInputStream` to send `InputStream`s.

`@Sender` - Can be used instead of `@Arg` for a command method argument of type `Address`. The argument will be
auto-filled with the command's sender's address.

`@JsonSerialization` - This allows for the customization of the serializer and deserializer to convert to and from JSON.
Specified at the interface level. 

  * `@TypeAdapter`

`@RequiresPermissions`- Added to a method to auto add in security permission check for the requester.

`@RequiresRoles` - Added to a method to add in a security check for a user role.

`@NullSessionAllowed` - When added, all security is disabled for this command. This can be specified at the interface,
or at the method level.


Example:

    package com.edifecs.helloexample.handler;
    
    import com.edifecs.epp.isc.async.MessageFuture;
    import com.edifecs.epp.isc.core.command.AbstractCommandHandler;
    import com.edifecs.helloexample.api.HelloMessage;
    
    public class HelloExampleHandler extends AbstractCommandHandler implements IHelloExampleHandler {
    	
    	private String greetee = "";
    	
    	public HelloExampleHandler(String greetee) {
    		super();
    		this.greetee = greetee;
    	}
    	
    	@Override
    	public HelloMessage greeting() {
    		HelloMessage helloMessage = new HelloMessage();
    		helloMessage.setMessage("Hello " + this.greetee + "!");
    		return helloMessage;
    	}
    
        @Override
        public MessageFuture<HelloMessage> greetingFromTheFuture() {
            return MessageFuture.of(greeting());
        }
    }
