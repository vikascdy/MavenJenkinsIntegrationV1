
// USERMANAGER.JS
// Handles user authentication, creation, and permissions.
// ----------------------------------------------------------------------------

Ext.define('Util.UserManager', {}); // Placeholder, ignore this.

window.UserManager = {
    eulaAlreadyAccepted: false,

    username:  null,
    admin:     false,
    level:     null,
    userkey:   null,
    firstName: null,
    lastName:  null,
    email:     null,
    confirmed: null,
    
    // Authenticates a user with the backend. Should receive an authentication
    // level ('admin' or 'maintenance') and a hexadecimal user key which will
    // be passed back to the server when operations are performed to verify the
    // user's authenticity.
    authenticate: function(username, password, callback, scope) {
        var loadingWindow = Ext.widget('progresswindow', {text: 'Authenticating...'});
        Ext.Ajax.request({
            url: JSON_URL + '/users.auth',
            params: {
                username: username,
                password: password
            },
            success: function(response) {
                loadingWindow.destroy();
                var respJson = Ext.decode(response.responseText);
                if (respJson.success) {
                    UserManager.username  = respJson.username;
                    UserManager.admin     = respJson.level == 'admin';
                    UserManager.level     = respJson.level;
                    UserManager.userkey   = respJson.userkey;
                    UserManager.firstName = respJson.firstName;
                    UserManager.lastName  = respJson.lastName;
                    UserManager.email     = respJson.email;
                    UserManager.confirmed = respJson.confirmed;
                    SessionManager.createUserSession(respJson);
                    Ext.callback(callback, scope);
                } else {
                    Functions.errorMsg(respJson.error, "Authentication Failed");
                }
            },
            failure: function(response) {
                loadingWindow.destroy();
                Functions.errorMsg("Could not connect to backend to authenticate user.", "Authentication Failed");
            }
        });
    },

    // Deletes the currently-loaded user credentials.
    logout: function() {
        UserManager.username  = null;
        UserManager.admin     = false;
        UserManager.level     = null;
        UserManager.userkey   = null;
        UserManager.firstName = null;
        UserManager.lastName  = null;
        UserManager.email     = null;
        UserManager.confirmed = null;
		
		Functions.jsonCommand("esm-service", "logout", {
                "subject" : null
            }, {
            success : function(response) {
                Ext.callback(callback, scope);
            }
        });
		
    },

    // Given a username, password, authentication level (admin or
    // maintenance), and a 'creation key' (hexadecimal string received from
    // the backend using getCreationKey()), tells the backend to create a new
    // user.
    create: function(params, callback, scope) {
        var loadingWindow = Ext.widget('progresswindow', {text: 'Creating user...'});
        Ext.Ajax.request({
            url: JSON_URL + '/users.create',
            params: Functions.merge(params, {confirmed: UserManager.eulaAlreadyAccepted}),
            success: function(response) {
                loadingWindow.destroy();
                UserManager.eulaAlreadyAccepted = false;
                var respJson = Ext.decode(response.responseText);
                if (respJson.success) {
                    Ext.callback(callback, scope);
                } else {
                    Functions.errorMsg(respJson.error, "Failed to Create User");
                }
            },
            failure: function(response) {
                loadingWindow.destroy();
                Functions.errorMsg("Could not connect to backend to create user.", "Failed to Create User");
            }
        });
    },
    
    //Updates user information
    update: function(params, callback, scope) {
        var loadingWindow = Ext.widget('progresswindow', {text: 'Updating user...'});
        Ext.Ajax.request({
            url: JSON_URL + '/users.update',
            params: {
            	firstName:params.firstName,
            	lastName:params.lastName,
            	email:params.email,
            	username:params.username,
            	level:params.level
            },
            success: function(response) {
                loadingWindow.destroy();
                var respJson = Ext.decode(response.responseText);
                if (respJson.success) {
                	UserManager.firstName=params.firstName;
                	UserManager.lastName=params.lastName;
                    Ext.callback(callback, scope);
                } else {
                    Functions.errorMsg(respJson.error, "Failed to update User");
                }
            },
            failure: function(response) {
                loadingWindow.destroy();
                Functions.errorMsg("Could not connect to backend to update user.", "Failed to Update User");
            }
        });
    },
    
    
    //Takes username and deletes the user from backend
    deleteUser: function(username, callback, scope) {
        var loadingWindow = Ext.widget('progresswindow', {text: 'Deleting user...'});        
        Ext.Ajax.request({
            url: JSON_URL + '/users.delete',
            params: {
                username:username
            },
            success: function(result, request) {
           	 var respJson=Ext.decode(result.responseText);
           	 if (respJson.success) {
           		loadingWindow.destroy();
                 Ext.callback(callback, scope);
             } else {
            	 loadingWindow.destroy();
                 Functions.errorMsg(respJson.error, "Failed to Delete User");
             }
            },
            failure: function() {
            	loadingWindow.destroy();
                Functions.errorMsg("Could not connect to backend to delete user.", "Failed to Delete User");
            }
        });       
    },
    
    
    //Used to change password for the user logged into the application
    changeUserPassword: function(username,password,newPassword, callback, scope) {
        var loadingWindow = Ext.widget('progresswindow', {text: 'Changing user password...'});  
        Ext.Ajax.request({
        	url: JSON_URL + '/users.changePassword',
        	 params: {
                 username: username,
                 password: password,
                 newPassword: newPassword
             },
            success: function(result, request) {
           	 var respJson=Ext.decode(result.responseText);
           	 if (respJson.success) {
           		loadingWindow.destroy();
                Ext.callback(callback, scope);
             } else {
            	 loadingWindow.destroy();
            	 Ext.getCmp('passwordWindow').destroy();
                 Functions.errorMsg(respJson.error, "Failed to change password");
             }
            },
            failure: function() {
            	loadingWindow.destroy();
            	Ext.getCmp('passwordWindow').destroy();
                Functions.errorMsg("Could not connect to backend to change password.", "Failed to change password");
            }
        });       
    },
    
    //Used to reset password for other users
    resetUserPassword: function(username,newPassword,callback, scope) {
        var loadingWindow = Ext.widget('progresswindow', {text: 'Reseting user password...'});   
        Ext.Ajax.request({
        	 url: JSON_URL + '/users.resetPassword',
        	 params: {
                 username: username,
                 newPassword: newPassword
             },
            success: function(result, request) {
           	 var respJson=Ext.decode(result.responseText);
           	 if (respJson.success) {
           		loadingWindow.destroy();
                 Ext.callback(callback, scope);
             } else {
            	 loadingWindow.destroy();
            	 Ext.getCmp('passwordWindow').destroy();
                 Functions.errorMsg(respJson.error, "Failed to reset password");
             }
            },
            failure: function() {
            	loadingWindow.destroy();
            	Ext.getCmp('passwordWindow').destroy();
                Functions.errorMsg("Could not connect to backend to reset password.", "Failed to reset password");
            }
        });       
    },
    
    

    // Checks if the user is permitted to create new users. Passes the current
    // user's userkey (or null if not logged in), and will pass a one-time-use
    // randomly-generated 'creation key' to its callback if the user is an
    // admin or if no users have been created yet. This key must be passed to
    // create() to create a user.
    getCreationKey: function(callback, scope) {
        Ext.Ajax.request({
            url: JSON_URL + '/users.ckey',
            params: {
                userkey: UserManager.userkey
            },
            success: function(response) {
                var respJson = Ext.decode(response.responseText);
                if (respJson.success) {
                    Ext.callback(callback, scope, [respJson.ckey]);
                } else {
                    Functions.errorMsg(respJson.error, "Authentication Failed");
                }
            },
            failure: function(response) {
                Functions.errorMsg("Could not connect to backend to authenticate.", "Authentication Failed");
            }
        });
    },
    
    // Checks if this is the first run of the installer, and passes a boolean
    // with the result of this check to the callback.
    checkIfFirstRun: function(callback, scope) {
        Ext.Ajax.request({
            url: JSON_URL + '/users.firstrun',
            success: function(response) {
                var respJson = Ext.decode(response.responseText);
                if (respJson.success) {
                    Ext.callback(callback, scope, [respJson.firstrun]);
                } else {
                    Functions.errorMsg(respJson.error, "Connection Failed");
                }
            },
            failure: function(response) {
                Functions.errorMsg("Could not connect to backend.", "Connection Failed");
            }
        });
    },

    // Sets the 'confirmed' flag on the current user, to show that the user has
    // accepted the EULA.
    setConfirmedFlag: function(callback, scope) {
        Functions.ajax(JSON_URL + '/users.confirm', 'Confirming...',
            {username: UserManager.username, userkey: UserManager.userkey},
            function(response) {
                var respJson = Ext.decode(response.responseText);
                if (respJson.success) {
                    Log.debug("Setting confirmed flag...");
                    UserManager.confirmed = true;
                    Ext.callback(callback, scope);
                } else {
                    Functions.errorMsg(respJson.error, "Confirmation Failed");
                }
        });
    }
    
};

