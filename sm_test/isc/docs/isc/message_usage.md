# ISC Message Usage

## Synchronous Outbound Message

All available methods can be found in the ICommandCommunicator interface.

For example, we have the ability to send a message or object to a single node, or to multiple nodes. If it’s a single node on the cluster, it will return the returned object directly from the handler, or throw an exception if there was a problem on the other side.

    public MessageResponse sendSync(Collection<Address> destinations, String command, Args args)throws Exception {

If the message is sent to multiple Addresses, then it returns a MessageResponse object, which is a collection of all the responses for all addresses the message was sent too. This Includes a list of errors for those machines that threw an exception within the message handler.

Example Message Sending:
  
    Args args = new Args();
    args.put("response", "Say Hi!")
    Object response = commandCommunicator.sendSync(receiver, “helloWorldCommand”, args);

Only primitives and Objects that are Serializable are able to be transmitted.
  

## Asynchronous Outbound Messages using Futures / Callbacks

Command Definition

    def send(
      destination: Address,
      command: String,
      arguments: java.util.Map[String, Serializable]
    ): MessageFuture[Serializable]
      
Example Sending message in java 7:

    isc.send(address, "bar", args).thenReturn(
        new ChainCallback<String, String>() {
          public MessageFuture<String> call(String arg) {
            // Do Something when you get a return
            return isc.send(arg, 1);
          }
        }).thenReturn(new ChainCallback<String, String>() {
          public MessageFuture<String> call(String arg) {
            // Do Something once you get a response from the embedded call
            return isc.send(arg, 1);
          }
        }).then(new Callback<String>() {
          public void call(String arg) {
            // Finally do this
            q.offer(arg);
          }
        });    
        
For Scala:

    //TODO: Give Scala Example
    
    
### Rest Based Remote Calls

If the command support rest (does not have `@Rest(enabled = false)` annotation) you can access any service command
through the rest/json servlet.

The URL for the command you want to execute is broken down as:
  
https://<server>/rest/service/<service name>/<namespace>.<command name>
  
or to get an instance using the rest API
  
https://<server>/rest/service/<service name>/<namespace>/<record id>
  
Since it supports Rest, you can send the commands in GET, POST, UPDATE, DELETE, etc...
  
You can find more information about usage in the examples and development guides in /apps/ and in the UI Development
Guides.
  
Here is some simple usage:
  

    Request URL:http://192.168.103.237:8080/rest/service/Security%20Service/login
    Request Method:POST

    data:{"username":"admin","password":"admin","remember":false}




    Request URL:http://192.168.103.237:8080/rest/service/Doormat%20Service/getMenus?_dc=1392225462595
    Request Method:GET

    Response:
      [{
          "id" : "settings",
          "active" : true,
          "name" : "Settings",
          "taskheading" : "Tasks",
          "defaultLinkUrl" : "/security/#!/ManageUsers",
          "icon" : "/packages/ext-theme-edifecs/build/resources/images/edifecs-components/doormat/settings.png",
          "menu" : [{
                "columnOne" : [{
                      "text" : "Manage Users",
                      "linkUrl" : "/security/#!/ManageUsers"
                   }, {
                      "text" : "Manage Groups",
                      "linkUrl" : "/security/#!/ManageGroups"
                   }, {
                      "text" : "Manage Roles",
                      "linkUrl" : "/security/#!/ManageRoles"
                   }, {
                      "text" : "My Organization",
                      "linkUrl" : "/security/#!/MyOrganization"
                   }
                ]
             }, {
                "columnTwo" : [{
                      "text" : "Management Console",
                      "linkUrl" : "/config/#!/config"
                   }, {
                      "text" : "Content Repository",
                      "linkUrl" : "/config/#!/content"
                   }
                ]
             }
          ]
       }
      ]



  The servlet integrates into the Security Module automatically, so if you were to call the getMenu's option above
  without first logging in you will get a response like this.
  
    {
        "success" : false,
        "errorClass" : "com.edifecs.security.exception.AuthenticationFailureException",
        "error" : "User is not authenticated, no user ID is available for the user.",
        "stackTrace" : "com.edifecs.security.exception.AuthenticationFailureException: User is not authenticated, no userID is available for the user.\n\tat com.edifecs.security.service.handler.UserCommandHandler.getUserId(UserCommandHandler.java:50)\n\tat com.edifecs.security.service.handler.UserCommandHandler.getCurrentUser(UserCommandHandler.java:58)\n\tat sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n\tat sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:57)\n\tat sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\n\tat java.lang.reflect.Method.invoke(Method.java:606)\n\tat com.edifecs.security.remote.SecurityManager.callMethodAsUser(ShiroSecurityBackend.java:243)\n\tat com.edifecs.messaging.message.command.AbstractCommandHandler$CommandHandler.handleCommand(AbstractCommandHandler.java:796)\n\tat com.edifecs.messaging.message.command.AbstractCommandHandler.handleCommand(AbstractCommandHandler.java:266)\n\tat com.edifecs.messaging.receiver.CommandMessageReceiver.handleIncomingCommandMessage(CommandMessageReceiver.java:70)\n\tat com.edifecs.messaging.receiver.CommandMessageReceiver.handleIncomingSyncCommandMessage(CommandMessageReceiver.java:99)\n\tat com.edifecs.messaging.communicator.Communicator.sendSyncMessage(Communicator.java:63)\n\tat com.edifecs.messaging.CommandCommunicator.sendSync(CommandCommunicator.java:133)\n\tat com.edifecs.messaging.CommandCommunicator.processMessage(CommandCommunicator.java:186)\n\tat com.edifecs.messaging.CommandCommunicator.sendSyncMessage(CommandCommunicator.java:247)\n\tat com.edifecs.server.servlet.json.JsonServletBase.executeCommand(JsonServletBase.java:471)\n\tat com.edifecs.server.servlet.json.JsonServletBase.performAction(JsonServletBase.java:486)\n\tat com.edifecs.server.servlet.json.JsonServletBase.handleServiceCommand(JsonServletBase.java:327)\n\tat com.edifecs.server.servlet.json.JsonServletBase.handleMessage(JsonServletBase.java:220)\n\tat com.edifecs.server.servlet.json.JsonServlet.doGet(JsonServlet.java:47)\n\tat javax.servlet.http.HttpServlet.service(HttpServlet.java:621)\n\tat javax.servlet.http.HttpServlet.service(HttpServlet.java:728)\n\tat org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:305)\n\tat org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:210)\n\tat org.apache.tomcat.websocket.server.WsFilter.doFilter(WsFilter.java:51)\n\tat org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:243)\n\tat org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:210)\n\tat org.apache.catalina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:222)\n\tat org.apache.catalina.core.StandardContextValve.invoke(StandardContextValve.java:123)\n\tat org.apache.catalina.authenticator.AuthenticatorBase.invoke(AuthenticatorBase.java:502)\n\tat org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:171)\n\tat org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:100)\n\tat org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:118)\n\tat org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java:408)\n\tat org.apache.coyote.http11.AbstractHttp11Processor.process(AbstractHttp11Processor.java:1041)\n\tat org.apache.coyote.AbstractProtocol$AbstractConnectionHandler.process(AbstractProtocol.java:603)\n\tat org.apache.tomcat.util.net.JIoEndpoint$SocketProcessor.run(JIoEndpoint.java:312)\n\tat java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1145)\n\tat java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:615)\n\tat java.lang.Thread.run(Thread.java:724)\n"
    }
  
How is the JSON Structure determined from the CommandHandler?

The technology used is GSON: [Main Website](https://code.google.com/p/google-gson/)

Gson is a Java library that can be used to convert Java Objects into their JSON representation. It can also be used to
convert a JSON string to an equivalent Java object. Gson can work with arbitrary Java objects including pre-existing
objects that you do not have source-code of.

There are a few open-source projects that can convert Java objects to JSON. However, most of them require that you place
Java annotations in your classes; something that you can not do if you do not have access to the source-code. Most also
do not fully support the use of Java Generics. Gson considers both of these as very important design goals.

Gson Goals:

  * Provide simple toJson() and fromJson() methods to convert Java objects to JSON and vice-versa
  * Allow pre-existing unmodifiable objects to be converted to and from JSON
  * Extensive support of Java Generics
  * Allow custom representations for objects
  * Support arbitrarily complex objects (with deep inheritance hierarchies and extensive use of generic types)

Example:

    package com.edifecs.helloexample.api;
    
    import java.io.Serializable;
    
    public class HelloMessage implements Serializable {
    
    	private static final long serialVersionUID = -8812591935947207424L;
    
    	private String message;
    
    	public String getMessage() {
    		return message;
    	}
    
    	public void setMessage(String message) {
    		this.message = message;
    	}
    	
    }
    
Converts into:

    {
        message : "Hello World!"
    }
    
This works for complex objects as well:

    public class Messages implements Serializable {
    	private static final long serialVersionUID = 1L;
    
    	private List<HelloMessage> messages;
    
    	...
    }
    
    public class HelloMessage implements Serializable {
        private static final long serialVersionUID = 1L;
    
        private String message;
        private String name;

        ...
    }
        
-----

    {
        messages : [
        {
           message : "Hello",
           name : "William"
        },
        {
           message : "Goodbye",
           name : "Sue"
        }]

With GSON, it lets us pass just about any pojo back without any annotations. Now if you want to change how a pojo is
serialized or deserialized, there is an annotation that can be added to the command handler.

    @JsonSerialization(adapters = {
    	@TypeAdapter(HelloMessageSerializer.class)
    }

Type Adapter implementation details:

    package com.edifecs.helloworld;
    
    import java.io.IOException;
    import java.lang.reflect.Type;
    import java.util.Collection;
    
    import com.edifecs.epp.isc.json.JsonTypeAdapter;
    import com.edifecs.epp.isc.json.Schema;
    import com.google.gson.*;
    
    public class HelloMessageSerializer extends JsonTypeAdapter<HelloMessage> {
    
        static final String
            MESSAGE        = "textMessage",
            NAME           = "firstName",;
    
        @Override
        public TypeToken<HelloMessage> typeToken() {
            return TypeToken.get(HelloMessage.class);
        }
    
        @Override
        public void write(Gson gson, JsonWriter out, HelloMessage message) throws IOException {
            out.beginObject()
                .name(MESSAGE).value(message.getMessage)
                .name(NAME).value(message.getName());
            out.endObject();
        }
    
        @Override
        public DoormatMenu read(Gson gson, JsonReader in) throws IOException {
            throw new UnsupportedOperationException("Cannot deserialize a DoormatMenu.");
        }
    
        // Used in documentation to describe the JSON format
        @Override
        public Schema getSchema(Type t) {
            return Schema.Object()
               .withRequiredProperty(ID, Schema.String())
               .withRequiredProperty(ACTIVE, Schema.Boolean());
        }
    }

For further examples and help on how to implement the read and write methods, Search for help on GSON. There are a lot
of examples and help available.

### What Are Futures?

One critical design pattern is to make sure that there is no blocking threads. If there is a scenario where your
application needs to wait for any reason, using the concept of a future (callback) will greatly improve performance and
throughput on a single machine.
  
The concept of futures is actually not a part of Akka, but a part of Scala or Java. Scala futures are much easier to use
than in Java, however it can be done with the same result.
    
{{{http://docs.scala-lang.org/overviews/core/futures.html}Scala Futures}} - {{{http://docs.oracle.com/javase/7/docs/api/java/util/concurrent/Future.html}Java Futures}}

Java:

    interface ArchiveSearcher { String search(String target); }
    class App {
        ExecutorService executor = ...
        ArchiveSearcher searcher = ...
        void showSearch(final String target) throws InterruptedException {
          Future<String> future = executor.submit(new Callable<String>() {
            public String call() {
              return searcher.search(target);
          }});
        displayOtherThings(); // do other things while searching
        try {
         displayText(future.get()); // use future
        } catch (ExecutionException ex) { cleanup(); return; }
      }
    }
 
    FutureTask<String> future =
       new FutureTask<String>(new Callable<String>() {
         public String call() {
           return searcher.search(target);
       }});
       
    executor.execute(future);


Scala:
  
    val f: Future[List[String]] = future {
      session.getRecentPosts
    }
 
    f onFailure {
      case t => println("An error has occured: " + t.getMessage)
    }
 
    f onSuccess {
      case posts => for (post <- posts) println(post)
    }

### Implementation of Futures in the Message API

Futures are automated and abstracted from the application team when sending out messages. This is a type of ASync
Message processing.
  
To send a Async Message with a registered Future, you execute a call like this.
  
    commandCommunicator.sendASyncMessage(receiver, “helloWorldCommand”, “Say Hi!”,
      new Future(String name) {
        System.out.println("Hello " + name);
      }
    );

Adding this greatly helps with the ability to send and receive messages that deal with network latency and long running
jobs while optimizing CPU and resources.
  