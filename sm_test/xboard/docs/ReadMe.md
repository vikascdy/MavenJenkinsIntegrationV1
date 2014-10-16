## Overview:

1.	EXTJS talks to the JSON Servlet
2.	JSON Servlet talks to the Edifecs Business Logic Layer using the Cluster Message API
3.	Message API talks to the Services registered Service Handlers
4.	Service Handlers execute the business logic and returns result which is passed back to UI

![ExtJs](xboard/images/extJs.png)

I will use the shared security application as an example implementation.

## Service Command Message Handler

The command handler is the end point of a message sent through the message API and is where available methods and commands are handled. All access, permission, ands conversions are set through these classes.

To create a command handler, implement: 

	import com.edifecs.messaging.message.command.AbstractCommandHandler;

Add an annotation to the service, if you are using ServiceManager


	 Service(
		name = "My Service",
		version = "1.0.0.0",
		description = "",
		required = false,
		unmovable = true,
		…
		handlers = { @CommandHandler(clazz=com.edifecs.myapplication.handlers.MyHandler) }
	 )
	 public class MyService extends AbstractService {
  

Or create an instance of the handler and add it to the command communicator if you are not, or you need custom initialization logic.

	 AbstractCommandHandler handler = new MyHandler();
	 getCommandCommunicator().registerCommandHandler(getAddress(), handler, getLogger());

Here is a simple command handler to get a list of users,

		import com.edifecs.security.data.User;
		import com.edifecs.messaging.message.command.AbstractCommandHandler;
		import com.edifecs.messaging.message.command.annotations.Arg;
		import com.edifecs.messaging.message.command.annotations.Command;
		import com.edifecs.messaging.message.command.annotations.CommandHandler;
		import com.edifecs.messaging.message.command.annotations.JGroups;
		import com.edifecs.messaging.message.command.annotations.Rest;
		import org.apache.shiro.authz.annotation.RequiresPermissions;

		…

		@JGroups(enabled = true)
		@Rest(enabled = true)
		@CommandHandler(
				name="Administrative Command Handler",
				description="This is a set of API's to view and manage the security settings for users, groups, and organizations.",
				namespace="admin"
		)
		public class AdministrativeDataCommandHandler extends AbstractCommandHandler {

			private final ISecurityDataStore dataStore;

			public AdministrativeDataCommandHandler(ISecurityDataStore dataStore) throws HandlerConfigurationException {
				this.dataStore = dataStore;
			}

			@Command
			@RequiresPermissions("platform:security:administrative:user:list")
			public Collection<User> getUsers(
					@Arg(name = "startRecord", required = true) long startRecord,
					@Arg(name = "recordCount", required = true) long recordCount)
					throws Exception {
						   Long userId = getSecurityManager().getUser().getId();
				return dataStore.getRange(User.class, userId, startRecord, recordCount);
			}
			}

 This example shows several key concepts:
 
•	Ability to control which protocols the commands are available for
•	Concept of a command namespace
•	How to create a new command and define the required arguments
•	Annotated permission requirements at the method level to prevent users from even trying to access commands they cannot execute.
•	The ability to use the users session to filter data

 
## EXTJS
Building an EXTJS consists of a complete MVC model.

### Model:
The EXTJS model, if aligned with the Java model returned in the java model (or a raw JSON output), it will result in a direct and automatic translation from the Java object to the EXTJS model.

Java POJO

    public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private boolean humanUser;
    private boolean active;
    private boolean deleted;
    private Date createdDateTime;
    private Date modifiedDateTime;
    private Date lastLoginDateTime;
    private Contact contact;
    private Credential credential;
    …
    }
	
