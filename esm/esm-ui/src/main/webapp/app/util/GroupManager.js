// USERMANAGER.JS
// Handles group authentication, creation, and permissions.
// ----------------------------------------------------------------------------

Ext.define('Util.GroupManager', {});

window.GroupManager = {
    GroupPermissionMap: null,

    getRolesForGroup: function(group, callback, scope) {
        Functions.jsonCommand("esm-service", "role.getRolesForGroup", {
                group: {"id": group.get('id') },
                "startRecord":0,
                "recordCount":-1
            }, {
            success: function(response) {
                Ext.callback(callback, scope, [response.resultList]);
            }
        });
    },

    getUsersForGroup: function(group, callback, scope) {
        Functions.jsonCommand("esm-service", "user.getUsersForGroup", {
                groupId: group.get('id'),
                "startRecord":0,
                "recordCount":-1
            }, {
            success: function(response) {
                Ext.callback(callback, scope, [response.resultList]);
            }
        });
    },

    checkGroupName: function(groupName, callback, scope) {
        var groupsListStore = Ext.StoreManager.lookup('GroupsList');
        Ext.callback(callback, scope, [groupsListStore.find('canonicalName', groupName) == -1]);
    },

    createGroup: function(GroupInfo, callback, scope) {
        var group = {
            "canonicalName": GroupInfo.canonicalName,
            "description": GroupInfo.description,
            "maximumUsers": GroupInfo.maximumUsers === "" ? 0 : GroupInfo.maximumUsers
        };

        Functions.jsonCommand("esm-service", "group.createGroup", {
            group: group
        }, {
            success: function(response) {
                Ext.Msg.alert("Group Created", "Group created successfully.");
                Ext.callback(callback, scope, [response]);
            }
        });
    },

    updateGroup: function(groupInfo, groupId, callback, scope) {
        var group = {
            "id":groupId,
            "canonicalName":groupInfo.canonicalName,
            "description":groupInfo.description,
            "maximumUsers":groupInfo.maximumUsers
        };

        Functions.jsonCommand("esm-service", "group.updateGroup", {
            group: group
        }, {
            success: function(response) {
                Ext.Msg.alert("Group Updation", "Group updated successfully.");
                Ext.callback(callback, scope, [group]);
            },
                failure: function(response) {
                    Functions.errorMsg(response.error, "Updation Error");
            }
        });
    },

    deleteGroup: function(groupIds, callback, scope) {
        Functions.jsonCommand("esm-service", "group.deleteGroups", {
            ids: groupIds
        }, {
            success: function(response) {
                Ext.callback(callback, scope);
            },
            failure : function(response) {
                Functions.errorMsg(response.error,
                    "Failed To Delete Group");
            }
        });
    },

    addRolesToGroup: function(group, roles, callback, scope) {
    	
        if (group.maximumUsers === "")
            group.maximumUsers = null;
        var rolesArray=[];

        Ext.each(roles, function(role) {
            rolesArray.push({
                "id":role.get('id'),
                "canonicalName":role.get('canonicalName'),
                "description":role.get('description')
            });
        });
        
        Functions.jsonCommand("esm-service", "role.addRolesToGroup", {
            group: group,
            roles:  rolesArray
        }, {
            success: function(response) {
                Ext.callback(callback, scope, [true]);
            },
            failure: function(response) {
                Ext.callback(callback, scope, [false]);
            }
        });

    },
    
    removeRolesFromGroup: function(group, roles, callback, scope) {
        if (group.maximumUsers === "")
            group.maximumUsers = null;
        var rolesArray=[];

        Ext.each(roles, function(role) {
        	rolesArray.push({
                "id":role.get('id'),
                "canonicalName":role.get('canonicalName'),
                "description":role.get('description')
        	});           
        });   
        
        Functions.jsonCommand("esm-service", "role.removeRolesFromGroup", {
            group: group,
            roles:  rolesArray
        }, {
            success: function(response) {
                Ext.callback(callback, scope, [true]);
            },
            failure: function(response) {
                Ext.callback(callback, scope, [false]);
            }
        });
    },
    
    addOrganizationsToGroup : function(group, orgs, callback, scope) {
    	
        if (group.maximumUsers === "")
            group.maximumUsers = null;
        var OrgsArray=[];

        Ext.each(orgs, function(org) {
        	var orgObj=org.data;
        	var securityRealms = org.get('securityRealms')[0];
        	if(securityRealms)
        		orgObj["securityRealms"] = [securityRealms.data];
        	OrgsArray.push(orgObj);
        });
        
        Functions.jsonCommand("esm-service", "group.addOrganizationsToGroup", {
            group: group,
            organizations:  OrgsArray
        }, {
            success: function(response) {
                Ext.callback(callback, scope, [true]);
            },
            failure: function(response) {
                Ext.callback(callback, scope, [false]);
            }
        });

    },
    
    
    removeOrganizationsFromGroup: function(group, orgs, callback, scope) {
        if (group.maximumUsers === "")
            group.maximumUsers = null;
        var OrgsArray=[];

        Ext.each(orgs, function(org) {
        	OrgsArray.push(org.data);           
        });   
        
        Functions.jsonCommand("esm-service", "group.removeOrganizationsFromGroup", {
            group: group,
            organizations:  OrgsArray
        }, {
            success: function(response) {
                Ext.callback(callback, scope, [true]);
            },
            failure: function(response) {
                Ext.callback(callback, scope, [false]);
            }
        });
    },

    addUsersToGroup: function(group, users, callback, scope) {
        if (group.maximumUsers === "")
            group.maximumUsers = null;
        var usersArray=[];
        
        Ext.each(users, function(user) {
        	usersArray.push({
                "id":user.get('id')
            });           
        });
        
        Functions.jsonCommand("esm-service", "group.addUsersToGroup", {
            group: group,
            users:  usersArray
        }, {
            success: function(response) {
                Ext.callback(callback, scope, [true]);
            },
            failure: function(response) {
                Ext.callback(callback, scope, [false]);
            }
        });
    },

    removeUsersFromGroup: function(group, users, callback, scope) {
        if (group.maximumUsers === "")
            group.maximumUsers = null;
        var usersArray=[];
        
        Ext.each(users, function(user) {
        	usersArray.push({
                "id":user.get('id')
            });
        });
        Functions.jsonCommand("esm-service", "group.removeUsersFromGroup", {
            group: group,
            users:  usersArray
        }, {
            success: function(response) {
                Ext.callback(callback, scope, [true]);
            },
            failure: function(response) {
                Ext.callback(callback, scope, [false]);
            }
        });
    },

    removeUserGroupFromTenant : function(tenantId, userGroupId,  callback, scope) {

        Functions.jsonCommand("esm-service", "group.removeGroupFromTenant",
            {
                "tenantId" : tenantId,
                "groupId"  :  userGroupId
            }, {
                success : function(response) {
                    Ext.callback(callback, scope, [response]);
                },
                failure : function(response) {
                    Functions.errorMsg(response.error,
                        "Failed To Remove Role");
                }
            });

    },
	
	getGroupById : function(id, callback, scope) {

		Functions.jsonCommand("esm-service", "group.getGroupById",
				{
					"id" : id
				}, {
					success : function(response) {
						Ext.callback(callback, scope, [response]);
					},
					failure : function(response) {
						Functions.errorMsg(response.error,
								"Failed To Retrieve Group Info");
					}
				});
	},
	
	updateGroupRecord : function(groupId,callback){
		
		Security.loadingWindow = Ext.widget('progresswindow', {
            text: 'Updating Group Information...'
        });
		
		var groupsList=Security.viewport.down('#tenantUserGroupGrid');
		var groupsListStore=Ext.StoreManager.lookup('tenantUserGroupStoreId');
		GroupManager.getGroupById(groupId, function(groupObj){
			var groupRecord = Ext.create('Security.model.TenantUserGroups',groupObj);
			var index=groupsListStore.find('id',groupId);
	        groupsListStore.removeAt(index);
	        groupsListStore.insert(index,groupRecord);
			groupsList.getSelectionModel().select(groupRecord);
		},this);
		
		Security.removeLoadingWindow(function() {
			Ext.callback(callback,this,[]);	
        });
			
	},
};

