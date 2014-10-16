Ext.define('Util.NavigationManager', {});

window.NavigationManager = {

    initiateRoutes : function(callback) {


        Path.map("#!/LoginPage").to(function() {
            Functions.jsonCommand("esm-service", "isSubjectAuthenticated", {}, {
                success: function(response) {

                    if (response === true) {
                        window.location='/esm/#!/Site';
                    }
                    else
                        Security.setPage(Ext.create('Security.view.core.LoginPage'));
                }
            });
        });

        Path.map("#!/Home").to(function() {
            var page = Ext.create('Security.view.core.Home');
            Security.setPage(page);
        });



        /********** User Related Views **********/



        Path.map("#!/ManageUsers/:id").to(function() {
            var page = Ext.create('Security.view.user.ManageUsers');
            Security.setPage(page);
            page.fireEvent('statechange',[me.params["id"]]);
        });

        Path.map("#!/AccountSettings").to(function() {
            Security.loadingWindow = Ext.widget('progresswindow', {text: 'Loading User Profile...'});
            UserManager.getCurrentUser(function(currentUser){
            	
            	TenantManager.getCurrentUserTenant(function(tenantObj){

		                UserManager.customPasswordType(tenantObj,function(){
		                	
		                	if(currentUser){
			                    var userRecord=Ext.create('Security.model.User',currentUser);
			                    if (userRecord) {
			                    	Security.removeLoadingWindow(function(){
				                        var page = Ext.create('Security.view.user.AccountSettings');
				                        Security.setPage(page);
				                        page.update(userRecord,true,function(){});
			                        });
			                    } else {
			                        location.href='#!/Site';
			                    }
			                }
			                else
			                {
			                    Ext.MessageBox.show({
			                        title: 'Remote Error',
			                        msg: 'Your session expired. Please Re-login !',
			                        buttons: Ext.MessageBox.YES,
			                        buttonText:{
			                            yes: "Login"
			                        },
			                        fn: function(){
			                            window.location='login';
			                        }
			                    });
			                }
		                });
		                
            	});
            });
        });

        Path.map("#!/ForgotPassword").to(function() {
            Security.setPage(Ext.create('Security.view.core.ForgotPassword'));
        });

        Path.map("#!/ResetPassword/:id").to(function() {
            var me=this;
//        	PasswordManager.changePassword(me.params["id"],function(status,error){
            Security.setPage(Ext.create('Security.view.core.ResetPassword',{
                token:me.params["id"]
            }));
//        	},this);        	
        });

        Path.map("#!/ChangePassword").to(function() {
            var me=this;
            UserManager.getCurrentUser(function(user){
            	NavigationManager.getCurrentUserTenant(function(tenantObj){

	            UserManager.customPasswordType(tenantObj,function(){
	            	
	            	if(user && user.changePasswordAtFirstLogin==true)
	                {
	                    Security.setPage(Ext.create('Security.view.core.ChangePassword',{
	                        user:user
	                    }));
	                }
	                else{
	                    Ext.MessageBox.show({
	                        title: 'Invalid Attempt',
	                        msg: 'Trying to change password for invalid user.',
	                        buttons: Ext.MessageBox.YES,
	                        buttonText:{
	                            yes: "Home"
	                        },
	                        fn: function(){
	                            window.location='/';
	                        }
	                    });
	                	}
	            	
	            });
                
            	});

            },this);

        });

        Path.map("#!/logout").to(function() {
            UserManager.logout(function(){
                window.location='/esm/login';
            },this);
        });

        Path.map("#!/LoginPage?redirectURL=/esm/#!/logout").to(function() {
            UserManager.logout(function(){
                window.location='/esm/login';
            },this);
        });





//        
//        /********** Role Related Views **********/
//        
//        
//        Path.map("#!/ManageRoles/:id").to(function() {
//        		var me=this;
//        		var page = Ext.create('Security.view.role.ManageRoles');
//        		Security.setPage(page);
//        		page.fireEvent('statechange',[me.params["id"]]);
//        });
//        
//        
//        
//        /********** Group Related Views **********/
//        
//        
//        Path.map("#!/ManageGroups").to(function() {
//
//        		var page = Ext.create('Security.view.group.ManageGroups');
//        		Security.setPage(page);
//        		page.fireEvent('statechange',[]);
//        });        
//        
//        Path.map("#!/ManageGroups/:id").to(function() {
//        	var me=this;
//        		var page = Ext.create('Security.view.group.ManageGroups');
//        		Security.setPage(page);
//        		page.fireEvent('statechange',[me.params["id"]]);
//        });



        /********** Site Related Views **********/
        Path.map("#!/Site").to(function() {
        	var me=this;
            var page = Ext.create('Security.view.site.SiteConfiguration');
            page.setDisableLinking(false);
            Security.setPage(page,function(){
                page.fireEvent('statechange',[]);
                var overviewPage = Ext.widget({xtype:'siteoverview'});
                SiteManager.loadSitePages(overviewPage);
            });
        });

        Path.map("#!/Site/Overview").to(function() {
        	var me=this;
            var page = Ext.create('Security.view.site.SiteConfiguration');
            page.setDisableLinking(false);
            Security.setPage(page,function(){
                page.fireEvent('statechange',[]);
                var overviewPage = Ext.widget({xtype:'siteoverview'});
                SiteManager.loadSitePages(overviewPage);
            });
        });        

        Path.map("#!/Site/ManageTenants").to(function() {
        	var me=this;
            var page = Ext.create('Security.view.site.SiteConfiguration');
            page.setDisableLinking(false);
            Security.setPage(page,function(){
//                page.fireEvent('statechange',[]);
                SiteManager.getDefaultSiteRecord(function(defaultSite){
	                var siteTenantsPage = Ext.widget({xtype:'managesitetenants'});
	                siteTenantsPage.setLoadingParams({'siteId':defaultSite.get('id')});
	                SiteManager.loadSitePages(siteTenantsPage);
                });
            });
        });
		Path.map("#!/Site/ManageApps").to(function() {
        	var me=this;
			
            var page = Ext.create('Security.view.site.SiteConfiguration');
            page.setDisableLinking(false);
            Security.setPage(page,function(){
//                page.fireEvent('statechange',[]);
                SiteManager.getDefaultSiteRecord(function(defaultSite){
	                var siteAppsPage = Ext.widget({xtype:'managesiteapps'});
	               // siteAppsPage.setLoadingParams({'siteId':defaultSite.get('id')});
	                SiteManager.loadSitePages(siteAppsPage);
					 var siteappsListStore = Ext.StoreManager.lookup('AppsListStore');
		        siteappsListStore.load({
		        	
		        })
                });
            });
        });
        
        Path.map("#!/Site/CreateTenant").to(function() {    

			var me=this;
            var page = Ext.create('Security.view.site.SiteConfiguration');
            page.setDisableLinking(false);
            Security.setPage(page,function(){
	                SiteManager.loadSitePages(Ext.widget({xtype:'createtenant', redirectPage:"#!/Site/ManageTenants"}));
            });    	
        });




        /********** Tenant Related Views **********/

        Path.map("#!/ManageTenant").to(function() {
            TenantManager.getCurrentUserTenant(function(tenantObj){
                if(tenantObj)
                    window.location='#!/TenantConfig/'+tenantObj.id;
            });
        });
        

        Path.map("#!/Tenant/Overview").to(function() {
            TenantManager.loadTenantPages(Ext.widget({xtype:'tenantoverview'}));
        });
        
        
        

        Path.map("#!/TenantConfig/:id").to(function() {
            NavigationManager.showTenantOverviewPage(this.params["id"],true);            
        });
        
        Path.map("#!/TenantConfig/:id/?site=#!/Site").to(function() {
            NavigationManager.showTenantOverviewPage(this.params["id"],false);
        });
        
        
        


        Path.map("#!/ManageOrganization").to(function() {
            TenantManager.getCurrentUserTenant(function(tenantObj){
                if(tenantObj)
                    window.location='#!/TenantConfig/'+tenantObj.id+'/ManageOrganization';
            });
        });       
        
        
        

        Path.map("#!/Tenant/CreateOrganization?redirect=#!/TenantConfig/:tenantId/ManageOrganization/?site=#!/Site").to(function() {		
			var page = Ext.widget({xtype:'createorganization', redirectPage:"#!/TenantConfig/"+this.params["tenantId"]+"/ManageOrganization/?site=#!/Site"});		
			NavigationManager.showCreateOrganizationPage(page, this.params["tenantId"], false);
			
        });
        
        Path.map("#!/Tenant/CreateOrganization?redirect=#!/TenantConfig/:tenantId/ManageOrganization").to(function() {		
        	var page = Ext.widget({xtype:'createorganization', redirectPage:"#!/TenantConfig/"+this.params["tenantId"]+"/ManageOrganization"});			
			NavigationManager.showCreateOrganizationPage(page, this.params["tenantId"], false);
			
        });
        
        
        
        
        
        
        Path.map("#!/Tenant/CreateRole?redirect=#!/TenantConfig/:tenantId/ManageRoles/?site=#!/Site").to(function() {
        	var page = Ext.widget({xtype:'createtenantrole', redirectPage:"#!/TenantConfig/"+this.params["tenantId"]+"/ManageRoles/?site=#!/Site"});
			NavigationManager.showCreateRolePage(page, this.params["tenantId"], false);
        });
        
        Path.map("#!/Tenant/CreateRole?redirect=#!/TenantConfig/:tenantId/ManageRoles").to(function() {
        	var page = Ext.widget({xtype:'createtenantrole', redirectPage:"#!/TenantConfig/"+this.params["tenantId"]+"/ManageRoles"});
			NavigationManager.showCreateRolePage(page, this.params["tenantId"], false);
        });
        
        
        
        
        
        Path.map("#!/Tenant/CreateGroup?redirect=#!/TenantConfig/:tenantId/ManageGroups/?site=#!/Site").to(function() {
        	var page = Ext.widget({xtype:'createtenantusergroup', redirectPage:"#!/TenantConfig/"+this.params["tenantId"]+"/ManageGroups/?site=#!/Site"});
			NavigationManager.showCreateGroupPage(page, this.params["tenantId"], false);
        });
        
        Path.map("#!/Tenant/CreateGroup?redirect=#!/TenantConfig/:tenantId/ManageGroups").to(function() {
        	var page = Ext.widget({xtype:'createtenantusergroup', redirectPage:"#!/TenantConfig/"+this.params["tenantId"]+"/ManageGroups"});
			NavigationManager.showCreateGroupPage(page, this.params["tenantId"], false);
        });
        
        
        
        


        Path.map("#!/TenantConfig/:id/ManageOrganization").to(function() {
        	NavigationManager.showManageOrganizationPage(this.params["id"],true);
        });
        
        Path.map("#!/TenantConfig/:id/ManageOrganization/?site=#!/Site").to(function() {
            NavigationManager.showManageOrganizationPage(this.params["id"],false);
        });
        
        
        Path.map("#!/Tenant/CreateOrganization").to(function() {
            TenantManager.getCurrentUserTenant(function(tenantObj){
                if(tenantObj)
                    window.location='#!/TenantConfig/'+tenantObj.id+'/CreateOrganization';
            });
        });
        
        Path.map("#!/TenantConfig/:id/CreateOrganization").to(function() {
            var me=this;
            var page = Ext.create('Security.view.tenant.TenantConfiguration');
            page.setDisableLinking(true);
            Security.setPage(page,function(){
                page.fireEvent('statechange',[me.params["id"]]);
                var orgPage = Ext.widget({xtype:'createorganization'});
                TenantManager.loadTenantPages(orgPage);
            });
        });
        
        
        Path.map("#!/Tenant/CreateRole").to(function() {
            TenantManager.getCurrentUserTenant(function(tenantObj){
                if(tenantObj)
                    window.location='#!/TenantConfig/'+tenantObj.id+'/CreateRole';
            });
        });
        
        Path.map("#!/TenantConfig/:id/CreateRole").to(function() {
            var me=this;
            var page = Ext.create('Security.view.tenant.TenantConfiguration');
            page.setDisableLinking(true);
            Security.setPage(page,function(){
                page.fireEvent('statechange',[me.params["id"]]);
                var rolePage = Ext.widget({xtype:'createtenantrole'});
                TenantManager.loadTenantPages(rolePage);
            });
        });
        
        
        
        Path.map("#!/ManageRoles").to(function() {
            TenantManager.getCurrentUserTenant(function(tenantObj){
                if(tenantObj)
                    window.location='#!/TenantConfig/'+tenantObj.id+'/ManageRoles';
            });
        });

        Path.map("#!/TenantConfig/:id/ManageRoles").to(function() {
        		NavigationManager.showManageRolesPage(this.params["id"],true);
        });
        
        Path.map("#!/TenantConfig/:id/ManageRoles/?site=#!/Site").to(function() {
        	NavigationManager.showManageRolesPage(this.params["id"],false);
        });


        Path.map("#!/ManageGroups").to(function() {
            TenantManager.getCurrentUserTenant(function(tenantObj){
                if(tenantObj)
                    window.location='#!/TenantConfig/'+tenantObj.id+'/ManageGroups';
            });
        });




        Path.map("#!/TenantConfig/:id/ManageGroups").to(function() {
        	NavigationManager.showManageGroupsPage(this.params["id"],false);
        });

        
        Path.map("#!/TenantConfig/:id/ManageGroups/?site=#!/Site").to(function() {
        	NavigationManager.showManageGroupsPage(this.params["id"],false);
        });

        Path.map("#!/Settings").to(function() {
            TenantManager.getCurrentUserTenant(function(tenantObj){
                if(tenantObj)
                    window.location='#!/TenantConfig/'+tenantObj.id+'/Settings';
            });
        });


        Path.map("#!/TenantConfig/:id/Settings").to(function() {
        	NavigationManager.showTenantSettingsPage(this.params["id"],false);           
        });
        
        Path.map("#!/TenantConfig/:id/Settings/?site=#!/Site").to(function() {
        	NavigationManager.showTenantSettingsPage(this.params["id"],true);
    	});
        
        
        Path.map("#!/Apps").to(function() {
            TenantManager.getCurrentUserTenant(function(tenantObj){
                if(tenantObj)
                    window.location='#!/TenantConfig/'+tenantObj.id+'/Apps';
            });
        });
        
        
        
        Path.map("#!/TenantConfig/:id/Apps").to(function() {
        	NavigationManager.showTenantAppsPage(this.params["id"],false);           
        });
        
        Path.map("#!/TenantConfig/:id/Apps/?site=#!/Site").to(function() {
        	NavigationManager.showTenantAppsPage(this.params["id"],true);
    	});
        
        

       


        /********** Organization Related Views **********/

        Path.map("#!/ManageMyOrganization").to(function() {
            OrganizationManager.getCurrentUserOrganization(function(orgObj){
                if(orgObj)
                    window.location='#!/OrganizationConfig/'+orgObj.id;
            });
        });

        Path.map("#!/ManageSubOrganization").to(function() {
            OrganizationManager.getCurrentUserOrganization(function(orgObj){
                if(orgObj)
                    window.location='#!/OrganizationConfig/'+orgObj.id+'/ManageSubOrganization';
            });
        });

        Path.map("#!/OrganizationConfig/:id/ManageSubOrganization").to(function() {
        	NavigationManager.showManageSubOrganizationPage(this.params["id"],true);
        });
        
        Path.map("#!/OrganizationConfig/:id/ManageSubOrganization/?tenant=#!/TenantConfig/:tenantId/ManageOrganization").to(function() {
        	NavigationManager.showManageSubOrganizationPage(this.params["id"],false);
        });

        
        Path.map("#!/AssignRoles").to(function() {
            OrganizationManager.getCurrentUserOrganization(function(orgObj){
                if(orgObj)
                    window.location='#!/OrganizationConfig/'+orgObj.id+'/AssignRoles';
            });
        });

        Path.map("#!/OrganizationConfig/:id/AssignRoles").to(function() {
        	NavigationManager.showAssignRolesPage(this.params["id"],true);
        });
        
        Path.map("#!/OrganizationConfig/:id/AssignRoles/?tenant=#!/TenantConfig/:tenantId/ManageOrganization").to(function() {
        	NavigationManager.showAssignRolesPage(this.params["id"],false);
        });


        Path.map("#!/ManageUsers").to(function() {
            OrganizationManager.getCurrentUserOrganization(function(orgObj){
                if(orgObj)
                    window.location='#!/OrganizationConfig/'+orgObj.id+'/ManageUsers';
            });
        });

        Path.map("#!/OrganizationConfig/:id/ManageUsers").to(function() {
        	NavigationManager.showManageUsersPage(this.params["id"],true);
        });

        Path.map("#!/OrganizationConfig/:id/ManageUsers/?tenant=#!/TenantConfig/:tenantId/ManageOrganization").to(function() {
        	NavigationManager.showManageUsersPage(this.params["id"],true);
        });

        
        
        Path.map("#!/Organization/CreateUser?redirect=#!/OrganizationConfig/:orgId/ManageUsers/?tenant=#!/TenantConfig/:tenantId/ManageOrganization").to(function() {
        	var redirectPage = "#!/OrganizationConfig/"+this.params["orgId"]+"/ManageUsers/?tenant=#!/TenantConfig/"+this.params["tenantId"]+"/ManageOrganization";
			NavigationManager.showCreateUsersPage(redirectPage, this.params["orgId"], false);
			
        });
        
        Path.map("#!/Organization/CreateUser?redirect=#!/OrganizationConfig/:orgId/ManageUsers").to(function() {
        	var redirectPage = "#!/OrganizationConfig/"+this.params["orgId"]+"/ManageUsers";
			NavigationManager.showCreateUsersPage(redirectPage, this.params["orgId"], false);
				 
        });
		
		Path.map("#!/Organization/CreateUser?redirect=#!/OrganizationConfig/:orgId/ManageSubOrganization").to(function() {	
			var redirectPage = "#!/OrganizationConfig/"+this.params["orgId"]+"/ManageUsers";
			NavigationManager.showCreateUsersPage(redirectPage, this.params["orgId"], false);
			 
        });
		Path.map("#!/Organization/CreateUser?redirect=#!/OrganizationConfig/:orgId/ManageSubOrganization/?tenant=#!/TenantConfig/:tenantId/ManageOrganization").to(function() {
					
			var redirectPage = "#!/OrganizationConfig/"+this.params["orgId"]+"/ManageUsers";
			NavigationManager.showCreateUsersPage(redirectPage, this.params["orgId"], false);
			 
        });
        
        
        


        Path.map("#!/OrganizationConfig/:id").to(function() {
            var me=this;
            var page = Ext.create('Security.view.organization.OrganizationConfiguration');
            page.setDisableLinking(true);
            Security.setPage(page);
            page.fireEvent('statechange',[me.params["id"]]);
            
            NavigationManager.showOrganizationOverviewPage(this.params["id"],true);
        });        
        
        Path.map("#!/OrganizationConfig/:orgId/?tenant=#!/TenantConfig/:tenantId/ManageOrganization").to(function() {
            NavigationManager.showOrganizationOverviewPage(this.params["orgId"],false);
        });
        
        Path.map("#!/OrganizationConfig/:orgId/?tenant=#!/TenantConfig/:tenantId/ManageOrganization/?site=#!/Site").to(function() {
            NavigationManager.showOrganizationOverviewPage(this.params["orgId"],false);
        });
        
        Path.map("#!/OrganizationConfig/:orgId/?tenant=#!/TenantConfig/:tenantId/?site=#!/Site").to(function() {
            NavigationManager.showOrganizationOverviewPage(this.params["orgId"],false);
        });



        Path.root("/esm/#!/Site");

        Path.rescue(function() {
            Functions.jsonCommand("esm-service", "isSubjectAuthenticated", {}, {
                success: function(response) {

                    if (response === true) {
                        window.location='/esm/#!/Site';
                    }
                    else
                        window.location='#!/LoginPage';
                }
            });
        });

        Path.listen();

        Ext.callback(callback, this);

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
    
    showTenantOverviewPage : function(id,disabledLinking){
    	var me=this;
        var page = Ext.create('Security.view.tenant.TenantConfiguration');
        page.setDisableLinking(disabledLinking);
        Security.setPage(page,function(){
            page.fireEvent('statechange',[id]);
            var overviewPage = Ext.widget({xtype:'tenantoverview'});
            TenantManager.loadTenantPages(overviewPage);
        });
    },
    
    showManageOrganizationPage : function(id,disabledLinking){
    	var me=this;
        var page = Ext.create('Security.view.tenant.TenantConfiguration');
        page.setDisableLinking(disabledLinking);
        Security.setPage(page,function(){
            page.fireEvent('statechange',[id]);
            var orgPage = Ext.widget({xtype:'managetenantorganizations'});
            orgPage.setLoadingParams({'id':id});
            TenantManager.loadTenantPages(orgPage);
        });
    },
	
	showCreateOrganizationPage : function(orgPage, id, disabledLinking){
    	var me=this;
        var page = Ext.create('Security.view.tenant.TenantConfiguration');
        page.setDisableLinking(disabledLinking);
        Security.setPage(page,function(){
            page.fireEvent('statechange',[id]);
            TenantManager.loadTenantPages(orgPage);
        });
    },
    
    
    showManageRolesPage : function(id, disableLinking){
        var me=this;
        var page = Ext.create('Security.view.tenant.TenantConfiguration');
        page.setDisableLinking(disableLinking);
        Security.setPage(page,function(){
            page.fireEvent('statechange',[id]);
            var rolePage = Ext.widget({xtype:'managetenantroles'});
            rolePage.setLoadingParams({'id':id});
            TenantManager.loadTenantPages(rolePage);
        });
    },
	
	showCreateRolePage : function(rolePage, id, disabledLinking){
    	var me=this;
        var page = Ext.create('Security.view.tenant.TenantConfiguration');
        page.setDisableLinking(disabledLinking);
        Security.setPage(page,function(){
            page.fireEvent('statechange',[id]);
            TenantManager.loadTenantPages(rolePage);
        });
    },
    
    showManageGroupsPage : function(id, disableLinking){
        var me=this;
        var page = Ext.create('Security.view.tenant.TenantConfiguration');
        page.setDisableLinking(disableLinking);
        Security.setPage(page,function(){
        	page.fireEvent('statechange',[id]);
            var groupPage = Ext.widget({xtype:'managetenantusergroups'});
            groupPage.setLoadingParams({'id':id});
            TenantManager.loadTenantPages(groupPage);
        });
    },
	
	showCreateGroupPage : function(groupPage, id, disabledLinking){
    	var me=this;
        var page = Ext.create('Security.view.tenant.TenantConfiguration');
        page.setDisableLinking(disabledLinking);
        Security.setPage(page,function(){
            page.fireEvent('statechange',[id]);
            TenantManager.loadTenantPages(groupPage);
        });
    }, 
    
    showTenantSettingsPage : function(id, disableLinking){
    	 var me=this;
         var page = Ext.create('Security.view.tenant.TenantConfiguration');
         page.setDisableLinking(disableLinking);
         Security.setPage(page,function(){
             page.fireEvent('statechange',[id]);
             TenantManager.getTenantById(id,function(tenantObj){
                 if(tenantObj){
                	 TenantManager.getTenantAppConfigurations(function(appConfig){
                         var tenantRecord = Ext.create('Security.model.Tenants',tenantObj);
                         var policyPage = Ext.widget({xtype:'settingsconfig',tenantId:id});

                         var appConfigArray=[{
                            xtype: 'component',
                            margin: '0 0 20 0',
                            html: '<h2>Tenant Apps Settings</h2>'
                        }];

                         var policyRecord = Ext.create('Security.model.PasswordPolicy',tenantRecord.get('passwordPolicy'));
                         var logoRecord=Ext.create('Security.model.Logo', {'logo': tenantRecord.get('logo')});
                         var landingPageRecord=Ext.create('Security.model.LandingPage', {'landingPage': tenantRecord.get('landingPage')});

                         Ext.getCmp('passwordPolicyConfigForm').loadRecord(policyRecord);
                         Ext.getCmp('tenantLogo').loadRecord(logoRecord);
                         Ext.getCmp('tenantLandingPage').loadRecord(landingPageRecord);

                         if (appConfig.success == true) {
		                     var flexFieldComponent = Ext.getCmp('tenantAppSettings').down('flexfieldcomponent');
		                     var tenantAppSettings=Ext.getCmp('tenantAppSettings');


	        		        	for(var i in appConfig){
	        		        		appConfigArray.push({
	        		                    xtype: 'component',
	        		                    margin: '20 0 0 0',
	        		                    html: '<h3>'+i+'</h3>'
	        		                });
	        		        		
	        		        		if(appConfig[i].length==0)
	        		        			appConfigArray.push({
	 	        		               	   xtype:'component',
 	        		               	   		html:'<i>No Settings Found</i>'
	 	        		                 });  	        		        		
	        		        		else
		        		        		appConfigArray.push({
		        		               	   xtype:'flexfieldcomponent',
		        		              	   layout:'anchor',
		        		              	   defaults:{'anchor':'50%'},
		        		              	   margin:'10 0 10 0',
		        		              	   useStaticData : true,
		        		              	   staticFlexGroupArray : appConfig[i],
		        		              	   fieldConfigurationUrl:'resources/json/TenantAppsFieldConfiguration.json',
		        		                 });        		
	        		        	}
	        		        	Ext.each(appConfigArray,function(config){
	        		        		var widget = Ext.widget(config);
	        		        		if(config.xtype=='flexfieldcomponent')
	        		        			widget.setEntityId(tenantRecord.get('id'));
	            		        	tenantAppSettings.add(widget);	            		        	
	            		        		
	            		        });
	        		        	
	        		        	if(flexFieldComponent)
	                     			flexFieldComponent.setEntityId(tenantRecord.get('id'));
	                     		 
                         }

                         TenantManager.loadTenantPages(policyPage);
                    });
             		 
                 }
             },this);
         });    	
    },
    
    showTenantAppsPage: function(id, disableLinking){
	   	 var me=this;
	     var page = Ext.create('Security.view.tenant.TenantConfiguration');
	     page.setDisableLinking(disableLinking);
	     Security.setPage(page,function(){
	         page.fireEvent('statechange',[id]);
			 
	         var appsPage = Ext.widget({xtype:'manageapps'});
			 appsPage.update(id, function(){			 
			  		TenantManager.loadTenantPages(appsPage);
			 });
		 });
	            	
	},
	    
	
	showOrganizationOverviewPage : function(id, disableLinking){
        var me=this;
        var page = Ext.create('Security.view.organization.OrganizationConfiguration');
        page.setDisableLinking(disableLinking);
        Security.setPage(page,function(){
        	page.fireEvent('statechange',[id]);
            var overviewPage = Ext.widget({xtype:'organizationoverview'});
            OrganizationManager.loadOrganizationPages(overviewPage);
        });  
    },

    showManageSubOrganizationPage : function(id, disableLinking){
	    var me=this;
	    var page = Ext.create('Security.view.organization.OrganizationConfiguration');
	    page.setDisableLinking(disableLinking);
	    Security.setPage(page,function(){
	        page.fireEvent('statechange',[id]);
	        
	        OrganizationManager.getOrganizationById(id,function(orgObj){
				
	            var organizationRecord = Ext.create('Security.model.Organizations',orgObj);
				var orgPage = Ext.widget({xtype:'manageorganizationorgs', orgRecord:organizationRecord});
	            orgPage.update(organizationRecord);
	            OrganizationManager.loadOrganizationPages(orgPage);
	        },this);
	    });
    },
    
    showAssignRolesPage : function(id, disableLinking){
        var me=this;
        var page = Ext.create('Security.view.organization.OrganizationConfiguration');
        page.setDisableLinking(disableLinking);
        Security.setPage(page,function(){
            page.fireEvent('statechange',[id]);
            OrganizationManager.getOrganizationById(id,function(orgObj){
                var organizationRecord = Ext.create('Security.model.Organizations',orgObj);
                var rolePage = Ext.widget({xtype:'assignorganizationroles',organization:organizationRecord});
                OrganizationManager.loadOrganizationPages(rolePage);
            },this);
        });    
    },
    
    showManageUsersPage : function(id, disableLinking){
        var me=this;
        var page = Ext.create('Security.view.organization.OrganizationConfiguration');
        page.setDisableLinking(disableLinking);
        Security.setPage(page,function(){
            page.fireEvent('statechange',[id]);
            OrganizationManager.getOrganizationById(id,function(orgObj){
            	 UserManager.customPasswordType(orgObj.tenant,function(){
	                	var userPage = Ext.widget({xtype:'manageorganizationusers'});
	                    userPage.setLoadingParams({'organizationId':id});
	                    OrganizationManager.loadOrganizationPages(userPage);	
            	 });
            },this);                
        });  
    },
	
	showCreateUsersPage : function(redirectPage, id, disableLinking){
        var me=this;
        var page = Ext.create('Security.view.organization.OrganizationConfiguration');
        page.setDisableLinking(disableLinking);
        Security.setPage(page,function(){
            page.fireEvent('statechange',[id]);
			
			OrganizationManager.getOrganizationById(id,function(orgObj){
        		var organizationRecord = Ext.create('Security.model.Organizations',orgObj);
        		var userPage = Ext.widget({xtype:'createorganizationuser',organization:organizationRecord,redirectPage:redirectPage });
				TenantManager.getTenantById(organizationRecord.get("tenant").id, function(tenant){
					UserManager.customPasswordType(tenant, function(){
					OrganizationManager.loadOrganizationPages(userPage);
					});
				});
        	},this);  

        });  
    }

};