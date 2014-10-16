Ext.define('Util.SessionManager', {});

window.SessionManager = {
		

    getCurrentUser: function(callback, scope) {
        Functions.jsonCommand("esm-service", "subject.getUser", {}, {
            success: function(response) {
                Ext.callback(callback, scope, [response]);
            }
        });
    },
    
    createUserCookie: function(user, sessionId, callback, scope) {
         user.sessionId = sessionId;
         Ext.util.Cookies.set('userSession',Ext.encode(user));
         Ext.callback(callback, scope);
    },
    
    deleteUserCookie : function() {
         Ext.util.Cookies.clear('userSession');
    },
	
    parseToken: function(token) {
        var state = {};
        if (!token) {
            return state;
        }
        var paths = token.split('/');
        if (paths.length > 1 && paths[0] == '!') {
            state.viewName = paths[1];
            state.path = [];
            for (var i = 2; i < paths.length; i++) {
                state.path.push(paths[i]);
            }
        }
        return state;
    },

    getTenantLandingURL: function(user){
        TenantManager.getCurrentUserTenant(function(tenant){
           if(tenant){
               return tenant.landingPage;
           }
           else{
               return null;
           }
        });
    },


    getRedirectURL: function() {
        var redirectURL = window.location.search.substring(1);
        
        redirectURL = redirectURL.split('redirectURL=');
        redirectURL = redirectURL[1];
        
        if (redirectURL === null) {
                redirectURL = DEFAULT_REDIRECT;
        }
        
        return redirectURL;
    }
};

