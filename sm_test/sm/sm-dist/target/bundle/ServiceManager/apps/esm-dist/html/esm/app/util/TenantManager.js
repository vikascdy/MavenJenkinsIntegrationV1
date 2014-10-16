// TENANTMANAGER.JS
// ----------------------------------------------------------------------------

Ext.define('Util.TenantManager', {});

window.TenantManager = {
		
	loadTenantPages : function(tenantPage){
			
        Functions.jsonCommand("esm-service", "isSubjectAuthenticated", {}, {
                success: function(response) {

                    if (response === true) {
                        Security.loadingWindow = Ext.widget('progresswindow', {text: 'Loading Page...'});

                        Security.selectTreeNode(tenantPage,function(){
                    		Security.removeLoadingWindow(function(){
                        	});
                    		var ctr = Ext.getCmp('Home-page-container');
                            ctr.removeAll();
                            ctr.add(tenantPage);
                        });
                    }
                    else
                    	Functions.showSessionTimeoutWindow();
                }
        });
    },
	    
    getDefaultTenantRecord : function(callback){
        var tenantStore = Ext.create('Ext.data.Store',{model:'Security.model.SiteTenants'});
        tenantStore.load({
            scope: this,
            callback: function(records, operation, success) {
                if(records.length>0){
                    Ext.callback(callback,this,[records[0]]);
                }
            }
        });
    },


    getCurrentUserTenant : function(callback){

        Functions.jsonCommand("esm-service", "subject.getTenant", {

        }, {
            success : function(response) {
                Ext.callback(callback, this, [response]);
            },
            failure : function(response) {
                Ext.callback(callback, this, [null]);
            }
        });
    },


    saveTenant : function(values, isUpdation, callback) {

		var tenantRecord = null;

		if (isUpdation){
			tenantRecord = values;
			tenantRecord.commit();
		}
		else
			tenantRecord = Ext.create('Security.model.Tenants', values);

		tenantRecord.save({
			success : function(tenantRecord) {
		        Functions.errorMsg("Tenant created successfully", 'Success', null, 'INFO');
				Ext.callback(callback, this, [tenantRecord]);
			},
			failure : function(response) {
				Functions.errorMsg(response.error,
						"Failed To Create Tenant");
			}
		});

	},
	
	createTenant : function(tenant,callback){

        Functions.jsonCommand("esm-service", "tenant.createTenant", {
            "tenant":tenant
        }, {
            success : function(response) {
                Ext.callback(callback, this, [response]);
            },
            failure : function(response) {
			Functions.errorMsg(response.error,
						"Failed To Create Tenant");
            }
        });
    },
	
	updateTenant : function(tenant,callback){

        Functions.jsonCommand("esm-service", "tenant.updateTenant", {
            "tenant":tenant
        }, {
            success : function(response) {
                Ext.callback(callback, this, [response]);
            },
            failure : function(response) {
                Ext.callback(callback, this, [null]);
            }
        });
    },

    updateTenantPasswordPolicy : function(tenantId,passwordPolicy,callback){

        Functions.jsonCommand("esm-service", "tenant.updateTenantPasswordPolicy", {
            "tenantId":tenantId,
            "policy":passwordPolicy
        }, {
            success : function(response) {
                Ext.callback(callback, this, [response]);
            },
            failure : function(response) {
                Ext.callback(callback, this, [null]);
            }
        });
    },

    updateTenantLogo : function(tenantId,data,callback){

        Functions.jsonCommand("esm-service", "tenant.updateTenantLogo", {
            "tenantId":tenantId,
            "data":data
        }, {
            success : function(response) {
                Ext.callback(callback, this, [response]);
            },
            failure : function(response) {
                Ext.callback(callback, this, [null]);
            }
        });
    },

    updateTenantLandingPage : function(tenantId,data,callback){

        Functions.jsonCommand("esm-service", "tenant.updateTenantLandingPage", {
            "tenantId":tenantId,
            "landingPage":data
        }, {
            success : function(response) {
                Ext.callback(callback, this, [response]);
            },
            failure : function(response) {
                Ext.callback(callback, this, [null]);
            }
        });
    },

	deleteTenant : function(tenant, callback) {

		tenant.destroy();
		Ext.callback(callback, this);

	},
	
	getTenantById : function(tenantId,callback){
		
        Functions.jsonCommand("esm-service", "tenant.getTenantById", {
        		"id":tenantId
            }, {
            success : function(response) {
                Ext.callback(callback, this, [response]);
            },
            failure : function(response) {
            	if(response.errorClass=='com.edifecs.security.exception.AuthenticationFailureException')
                {
            		Functions.showSessionTimeoutWindow();
                }
				else
                Ext.callback(callback, this, [null]);
            }
        });
	},

    getTenantLogo : function(tenantId,callback){

        Functions.jsonCommand("esm-service", "tenant.getTenantLogo", {
            "tenantId":tenantId
        }, {
            success : function(response) {
                Ext.callback(callback, this, [response]);
            },
            failure : function(response) {
                Ext.callback(callback, this, [null]);
            }
        });
    },

    getLandingPage : function(tenantId,callback){

        Functions.jsonCommand("esm-service", "tenant.getLandingPage", {
            "tenantId":tenantId
        }, {
            success : function(response) {
                Ext.callback(callback, this, [response]);
            },
            failure : function(response) {
                Ext.callback(callback, this, [null]);
            }
        });
    },
	
	deleteTenantFromSite : function(tenantIds,  callback, scope) {

		Functions.jsonCommand("esm-service", "tenant.deleteTenants",
			{
				"ids" : tenantIds
			}, {
				success : function(response) {
					Ext.callback(callback, scope, [response]);
				},
				failure : function(response) {
					Functions.errorMsg(response.error,
							"Failed To Delete Tenant");
				}
			});

	},
	
	removeTenantFromSite : function(tenantId,siteId,  callback, scope) {

		Functions.jsonCommand("esm-service", "tenant.removeTenantFromSite",
			{
				"tenantId" : tenantId,
				"siteId"  :  siteId
			}, {
				success : function(response) {
					Ext.callback(callback, scope, [response]);
				},
				failure : function(response) {
					Functions.errorMsg(response.error,
							"Failed To Remove Tenant");
				}
			});

	},


	getOrganizationForTenant : function(tenant, callback) {

		var OrganizationsList = Ext.StoreManager.lookup('OrganizationsList');
		OrganizationsList.load({
			callback : function(records) {
				Ext.callback(callback, this, [ records ]);
			}
		});
	},
	
	getTenantOrganizations : function(tenantId, startRecord, recordCount, callback){
		
		 Functions.jsonCommand("esm-service", "organization.getOrganizationsForTenant", {
    		"id":tenantId,
    		"startRecord":startRecord,
    		"recordCount":recordCount
        }, {
        success : function(response) {
            Ext.callback(callback, this, [response]);
        },
        failure : function(response) {
            Ext.callback(callback, this, [null]);
        }
    });
		 
	},
	
	addOrganizationToTenant : function(tenantId, organizationId, callback){
		
		 Functions.jsonCommand("esm-service", "organization.addOrganizationToTenant", {
     		"tenantId":tenantId,
     		"organizationId":organizationId
         }, {
         success : function(response) {
             Ext.callback(callback, this, [response]);
         },
         failure : function(response) {
             Ext.callback(callback, this, [null]);
         }
     });
		 
	},
	
	createRoleForTenant : function(tenant, role, callback){

        Functions.jsonCommand("esm-service", "role.createRoleForTenant", {
            "tenant":tenant,
            "role":role
        }, {
            success : function(response) {
                Ext.callback(callback, this, [response]);
            },
			failure : function(response) {
				Functions.errorMsg(response.error,
						"Failed To Add Role");
			}
        });

    },

    createGroupForTenant : function(tenant, group, callback){

        Functions.jsonCommand("esm-service", "group.createGroupForTenant", {
            "tenant":tenant,
            "group":group
        }, {
            success : function(response) {
                Ext.callback(callback, this, [response]);
            },
			failure : function(response) {
				Functions.errorMsg(response.error,
						"Failed To Add Group");
			}
        });

    },
    
    getTenantAppConfigurations : function(callback){

        Functions.jsonCommand("esm-service", "AppStore.getTenantAppConfigurations", {}, {
            success : function(response) {
                Ext.callback(callback, this, [response]);
            },
			failure : function(response) {
                Ext.callback(callback, this, [response]);
			}
        });

    }
};
