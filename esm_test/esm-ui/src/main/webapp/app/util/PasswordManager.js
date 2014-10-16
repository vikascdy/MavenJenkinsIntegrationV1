Ext.define('Util.PasswordManager', {});

window.PasswordManager = {

		sendResetPasswordEmail: function(email, callback, scope) {

		        Functions.jsonCommand("esm-service", "user.sendResetPasswordEmail", {
		                email : email
		            }, {
		            success : function(response) {
		            		Ext.callback(callback, scope, [true]);
		            },
		            failure : function(response) {
		            	Security.removeLoadingWindow(function() {
		            		Ext.Msg.alert("Invalid Operation", response.error);
		            	});
		            }
		        });
		    },
		    
	    
		    updatePassword: function(newPasswd, token, callback, scope) {

		        Functions.jsonCommand("esm-service", "password.updatePassword", {
		        	newPasswd : newPasswd,
		        	token:token
		            }, {
		            success : function(response) {
		            		if(response.status==false)
		            			Ext.callback(callback, scope, [response.error]);
		            		else
		            			Ext.callback(callback, scope, [true]);
		            },
		            failure : function(response) {
		            	Security.removeLoadingWindow(function() {
		            		Ext.callback(callback, scope, [response.error]);
                        });		            	
		            }
		        });
		    }
		    
		    
		    
};