EXTJS Model

    Ext.define('Security.model.User', {
    extend: 'Ext.data.Model',
    requires: [
        'Security.model.Contact'
    ],
    fields:[
        {name:'id', type:'long'},
        {name:'humanUser', type:'boolean'},
        {name:'active', type:'boolean', defaultValue:true},
        {name:'deleted', type:'boolean', defaultValue:false},
        {name:'createdDateTime', type:'date', dateFormat:'time'},
        {name:'modifiedDateTime', type:'date', dateFormat:'time'},
        {name:'lastLoginDateTime', type:'date', dateFormat:'time'},
        {name:'contact', type: Functions.childArrayType('Security.model.Contact')},
        {name:'name', convert : function(value, record) {
            var contact = record.get('contact')[0];
            return contact.get('salutation') + ' ' + contact.get('firstName') + ' ' + contact.get('lastName');
        }
        }
    ]
    }


There are customizations that can be done on either side to add additional logic and translation as shown in the above EXTJS model where the name field is auto created as a combination of Salutation + First Name + Last Name.
On the Server side, it’s done as annotations on the Command Handler that allows for customization of the JSON Serialization and Deserialization Process.

### Store:

Represents an in memory cache on the client side, there are many ways to load content into a store, this example uses third party libs to calculate the resulted output.

    Ext.define('Security.store.UsersList', {
    extend:   'Ext.data.Store',
    storeId:  'usersList',
    requires: 'Security.model.User',
    model:    'Security.model.User',
    autoLoad: false
    });

### Controller:

Is in charge of linking the views/stores/models together. There are many ways to populate the data, this is an example Utility function used that creates a standard error handling solution for the requests to the JSON Servlet.
Utility Function – Understands the Message API and has a generic way to handle errors.
 
    jsonCommand: function(service, command, params, callbacks) {
        callbacks = callbacks || {};
        Ext.Ajax.request({
            url: "/rest/service/" + encodeURIComponent(service) + "/" + encodeURIComponent(command),
            params: {
                data: Ext.encode(params)
            },
            success: function(result) {
                var json = Ext.decode(result.responseText);
                if (json.success === false) {
                    if (callbacks.failure) {
                        callbacks.failure(json);
                    } else {
                        Functions.errorMsg("<b> Error occurred in command '" + command + "':</b><br /><br />" +
                                           "<code>" + json.errorClass + "</code><br /><br />" +
                                           json.error,
                                           "Command Error");
                    }
                } else {
                    if (callbacks.success)
                        callbacks.success(json);
                }
            },
            failure: function(result) {
                if (callbacks.failure) {
                    callbacks.failure({success: false, error: result.statusText, errorClass: "HTTP " + result.status});
                } else {
                    Functions.errorMsg("<b>Failed to send command '" + command + "':</b><br /><br />" +
                                       "<code>HTTP " + result.status + " - " + result.statusText + "</code><br /><br />" +
                                       "Command Error");
                }
            }
        });
    },

Controller with a method that when called will execute the Utility function and populate the response.

    Ext.define('Security.controller.UsersController', {
    extend: 'Ext.app.Controller',
    stores: [
        'UsersList'
    ],
    models: [
        'User'
    ],
    …
    loadUserStore: function (selectedUserId) {
        Ext.log('loadUserStore ' + selectedUserId);
           Functions.jsonCommand("Security Service", "getUsers", {
                startRecord : startRecord,
                recordCount : recordCount
          }, {
            success: function(response) {
                  Ext.log('User store loaded');
                  var userIndex = 0;
                  if (selectedUserId) {
                      userIndex = this.getUsersListStore().findExact('id', Ext.Number.from(selectedUserId));
                  }              
                  this.selectUserRecord(userIndex > 0 ? userIndex : 0);
            }
        });
    },
    …
### View:

Show the contents of a store to the user.

    Ext.define('Security.view.user.UsersList', {
    extend: 'Ext.grid.Panel',
    alias : 'widget.userslist',
    requires: [
        'Security.store.UsersList',
        'Ext.form.field.Text',
        'Ext.Img'
    ],
    title : 'Users',
    hideHeaders: true,
    store : 'UsersList',
    columns : [
        {
            header: 'Name', dataIndex: 'name', flex: 1
        }
    ],
    tools :[
        {
            type:'refresh',
            tooltip: 'Refresh Users List',
            action: 'refreshUsersList'
        }
    ],
    dockedItems : [
        {
            dock: 'top',
            xtype: 'toolbar',
            padding:5,
            items: [
                {
                    xtype: 'textfield',
                    name: 'search',
                    action: 'searchUser',
                    emptyText:'Filter',
                    flex:1
                },
                {
                    xtype:'image',
                    src:'resources/icons/filter.png'
                }
            ]
        }
    ]
    });


## Uploading file with JSON as Multipart form

In many cases we need to send a file with related information up to the server. This can only be done using a multipart form submission. To do this you must use EXTJS Forms and a filefield.

    Ext.define('SM.view.content.FileUploadWindow', {
    extend: 'Ext.window.Window',
    alias : 'widget.fileuploadwindow',
    layout: 'fit',
    width : 500,
    height: 160,
    autoShow: true,
    modal: true,
    padding: '10',
    resizable: false,
    draggable: false,
    title : 'Upload File',
    closeAction: 'destroy',
    border: false,
    parentDir: null,
    items: [{
        xtype: 'form',
        padding: '0 0 0 10',
        itemId: 'uploadForm',
        border: false,
        defaults: {anchor: '100%'},
        items: [{
            xtype: 'displayfield',
            itemId: 'parentDir',
            fieldLabel: 'Parent Directory',
            value: 'Loading...'
        }, {
            xtype: 'filefield',
            itemId: 'upload',
            emptyText: 'Select a file to upload',
            fieldLabel: 'File',
            name: 'upload',
            allowBlank: false
        }, {
            xtype:  'container',
            layout: 'hbox',
            margin: '10 0 0 0',
            items: [{
                xtype:'tbspacer',
                flex:1
            }, {
                xtype: 'button',
                text: 'Upload',
                itemId: 'uploadButton',
                disabled: true,
                formBind: true
            }, {
                xtype:'button',
                text: 'Cancel',
                margin:'0 0 0 5',
                handler: function(btn) {
                    btn.up('window').close();
                }
            }]
        }]
    }],
    initComponent: function(config) {
        this.callParent(arguments);
        this.down('#parentDir').setValue(this.parentDir);
    }
    });

#### In the Controller

Register the action to the button:

	  'fileuploadwindow #uploadButton': {
		click: function(btn) {
			controller.uploadFileFromForm(
			btn.up('#uploadForm'), btn.up('window').parentDir);
		}
	   },

Execute the form submission:

    var JSON_UPLOAD_URL = “/rest/service/Content Repository Service”;
    …
    uploadFileFromForm: function(formPanel, parentDir) {
        formPanel.getForm().submit({
            url: JSON_UPLOAD_URL + '/content.uploadFile',
            params: {
                // Pass any variables you want to pass
                data: Ext.encode({
                    upload: null,
                    path: parentDir + '/' + filename,
                    username: "unknown" // TODO: Load username properly.
                })
            },
            success: function(form, action) {
                // Do your success logic            },
            failure: function(form, action) {
                // Do your fail logic                }
            }
        });
    },
	
	
## JSON Servlet

The JSON Servlet is used to bridge the gap between the web application server, and the backend cluster. 

The JSON Servlet has two modes:

1.	Servlet – Directly joins the cluster, and communicated directly with the required resources. If running within a JVM with an existing Connection to the cluster, it will share the connection and communicate locally where needed.
2.	Proxy – Acts as a pass through to forward all HTTP Requests to another JSON Servlet in an embedded mode

The key difference is in the environment. There are pros and cons to both solutions.

Regardless, the JSON content is passed through the Message API to the recipient node for processing.

Once reaching the designated server, the JSON is parsed and validated and passed to the command handler for execution as if it was passed as a native JAVA object.

To customize the JSON use this annotation at the class level.

	import com.edifecs.servicemanager.ui.converters.JsonSerializers.*;
	@JsonSerialization(adapters = {
	@TypeAdapter(type = Config.class,     serializer = ConfigSerializer.class,
										Deserializer = ConfigSerializer.class),
	@TypeAdapter(type = Cluster.class,    serializer = ClusterSerializer.class,
										Deserializer = ClusterSerializer.class),
	}
                                        
It is based off the GSON JSON converter, so to create the serializers, you implement:

	import com.google.gson.JsonDeserializer;
	import com.google.gson.JsonSerializer;
	public static class ConfigSerializer implements JsonSerializer<Config>, JsonDeserializer<Config> {
			@Override
			public Config deserialize(JsonElement json, Type typeOfT,
					JsonDeserializationContext context) throws JsonParseException { … }
			@Override
			public JsonElement serialize(Config src, Type typeOfSrc,
					JsonSerializationContext context) { … }
	}
