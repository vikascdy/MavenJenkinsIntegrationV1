// USERMANAGER.JS
// Handles user authentication, creation, and permissions.
// ----------------------------------------------------------------------------

Ext.define('Util.UserManager', {}); // Placeholder, ignore this.

window.UserManager = {

   
    
    // Deletes the currently-loaded user credentials.
    logout: function(callback, scope) {
        Functions.jsonCommand("esm-service", "logout", {
                "subject" : null
            }, {
            success : function(response) {
                Ext.callback(callback, scope);
            }
        });
    }


};

