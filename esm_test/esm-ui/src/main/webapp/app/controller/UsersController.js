Ext.define('Security.controller.UsersController', {
    extend: 'Ext.app.Controller',

    stores: [
        'UsersList',
        'UserAssignedRolesList',
        'UserGroupsList'
    ],
    models: [
        'User',
        'Contact',
        'Language',
        'TimeZone',
        'UserRoleAssignment'
    ],
    views: [
        'user.UsersList',
        'user.UserInfo',
        'user.UserProfileInfo',
        'user.ManageUsers',
        'user.UserProfile',  
        'user.UserProfileProps',
        'user.AddUser',
        'user.EditUser',
        'user.EditUserCredentials',
        'user.UserCredentialsForm',
        'user.UserRoleAssignments',
        'user.UserGroupAssignments',
        'user.UsersUploadWindow',
        'user.BatchUsersList',
        'common.EditPermissions',
        'common.PermissionGroups',
        'common.AddRoleOrUserOrGroup',
		'user.AccountSettings',
        'user.AccountSettingsProp',
        'user.ProfileSettings',
        'user.EmailListView',
        'user.UserDetailPane',
        'user.UserDetailPaneHeader',
        'user.UserDetailPaneTabPanel',
        'user.UserActionMenu'
    ],

    refs:[{
        ref:'userInfo',
        selector:'userinfo'
    }, {
        ref:'userProfileInfo',
        selector:'userprofileinfo'
    },{
        ref:'accountSettings',
        selector:'accountsettings'
    },{
        ref: 'manageUsers',
        selector: 'manageusers'
    },{
        ref: 'userProfile',
        selector: 'userprofile'
    }, {
        ref:'userProps',
        selector:'userinfo userprops'
    },{
        ref:'userProfileProps',
        selector:'userprofileinfo userprofileprops'
    }, {
        ref:'usersList',
        selector:'userslist'
    }, {
        ref:'userRoles',
        selector:'userroleassignments'
    }, {
        ref:'userGroups',
        selector:'usergroupassignments'
    },{
        ref:'userRolesList',
        selector:'userroles'
    },{
        ref:'userGroupsList',
        selector:'usergroups'
    },{
        ref:'manageOrganizationUsers',
        selector:'manageorganizationusers'
    },{
        ref:'userDetailPaneTabPanel',
        selector:'userdetailpanetabpanel'
    },{
        ref:'organizationConfiguration',
        selector:'organizationconfiguration'
    }],

    init: function () {
        this.definePasswordType();
        this.defineUserFileUploadType();
        this.control({
            'manageusers': {
                statechange: this.onStateChange
            },
            'manageusers userslist': {
                selectionchange: this.onUserSelectionChange
            },
            'manageusers #newuser': {
                click: this.newUser
            },
            'manageusers #bulkusers': {
                click: this.uploadBulkUsers
            },
            'manageusers #edituser': {
                click: this.editUser
            },
            'manageusers #updateuser': {
                click: this.updateCredentials
            // TODO: Make this configurable in 1.5
//            },
//            'manageusers #deleteuser': {
//                click: this.deleteUser
            },
            'manageusers #emailuser': {
                click: this.emailUser
            },
            'manageusers userslist textfield[action=searchUser]': {
                change: this.searchUser
            },
            'manageusers userslist tool[action=refreshUsersList]': {
                click: this.loadUserStore
            },
            'userroles tool[action=newuserassignment]': {
                click: this.showRoleToUserWindow
            },
            'addroleoruserorgroup button[action=addRolesToUser]':{
            	click : this.addRolesToUser
            },
            'userroles tool[action=removeuserassignment]': {
                click: this.removeUserAssignment
            },
            'usergroups tool[action=newgroupassignment]': {
                click: this.showGroupToUserWindow
            },
            'addroleoruserorgroup button[action=addGroupsToUser]':{
            	click : this.addGroupsToUser
            },
            'usergroups tool[action=removegroupassignment]': {
                click: this.removeGroupAssignment
            },
            'adduser button[action=saveNewUser]': {
                click: this.saveNewUser
            },    
            'usercredentialsform button[action=updateCred]': {
                click: this.saveNewCredentials
            },
            'profilesettings button[action=updateCred]': {
                click: this.saveNewCredentials
            },
			'profilesettings button[action=updateCertificate]': {
                click: this.saveCertificate
            },
            'batchuserslist #importUsers':{
            	click : this.importUsers
            }
        });
    },

    onStateChange: function(path) {
        if (path.length > 0) {
            var userId = path[0];
            if (Ext.isNumeric(userId)) {
                var selected = this.getUsersList().getSelectionModel().getSelection();
                if (selected.length > 0 && selected[0].getId() == userId) {
                    return; //ignore - user is already selected
                }

                var userIndex = this.getUsersListStore().findExact('id', Ext.Number.from(userId));
                if (userIndex != -1) {
                    this.selectUserRecord(userIndex);
                    return;
                } else { //user is not yet loaded
                    this.loadUserStore(userId);
                }
            } else {
                this.application.fireEvent('changeurlpath', []);
            }
        } else {
            this.loadUserStore();
        }
    },

    newUser: function() {
        var addUserWindow = Ext.create('Security.view.user.AddUser', {
            title: "Create User"
        });
        addUserWindow.show();
    },
    
    uploadBulkUsers: function() {
        var usersUploadWindow = Ext.create('Security.view.user.UsersUploadWindow');
        usersUploadWindow.show();
    },
    
    
    showRoleToUserWindow : function(){
    	
    	var me = this;
    	
    	var currentRecord = this.currentRecord();
    	
    	 var originalRecordIds=new Ext.util.HashMap();

         if (currentRecord) {
             var rolesStore = this.getUserRoles().store;
             
             rolesStore.each(function(rec){
             	originalRecordIds.add(rec.get('id'),rec.get('canonicalName'));
             });

             var availableRolesStore = Ext.create('Security.store.TenantRoles');
             
             var organizationConfiguration = this.getOrganizationConfiguration();
             var organizationRecord = organizationConfiguration.getOrganizationRecord();
             
             availableRolesStore.getProxy().setExtraParam('id',organizationRecord.get('tenant').id);
//             availableRolesStore.load({
//                 callback: function() {
//                     if (rolesStore.data !== undefined) {
//                         var selectedRoles = rolesStore.getRange();
//                         Ext.each(selectedRoles, function(role) {
//                             var index = availableRolesStore.find('id', role.get('id'));
//                             if(index!=-1)
//                            	 availableRolesStore.removeAt(index);
//                         });
//                     }
//                 }
//             });
             
             var addRoleWindow = Ext.create('Security.view.common.AddRoleOrUserOrGroup', {
            	addAction:'addRolesToUser', 
            	title:'Available Roles',
             	originalRecordIds:originalRecordIds,
             	store:availableRolesStore                 
             });
             addRoleWindow.show();

             
         }
    },
    
    addRolesToUser : function(btn){		
    	var me=this;
    	var currentRecord = this.currentRecord();		
    	var addWindow=btn.up('window');
    	var rolesList = addWindow.down('grid');
    	var selection = rolesList.getSelectionModel().getSelection();		
    	var userDetailPaneTabPanel = this.getUserDetailPaneTabPanel();
    	if(selection.length>0){
    		UserManager.addRolesToUser(currentRecord, selection, function(status) {
				
                if (status) {                    
                        		UserManager.getUserAuthType(currentRecord.get('id'),function(authType){
		                        	me.getUserRolesList().update(currentRecord,function(){
		                        		userDetailPaneTabPanel.update(currentRecord,authType,false);
		                        		Ext.Msg.alert("Role Assigned", "Role succesfully assigned to user.");
		                                addWindow.close();
		                        	},this);
                        		});                      	
                                          
                } else {
                    Functions.errorMsg("Could not connect to backend to assign user new roles.", "Role Assignment Failed");
                }
            });
    		
    	}
    	else
    		Functions.errorMsg("No role selected to add.", "Selection Error");   
    	
    },

    removeUserAssignment: function() {
        var selectionModel = this.getUserRoles().getSelectionModel();
        var store = this.getUserRoles().store;
        var selection = selectionModel.getSelection();
        var userDetailPaneTabPanel = this.getUserDetailPaneTabPanel();
        var currentRecord = this.currentRecord();
        var me = this;
        var flag=1;
        if (selection.length > 0) {
        	
        	Ext.each(selection,function(role){
        		if(role.get('roleType')=='TRANS')
        			flag=0;
        	});
        	
        	if(flag==0)
        		Functions.errorMsg("Cannot delete a transitive role.", "Role Deletion Failed");
        	else
        	if(flag==1)
	        	 Ext.Msg.confirm(
	        			 "Unassign Role?",
	                     'Are you sure you want to unassign roles.',
	                     function(btn) {
	                         if (btn == 'yes')                        	 
					            UserManager.removeRolesFromUser(currentRecord, selection, function(status) {
					                if (status) {				                   
					                        		UserManager.getUserAuthType(currentRecord.get('id'),function(authType){
							                        	me.getUserRolesList().update(currentRecord,function(){
							                        		userDetailPaneTabPanel.update(currentRecord,authType,false);
	//						                        		me.getUserRoles().getStore().remove(selection);
							                                Ext.Msg.alert("Role Unassigned", "Role was succesfully unassigned from user.");                        	
							                        	},this);
					                        		});
					                        	
					                } else {
					                    Functions.errorMsg("Could not connect to backend to remove a role for user.", "Role Deletion Failed");
					                }
					            },this);
	                     }
	                 );
        } else {
            Ext.Msg.alert("Role Deletion", "You must first select one or more roles to delete.");
        }

    },

    showGroupToUserWindow : function(){
    	
    	var me = this;
    	
    	var currentRecord = this.currentRecord();
    	
    	 var originalRecordIds=new Ext.util.HashMap();

         if (currentRecord) {
             var groupsStore = this.getUserGroups().store;
             
             groupsStore.each(function(rec){
             	originalRecordIds.add(rec.get('id'),rec.get('canonicalName'));
             });

             var availableGroupsStore = Ext.create('Security.store.TenantGroups');
             
             var organizationConfiguration = this.getOrganizationConfiguration();
             var organizationRecord = organizationConfiguration.getOrganizationRecord();
             
             availableGroupsStore.getProxy().setExtraParam('id',organizationRecord.get('tenant').id);
             
//             availableGroupsStore.load({
//                 callback: function() {
//                     if (groupsStore.data !== undefined) {
//                         var selectedGroups = groupsStore.getRange();
//                         Ext.each(selectedGroups, function(group) {
//                             var index = availableGroupsStore.find('id', group.get('id'));
//                             if(index!=-1)
//                            	 availableGroupsStore.removeAt(index);
//                         });
//                     }
//                 }
//             });
             
             var addGroupWindow = Ext.create('Security.view.common.AddRoleOrUserOrGroup', {
            	title:'Available Groups',
            	addAction:'addGroupsToUser',
             	originalRecordIds:originalRecordIds,
             	store:availableGroupsStore                 
             });
             addGroupWindow.show();

             
         }
    },
    
    addGroupsToUser : function(btn){
    	var me=this;
    	var currentRecord = this.currentRecord();
    	var addWindow=btn.up('window');
    	var groupsList = addWindow.down('grid');
    	var selection = groupsList.getSelectionModel().getSelection();
    	var userDetailPaneTabPanel = this.getUserDetailPaneTabPanel();
    	if(selection.length>0){

    		UserManager.addGroupsToUser(currentRecord, selection, function(status) {
                if (status) {                   
                        		UserManager.getUserAuthType(currentRecord.get('id'),function(authType){
		                        	me.getUserGroupsList().update(currentRecord,function(){
	                        			userDetailPaneTabPanel.update(currentRecord,authType,false);
		                        		Ext.Msg.alert("Group Added", "Group succesfully added to user.");
		                                addWindow.close();
		                        	},this);
                        		});
                      
                } else {
                    Functions.errorMsg("Could not connect to backend to add new group.", "Group Addition Failed");
                }
            });
    		
    	}
    	else
    		Functions.errorMsg("No group selected to add.", "Selection Error");   
    	
    },

    removeGroupAssignment: function() {
        var selectionModel = this.getUserGroups().getSelectionModel();
        var store = this.getUserGroups().store;
        var selection = selectionModel.getSelection();
        var userDetailPaneTabPanel = this.getUserDetailPaneTabPanel();
        var currentRecord = this.currentRecord();
        var me = this;
        
        if (selection.length > 0) {
        	 Ext.Msg.confirm(
        			 "Remove Group?",
                     'Are you sure you want to remove group.',
                     function(btn) {
                         if (btn == 'yes')                        	 
				            UserManager.removeGroupsFromUser(currentRecord, selection, function(status) {
				                if (status) {			                   		                        	
				                        		UserManager.getUserAuthType(currentRecord.get('id'),function(authType){
				                        			me.getUserGroupsList().update(currentRecord,function(){
				                        				userDetailPaneTabPanel.update(currentRecord,authType,false);
//						                        		me.getUserGroups().getStore().remove(selection);
						                                Ext.Msg.alert("Group Removed", "Group was succesfully removed from user.");                        	
						                        	},this);
				                        		});
				                        
				                } else {
				                    Functions.errorMsg("Could not connect to backend to remove a group for user.", "Group Deletion Failed");
				                }
				            },this);
                     }
                 );
        } else {
            Ext.Msg.alert("Group Deletion", "You must first select one or more group to delete.");
        }

    },
    
    updateUser: function(btn, eventObj) {
        var window = btn.up('window');
        var index = window.userInfo.index;
        var form = window.down('form').getForm();
        var me = this;
        if (form.isValid()) {
            var values = form.getValues();
            var loadingWindow = Ext.widget('progresswindow', {text: 'Updating User Information...'});
            UserManager.updateUser(values, window.userId, window.contactId, function(updatedUser) {
                
                var userRecord=Ext.create('Security.model.User',updatedUser);
                
                if(window.mode=='editProfile')
                	me.getAccountSettings().update(userRecord);
                else
                	me.getUserInfo().update(userRecord);

                
                me.getUsersListStore().load({
                    callback : function() {
                    	
                    	if(window.mode!='editProfile')
                    		me.getUsersList().getSelectionModel().select(index);
                    	if(me.getUserInfo())
                    		me.getUserInfo().currentRecord=userRecord;
                    	loadingWindow.destroy();
                    	window.close();
                        Ext.Msg.alert("User Updated", "User information was succesfully updated.");
                    }
                });
            });
        } else {
            Ext.Msg.alert("Error", "The form contains validation errors and cannot be saved.");
        }
    },

    saveNewCredentials : function(btn, eventObj) {
        var formPanel = btn.up('form');
        var form = formPanel.getForm();

        var me = this;
        if (form.isValid()) {
            var values = form.getValues();
            Security.loadingWindow = Ext.widget('progresswindow', {
                 text: 'Updating User Credentials...'
             });
            UserManager.updateUsernamePasswordAuthenticationToken(formPanel.userInfo, values.username, values.password, function(status,error) {
            	Security.removeLoadingWindow(function() {
            		if (status) {
                    	form.reset();
                        Ext.Msg.alert("Credentials Updated", "User credentials updated successfully.");
                        if(formPanel.up('editusercredentials'))
                        	formPanel.up('editusercredentials').close();
                    }else
                    	Functions.errorMsg(error,
                        "Failed To Update User Credentials");
                });                
            },this);
        } else {
            Ext.Msg.alert("Error", "The form contains validation errors and cannot be saved.");
        }
    },
	
	saveCertificate : function(btn, eventObj) {
        var formPanel = btn.up('form');
        var form = formPanel.getForm();

        var me = this;
        if (form.isValid()) {
		
			var organizationConfiguration = this.getOrganizationConfiguration();
            var organizationRecord = organizationConfiguration.getOrganizationRecord();
			 
            var values = form.getValues();
            Security.loadingWindow = Ext.widget('progresswindow', {
                 text: 'Updating User Certificate...'
             });
            UserManager.updateCertificateTokenForUser(formPanel.userInfo, organizationRecord.get('tenant').canonicalName, organizationRecord.get('canonicalName'), values.certificate, function(status,error) {
            	Security.removeLoadingWindow(function() {
            		if (status) {
                    	
                        Ext.Msg.alert("Credentials Updated", "User certificate updated successfully.");
                    }else
                    	Functions.errorMsg(error,
                        "Failed To Update User Certificate");
                });                
            },this);
        } else {
            Ext.Msg.alert("Error", "The form contains validation errors and cannot be saved.");
        }
    },

    saveNewUser: function(btn, eventObj) {
        var window = btn.up('window');
        var form = window.down('form').getForm();
        var me = this;
        if (form.isValid()) {
            var values = form.getValues();
            var userName = values.username;
            UserManager.checkUsername(userName, function(valid) {
                if (valid) {
                    UserManager.createUser(values, function(user) {
                        me.getUsersListStore().load({
                            callback : function() {
                                var index = this.find('id', user.id);
                                me.getUsersList().getSelectionModel().select(index);
                                window.close();
                                Ext.Msg.alert("User Created", "New user was succesfully added.");
                            }
                        });
                    });
                } else {
                    Functions.errorMsg("A user with same name already exists.", "Duplicate User");
                }
            });
        } else {
            Ext.Msg.alert("Error", "The form contains validation errors and cannot be saved.");
        }
    },
    
    editUser: function(btn) {
        var currentRecord = this.currentRecord();
        if (currentRecord) {
        	var contactInfo=Ext.create('Security.model.Contact',currentRecord.get('contact'));
            var editWindow = Ext.create('Security.view.user.EditUser', {
            	mode:btn.mode,
                userId:currentRecord.get('id'),
                contactId:currentRecord.get('contact').id,
                userInfo:currentRecord,
                contactInfo:contactInfo
            });
            editWindow.show();
            
//            UserManager.getCustomFieldsFor(currentRecord.get('id'), 'user', function(records) {
//        		editWindow.bindCustomFields(records);
//            }, this);
        }
    },

    updateCredentials: function() {
        var currentRecord = this.currentRecord();
        if (currentRecord) {
            var editWindow = Ext.create('Security.view.user.EditUserCredentials', {
                userId:currentRecord.get('id'),
                userInfo:currentRecord
            });
            editWindow.show();
        }
    },

    currentRecord:function() {
    	if(this.getUserDetailPaneTabPanel())
    		return this.getUserDetailPaneTabPanel().currentRecord;
    	else
		if(this.getAccountSettings())
			return this.getAccountSettings().currentRecord;
    },

    deleteUser: function() {
        var userList = this.getUsersList();
        var store = userList.store;
        var selectionModel = userList.getSelectionModel();
        var selection = selectionModel.getSelection();
        if (selection.length >= 0) {
            var toBeDeleted = selection[0];
            var name = toBeDeleted.get('name');
            Ext.Msg.confirm('Delete User', "Are you sure you want to delete the user '" + name + "' ?",
                function(btn) {
                    if (btn == 'yes') {
                        UserManager.deleteUser(toBeDeleted.get('id'), function() {
                            store.remove(toBeDeleted);
                            selectionModel.deselectAll();
                            selectionModel.select(0);
                            Ext.Msg.alert('User Deleted', 'User "' + name + '" Deleted Successfully.');
                        });

                    }
                });
        }
    },
    
    emailUser: function() {
        var userList = this.getUsersList();
        var selectionModel = userList.getSelectionModel();
        var selection = selectionModel.getSelection();
        if (selection.length >= 0) {
            var record = selection[0];
          
            var name = record.get('name');
            var contact=record.get('contact');
            
            Ext.Msg.confirm('Send Email', "Are you sure you want to send email to  user '" + name + "' ?",
                function(btn) {
                    if (btn == 'yes') {
                    	 Security.loadingWindow = Ext.widget('progresswindow', {
		                        text: 'Sending Email...'
		                    });
                    	PasswordManager.sendResetPasswordEmail(
                    			contact.emailAddress, function() {
						Security.removeLoadingWindow(function() {

                        });
					}, this);
                        
                    }
                });
        }
    },

    loadUserStore: function (selectedUserId) {
        Ext.log('loadUserStore ' + selectedUserId);
        var me=this;
        this.getUsersListStore().load({
            scope: this,
            callback: function(records, operation, success) {
                if (success !== false) {
                    Ext.log('User store loaded');
                    var userIndex = 0;
                    if (selectedUserId) {
                        userIndex = this.getUsersListStore().findExact('id', Ext.Number.from(selectedUserId));
                    }
                    this.selectUserRecord(userIndex > 0 ? userIndex : 0);
                    me.getUsersList().updateLayout();
			        me.getUsersList().setLoading(false);
                } 
//                else {
//                    window.location = 'login';
//                }
            }
        });

    },

    /**
     * Selects a User instance by record instance or index.
     * @param {Security.model.User/Number} record An User instance or its index
     */
    selectUserRecord: function(record) {
        var selectionModel = this.getUsersList().getSelectionModel();
        if (!selectionModel.isSelected(record)) {
            selectionModel.select(record);
        }
    },

    onUserSelectionChange: function(selectionModel, selected) {
        var grid = this.getUsersList();
        var manageUserActions = grid.up('container').down('#manageUserActions');
        if (selected.length > 0) {
        	var selection=selected[0];
        	var firstName=selection.get('firstName');
        	
            manageUserActions.down('#edituser').setDisabled(selected.length === 0);
            manageUserActions.down('#updateuser').setDisabled(selected.length === 0);
            // TODO: Make this configurable in 1.5
//            manageUserActions.down('#deleteuser').setDisabled(selected.length === 0);
            manageUserActions.down('#emailuser').setDisabled(selected.length === 0);
            
			// FIXME: This is hardcoded but should be an option assigned to the user. For example, if its LDAP, you cannot sent the email notification.
            if(firstName=='System' || firstName =='Admin')
            {
	            manageUserActions.down('#emailuser').setDisabled(true);
            }        	
        	
            if(Security.authType=='LDAP'){
                manageUserActions.down('#edituser').setDisabled(true);
                manageUserActions.down('#updateuser').setDisabled(true);
            }
            
            var selectedUserId = selected[0].getId();
            this.getUserInfo().update(selected[0]);
            this.application.fireEvent('changeurlpath', [selectedUserId]);
        }
    },
    
    searchUser: function(field, newValue, oldValue) {
        var store = this.getUsersList().store;
        store.suspendEvents();
        store.clearFilter();
        store.resumeEvents();
        store.filter({
            property: 'name',
            anyMatch: true,
            value   : newValue
        });
        var filterRecords = store.getRange();
        if (filterRecords.length > 0)
            this.selectUserRecord(0);
        field.focus(false, 50);
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
    
    defineUserFileUploadType : function(){
    	 Ext.apply(Ext.form.VTypes, {
    		 fileUpload: function(val, field) {                              
                 var fileName = /^.*\.(csv|CSV|json|JSON)$/i;
                 return fileName.test(val);
           },                 
           fileUploadText: 'File must be in .csv or .json format'
         });
    },
    
    importUsers : function(btn){
    	var me=this;
    	var window=btn.up('window');
    	var batchUsersList = window.down('grid');
    	var selection = batchUsersList.getSelectionModel().getSelection();
    	if(selection.length>0){

    		var usersArray=[];
    		Ext.each(selection,function(rec){
    			usersArray.push(rec.get('value'));
    		});
    		Security.loadingWindow = Ext.widget('progresswindow', {text: 'Importing Users...'});
    		UserManager.batchImportUsers(usersArray,function(){
    			 me.getUsersListStore().load({
    		            scope: this,
    		            callback: function(records, operation, success) {
    		            	 Security.removeLoadingWindow(function(){
    		            		window.close();    		            		
     		            		Ext.Msg.alert("User Imported", "Succesfully imported "+selection.length+" users to database."); 
    		            	 });
    		            		
    		            }
    			 });
    		},this);
    	}
    	else
    		Functions.errorMsg("No user selected to import from the list.", "Selection Error");
    	
    	
    }
});
