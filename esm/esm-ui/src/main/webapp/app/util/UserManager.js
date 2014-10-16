// USERMANAGER.JS
// Handles user authentication, creation, and permissions.
// ----------------------------------------------------------------------------

Ext.define('Util.UserManager', {}); // Placeholder, ignore this.

window.UserManager = {

    sessionId : null,
    username :  null,
    
    checkSource : function(parameter) {
        var hash=location.hash.substring(2, location.hash.length);
        var params = hash.split("?");
        if(params.length==1)
            return false;
        return params[1].substring(params[1].indexOf('=') + 1);
    },
    
    clearLoginMessage : function(){
         var message = Ext.getCmp("login-message");
         message.setText("", true);    
         message.setVisible(false);
    },

    showMessage: function (messageText) {
        var message = Ext.getCmp("login-message");
        message.setText(messageText.message, true);    
        message.setVisible(true);
    },



    login: function(username, password, domain, organization, remember, callback, scope) {
        
        Security.loadingWindow = Ext.widget('loadingwindow');
        
        var redirectURL=null;
//        Functions.jsonCommandPath("login", "esm-service", "login", {
        "rest" / "login"

        Functions.jsonShortCommand("login", {
                username : username,
                password : password,
                domain : domain,
                organization : organization,
                remember : remember
            }, {
            success : function(response) {
                UserManager.clearLoginMessage();
                Security.removeLoadingWindow(function(){
                        UserManager.username = username;
                        
                        UserManager.getCurrentUser(function(user){
        
                            if(user && user.changePasswordAtFirstLogin==true)
                                window.location='#!/ChangePassword';
                            else
                                {
                                    redirectURL = UserManager.checkSource('redirectURL');
                                    if(redirectURL) {
                                        console.log("Redirecting to : " + redirectURL);
                                    }
                                    Ext.callback(callback, scope, [redirectURL]);
                                }
                        },this);                
                });
            },
            failure : function(response) {
                Security.removeLoadingWindow(function(){
                    UserManager.showMessage(response.error);
                });
            }
        },
        {
            'remember':remember
        }
        );
    },
    
    // Deletes the currently-loaded user credentials.
    logout: function(callback, scope) {
        Functions.jsonCommand("esm-service", "logout", {
                "subject" : null
            }, {
            success : function(response) {
                Ext.callback(callback, scope);
            }
        });
    },
    
    checkUsername : function(username, callback, scope) {
        Log.info("Checking username '{0}'.", username);
        Functions.jsonCommand("esm-service", "user.getUserByUsername", {
                username   : username
            }, {
            success : function(response) {
                Ext.callback(callback, scope, [false,response]);
            },
            failure : function(response) {
                Ext.callback(callback, scope, [true]);
            }
        });
    },
    
    getCurrentUser : function(callback, scope) {

        Functions.jsonCommand("esm-service", "subject.getUser", {

            }, {
            success : function(response) {
                Ext.callback(callback, scope, [response]);
            },
            failure : function(response) {
                Ext.callback(callback, scope, [null]);
            }
        });
    },
    
    prop0Exists: function(obj, prop) {
        if(obj && obj.get(prop) && (obj.get(prop) instanceof Array) && obj.get(prop).length>0) {
            return true;
        }
        return false;
    },

    createUserContactObject: function(userInfo,contactId) {
        return {
            "id":contactId,
            "firstName":userInfo.firstName,
            "middleName":userInfo.middleName,
            "lastName":userInfo.lastName,
            "salutation":userInfo.salutation,
            "emailAddress":userInfo.emailAddress
        };
    },
    createUserInfoObject: function(userInfo, contact, canonicalName, userId, humanUser) {
        var userObj={
            "canonicalName":canonicalName,
            "humanUser":humanUser,
            "active":userInfo.active,
            "suspended":userInfo.suspended,
            "deleted":false,
            "createdDateTime":userInfo.createdDateTime,
            "modifiedDateTime":userInfo.modifiedDateTime,
            "username":userInfo.username,
            "contact":contact
        };
        if(userId)
            userObj['id'] = userId;
        if(userInfo.lastLoginDateTime)
            userObj['lastLoginDateTime'] = userInfo.lastLoginDateTime;
        return userObj;
    },
    
    checkLastLoginDateTime : function(user){
        
        if(user.hasOwnProperty('lastLoginDateTime') && user['lastLoginDateTime'].length==0){
            delete user.lastLoginDateTime;
        }
        return user;
    },
    
    
    createUser: function(userInfo, callback, scope) {
        var canonicalName = userInfo.salutation + ' ' + userInfo.firstName + ' ' + userInfo.lastName;
        var contact = UserManager.createUserContactObject(userInfo);
        var user = UserManager.createUserInfoObject(userInfo, contact, canonicalName, null, true);

        // var loadingWindow = Ext.widget('progresswindow', {text: 'Creating User...'});
        
        Functions.jsonCommand("esm-service", "user.createUser", {
                "user" : user
            }, {
            success : function(response) {
                UserManager.addUsernamePasswordAuthenticationTokenToUser(response, userInfo.username, userInfo.password, callback, scope);
            },
            failure : function(response) {
                Functions.errorMsg(response.error, "Failed to Create User");
            }
        });
    },

    createUserForOrganization : function(userInfo, organizationId, callback, scope) {
        var canonicalName = userInfo.salutation + ' ' + userInfo.firstName + ' ' + userInfo.lastName;
        var contact = UserManager.createUserContactObject(userInfo);
        var user = UserManager.createUserInfoObject(userInfo, contact, canonicalName, null, true);

        var authenticationToken = {
            "username":userInfo.username,
            "password":Ext.Array.toArray(userInfo.password)
        };

        Functions.jsonCommand("esm-service", "user.createUser", {
            "user" : user,
            "token" : authenticationToken,
            "organizationId" : organizationId
        }, {
            success : function(response) {
                Ext.callback(callback,scope,[response]);
            },
            failure : function(response) {
                Functions.errorMsg(response.error, "Failed to Create User");
            }
        });
    },
    
    createCertificateUser : function(userInfo,organization, organizationId, callback, scope) {
        var canonicalName = userInfo.salutation + ' ' + userInfo.firstName + ' ' + userInfo.lastName;
        var contact = UserManager.createUserContactObject(userInfo);
        var user = UserManager.createUserInfoObject(userInfo, contact, canonicalName, null, false);

        Functions.jsonCommand("esm-service", "user.createCertificateUser", {
            "user" : user,
            "domain" : userInfo.domain,
            "certificate" : userInfo.certificate,
            "username":userInfo.username,
            "organization":organization,
            "organizationId" : organizationId
        }, {
            success : function(response) {
                Ext.callback(callback,scope,[response]);
            },
            failure : function(response) {
                Functions.errorMsg(response.error, "Failed to Create User");
            }
        });
    },
    
    createLdapUser : function(userInfo, organizationId, callback, scope) {
        var canonicalName = userInfo.salutation + ' ' + userInfo.firstName + ' ' + userInfo.lastName;
        var contact = UserManager.createUserContactObject(userInfo);
        var user = UserManager.createUserInfoObject(userInfo, contact, canonicalName, null, false);

        var authenticationToken = {
            "username":userInfo.username,
            "domain":userInfo.domain
        };

        Functions.jsonCommand("esm-service", "user.createLdapUser", {
            "user" : user,
            "token" : authenticationToken,
            "organizationId" : organizationId
        }, {
            success : function(response) {
                Ext.callback(callback,scope,[response]);
            },
            failure : function(response) {
                Functions.errorMsg(response.error, "Failed to Create User");
            }
        });
    },
    
    getLdapUserAttributes : function(username, organizationId, callback, scope) {
            
            Functions.jsonCommand("esm-service", "user.getLdapUserAttributes", {
                    "username" : username,
                    "organizationId" : organizationId
                }, {
                success : function(response) {
                    Ext.callback(callback, scope, [response]);
                },
                failure : function(response) {
                    Security.removeLoadingWindow(function(){
                        Functions.errorMsg(response.error, "Failed to Get User Credentials");
                   });  
                    
                }
            });
        },
    
    updateUser: function(userInfo, userId, contactId, callback, loadingWindow) {

    
        var canonicalName = userInfo.salutation + ' ' + userInfo.firstName + ' ' + userInfo.lastName;
        var contact = UserManager.createUserContactObject(userInfo,contactId);        
        var user = UserManager.createUserInfoObject(userInfo, contact, canonicalName, userId);
    
        if(window.location.hash == '#!/AccountSettings')
        {
             Functions.jsonCommand("esm-service", "user.updateCurrentUser",  {
                "user" : user
             },
                {
            success : function(response) {
                Ext.callback(callback, this, [response]);
            },
            failure : function(response) {
                Functions.errorMsg(response.error, "Failed to Update User");
                if(loadingWindow)crede
                    loadingWindow.destroy();
            }
        });
        }
        else{
        Functions.jsonCommand("esm-service", "user.updateUser", {
                "user" : user
            }, {
            success : function(response) {
                Ext.callback(callback, this, [response]);
            },
            failure : function(response) {
                Functions.errorMsg(response.error, "Failed to Update User");
                if(loadingWindow)
                    loadingWindow.destroy();
            }
        });
    }
    },
    
    getUsers: function(startRecord, recordCount, callback) {
        Log.info("Requesting list of users starting at '{0}', of quantity {1}, from server.", startRecord, recordCount);
        Functions.jsonCommand("esm-service", "user.getUsers", {
                startRecord : startRecord,
                recordCount : recordCount
            }, {
            success : function(response) {
                Ext.callback(callback, scope, [user]);
            }
        });
    },

    addUsernamePasswordAuthenticationTokenToUser : function(user, username, password, callback, scope) {
        var authenticationToken = {
            "username":username,
            "password":Ext.Array.toArray(password)
        };
        
        Functions.jsonCommand("esm-service", "user.addUsernamePasswordAuthenticationTokenToUser", {
                "user" : user,
                "authenticationToken" : authenticationToken
            }, {
            success : function(response) {
                Ext.callback(callback, scope, [user]);
            },
            failure : function(response) {
                Functions.errorMsg("Could not connect to backend to create user credentials.", "Failed to Create User Credentials");
            }
        });
    },

    updateUsernamePasswordAuthenticationToken : function(user, username, password, callback, scope) {
        
        
        user = UserManager.checkLastLoginDateTime(user);
        
        var authenticationToken = {
            "username":user.username,
            "password":Ext.Array.toArray(password)
        };
        if(window.location.hash == '#!/AccountSettings')
        {
        Functions.jsonCommand("esm-service", "user.updateCurrentUserAuthenticationToken",  {
             "authenticationToken" : authenticationToken
         },{
            success : function(response) {
                Ext.callback(callback, scope, [true,null]);
            },
            failure : function(response) {
                Ext.callback(callback, scope,[false,response.error]);
            }
        });

        }
        else{
        Functions.jsonCommand("esm-service", "user.updateUsernamePasswordAuthenticationToken", {
                "user" : user,
                "authenticationToken" : authenticationToken
            }, {
            success : function(response) {
                Ext.callback(callback, scope, [true,null]);
            },
            failure : function(response) {
                Ext.callback(callback, scope,[false,response.error]);
            }
        });
    }
    },
    
    
    
    updateCertificateTokenForUser : function(user, domain, organization, certificate, callback, scope) {
        
        
        user = UserManager.checkLastLoginDateTime(user);
        
        Functions.jsonCommand("esm-service", "user.updateCertificateTokenForUser", {
                "user" : user,
                "domain" : domain,
                "organization" : organization,
                "certificate" : certificate,
            }, {
            success : function(response) {
                Ext.callback(callback, scope, [true,null]);
            },
            failure : function(response) {
                Ext.callback(callback, scope,[false,response.error]);
            }
        });
    },
    
    changePasswordAtFirstLogin : function(user, username, password, callback, scope) {
        
        
        user = UserManager.checkLastLoginDateTime(user);
        
        var authenticationToken = {
            "username":user.username,
            "password":Ext.Array.toArray(password)
        };
        
        Functions.jsonCommand("esm-service", "user.changePasswordAtFirstLogin", {
                "user" : user,
                "authenticationToken" : authenticationToken
            }, {
            success : function(response) {
                Ext.callback(callback, scope, [true]);
            },
            failure : function(response) {
                Functions.errorMsg(response.error,
                "Failed To Update User Credentials");
                Ext.callback(callback, scope,[response.error]);
            }
        });
    },

    deleteUser : function(userIds, callback, scope) {
        Functions.jsonCommand("esm-service", "user.deleteUsers", {
                ids : userIds
            }, {
            success : function(response) {
                Ext.callback(callback, scope,[true,response]);
            },
            failure : function(response){
                Ext.callback(callback, scope,[false,response]);
            }
        });
    },    

    addRolesToUser : function(user, roles, callback, scope) {
        var rolesArray=[];
        
        var userInfo = UserManager.checkLastLoginDateTime(user.data);
        
        Ext.each(roles, function(role) {
            rolesArray.push({
                "id": role.get('id'),
                "canonicalName": role.get('canonicalName'),
                "description": role.get('description')
            });            
            
        });        
        Functions.jsonCommand("esm-service", "role.addRolesToUser", {
            "user" : userInfo,
            "roles" : rolesArray
        }, {
        success : function(response) {
            Ext.callback(callback, scope, [true]);
        },
        failure : function(response) {
            Ext.callback(callback, scope, [false]);
        }
     });
        
    },

    removeRolesFromUser : function(user, roles, callback, scope) {
        var rolesArray=[];
        
        var userInfo = UserManager.checkLastLoginDateTime(user.data);
        
        Ext.each(roles, function(role) {
            rolesArray.push(role.data);           
        });

        Functions.jsonCommand("esm-service", "role.removeRolesFromUser", {
            "user" : userInfo,
            "roles" : rolesArray
        }, {
        success : function(response) {
            Ext.callback(callback, scope, [true]);
        },
        failure : function(response) {
            Ext.callback(callback, scope, [false]);
        }
    });

    },

    getGroupsForUser: function(user, callback, scope) {
        Functions.jsonCommand("esm-service", "group.getGroupsForUser", {
                "userId": user.get('id'),
                "startRecord":0,
                "recordCount":-1
            }, {
            success: function(response) {
                Ext.callback(callback, scope, [response.resultList]);
            }
        });
    },
    
    addGroupsToUser : function(user, groups, callback, scope) {
        var groupsArray=[];
        
        var userInfo = UserManager.checkLastLoginDateTime(user.data);
        
        Ext.each(groups, function(group) {
            groupsArray.push({
                "id": group.get('id'),
                "canonicalName": group.get('canonicalName'),
                "description": group.get('description')
            });            
            
        });        
        Functions.jsonCommand("esm-service", "group.addGroupsToUser", {
            "user" : userInfo,
            "groups" : groupsArray
        }, {
        success : function(response) {
            Ext.callback(callback, scope, [true]);
        },
        failure : function(response) {
            Ext.callback(callback, scope, [false]);
        }
    });
    },

    removeGroupsFromUser : function(user, groups, callback, scope) {
        var groupsArray=[];
        
        var userInfo = UserManager.checkLastLoginDateTime(user.data);
        
        Ext.each(groups, function(group) {
            groupsArray.push(group.data);           
        });
        
        Functions.jsonCommand("esm-service", "group.removeGroupsFromUser", {
            "user" : userInfo,
            "groups" : groupsArray
        }, {
        success : function(response) {
            Ext.callback(callback, scope, [true]);
        },
        failure : function(response) {
            Ext.callback(callback, scope, [false]);
        }
    });

    },
    
    getPermissionsForUser : function(user, callback, scope) {
        var userObj = {
            "id" : user.get('id')
        };
    
        Functions.jsonCommand("esm-service", "permission.getPermissionsForUser", {
                user : userObj,
                "startRecord":0,
                "recordCount":-1
            }, {
                success : function(response) {
                    Ext.callback(callback, scope, [response.resultList]);
                }, 
                failure : function(response) {
                    Ext.callback(callback, scope, [null, response]);
                }
            }
        );
    },
    
    batchImportUsers : function(users, callback, scope) {
        Functions.jsonCommand("esm-service", "user.batchImportUsers", {
                users : users
            }, {
            success : function(response) {
                Ext.callback(callback, scope, [response]);
            }
        });
    },

    getCustomFieldsFor: function(id, type, callback, scope) {
        Functions.jsonCommand("esm-service", "getCustomFieldsFor", {
                "id":id,
                "type":type
            }, {
            success: function(response) {
                Ext.callback(callback, scope, [response.resultList]);
            }
        });
    },
    
    searchByFirstOrMiddleOrLastName : function(seed, callback, scope){
        
         Functions.jsonCommand("esm-service", "user.searchByFirstOrMiddleOrLastName", {
             seed : seed
         }, {
         success : function(response) {
             Ext.callback(callback, scope, [response.resultList]);
         },
         failure : function(response) {
             Ext.callback(callback, scope, []);
         }
     });
         
    },
    
    getCredentialForUser : function(userId,  callback, scope) {  
        if(window.location.hash == '#!/AccountSettings' )
        {         
                     Functions.jsonCommand("esm-service", "user.getCredentialForCurrentUser",{},
                    {                         
                        success : function(response) {                            
                            Ext.callback(callback, scope, [response, null]);
                        },
                        failure : function(response) {
                             console.log(response);
                            Ext.callback(callback, scope, [null,response.error]);
                        }
                    });
        }
        else
            { 
                        Functions.jsonCommand("esm-service", "user.getCredentialForUser",
                    {
                        "id"  :  userId
                        
                    }, {
                        success : function(response) {                           
                            Ext.callback(callback, scope, [response, null]);
                        },
                        failure : function(response) {
                            Ext.callback(callback, scope, [null,response.error]);
                        }
                    });
            }
       
    },

    matchPattern : function(regex, seed,  callback, scope) {

        Functions.jsonCommand("esm-service", "user.matchPattern",
            {
                "regex" : regex,
                "seed"  :  seed
            }, {
                success : function(response) {
                    Ext.callback(callback, scope, [response]);
                },
                failure : function(response) {
                    Functions.errorMsg(response.error,
                        "Failed To Match Regular Expression");
                }
            });

    },
    
    customPasswordType: function(tenant, callback)
    {   

        var passwordPolicy = tenant.passwordPolicy;

        var RegEX = passwordPolicy.passwdRegex; 
        var customRegEX = new RegExp(RegEX , "i");
        var customMsg =passwordPolicy.passwdRegexDesc;

        Ext.apply(Ext.form.VTypes, {
            regeXPassword: function(v,field) {
                return customRegEX.test(v);
                 },
            regeXPasswordText: customMsg,
        }); 

        Ext.callback(callback,this,[]);
    },
    
    getUserAuthType : function(userId, callback){
        
        var authType=null;
        
        UserManager.getCredentialForUser(userId,function(credentialObj, error){
            
            if(credentialObj){
                var credentialTypeObj = credentialObj[0].credentialType;
                var credentialType = credentialTypeObj.canonicalName;
                
                switch(credentialType){
                    case 'com.edifecs.epp.security.data.token.UsernamePasswordAuthenticationToken' : authType="UsernamePassword"; break;
                    case 'com.edifecs.epp.security.data.token.CertificateAuthenticationToken' : authType="Certificate"; break;
                    case 'com.edifecs.epp.security.data.token.LdapAuthenticationToken' : authType="LDAP"; break;
                }
                
                
                Ext.callback(callback,this,[authType,credentialObj]);
            }
            else
                    Ext.callback(callback,this,[null, error]);
        });
    }
};

