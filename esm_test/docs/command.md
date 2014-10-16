# Command API

## Javadocs
All external commands can be found in the following java packages. They can be reached with either the Rest API or the Command API based on the annotations.

      com.edifecs.security.handler (http://192.168.103.213:8080/servicemanager/module/security/security-service/apidocs/com/edifecs/security/handler/package-summary.html)
      com.edifecs.security.handler.rest(http://192.168.103.213:8080/servicemanager/module/security/security-service/apidocs/com/edifecs/security/handler/rest/package-summary.html)
 
Besides using the Rest API, there is a Java client API that can be used to to connect. It its optimized with caching capabilities and is a better optimized solution for automated systems.

     com.edifecs.security.ISecurityManager(http://192.168.103.213:8080/servicemanager/module/security/security-api/apidocs/com/edifecs/security/package-summary.html)

## Example Usage

### Using REST API

When using the REST api, the servlet that handles the HTTP request tracks the users session across calls. If the user hits another instance of the JSON Servlet, then the user will need to login again.

Both GET and POST works, though get is not secure in an SSL connection, and should not be used for any sensitive data. POST is recommended.

  
    http://localhost:8080/rest/service/Security%20Service/login
    data:{"username":"admin","password":"admin","remember":false}
  
    http://localhost:8080/rest/service/Security%20Service/user.getCurrentUser
    data:{}
  
    http://localhost:8080/rest/service/Security%20Service/isSubjectAuthenticated
    data:{}

### Using Message API

In the message API there is a set of helper classes that help get information related to the current user. Its called SecurityManager, behind the API, it just makes the necessary remote calls to the Security Service 
for the developer.

In both cases, the CommandCommunicator takes care of tying the user session to the thread that logged in. If there are any threads that are spawned from the thread, they are also authenticated with that user's session.
This ensures that all threads originating from the request are all tied to the calling user.

#### Adding Permissions to a Command

The Message API currently allows for the passing of messages from one service to another. Currently it is up to the service to define what to do with the messages that are received through the API.

To add in security and to create a simplified command-based execution, we will be creating a command-based message API that will auto expose annotated methods into the Akka cluster, as well as add the ability to 
expose the methods in other formats such as REST WS calls.

This will enable the ability to also add in Security level annotations at the method level that will automatically authenticate the user and their session for ALL commands executed through the message API, as well as 
make sure that the user's session is passed into any transaction so that filtering can be properly applied on all data.

Example Service Message Receiver Class:

    public class ServiceMessageReceiver extends AbstractMessageReceiver {

    @Command(name = "startservice", description = "Start a service")
    @RequiresPermission("product:something:detals:view")
    public Boolean startService(
        @Argument(name = "serviceName", required = true, description = "Service Name", index = 0) String serviceName)
        throws ServiceException {
        
        SecurityManager securityManager =  = getCommandCommunicator().getSecurityManager();
        User currentUser = securityManager.getSubjectManager().getUser();
        
        ServiceManager.getInstance().startService(serviceName);
        getLogger().debug("Service Started: " + serviceName);

        return true;
  }

To register a class for the service to expose, you either register a package with a set of annotated classes, or register each class at a time.

Apache Shiro is used to check and validate the security permissions based on the configured Realms setup for the customer. So for stand alone, we will connect to our own User DB for all authentications. For more 
advanced installs we can configure multiple Realms that can check AD, CAS, LDAP, Open ID or others that appear in the future.

#### SecurityManager API

       SecurityManager securityManager = getCommandCommunicator().getSecurityManager();
       securityManager.getAuthenticationManager().login(username, password);
       boolean isPermitted = securityManager.getAuthorizationManager().hasPermission(permission);
       User currentUser = securityManager.getSubjectManager().getUser();
       securityManager.logout();
       
#### Directly Using Message API

    ICommandCommunicator cc = getCommandCommunicator();
    SecurityManager securityManager = cc.getSecurityManager();

    // Lookup the SecurityService in the AddressRegistry
    Address address = commandCommunicator.getAddressRegistry().getAddressForServiceTypeName("Security Service");
  
    User currentUser = (User) commandCommunicator.sendSyncMessage(address, "user.getCurrentUser");
    Organization currentOrganization = (Organization) commandCommunicator.sendSyncMessage(address, "user.getUsersOrganization");
    securityManager.logout();