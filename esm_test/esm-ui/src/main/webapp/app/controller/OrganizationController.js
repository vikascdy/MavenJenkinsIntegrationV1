Ext.define('Security.controller.OrganizationController', {
    extend: 'Ext.app.Controller',

    stores: [
             'OrganizationsList',
             'OrganizationRoles'
         ],
         models: [
             'Organizations'
         ],
         views: [
             'organization.MyOrganization',
             'organization.CreateOrganization',
             'organization.CreateChildOrganization',
             'organization.OrganizationDetailPane',
             'organization.OrganizationDetailPaneHeader',
             'organization.OrganizationConfiguration',
             'organization.OrganizationOverview',
             'organization.OrganizationRoles',
             'organization.AssignOrganizationRoles',
             'organization.ManageOrganizationRoles',
             'organization.ManageOrganizationUsers',
             'organization.ManageOrganizationOrgs',
             'organization.OrganizationTree',
             'organization.CreateOrganizationRole',
             'organization.CreateOrganizationUser',
             'organization.ChildOrganizationConfigWindow',
			 'core.UploadWindow'
         ],

         refs:[{
             ref: 'myOrganization',
             selector: 'myorganization'
         }, {
             ref:'organizationConfiguration',
             selector:'organizationconfiguration'
         },{
             ref:'manageOrganizationRoles',
             selector:'manageorganizationroles'
         },{
             ref:'manageOrganizationUsers',
             selector:'manageorganizationusers'
         },{
             ref:'manageOrganizationOrgs',
             selector:'manageorganizationorgs'
         },{
             ref:'assignOrganizationRoles',
             selector:'assignorganizationroles'
         },{
             ref:'organizationRoles',
             selector:'organizationroles'
         },{
             ref:'accountSettings',
             selector:'accountsettings'
         }],

         init: function () {
             this.definePasswordType();
             this.control({
                 'manageorganizations': {
                     statechange: this.onStateChange
                 },
                 'myorganization': {
                     statechange: this.showMyOrganization
                 },
                 'manageorganizations organizationslist': {
                     selectionchange: this.onOrganizationSelectionChange
                 },
                 'manageorganizations #neworganization': {
                     click: this.newOrganization
                 },
                 'organizationinfo #editorganization': {
                     click: this.editOrganization
                 },
                 'manageorganizations #deleteorganization': {
                     click: this.deleteOrganization
                 },
                 'manageorganizations organizationslist textfield[action=searchOrganization]': {
                     change: this.searchOrganization
                 },
                 'manageorganizations organizationslist tool[action=refreshOrganizationsList]': {
                     click: this.loadOrganizationStore
                 },
                 'addeditorganization button[action=saveNewOrganization]': {
                     click: this.saveNewOrganization
                 },
                 'addeditorganization button[action=updateOrganization]': {
                     click: this.updateOrganization
                 },
                 'managetenantorganizations #tenantOrganizationGrid gridview' :{
                	 cellclick: this.showOrganizationDetails
                },
                'organizationconfiguration':{
                	'statechange' : this.loadOrganizationRecord
                },
                '#configOrganizationMenu':{
                    itemclick :this.handleTreeOptions
                },
                'manageorganizationroles #organizationRoleGrid':{
                    'selectionchange' : this.showRoleSummaryForOrganization,
                    'newItem':this.showCreateOrganizationRolePage,
                    'deleteItem':this.deleteOrganizationRoleRecord,
					'importItem':this.openUploadWindow
					
                } ,
                'manageorganizationusers #organizationUserGrid':{
                    'selectionchange' : this.showUserSummaryForOrganization,
                    'newItem':this.showCreateOrganizationUserPage,
                    'deleteItem':this.deleteOrganizationUserRecord,
					'importItem':this.openUploadOrganizationUserWindow
                },
                'accountsettings #edituser': {
                    click: this.editUser
                },
                'useractionmenu #updateCredentials':{
                	'click' : this.updateCredentials
                },
                'createorganizationuser button[action="createUser"]':{
                    'click' : this.addUserToOrganization
                },
                'createorganizationuser button[action="getLdapUserAttributes"]':{
                    'click' : this.getLdapUserAttributes
                },
                'manageorganizationorgs #organizationTree':{
                    'cellclick' : this.handleOrganizationTree,
                    'newItem':this.showCreateChildOrganizationWindow,
                    'deleteItem':this.deleteChildOrganizationRecord
                },
                'createchildorganization button[action="createOrganization"]':{
                    'click' : this.createChildOrganization
                },
                'organizationroles':{
                	'selectionchange' : this.handleOrganizationRoleSelection,
                	'addRole' : this.showAddOrganizationRolesWindow,
                	'removeRole' : this.removeOrganizationRole,
                },
                'addroleoruserorgroup button[action=addRolesToOrganization]':{
                	click : this.assignRoleToOrganization                	
                },
                'testconnectionwindow':{
                	'updateOrganizationRecord' : this.updateOrganizationConfigRecord
                },
				 'edituser button[action=updateUser]': {
				   click: this.updateUser
				}
                
                
             });
         },

         onStateChange: function(path) {
             if (path.length > 0) {
                 var organizationId = path[0];
                 if (Ext.isNumeric(organizationId)) {
                     var selected = this.getOrganizationsList().getSelectionModel().getSelection();
                     if (selected.length > 0 && selected[0].getId() == organizationId) {
                         return; //ignore - organization is already selected
                     }

                     var organizationIndex = this.getOrganizationsListStore().findExact('id', Ext.Number.from(organizationId));
                     if (organizationIndex != -1) {
                         this.selectOrganizationRecord(organizationIndex);
                         return;
                     } else { //organization is not yet loaded
                         this.loadOrganizationStore(organizationId);
                     }
                 } else {
                     this.application.fireEvent('changeurlpath', []);
                 }
             } else {
                 this.loadOrganizationStore();
             }
         },
         
         loadOrganizationRecord: function(path) {
        	 var me=this;
             if (path.length > 0) {
                 var organizationId = path[0];
                 
                 OrganizationManager.getOrganizationById(organizationId,function(organizationObj){
                	 var organizationRecord = Ext.create('Security.model.Organizations',organizationObj);
                	 var organizationConfiguration= me.getOrganizationConfiguration();
                	 organizationConfiguration.setOrganizationId(organizationRecord.get('id'));
                	 organizationConfiguration.setOrganizationRecord(organizationRecord);
                	 if(organizationConfiguration.down('organizationoverview'))
                		 organizationConfiguration.down('organizationoverview').showOrganizationInfo(organizationRecord);
                 });

             }
         },
         
         showMyOrganization: function(){
        	 this.getOrganizationsListStore().load({
                 scope: this,
                 callback: function(records, operation, success) {
                     if (success !== false) {
                    	 this.getMyOrganization().down('organizationinfo').update(records[0]);
                     } 
//                     else {
//                         window.location = 'login';
//                     }
                 }
             });
        	 
         },
         
         
         newOrganization: function() {
             var addOrganizationWindow = Ext.create('Security.view.organization.AddEditOrganization', {
            	 mode:'create',
            	 id:'addEditOrganization',
                 title: "Create Organization",
                 hideRealmUpdateBtn:true
             });
             addOrganizationWindow.show();
         },


		openUploadOrganizationUserWindow: function(grid){
		var organizationConfiguration = this.getOrganizationConfiguration();
        var organizationRecord = organizationConfiguration.getOrganizationRecord();
			Ext.widget({	
			xtype :'uploadwindow',
			heading:'Import Users',
			targetUrl:'user.importUsersJson',
			paramName:'organizationId',
			paramValue:organizationRecord.get('id'),
            grid :grid
		}).show();
			},
			
			
         updateOrganization: function(btn, eventObj) {
             var window = btn.up('window');

             var form = window.down('form').getForm();
             var me = this;
             if (form.isValid()) {

                 var record = form.getRecord();
                 form.updateRecord(record);

                 OrganizationManager.saveOrganization(record.data,false, function(organization) {
//                	 SecurityRealmManager.generatePropertiesForRealm(function(securityRealmConfig,realConfigForm){
//                		 securityRealmConfig.id=realConfigForm.getRealmId();
//                		 SecurityRealmManager.updateRealm(securityRealmConfig,function(){
		                     me.getOrganizationsListStore().load({
		                         callback : function() {
		                        	 var index = this.find('id', record.get('id'));
		                        	 var updatedRecord= this.getAt(index);
		                        	 if( me.getOrganizationsList())
		                        		 me.getOrganizationsList().getSelectionModel().select(index);
		                        	 
		                         		me.getOrganizationInfo().update(updatedRecord);
		                             window.close();
		                             Ext.Msg.alert("Organization Updated", "Organization information was succesfully updated.");
		                         }
		                     });
//                		 },this);
//                	 });
                 });
             } else {
                 Ext.Msg.alert("Error", "The form contains validation errors and cannot be saved.");
             }
         },


         saveNewOrganization: function(btn, eventObj) {
             var window = btn.up('window');
             var form = window.down('form').getForm();
             var me = this;
             if (form.isValid()) {
                 var values = form.getValues();
//                 var organizationName = values.organizationname;
//                 OrganizationManager.checkOrganizationname(organizationName, function(valid) {
//                     if (valid) {
                     OrganizationManager.saveOrganization(values,false, function(organization) {
                    	 SecurityRealmManager.generatePropertiesForRealm(function(securityRealmConfig){
                        	 OrganizationManager.addRealmToOrganization(securityRealmConfig,organization.get('id'),function(){
                        		 me.getOrganizationsListStore().load({
                                     callback : function() {
                                         var index = this.find('id', organization.get('id'));
                                         me.getOrganizationsList().getSelectionModel().select(index);
                                         window.close();
                                         Ext.Msg.alert("Organization Created", "New organization was succesfully added.");
                                     }
                                 });
                        	 });
                    	 });
                             
                         });
//                     } else {
//                         Functions.errorMsg("A organization with same organizationname already exists.", "Duplicate Organization");
//                     }
//                 });
                         } else {
                 Ext.Msg.alert("Error", "The form contains validation errors and cannot be saved.");
             }
         },
         editOrganization: function(btn) {
             var currentRecord = this.currentRecord();
             if (currentRecord) {
                 var editWindow = Ext.create('Security.view.organization.AddEditOrganization', {
                     title:'Edit Organization',
                     id:'addEditOrganization',
                     mode:'edit'
                 });
                 editWindow.down('addorganizationform').loadRecord(currentRecord);
                 //editWindow.down('realconfigform').updatedRealmConfiguration(currentRecord.get('securityRealms'));
                 editWindow.show(btn.getEl());
             }
         },


         currentRecord:function() {
             return this.getOrganizationInfo().currentRecord;
         },

         deleteOrganization: function() {
             var organizationList = this.getOrganizationsList();
             var selectionModel = organizationList.getSelectionModel();
             var selection = selectionModel.getSelection();
             if (selection.length >= 0) {
                 var toBeDeleted = selection[0];
                 var name = toBeDeleted.get('canonicalName');
                 Ext.Msg.confirm('Delete Organization', "Are you sure you want to delete the organization '" + name + "' ?",
                     function(btn) {
                         if (btn == 'yes') {
                             OrganizationManager.deleteOrganizationRecord(toBeDeleted.get('id'), function() {
                                 selectionModel.deselectAll();
                                 selectionModel.select(0);
                                 Ext.Msg.alert('Organization Deleted', 'Organization "' + name + '" Deleted Successfully.');
                             });

                         }
                     });
             }
         },
         
         emailOrganization: function() {
             var organizationList = this.getOrganizationsList();
             var selectionModel = organizationList.getSelectionModel();
             var selection = selectionModel.getSelection();
             if (selection.length >= 0) {
                 var record = selection[0];
               
                 var name = record.get('name');
                 
                 var contact = record.get('contact');
                 
                 Ext.Msg.confirm('Send Email', "Are you sure you want to send email to  organization '" + name + "' ?",
                     function(btn) {
                         if (btn == 'yes') {
                         	 Security.loadingWindow = Ext.widget('progresswindow', {
     		                        text: 'Sending Email...'
     		                    });
                         	PasswordManager.sendResetPasswordEmail(contact.emailAddress, function() {
     						Security.removeLoadingWindow(function() {

                             });
     					}, this);
                             
                         }
                     });
             }
         },

         loadOrganizationStore: function (selectedOrganizationId) {
             Ext.log('loadOrganizationStore ' + selectedOrganizationId);
             this.getOrganizationsListStore().load({
                 scope: this,
                 callback: function(records, operation, success) {
                     if (success !== false) {
                         Ext.log('Organization store loaded');
                         var organizationIndex = 0;
                         if (selectedOrganizationId) {
                             organizationIndex = this.getOrganizationsListStore().findExact('id', Ext.Number.from(selectedOrganizationId));
                         }
                         this.selectOrganizationRecord(organizationIndex > 0 ? organizationIndex : 0);
                     } 
//                     else {
//                         window.location = 'login';
//                     }
                 }
             });

         },

         /**
          * Selects a Organization instance by record instance or index.
          * @param {Security.model.Organization/Number} record An Organization instance or its index
          */
         selectOrganizationRecord: function(record) {
             var selectionModel = this.getOrganizationsList().getSelectionModel();
             if (!selectionModel.isSelected(record)) {
                 selectionModel.select(record);
             }
         },

         onOrganizationSelectionChange: function(selectionModel, selected) {
             var grid = this.getOrganizationsList();
             var manageOrganizationActions = grid.up('container').down('#manageOrganizationActions');
             if (selected.length > 0) {
             	var selection=selected[0];
             	
                 manageOrganizationActions.down('#editorganization').setDisabled(selected.length === 0);
                 manageOrganizationActions.down('#deleteorganization').setDisabled(selected.length === 0);
            	
                 var selectedOrganizationId = selected[0].getId();
                 this.getOrganizationInfo().update(selected[0]);
                 this.application.fireEvent('changeurlpath', [selectedOrganizationId]);
             }
             else
         	{
            	 manageOrganizationActions.down('#editorganization').setDisabled(true);
            	 manageOrganizationActions.down('#deleteorganization').setDisabled(true);
         	}
         },
         
         searchOrganization: function(field, newValue, oldValue) {
             var store = this.getOrganizationsList().store;
             store.suspendEvents();
             store.clearFilter();
             store.resumeEvents();
             store.filter({
                 property: 'canonicalName',
                 anyMatch: true,
                 value   : newValue
             });
             var filterRecords = store.getRange();
             if (filterRecords.length > 0)
                 this.selectOrganizationRecord(0);
         },

         definePasswordType: function() {
             Ext.apply(Ext.form.VTypes, {
                 password : function(val, field) {
                     if (field.initialPassField) {
                         var pwd = Ext.getCmp(field.initialPassField);
                         return (val == pwd.getValue());
                     }
                     return true;
                 },
                 passwordText: 'The password and confirmation do not match!'
             });
         },
         
         showOrganizationDetails : function(view, cell, cellIndex, record, row, rowIndex, e){
         	if(e){
   	         var linkClicked = (e.target.tagName == 'A');
   	         if (linkClicked) {   
   	                 location.href='#!/OrganizationConfig/'+record.get('id')+'/?tenant='+window.location.hash;
   	             e.stopEvent();
   	             return false;
   	         }
       	}    	
       },
       
       handleTreeOptions:function (treeView, record, item, index) {

           var organizationConfiguration = this.getOrganizationConfiguration();
           var organizationRecord = organizationConfiguration.getOrganizationRecord();
           if (record) {
        	   var organizationTenant = organizationRecord.get('tenant');
        	   var organizationUrl = location.hash;
           		var newUrl = "#!/OrganizationConfig/"+organizationRecord.get('id')+"/?tenant=#!/TenantConfig/"+organizationTenant.id+"/ManageOrganization";
        	   
        	   
        	   UserManager.customPasswordType(organizationRecord.get('tenant'),function(){
        		   
	               treeView.getSelectionModel().select(record);
	
	               if (record.childNodes.length == 0) {
	                   var page = Ext.widget('organizationoverview');
	                   switch (record.internalId) {
	                       case 'overview' :
	                           break;
	                       case 'manageSubOrganizations' :
	                    	   newUrl = "#!/OrganizationConfig/"+organizationRecord.get('id')+"/ManageSubOrganization/?tenant=#!/TenantConfig/"+organizationTenant.id+"/ManageOrganization";
	                           break;
	                       case 'assignRoles' :
	                    	   newUrl = "#!/OrganizationConfig/"+organizationRecord.get('id')+"/AssignRoles/?tenant=#!/TenantConfig/"+organizationTenant.id+"/ManageOrganization";
	                           break;
	                       case 'manageUsers' :
	                    	   newUrl = "#!/OrganizationConfig/"+organizationRecord.get('id')+"/ManageUsers/?tenant=#!/TenantConfig/"+organizationTenant.id+"/ManageOrganization";
	                           break;
	                       default :
	                           page = Ext.widget('organizationoverview');
	
	                   }
	                   
	                   if(organizationUrl!=newUrl)
	                   	window.location = newUrl;
	                   
//	                   OrganizationManager.loadOrganizationPages(page);
	               }
               
        	   });
           }
       },
       
       showCreateOrganizationRolePage:function (grid, page) {
	       	var redirectPage = Ext.widget({xtype:page.xtype});
	       	redirectPage.setLoadingParams(page.getLoadingParams());
           var ctr = Ext.getCmp('Home-page-container');
           ctr.removeAll();
           ctr.add(Ext.widget({xtype:'createorganizationrole',redirectPage:redirectPage }));
       },
       
       showCreateOrganizationUserPage:function (grid, page) {    	   
       	window.location='#!/Organization/CreateUser?redirect='+location.hash;
       },
           
       showRoleSummaryForOrganization:function (selectionModel, selected) {
           var me = this;
           if (selected.length > 0) 
               me.getManageOrganizationRoles().update(selected[0]);
           else
        	   me.getManageOrganizationRoles().reset();
       },
       
       showUserSummaryForOrganization:function (selectionModel, selected) {
           var me = this;
           if (selected.length > 0){
               me.getManageOrganizationUsers().update(selected[0]);
           }
           else
        	   me.getManageOrganizationUsers().reset();
       },
       
       deleteOrganizationRoleRecord:function (grid) {
           var organizationConfiguration = this.getOrganizationConfiguration();
           var organizationRecord = organizationConfiguration.getOrganizationRecord();
           var selection = grid.getSelectionModel().getSelection();
           if (selection.length > 0) {
               Ext.MessageBox.show({
                   title:'Delete Organization Role?',
                   msg:'Are you sure you want to delete this Role ?',
                   buttons:Ext.MessageBox.YESNO,
                   fn:function (btn) {
                       if (btn == 'yes') {
                           grid.down('#deleteItem').setDisabled(true);

                           RoleManager.removeRoleFromOrganization(organizationRecord.get('id'), selection[0].get('id'), function () {
                               grid.getStore().load({
                                   callback:function () {
                                       var record =
                                           grid.getStore().getAt(0);
                                       if (record) {
                                           grid.getSelectionModel().select(record);
                                           grid.updateLayout();
                                       }
                                   }
                               });
                           }, this);


                       }
                   }
               });
           }
           else
               Functions.errorMsg("Select a role to delete.", 'Error', null, 'ERROR');
       },
       
       deleteOrganizationUserRecord:function (grid) {
           var organizationConfiguration = this.getOrganizationConfiguration();
           var organizationRecord = organizationConfiguration.getOrganizationRecord();
           var flag=1;
		   var selections = grid.getSelectionModel().getSelection();
			
			Ext.each(selections,function(record){
				if(record.get('username')=='system' || record.get('username')=='admin' )
					{
					flag=0;
					return false;
					}
			});			
	       	
	       	if(flag==0)
	       		Functions.errorMsg("Cannot delete default User", 'Invalid operation ', null, 'WARN');
	       	else
	       	if(selections.length>0){
           
               Ext.MessageBox.show({
                   title:'Delete Organization User?',
                   msg:'Are you sure you want to delete selected users ?',
                   buttons:Ext.MessageBox.YESNO,
                   fn:function (btn) {
                       if (btn == 'yes') {
                           grid.down('#deleteItem').setDisabled(true);

                           UserManager.deleteUser([selections[0].get('id')], function (status,response) {
                           if(status){
                               grid.getStore().load({
                                   callback:function (records, operation, success) {
								   if(records.length == 0 && operation.page > 1)
								{
								var record =
                                        grid.getStore().loadPage((operation.page) - 1)
								}
                                    else
									{
                                       var record =
                                           grid.getStore().getAt(0);
                                       if (record) {
                                           grid.getSelectionModel().select(record);
                                           grid.updateLayout();
                                       }
                                   }
								   }
                               });
                               }
                               else
                               Functions.errorMsg(response.error, 'Invalid operation ', null, 'WARN');
                           }, this);


                       }
                   }
               });
			   }
           else
               Functions.errorMsg("Select a user to delete.", 'Error', null, 'ERROR');
       },
       
       getLdapUserAttributes: function(btn, eventObj) {
    	   var form = btn.up('form');
    	   var formBasic = form.getForm();
    	   var values = formBasic.getValues();
    	   var userDetailCont = form.down('#userDetailCont');
           var username = values.username;
           if(username && username!=undefined){
        	   
        	   Security.loadingWindow = Ext.widget('progresswindow', {text: 'Loading User Details...'});
        	   
	    	   var organizationConfiguration = this.getOrganizationConfiguration();
	           var organizationRecord = organizationConfiguration.getOrganizationRecord();
	           
	           
	           var securityRealm = organizationRecord.get('securityRealms');
	           
        	   if((securityRealm.length==0 || securityRealm[0].get('enabled')==false))
        		   Security.removeLoadingWindow(function(){
        			   Functions.errorMsg("LDAP not configured or enabled.", 'Error', null, 'ERROR');
	        	   });        		   
        	   else
        		   {
        		   	 UserManager.getLdapUserAttributes(username, organizationRecord.get('id'), function(user) {
        		   		 user['username'] = username;
        		   		 var userRecord = Ext.create('Security.model.User',user);
        		   		 userDetailCont.show();
        		   		 formBasic.reset();
        		   		 form.loadRecord(userRecord);
        		   		 
        		   		 Ext.each(userDetailCont.getLayout().getLayoutItems(),function(item){
        		   			 console.log(item);
        		   			 if(item.xtype=='textfield' || item.xtype=='textarea' || item.xtype=='checkbox' || item.xtype=='combobox'){
	        		   			 if(item.getValue().length>0)
	        		   				 item.setReadOnly(true);
        		   			 }
        		   		 });
        		   		 
        		   		Security.removeLoadingWindow(function(){
        	        		   return false;
        	        	   });  
        	           
        		   	 
        		   	 }, this);
        		   }
           }
           else
        	   Functions.errorMsg("Invaid form data.", 'Error', null, 'ERROR');   
          
       },
       
       
       addUserToOrganization: function(btn, eventObj) {
    	   var createOrganizationUser = btn.up('createorganizationuser');
    	   
    	   var form = btn.up('form');
    	   var formBasic = form.getForm();
           var organizationConfiguration = this.getOrganizationConfiguration();
           var organizationRecord = organizationConfiguration.getOrganizationRecord();
           var typeOfUser = createOrganizationUser.down('#typeOfUser').getValue();
           
           if (formBasic.isValid() && typeOfUser!=null) {
        	   
               var values = formBasic.getValues();
               var userName = values.username;
               var securityRealm = organizationRecord.get('securityRealms');
               
               if(typeOfUser=='ldap'){
            	   if((securityRealm.length==0 || securityRealm[0].get('enabled')==false))
            		   Functions.errorMsg("LDAP not configured or enabled.", 'Error', null, 'ERROR');
            	   else
            		   {
	            		   UserManager.createLdapUser(values, organizationRecord.get('id'), function(user) {
	                		   Functions.errorMsg("User successfully added to organization.", 'Success', null, 'INFO');
	                		   formBasic.reset();
	                	   }, this);
            		   }
               }   
               else
               if(typeOfUser=='certificate'){            	
			                	   UserManager.createCertificateUser(values,organizationRecord.get('canonicalName'), organizationRecord.get('id'), function(user) {
			                		   Functions.errorMsg("User successfully added to organization.", 'Success', null, 'INFO');
			                		   formBasic.reset();
			                	   }, this);
               }     
               else
            	   {
//		               UserManager.checkUsername(userName, function(valid) {
//		                   if (valid) {
			                	   UserManager.createUserForOrganization(values, organizationRecord.get('id'), function(user) {
			                		   Functions.errorMsg("User successfully added to organization.", 'Success', null, 'INFO');
			                		   formBasic.reset();
			                	   }, this);
//		                   } else {
//		                       Functions.errorMsg("A user with same name already exists.", "Duplicate User");
//		                   }
//		               });
            	   }
           } else {
        	   Functions.errorMsg("Invaid form data.", 'Error', null, 'ERROR');
           }
       },
       
//	   editUser: function(grid,btn) {
//	        var organizationConfiguration = this.getOrganizationConfiguration();
//	        var organizationRecord = organizationConfiguration.getOrganizationRecord();
//	        var selection = grid.getSelectionModel().getSelection();
//			var currentRecord = selection[0];
//	        if (selection.length > 0) {
//			  var contactInfo=Ext.create('Security.model.Contact',currentRecord.get('contact'));
//	          var editWindow = Ext.create('Security.view.user.EditUser', {
//				  mode:btn.mode,
//				  usersList:grid,
//				  userId:currentRecord.get('id'),
//				  contactId:currentRecord.get('contact').id,
//				  userInfo:currentRecord,
//				  contactInfo:contactInfo
//	         });
//	         editWindow.show();
//	            
//	         //UserManager.getCustomFieldsFor(currentRecord.get('id'), 'user', function(records) {
//	         //editWindow.bindCustomFields(records);
//	        // }, this);
//	       }
//    },
//    
       
       editUser: function(btn) {
    	   
    	    var currentRecord = null;
    	    var usersList = null;
	        var organizationConfiguration = this.getOrganizationConfiguration();
	        var accountSettingsPage = btn.up('accountsettings');
	        
	        if(organizationConfiguration){
		        var organizationRecord = organizationConfiguration.getOrganizationRecord();
		        usersList = organizationConfiguration.down('#organizationUserGrid');
		        var selection = usersList.getSelectionModel().getSelection();
				currentRecord = selection[0];
	        }    	   
    	   
    	   if(accountSettingsPage)
    		   currentRecord = accountSettingsPage.currentRecord;
           
           if (currentRecord) {
        	   
           	var contactInfo=Ext.create('Security.model.Contact',currentRecord.get('contact'));
               var editWindow = Ext.create('Security.view.user.EditUser', {
               	   mode:accountSettingsPage.readOnly ? 'readOnly' : 'editable',
                   userId:currentRecord.get('id'),
                   usersList:usersList,
                   contactId:currentRecord.get('contact').id,
                   userInfo:currentRecord,
                   contactInfo:contactInfo
               });
               editWindow.show();
               
//               UserManager.getCustomFieldsFor(currentRecord.get('id'), 'user', function(records) {
//           		editWindow.bindCustomFields(records);
//               }, this);
           }
       },
    updateCredentials: function(btn) {
    	var userActionMenu = btn.up('useractionmenu');
        var currentRecord = userActionMenu.getUserRecord();
        if (currentRecord) {
            var editWindow = Ext.create('Security.view.user.EditUserCredentials', {
                userId:currentRecord.get('id'),
                userInfo:currentRecord.data
            });
            editWindow.show();
        }
    },
	
	updateUser: function(btn, eventObj) {
        var window = btn.up('window');
        var index = window.userInfo.index;
        var form = window.down('form').getForm();
        var me = this;
        if (form.isValid()) {
            var values = form.getValues();
           // Security.loadingWindow = Ext.widget('progresswindow', {text: 'Loading User Details...'});
			
            UserManager.updateUser(values, window.userId, window.contactId, function(updatedUser) {		
                var userRecord=Ext.create('Security.model.User',updatedUser);
                if(window.mode=='readOnly'){
	                	me.getAccountSettings().update(userRecord,true,function(){
	                		
	                	});                	
                }
                else
                	{
					var grid = window.usersList;
					var index = grid.getStore().find('id',window.userId);
					if(index!=-1){
							
							grid.getStore().load();
							grid.getSelectionModel().select(userRecord);
							grid.getStore().insert(index,userRecord);
							 me.getManageOrganizationUsers().update(userRecord);
						}
					}
                    	
							window.close();							
							Ext.Msg.alert("User Updated", "User information was successfully updated.");
							Security.removeLoadingWindow();	
						
                    	
            });
			
        } else {
            Ext.Msg.alert("Error", "The form contains validation errors and cannot be saved.");
        }
    },

       
       handleOrganizationTree: function(view, cell, cellIndex, record, row, rowIndex, e) {
    	   
           var me=this;
           var linkClicked = (e.target.tagName == 'A');
           var clickedDataIndex =
               view.panel.headerCt.getHeaderAtIndex(cellIndex).dataIndex;
           
           if (linkClicked && clickedDataIndex == 'text' && rowIndex!=0) {
        	   e.stopEvent();
        	   
        	   OrganizationManager.getOrganizationById(record.get('id'), function(orgObj){
        		   var organization = Ext.create('Security.model.Organizations',orgObj);
        		   var idsuborg=record.data.id;
                   window.location = '#!/OrganizationConfig/'+idsuborg;
               
          /*             var childOrganizationConfigWindow = Ext.widget({
                           xtype:'childorganizationconfigwindow',
                           organization : organization
                       });*/
                       
        
                      /* childOrganizationConfigWindow.show(true,function(){
                           Security.loadingWindow = Ext.widget('progresswindow', {text: 'Loading Organization Detail...'});
                           childOrganizationConfigWindow.updateChildOrganizationDetail(organization,function(){
                               Security.removeLoadingWindow(function(){
                                   return false;
                               });                 
                           });   
                       });     */   
		               
        	   });
               
               
           }
           else
        	   {
        	   e.stopEvent();
        	   return false;
        	   }
       },
       
//       handleOrganizationTree : function(tree, record, item, index){
//    	   var manageOrganizationOrgs = this.getManageOrganizationOrgs();
//    	   
//    	   if(record && index!=0)
//	    	   manageOrganizationOrgs.updateChildOrganizationDetail(record.get('id'));
//    	   else
//    		   manageOrganizationOrgs.disableOrganizationDetail();
//    	   
//       },
       
       showCreateChildOrganizationWindow : function(btn, organizationTree){
    	   
    	   var selectedNode = organizationTree.getView().getChecked();

    	   if(selectedNode.length>0){
    		   var window = Ext.widget({
    			   xtype:'createchildorganization',
    			   parentOrg:selectedNode[0]
    		   });
    		   window.show(btn.getEl());
    	   }
    	   else
    		   Functions.errorMsg("No Organization Selected !", 'Warning', null, 'WARN');    	   
       },
       
       deleteChildOrganizationRecord : function(btn, organizationTree){
		   var rootNode =  organizationTree.getRootNode();		   
		   var selected = organizationTree.getView().getChecked();
    	   var manageOrganizationOrgs = this.getManageOrganizationOrgs();
    	   if(selected.length>0){    		   
			if(selected[0] == rootNode)
			{			
			 Functions.errorMsg("Root Organization cannot be deleted !", 'Warning', null, 'WARN');    	
			
			}
				else
				{    		  
					Ext.MessageBox.show({
					   title:'Delete Organization?',
					   msg:'Are you sure you want to delete this organization ?',
					   buttons:Ext.MessageBox.YESNO,
					   fn:function (btn) {
						   if (btn == 'yes') {
								   OrganizationManager.removeChildOrganization(selected[0].parentNode.get('id'), selected[0].get('id'),function(response){
									   manageOrganizationOrgs.update(manageOrganizationOrgs.orgRecord);
								   });
						   }
					   }
				   });
			   }
    	   }
    	   else
    		   Functions.errorMsg("No Organization Selected !", 'Warning', null, 'WARN');    	   
       },
       
       createChildOrganization : function(btn){
    	   var me=this;
    	   var window=btn.up('window');
    	   var formBasic=window.down('form').getForm();
    	   var organizationObj = formBasic.getValues();
    	   var manageOrganizationOrgs = Security.viewport.down('manageorganizationorgs');
    	   var organizationRecord = manageOrganizationOrgs.currentRecord;
    	   
           OrganizationManager.createOrganizationForTenant(organizationRecord.get('tenant'), organizationObj, function (organizationRecord) {
        	   OrganizationManager.addChildOrganization(window.parentOrg.get('id'), organizationRecord.id,function(response){
                   if (response) {
                	   window.close();
                       Functions.errorMsg("Child Organization Created.", 'Success', null, 'INFO');                       
                       var manageOrganizationOrgs = me.getManageOrganizationOrgs();
					   
					   manageOrganizationOrgs.update(manageOrganizationOrgs.orgRecord);
                   }
               });
           }, this);
       },
       
       handleOrganizationRoleSelection : function(selectionModel, selected) {
           var grid = this.getOrganizationRoles();
           if (selected.length > 0) 
        	   grid.down('#removeRole').setDisabled(selected.length === 0);
           else
        	   grid.down('#removeRole').setDisabled(true);
       },
       
       
       showAddOrganizationRolesWindow : function(grid, organization){
       	var me = this;
        var currentRecord = grid.organization;
        var addRoleWindow = null;
       	 var originalRecordIds=new Ext.util.HashMap();

            if (currentRecord && addRoleWindow==null) {
            	var organizationRolesStore = Ext.StoreManager.lookup('OrganizationRoles');
            	organizationRolesStore.getProxy().setExtraParam('organizationId',currentRecord.get('id'));
            	
            	organizationRolesStore.load({
            		callback:function(records){
            			organizationRolesStore.getProxy().setExtraParam({});
            			organizationRolesStore.each(function(rec){
                        	originalRecordIds.add(rec.get('id'),rec.get('canonicalName'));
                        });

            			var tenantRolesModel = Ext.create('Security.model.TenantRoles');
                        var availableRolesStore = Ext.create('Ext.data.Store',{
                        	model:'Security.model.TenantRoles',
                        	proxy:tenantRolesModel.proxy
                        });
                        
                        addRoleWindow = Ext.create('Security.view.common.AddRoleOrUserOrGroup', {
	                       	addAction:'addRolesToOrganization', 
	                       	title:'Available Roles',
                        	originalRecordIds:originalRecordIds,
                        	store:availableRolesStore,
                        	extraParam : {'id':currentRecord.get('tenant').id},
                        	organizationId: currentRecord.get('id'),
                        	//closeAction:'destroy'
                        });
                        addRoleWindow.show();
            			
            		}
            	});
                
            }
       },
       
       assignRoleToOrganization:function (btn) {
    	   
    	   var me=this;
    	   var window = btn.up('window');
    	   var rolesList = window.down('grid');
    	   var selection = rolesList.getSelectionModel().getSelection();
    	   if(selection.length>0){
	
		               OrganizationManager.addRolesToOrganization(window.organizationId, selection, function (response) {
		                   if (response) {
		                	   window.close();
		                       Functions.errorMsg("Role successfully assigned to organization.", 'Success', null, 'INFO');
		                       me.getOrganizationRoles().getStore().load();
		                   }
		                   else
		                	   Functions.errorMsg("Failed to assign role to organization.", "Failure", null, 'ERROR');   
		               });
    	   }
    	   else
       		Functions.errorMsg("No role selected to assign.", "Selection Error");   
       },
       
       removeOrganizationRole : function(grid){
    	   var me=this;
    	   var assignOrganizationRoles = this.getAssignOrganizationRoles();
           var organization = assignOrganizationRoles.down('organizationroles').organization;
           
    	   var selModel = grid.getSelectionModel();
    	   var selected = selModel.getSelection();
    	   if(selected.length>0){    		   
    		   Ext.MessageBox.show({
                   title:'Delete Roles?',
                   msg:'Are you sure you want to delete selected Role(s) ?',
                   buttons:Ext.MessageBox.YESNO,
                   fn:function (btn) {
                       if (btn == 'yes') {
							   OrganizationManager.removeRolesFromOrganization(organization.get('id'), selected,function(response){
								   me.getOrganizationRoles().getStore().load();	   
							   });
                       }
                   }
    		   });
    	   }
    	   else
    		   Functions.errorMsg("No Role Selected !", 'Warning', null, 'WARN');     
    	   
       },
       
       updateOrganizationConfigRecord : function(orgId){

    	   var organizationConfiguration = this.getOrganizationConfiguration();
           OrganizationManager.getOrganizationById(orgId,function(orgObj){
               var orgRecord = Ext.create('Security.model.Organizations',orgObj);
               organizationConfiguration.setOrganizationRecord(orgRecord);
           });
       },
    	  
       
     });
