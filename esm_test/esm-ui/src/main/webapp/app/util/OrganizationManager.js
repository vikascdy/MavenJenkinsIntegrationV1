// ORGNIZATIONMANAGER.JS
// ----------------------------------------------------------------------------

Ext.define('Util.OrganizationManager', {});

window.OrganizationManager = {

    loadOrganizationPages : function(orgPage){

        Functions.jsonCommand("esm-service", "isSubjectAuthenticated", {}, {
            success: function(response) {

                if (response === true) {
                    Security.loadingWindow = Ext.widget('progresswindow', {text: 'Loading Page...'});
                    
	                    Security.selectTreeNode(orgPage,function(){
	                		Security.removeLoadingWindow(function(){
	                    	});
	                		var ctr = Ext.getCmp('Home-page-container');
	                        ctr.removeAll();
	                        ctr.add(orgPage);
	                    });    
                    	                                     
                }
                else
                	Functions.showSessionTimeoutWindow();
            },
            
            failure : function(response){
            	Functions.showSessionTimeoutWindow();
            }
        });
    },


    getCurrentUserOrganization : function(callback){

        Functions.jsonCommand("esm-service", "subject.getOrganization", {

        }, {
            success : function(response) {
                Ext.callback(callback, this, [response]);
            },
            failure : function(response) {
                Ext.callback(callback, this, [null]);
            }
        });
    },

	saveOrganization : function(values, isUpdation, callback) {

		var orgRecord = null;

		if (isUpdation) {
			orgRecord = values;
			orgRecord.commit();
		} else
			orgRecord = Ext.create('Security.model.Organizations', values);

		orgRecord.data['securityRealms']=[];
		orgRecord.save({
			success : function(orgRecord) {
				Ext.callback(callback, this, [ orgRecord ]);
			}
		});

	},
	
	updateOrganization : function(organization,  callback, scope) {

		Functions.jsonCommand("esm-service", "organization.updateOrganization",
			{
				"organization" : organization
			}, {
				success : function(response) {
					Ext.callback(callback, scope, [response]);
				},
				failure : function(response) {
					Functions.errorMsg(response.error,
							"Failed To Update Organization");
				}
			});

	},

	deleteOrganization : function(organization, callback) {

		organization.destroy();
		Ext.callback(callback, this);

	},
	
	
	deleteOrganizationRecord : function(organizationIds, callback,scope){
		
	Functions.jsonCommand("esm-service", "organization.deleteOrganizations",
			{
				"ids" : organizationIds
			}, {
				success : function(response) {
					Ext.callback(callback, scope, [response]);
				},
				failure : function(response) {
					Functions.errorMsg(response.error,
							"Failed To Delete Organization");
				}
			});
	},
	
	
	createOrganizationForTenant : function(tenant, organization,  callback, scope) {

		Functions.jsonCommand("esm-service", "organization.createOrganizationForTenant",
			{
				"tenant" : tenant,
				"organization" : organization
			}, {
				success : function(response) {
					Ext.callback(callback, scope, [response]);
				},
				failure : function(response) {
					Functions.errorMsg(response.error,
							"Failed To Create Organization");
				}
			});

	},
	
	removeOrganizationFromTenant : function(tenantId, organizationId,  callback, scope) {

		Functions.jsonCommand("esm-service", "organization.removeOrganizationFromTenant",
			{
				"tenantId" : tenantId,
				"organizationId"  :  organizationId
			}, {
				success : function(response) {
					Ext.callback(callback, scope, [response]);
				},
				failure : function(response) {
					Functions.errorMsg(response.error,
							"Failed To Remove Organization");
				}
			});

	},

	addRealmToOrganization : function(realm, organizationId, callback, scope) {

		if (realm.realmType != 'DATABASE') {

			Functions.jsonCommand("esm-service", "organization.addRealmToOrganization",
					{
						"realm" : realm,
						"organizationId" : organizationId
					}, {
						success : function(response) {
							Ext.callback(callback, scope, [response]);
						},
						failure : function(response) {
							Functions.errorMsg(response.error,
									"Failed To Add Realm");
						}
					});

		} else
			Ext.callback(callback, scope, []);

	},
	
	updateOrganizationRecord : function(organizationId,callback){
		
		Security.loadingWindow = Ext.widget('progresswindow', {
            text: 'Updating Organization Information...'
        });
		
		var organizationsList=Ext.StoreManager.lookup('organizationUserGridStoreId');
		OrganizationManager.getOrganizationById(organizationId, function(orgObj){
			var organizationRecord = Ext.create('Security.model.TenantOrganization',orgObj);
			var index=organizationsList.find('id',organizationId);
	        organizationsList.removeAt(index);
	        organizationsList.insert(index,organizationRecord);
			
		},this);
		
//		Ext.ModelManager.getModel('Security.model.Organizations').load(organizationId, {
//		    success: function(organization) {
//
//		        var index=organizationsList.find('id',organizationId);
//		        organizationsList.removeAt(index);
//		        organizationsList.insert(index,organization);
//		        //Security.viewport.down('organizationslist').getSelectionModel().select(index);                             
//		        Security.viewport.down('organizationinfo').update(organization);
//		        
//		    }
//		});
		Security.removeLoadingWindow(function() {
			Ext.callback(callback,this,[]);	
        });
			
	},
	
	getOrganizationById : function(id, callback, scope) {

		Functions.jsonCommand("esm-service", "organization.getOrganizationById",
				{
					"id" : id
				}, {
					success : function(response) {
						Ext.callback(callback, scope, [response]);
					},
					failure : function(response) {
						Functions.errorMsg(response.error,
								"Failed To Retrieve Organzation Info");
					}
				});
	},
	
	getChildOrganizationsById : function(id, callback, scope) {

		Functions.jsonCommand("esm-service", "organization.getChildOrganizationsById",
				{
					"organizationId" : id
				}, {
					success : function(response) {
						Ext.callback(callback, scope, [response]);
					},
					failure : function(response) {
						Functions.errorMsg(response.error,
								"Failed To Retrieve Sub Organzations");
					}
				});
	},
	
	getOrganizationDetail : function(id, callback, scope) {

		Functions.jsonCommand("esm-service", "organization.getOrganizationDetail",
				{
					"organizationId" : id
				}, {
					success : function(response) {
						Ext.callback(callback, scope, [response]);
					},
					failure : function(response) {
						Functions.errorMsg(response.error,
								"Failed To Retrieve Sub Organzations");
					}
				});
	},
	
	addChildOrganization : function(organizationId, childOrganizationId, callback, scope){

        Functions.jsonCommand("esm-service", "organization.addChildOrganization", {
            "organizationId":organizationId,
            "childOrganizationId":childOrganizationId
        }, {
            success : function(response) {
                Ext.callback(callback, scope, [response]);
            },
            failure : function(response) {
                Ext.callback(callback, scope, [null]);
            }
        });
	},
	
	removeChildOrganization : function(organizationId, childOrganizationId, callback, scope){

        Functions.jsonCommand("esm-service", "organization.removeChildOrganization", {
            "organizationId":organizationId,
            "childOrganizationId":childOrganizationId
        }, {
            success : function(response) {
                Ext.callback(callback, scope, [response]);
            },
            failure : function(response) {
                Ext.callback(callback, scope, [null]);
            }
        });
	},

	
	addRoleToOrganization : function(organizationId, roleId, callback){

	        Functions.jsonCommand("esm-service", "organization.addRoleToOrganization", {
	            "organizationId":organizationId,
	            "roleId":roleId
	        }, {
	            success : function(response) {
	                Ext.callback(callback, this, [response]);
	            },
	            failure : function(response) {
	                Ext.callback(callback, this, [null]);
	            }
	        });

    },
    
    addRolesToOrganization : function(organizationId, roles, callback){
    	var rolesArray=[];
    	
    	 Ext.each(roles, function(role) {
             rolesArray.push(role.data);            
             
         });    

        Functions.jsonCommand("esm-service", "organization.addRolesToOrganization", {
            "organizationId":organizationId,
            "roles":rolesArray
        }, {
            success : function(response) {
                Ext.callback(callback, this, [response]);
            },
            failure : function(response) {
                Ext.callback(callback, this, [null]);
            }
        });

    },
    
    removeRolesFromOrganization : function(organizationId, roles, callback){
    	var rolesArray=[];
    	
   	 Ext.each(roles, function(role) {
            rolesArray.push(role.data);            
            
        });    

       Functions.jsonCommand("esm-service", "organization.removeRolesFromOrganization", {
           "organizationId":organizationId,
           "roles":rolesArray
       }, {
           success : function(response) {
               Ext.callback(callback, this, [response]);
           },
           failure : function(response) {
               Ext.callback(callback, this, [null]);
           }
       });

   },

    getUsersForOrganization: function(organizationId, startCount, recordCount, callback){

        Functions.jsonCommand("esm-service", "user.getUsersForOrganization", {
            "organizationId":organizationId,
            "startCount":startCount,
            "recordCount":recordCount
        }, {
            success : function(response) {
                Ext.callback(callback, this, [response]);
            },
            failure : function(response) {
                Ext.callback(callback, this, [null]);
            }
        });

    }

};
