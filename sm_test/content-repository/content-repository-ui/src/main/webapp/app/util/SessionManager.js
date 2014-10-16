// SESSIONMANAGER.JS
// Handles user session timeout, inactive states listeners and user session cookie management.
// ----------------------------------------------------------------------------

Ext.define('Util.SessionManager', {}); // Placeholder, ignore this.

window.SessionManager = {

	loginTime : null,
	expiryDuration : 500, // time in seconds
	inactiveStateTime : 0,
	task : null,

	// Stops monitoring the active state time for the session
	// to avoid unnecessary looping
	endSession : function() {
		if (SessionManager.task) {
			SessionManager.task.stopped = true;
			var runner = new Ext.util.TaskRunner();
			runner.stop(SessionManager.task);
		}
		// SessionManager.task.destroy();
		// console.log("Session Destroyed");
	},

	// Displays session expired message and logs out.
	showExpiryMessage : function(showMessage) {
		SessionManager.endSession();
		SessionManager.destroyModalWindow();
		if (showMessage)
			Functions.errorMsg("Your session has expired. Please re-login.",
					"Session Expired");
		SessionManager.destroyUserCookie();
		SM.displayLoginPage();
	},

	// Starts and monitors inactive state duration for a session
	setSessionTimeout : function() {

		SessionManager.loginTime = Date.now();
		console.log("Session timeout : " + SessionManager.expiryDuration
				+ " seconds.")
		// console.log("Creating session");
		if (SessionManager.inactiveStateTime < SessionManager.expiryDuration) {

			SessionManager.task = Ext.TaskManager
					.start({
						scope : this,
						run : function() {
							SessionManager.inactiveStateTime = SessionManager.inactiveStateTime + 1;
//							 console.log("Inactive state count :"+SessionManager.inactiveStateTime);
							if (SessionManager.inactiveStateTime >= SessionManager.expiryDuration) {
								if (!SessionManager.checkCurrentPage()) // Check
																		// to
																		// display
																		// session
																		// expire
																		// message
																		// on
																		// pages
																		// other
																		// than
																		// login
								{
									SessionManager.showExpiryMessage(true);
								} else {
									SessionManager.endSession();
								}

							}
						},
						interval : 1000
					// time in seconds
					});

		}

		// Listeners to keep session active as per user activity
		Ext.getDoc().on('click', function() {
			SessionManager.resetInactiveStateTime();
		});
		Ext.getDoc().on('mousemove', function() {
			SessionManager.resetInactiveStateTime();
		});
		Ext.getDoc().on('keypress', function() {
			SessionManager.resetInactiveStateTime();
		});

	},

	// Function to check current page and decide whether to
	// show session expired message or not
	checkCurrentPage : function() {

		// This array contains list of pages where we want to avoid showing
		// session expiry message
		var pages = [ 'loginpage', 'noconnectionpage', 'clusternamepage' ];
		var pageId = SM.page.getId();

		for ( var i = 0; i < pages.length; i++) {
			if (pageId.search(pages[i]) != -1) {
				return true;
			}
		}
		return false;
	},

	// To reset inactive state time of the session
	resetInactiveStateTime : function() {
		SessionManager.inactiveStateTime = 0;
	},

	// Set user session
	createUserSession : function(user,callback,scope) {

		Ext.Ajax.request({
			url : JSON_URL + '/users.setUserSession',
			params : {
				username : user.contact.firstName,
				admin : true ,
				level : 'ADMIN',
				userkey : 'test',
				firstName : user.contact.firstName,
				lastName : user.contact.lastName?user.contact.lastName:'test',
				email : user.contact.email?user.contact.email:'test@test.com',
				confirmed : user.active,
				SessionId : user.sessionId,
				inactiveTime : SessionManager.expiryDuration
			},
			success : function(response) {

				var respJson = Ext.decode(response.responseText);
				if (respJson.status){
					Log.info("User session set.");
					SessionManager.loadUserSession(user);
					Ext.callback(callback,scope);
				}

			},
			failure : function(response) {
				Ext.Msg.alert('Operation Failed', 'Failed to set user session.');
			}
		});
		
		// Ext.util.Cookies.set('userSession',Ext.encode(userSession));
		// Log.info("User session created.");
	},

	// Destroys/Clears the user session
	destroyUserCookie : function() {

//		Ext.Ajax.request({
//			url : JSON_URL + '/users.destroyUserSession',
//			success : function(response) {
//
//				var respJson = Ext.decode(response.responseText);
//				// if(respJson.status)
//				// Log.info("User session destroyed.");
//
//			},
//			failure : function(response) {
//			}
//		});

		 Ext.util.Cookies.clear('userSession');
	},

	// Loads the user session from server
	loadUserSession : function(user) {
		UserManager.username = user.contact.firstName;
		UserManager.admin = true ;
		UserManager.level = 'ADMIN';
		UserManager.userkey = 'test';
		UserManager.firstName = user.contact.firstName;
		UserManager.lastName = user.contact.lastName?user.contact.lastName:'test',
		UserManager.email = user.contact.email?user.contact.email:'test@test.com',
		UserManager.confirmed = user.active;
	},

	// Get user object if it exits in session
	getUserSessionObj : function(callback, scope) {
		
//		Ext.Ajax.request({
//            url : '/rest/service/esm-service/user.getCurrentUser',
//            method:'POST',
//            success : function(response) {
//                var respObj = Ext.decode(response.responseText);
//                if (respObj.success != false) {
//                       Ext.callback(callback,this,[respObj]);
//                } else {
//                		Ext.callback(callback,this,null);
////                	var pathname=window.location.pathname;
////                	var searchString=window.location.search;
////                    window.location.href='/security/login/?redirectURL='+pathname+searchString;                    
//                }
//            },
//            failure : function(response) {
//                Ext.Msg.alert('Operation Failed', 'Unable to connect the server.');
//            }
//        });
		
		
//		Ext.Ajax.request({
//			url : JSON_URL + '/users.getUserSession',
//			success : function(response) {
//
//				var respJson = Ext.decode(response.responseText);
//				var user = respJson.user;
//				callback(user);
//			},
//			failure : function(response) {
//				callback(null);
//			}
//		});

		 var userSessionCookie=Ext.util.Cookies.get('userSession');
		 var userSessionObj = userSessionCookie ? Ext.decode(userSessionCookie) : null;
		 Ext.callback(callback,this,[userSessionObj]);

	}

// getUserInfoFromCookie: function(property){
// var user=Ext.decode(SessionManager.getUserSessionObj());
// if(user){
// switch(property){
// case 'level': return user.level == 'admin';
// default :return false;
// }
// return false;
// }
// }

};
