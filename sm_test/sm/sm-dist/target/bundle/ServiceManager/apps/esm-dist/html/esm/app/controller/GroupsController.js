Ext.define('Security.controller.GroupsController', {
    extend: 'Ext.app.Controller',

    stores: [
        'GroupsList',
        'GroupRolesList',
        'GroupUsersList',
        'GroupOrgsList'
    ],
    models: [
        'Group'
    ],
    views: [
        'group.GroupsList',
        'group.GroupInfo',
        'group.ManageGroups',
        'group.GroupProps',
        'group.GroupRoleAssignments',
        'group.GroupUserAssignments',
        'group.GroupOrgAssignments',
        'group.GroupRoles',
        'group.GroupUsers',
        'group.GroupOrganizations',
        'group.AddEditGroup',
        'group.AddUsersToGroup',
        'group.GroupDetailPane',
        'group.GroupDetailPaneHeader',
        'group.GroupDetailPaneTabPanel',
        'tenant.ManageTenantUserGroups'
    ],

    refs: [{
        ref: 'groupInfo',
        selector: 'groupinfo'
    }, {
        ref: 'manageGroups',
        selector: 'managegroups'
    }, {
        ref: 'groupProps',
        selector: 'groupinfo groupprops'
    }, {
        ref: 'groupsList',
        selector: 'groupslist'
    }, {
        ref: 'groupRoles',
        selector: 'grouproleassignments'
    }, {
        ref: 'groupUsers',
        selector: 'groupuserassignments'
    },{
        ref: 'groupOrgs',
        selector: 'grouporgassignments'
    },{
        ref: 'groupRolesList',
        selector: 'grouproles'
    }, {
        ref: 'groupUsersList',
        selector: 'groupusers'
    },{
        ref: 'groupOrgsList',
        selector: 'grouporganizations'
    }, {
        ref: 'groupDetailPaneTabPanel',
        selector: 'groupdetailpanetabpanel'
    },{
        ref:'tenantConfiguration',
        selector:'tenantconfiguration'
    }],

    init: function () {
        this.control({
            'managegroups': {
                statechange: this.onStateChange
            },
            'managegroups groupslist': {
                selectionchange: this.onGroupSelectionChange
            },
            'managegroups #newgroup': {
                click: this.newGroup
            },
            'managegroups #editgroup': {
                click: this.editGroup
            },
            'managegroups #deletegroup': {
                click: this.deleteGroup
            },
            'managegroups groupslist textfield[action=searchGroup]': {
                change: this.searchGroup
            },
            'managegroups groupslist tool[action=refreshGroupsList]': {
                click: this.loadGroupStore
            },
            
            
            'grouproles tool[action=newgroupassignment]': {
                click: this.showRoleToGroupWindow
            },
            'addroleoruserorgroup button[action=addRolesToGroup]':{
            	click : this.addRolesToGroup
            },
            'grouproles tool[action=removegroupassignment]': {
                click: this.removeGroupAssignment
            },
            
            
            'grouporganizations tool[action=neworgassignment]': {
                click: this.showOrgToGroupWindow
            },
            'addroleoruserorgroup button[action=addOrgsToGroup]':{
            	click : this.addOrgsToGroup
            },
            'grouporganizations tool[action=removeorgassignment]': {
                click: this.removeOrgAssignment
            },
            
            
            'groupusers tool[action=newuserassignment]': {
                click: this.showGroupUsersWindow
            },
//            'adduserstogroup button[action=searchItems]': {
//                click: this.searchUsers
//            },
//            'adduserstogroup textfield[itemId=searchStr]':{
//				specialkey: function(field, e){
//					if (e.getKey() == e.ENTER) {
//						 this.searchUsers(field);
//					}
//				}  
//			},
            'addroleoruserorgroup button[action=addUsersToGroup]': {
                click: this.addUsersToGroup
            },            
            'groupusers tool[action=removeuserassignment]': {
                click: this.removeGroupUserAssignment
            },
            'addeditgroup button[action=createGroup]': {
                click: this.saveNewGroup
            },
            'addeditgroup button[action=updateGroup]': {
                click: this.updateGroup
            }
        });
    },

    onStateChange: function(path) {
        if (path.length > 0) {
            var groupId = path[0];
            if (Ext.isNumeric(groupId)) {
                var selected = this.getGroupsList().getSelectionModel().getSelection();
                if (selected.length > 0 && selected[0].getId() == groupId) {
                    return; //ignore - group is already selected
                }

                var groupIndex = this.getGroupsListStore().findExact('id', Ext.Number.from(groupId));
                if (groupIndex != -1) {
                    this.selectGroupRecord(groupIndex);
                    return;
                } else { //group is not yet loaded
                    this.loadGroupStore(groupId);
                }
            } else {
                this.application.fireEvent('changeurlpath', []);
            }
        } else {
            this.loadGroupStore();
        }
    },

    newGroup: function() {
        var addGroupWindow = Ext.create('Security.view.group.AddEditGroup', {
            title: "Create Group",
            operation: 'create'
        });
        addGroupWindow.show();
    },

    showRoleToGroupWindow : function(){
    	
    	var me = this;
    	
    	var currentRecord = this.currentRecord();
    	
    	 var originalRecordIds=new Ext.util.HashMap();

         if (currentRecord) {
             var rolesStore = this.getGroupRoles().store;
             
             rolesStore.each(function(rec){
             	originalRecordIds.add(rec.get('id'),rec.get('canonicalName'));
             });

             var availableRolesStore = Ext.create('Security.store.TenantRoles');
             
             var tenantConfiguration = this.getTenantConfiguration();
             var tenantRecord = tenantConfiguration.getTenantRecord();
             
             availableRolesStore.getProxy().setExtraParam('id',tenantRecord.get('id'));
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
             	addAction:'addRolesToGroup', 
            	title:'Available Roles',
             	originalRecordIds:originalRecordIds,
             	store:availableRolesStore                 
             });
             addRoleWindow.show();

             
         }
    },
    
    addRolesToGroup  : function(btn){
    	var me=this;
    	var currentRecord = this.currentRecord();
    	var addWindow=btn.up('window');
    	var rolesList = addWindow.down('grid');
    	var selection = rolesList.getSelectionModel().getSelection();
    	if(selection.length>0){

    		 GroupManager.addRolesToGroup(currentRecord.data, selection, function(status) {
                if (status) {
                	 me.getGroupsListStore().load({
                        callback : function() {
                        	me.getGroupRolesList().update(currentRecord,function(){
                                 Ext.Msg.alert("Role Assigned", "Role succesfully assigned to group.");
                                 addWindow.close();
                        	},this);
                        	 
                        }
                    });
                } else {
                    Functions.errorMsg("Could not connect to backend to assign user new roles.", "Role Assignment Failed");
                }
            },this);
    		
    	}
    	else
    		Functions.errorMsg("No role selected to add.", "Selection Error");   
    	
    },
    

    removeGroupAssignment: function() {
        var selectionModel = this.getGroupRoles().getSelectionModel();
        var store = this.getGroupRoles().store;
        var selection = selectionModel.getSelection();
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
					        	
					            GroupManager.removeRolesFromGroup(currentRecord.data, selection, function(status) {
					                if (status) {
					                    me.getGroupsListStore().load({
					                        callback : function() {
					                        	me.getGroupRolesList().update(currentRecord,function(){

					                                 Ext.Msg.alert("Role Unassigned", "Role was succesfully unassigned from group.");
					                       	},this);
					                        	
					                           
					                        }
					                    });
					                } else {
					                    Functions.errorMsg("Could not connect to backend to remove a role for group.", "Role Deletion Failed");
					                }
					            });
                     });
        } else {
            Ext.Msg.alert("Role Deletion", "You must first select one or more roles to delete.");
        }
