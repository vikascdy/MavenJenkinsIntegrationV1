// ROLEMANAGER.JS
// Handles retrieving role data from the server.
// ----------------------------------------------------------------------------

Ext.define('Util.RoleManager', {});

window.RoleManager = {

    RolePermissionMap: null,
    
    copyChildRoles : function(role, newRole, callback){
    	
    	Functions.jsonCommand("esm-service", "role.getChildRolesForRole", {
    		"role":Ext.encode(role.data),
    		"startRecord":0,
    		"recordCount":-1
        }, {
        success: function(response) {
            Ext.callback(callback, scope, [response.resultList]);
        }
    });

   	
    },

    getRolesForUser: function(user, callback, scope) {
        Functions.jsonCommand("esm-service", "role.getRolesForUser", {
                "userId": user.get('id') ,
                "startRecord":0,
                "recordCount":-1
            }, {
            success: function(response) {
                Ext.callback(callback, scope, [response.resultList]);
            }
        });
    },
    
    checkRolename : function(rolename, callback, scope) {
        Log.info("Checking rolename '{0}'.", rolename);
        Functions.jsonCommand("esm-service", "role.getRoleByRoleName", {
        	roleName   : rolename
            }, {
            success : function(response) {
                Ext.callback(callback, scope, [false]);
            },
            failure : function(response) {
                Ext.callback(callback, scope, [true]);
            }
        });
    },
    
    createRole: function(RoleInfo, callback, scope) {

        var role = {
            "canonicalName":RoleInfo.canonicalName,
            "description":RoleInfo.description
        };

        Functions.jsonCommand("esm-service", "role.createRole", {
                role: role
            }, {
            success: function(response) {
                Ext.callback(callback, scope, [response]);
            },
            failure: function(response) {
                Functions.errorMsg(response.error, "Unable to Create Role");
            }
        });
    },

    updateRole: function(roleInfo, roleId, callback, scope) {
        var role = {
            "id":roleId,
            "canonicalName":roleInfo.canonicalName,
            "description":roleInfo.description
        };

        Functions.jsonCommand("esm-service", "role.updateRole", {
                "role": role
            }, {
            success: function(response) {
                Ext.Msg.alert("Role Updation", "Role updated successfully.");
                Ext.callback(callback, scope, [role]);
            },
                failure: function(response) {
                    Functions.errorMsg(response.error, "Updation Error");
            }
        });
    },
    
    deleteRole: function(roleIds, callback, scope) {
        Functions.jsonCommand("esm-service", "role.deleteRoles", {
                "ids": roleIds
            }, {
            success: function(response) {
                Ext.callback(callback, scope);
            }
        });
    },

    getAllPermissions: function(callback, scope) {
        Functions.jsonCommand("esm-service", "permission.getPermissions", {
	            "startRecord":0,
	            "recordCount":-1
            }, {
            success: function(response) {
                 Ext.callback(callback, scope, [response.resultList]);
            }
        });
    },

    getPermissionsForRole: function(role, callback, scope) {
        if (!role) {
            Ext.callback(callback, scope, [[]]);
            return;
        }
        Functions.jsonCommand("esm-service", "permission.getPermissionsForRole", {
                "role": { "id": role.get('id') },
		        "startRecord":0,
		        "recordCount":-1
            }, {
            success: function(response) {
                 Ext.callback(callback, scope, [response.resultList]);
            }
        });
    },

    addPermissionsToRole: function(role, permissions, callback, scope) {
        if (permissions.length <= 0) {
            Ext.callback(callback, scope);
        } else {
            Functions.jsonCommand("esm-service", "permission.addPermissionsToRole", {
                    "role": role,
                    "permissions": permissions
                }, {
                success: function(response) {
                    Ext.callback(callback, scope, [response]);
                }
            });
        }
    },

    removePermissionsFromRole: function(role, permission, callback, scope) {
        Functions.jsonCommand("esm-service", "permission.removePermissionsFromRole", {
                "role": role,
                "permissions": permission
            }, {
            success: function(response) {
                Ext.callback(callback, scope, [response]);
            }
        });
    },
    
    addChildRolesToRole: function(role, roles, callback, scope) {
    	
        var rolesArray=[];

        Ext.each(roles, function(role) {
            rolesArray.push(role.data);
        });
        
        Functions.jsonCommand("esm-service", "role.addChildRolesToRole", {
        	parentRole: role,
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
    
    removeChildRolesFromRole: function(role, roles, callback, scope) {
    	
        var rolesArray=[];

        Ext.each(roles, function(role) {
            rolesArray.push(role.data);
        });
        
        Functions.jsonCommand("esm-service", "role.removeChildRolesFromRole", {
        	parentRole: role,
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

    removeRoleFromTenant : function(tenantId, roleId,  callback, scope) {

        Functions.jsonCommand("esm-service", "role.removeRoleFromTenant",
            {
                "tenantId" : tenantId,
                "roleId"  :  roleId
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
    
    removeRoleFromOrganization : function(organizationId, roleId,  callback, scope) {

        Functions.jsonCommand("esm-service", "role.removeRoleFromOrganization",
            {
                "organizationId" : organizationId,
                "roleId"  :  roleId
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
	
	getRoleById : function(id, callback, scope) {

		Functions.jsonCommand("esm-service", "role.getRoleById",
				{
					"id" : id
				}, {
					success : function(response) {
						Ext.callback(callback, scope, [response]);
					},
					failure : function(response) {
						Functions.errorMsg(response.error,
								"Failed To Retrieve Role Info");
					}
				});
	},
	
	updateRoleRecord : function(roleId,callback){
		
		Security.loadingWindow = Ext.widget('progresswindow', {
            text: 'Updating Role Information...'
        });
		
		var rolesList=Security.viewport.down('#tenantRoleGrid');
		var rolesListStore=Ext.StoreManager.lookup('tenantRoleStoreId');
		RoleManager.getRoleById(roleId, function(roleObj){
			var roleRecord = Ext.create('Security.model.TenantRoles',roleObj);
			var index=rolesListStore.find('id',roleId);
	        rolesListStore.removeAt(index);
	        rolesListStore.insert(index,roleRecord);
			rolesList.getSelectionModel().select(roleRecord);
		},this);
		
		Security.removeLoadingWindow(function() {
			Ext.callback(callback,this,[]);	
        });
			
	},
};