//        this.getGroupInfo().updatePermissions(this.getGroupsList().getSelectionModel().getSelection()[0]);
    },
    
    
    
    
    
    showOrgToGroupWindow : function(){
    	
    	var me = this;
    	
    	var currentRecord = this.currentRecord();
    	
    	 var originalRecordIds=new Ext.util.HashMap();

         if (currentRecord) {
             var orgsStore = this.getGroupOrgs().store;
             
             orgsStore.each(function(rec){
             	originalRecordIds.add(rec.get('id'),rec.get('canonicalName'));
             });

             var availableOrgsStore = Ext.create('Security.store.TenantOrgs');
             
             var tenantConfiguration = this.getTenantConfiguration();
             var tenantRecord = tenantConfiguration.getTenantRecord();
             
             availableOrgsStore.getProxy().setExtraParam('id',tenantRecord.get('id'));

             var addOrgWindow = Ext.create('Security.view.common.AddRoleOrUserOrGroup', {
             	addAction:'addOrgsToGroup', 
            	title:'Available Organizations',
             	originalRecordIds:originalRecordIds,
             	store:availableOrgsStore                 
             });
             addOrgWindow.show();

             
         }
    },
    
    addOrgsToGroup  : function(btn){
    	var me=this;
    	var currentRecord = this.currentRecord();
    	var addWindow=btn.up('window');
    	var orgsList = addWindow.down('grid');
    	var selection = orgsList.getSelectionModel().getSelection();
    	if(selection.length>0){

    		 GroupManager.addOrganizationsToGroup(currentRecord.data, selection, function(status) {
                if (status) {
                	 me.getGroupsListStore().load({
                        callback : function() {
                        	me.getGroupOrgsList().update(currentRecord,function(){
                                 Ext.Msg.alert("Organization Added", "Organization succesfully added to group.");
                                 addWindow.close();
                        	},this);
                        	 
                        }
                    });
                } else {
                    Functions.errorMsg("Could not connect to backend to add organization.", "Failure");
                }
            },this);
    		
    	}
    	else
    		Functions.errorMsg("No role selected to add.", "Selection Error");   
    	
    },
    

    removeOrgAssignment: function() {
        var selectionModel = this.getGroupOrgs().getSelectionModel();
        var store = this.getGroupOrgs().store;
        var selection = selectionModel.getSelection();
        var currentRecord = this.currentRecord();
        var me = this;
        if (selection.length > 0) {
        	 Ext.Msg.confirm(
                     "Remove Organization?",
                     'Are you sure you want to remove organization.',
                     function(btn) {
                         if (btn == 'yes')       	
					        	
					            GroupManager.removeOrganizationsFromGroup(currentRecord.data, selection, function(status) {
					                if (status) {
					                    me.getGroupsListStore().load({
					                        callback : function() {
					                        	me.getGroupOrgsList().update(currentRecord,function(){
					                                 Ext.Msg.alert("Organization Removed", "Organization was succesfully removed from group.");
					                       	},this);
					                        	
					                        }
					                    });
					                } else {
					                    Functions.errorMsg("Could not connect to backend to remove a organization from group.", "Failure");
					                }
					            });
                     });
        } else {
            Ext.Msg.alert("Warning", "You must first select one or more organization to delete.");
        }
    },
    
    
    showGroupUsersWindow : function(){
    	
    	var currentRecord = this.currentRecord();
    	
   	 	var originalRecordIds=new Ext.util.HashMap();

        if (currentRecord) {
        	
    	
	        var tenantConfiguration = this.getTenantConfiguration();
	        var tenantRecord = tenantConfiguration.getTenantRecord();
	        
	        
	        var groupUsersList = Ext.StoreManager.lookup('GroupUsersList');
            
       	 	groupUsersList.each(function(rec){
	             	originalRecordIds.add(rec.get('id'),rec.get('name'));
	             });
	        
	        
	        var usersListStore = Ext.create('Ext.data.Store',{
	        	fields : [
	        	          {name:'groupCanonicalName',type:'string'},
	        	          {name:'canonicalName',type:'string'},
	        	          {name:'id',type:'long'}
	    	          ],
	    	          groupField:'groupCanonicalName'
	        });
	        
	        
	    	TenantManager.getTenantOrganizations(tenantRecord.get('id'),0,-1,function(tenantList){
	    		Ext.each(tenantList.resultList,function(organization){
	    				OrganizationManager.getUsersForOrganization(organization.id,0,-1,function(usersList){
	    					Ext.each(usersList.resultList,function(userObj){
	    						var userRecord = Ext.create('Security.model.User',userObj);
	    						if(!originalRecordIds.containsKey(userRecord.get('id'))){
		    						usersListStore.add({
		    							'groupCanonicalName':organization.canonicalName,
		    							'canonicalName':userRecord.get('name'),
		    							'id':userRecord.get('id')
		    							});
	    						}
	    					});
	    				});
	    		});
	    	});
	    	
		 
	             var addUsersToGroupWindow = Ext.create('Security.view.common.AddRoleOrUserOrGroup',{
	            	 title:'Available Users',
	            	 store:usersListStore,
	            	 addAction:'addUsersToGroup',
	            	 originalRecordIds:originalRecordIds,
	            	 enableGrouping:true,
	            	 groupFieldName : 'Organization'
	             });
	             addUsersToGroupWindow.show();
	             
	         }
             
    },
    
    searchUsers : function(btn){
    	
    	var me=this;
    	var addUsersToGroupWindow = btn.up('window');
    	var searchStr = addUsersToGroupWindow.down('#searchStr').getValue();
    	var baseGridList = addUsersToGroupWindow.down('basegridlist'); 	
    	
    	var currentRecord = this.currentRecord();
    	
    	 var originalRecordIds=new Ext.util.HashMap();

         if (currentRecord) {
        	 
        	 	var usersStore = this.getGroupUsers().store;

        	 	 usersStore.each(function(rec){
                  	originalRecordIds.add(rec.get('id'),rec.get('canonicalName'));
                  });
        	 
			    	  if(searchStr!='')
					   {
			    		  baseGridList.getStore().getProxy().setExtraParam('seed', searchStr);
			    		  baseGridList.getStore().removeAll();
			    		  baseGridList.getStore().load();
//			    				  {
//			    			    scope: this,
//			    			    callback: function(records, operation, success) {
//			    			    	
//			    			    	var selectedUsers = usersStore.getRange();
//			                         Ext.each(selectedUsers, function(user) {
//			                             var index = usersList.getStore().find('id', user.get('id'));
//			                             if(index!=-1)
//			                            	 usersList.getStore().removeAt(index);
//			                         });
//			    			    	
//			    			    }
//			    			});
//					   		UserManager.searchByFirstOrMiddleOrLastName(searchStr,function(users){
//					   			
//					   			
//					   		 if (usersStore.data !== undefined) {
//		                         var selectedUsers = usersStore.getRange();
//		                         Ext.each(selectedUsers, function(user) {
//		                             var index = usersList.getStore().find('id', user.get('id'));
//		                             usersList.getStore().removeAt(index);
//		                         });
//		                     }
//					   		},this);
					   }
				   else
					   {
					   baseGridList.down('#searchStr').markInvalid('Invalid search string.');
					   }
    	
         }
    	
    },
    
    addUsersToGroup : function(btn){
    	
    	var me=this;
    	var currentRecord = this.currentRecord();
    	var addWindow=btn.up('window');
    	var usersList = addWindow.down('grid');
    	var selection = usersList.getSelectionModel().getSelection();
    	if(selection.length>0){

    		 GroupManager.addUsersToGroup(currentRecord.data, selection, function(status) {
                if (status) {
                	 me.getGroupsListStore().load({
                        callback : function() {
                        	me.getGroupUsersList().update(currentRecord,function(){
                        		  //me.getGroupsList().getSelectionModel().select(currentRecord.index);
                                  Ext.Msg.alert("Users Added", "Users succesfully added to group.");
                                  addWindow.close();
                        	},this);
                        	
                        }
                    });
                } else {
                    Functions.errorMsg("Could not connect to backend to add new users.", "Users Addition Failed");
                }
            },this);
    		
    	}
    	else
    		Functions.errorMsg("No user selected to add.", "Selection Error");   
    	
    	
    },

    removeGroupUserAssignment: function() {
        var selectionModel = this.getGroupUsers().getSelectionModel();
        var store = this.getGroupUsers().store;
        var selection = selectionModel.getSelection();
        var currentRecord = this.currentRecord();
        var me = this;
        if (selection.length > 0) {
        	 Ext.Msg.confirm(
                     "Remove User?",
                     'Are you sure you want to remove user..',
                     function(btn) {
                         if (btn == 'yes')
			            GroupManager.removeUsersFromGroup(currentRecord.data, selection, function(status) {
			                if (status) {
			                    me.getGroupsListStore().load({
			                        callback : function() {
			                        	me.getGroupUsersList().update(currentRecord,function(){
			                                Ext.Msg.alert("User Removed", "User was succesfully unassigned from group.");
			                        	},this);                            
			                        }
			                    });
			                } else {
			                    Functions.errorMsg("Could not connect to backend to remove a user from group.", "Operation Failed");
			                }
			            });
                         
                     });
        } else {
            Ext.Msg.alert("Operation Failed", "You must first select one or more user to delete.");
        }
//        this.getGroupInfo().updatePermissions(this.getGroupsList().getSelectionModel().getSelection()[0]);
    },

    updateGroup: function(btn, eventObj) {
        var window = btn.up('window');
        var index = window.groupInfo.index;
        var form = window.down('form').getForm();
        var me = this;
        if (form.isValid()) {
            var values = form.getValues();
            GroupManager.updateGroup(values, window.groupInfo.get('id'), function(updatedGroup) {
                window.close();
                var groupRecord=Ext.create('Security.model.Group',updatedGroup);
                me.getGroupProps().update(updatedGroup);
                me.getGroupsListStore().load({
                    callback : function() {
                        me.getGroupsList().getSelectionModel().select(index);
                        if(me.getGroupInfo())
                    		me.getGroupInfo().currentRecord=groupRecord;
                        Ext.Msg.alert("Group Updated", "Group information was succesfully updated.");
                    }
                });
            });
        } else {
            Ext.Msg.alert("Error", "The form contains validation errors and cannot be saved.");
        }
    },

    saveNewGroup: function(btn, eventObj) {
        var window = btn.up('window');
        var form = window.down('form').getForm();
        var me = this;
        if (form.isValid()) {
            var values = form.getValues();
            var groupName = values.canonicalName;
            GroupManager.checkGroupName(groupName, function(valid) {
                if (valid) {
                    GroupManager.createGroup(values, function(group) {
                        me.getGroupsListStore().load({
                            callback : function() {
                                var index = this.find('id', group.id);
                                me.getGroupsList().getSelectionModel().select(index);
                                window.close();
                                Ext.Msg.alert("Group Created", "New group was succesfully added.");
                            }
                        });
                    });
                } else {
                    Functions.errorMsg("A group with same group name already exists.", "Duplicate Group");
                }
            });
        } else {
            Ext.Msg.alert("Error", "The form contains validation errors and cannot be saved.");
        }
    },

    editGroup: function() {
    	var currentRecord = this.currentRecord();

        if (currentRecord) {
            var addWindow = Ext.create('Security.view.group.AddEditGroup', {
                title: "Edit Group",
                operation: 'edit',
                groupInfo: currentRecord
            });
            addWindow.down('form').getForm().loadRecord(currentRecord);
            addWindow.show();
        }
    },

    currentRecord: function() {
//        return this.getGroupInfo().currentRecord;
    	
    	var groupDetailPaneTabPanel = this.getGroupDetailPaneTabPanel();    	
    	return groupDetailPaneTabPanel.currentRecord;
    	
    },

    deleteGroup: function() {
        var groupList = this.getGroupsList();
        var store = groupList.store;
        var selectionModel = groupList.getSelectionModel();
        var selection = selectionModel.getSelection();
        if (selection.length >= 0) {
            var toBeDeleted = selection[0];
            var name = toBeDeleted.get('canonicalName');
            Ext.Msg.confirm('Delete Group', "Are you sure you want to delete the group '" + name + "' ?",
                function(btn) {
                    if (btn == 'yes') {
                        GroupManager.deleteGroup(toBeDeleted.get('id'), function() {
                            store.remove(toBeDeleted);
                            selectionModel.deselectAll();
                            selectionModel.select(0);
                            Ext.Msg.alert('Group Deleted', 'Group "' + name + '" Deleted Successfully.');
                        });

                    }
                });
        }
    },

    loadGroupStore: function (selectedGroupId) {
        Ext.log('loadGroupStore ' + selectedGroupId);
        this.getGroupsListStore().load({
            scope: this,
            callback: function(records, operation, success) {
                if (success !== false) {
                    Ext.log('Group store loaded');
                    var groupIndex = 0;
                    if (selectedGroupId) {
                        groupIndex = this.getGroupsListStore().findExact('id', Ext.Number.from(selectedGroupId));
                    }
                    this.selectGroupRecord(groupIndex > 0 ? groupIndex : 0);
                } 
//                else {
//                    window.location = 'login';
//                }
            }
        });

    },

    /**
     * Selects a Group instance by record instance or index.
     * @param {Security.model.Group/Number} record An Group instance or its index
     */
    selectGroupRecord: function(record) {
        var selectionModel = this.getGroupsList().getSelectionModel();
        if (!selectionModel.isSelected(record)) {
            selectionModel.select(record);
        }
    },

    onGroupSelectionChange: function(selectionModel, selected) {
        var grid = this.getGroupsList();
        var manageGroupActions = grid.up('container').down('#manageGroupActions');
        if (selected.length > 0) {
            manageGroupActions.down('#editgroup').setDisabled(selected.length === 0);
            manageGroupActions.down('#deletegroup').setDisabled(selected.length === 0);
            var selectedGroupId = selected[0].getId();
            this.getGroupInfo().update(selected[0]);
            this.application.fireEvent('changeurlpath', [selectedGroupId]);
        }
    },

    searchGroup: function(field, newValue, oldValue) {
        var store = this.getGroupsList().store;
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
            this.selectGroupRecord(0);
        field.focus(false, 50);
    }
});

