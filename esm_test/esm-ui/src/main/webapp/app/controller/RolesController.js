Ext.define('Security.controller.RolesController', {
    extend: 'Ext.app.Controller',

    stores: [
        'RolesList',
        'MemberOfRoles',
        'IncludeRoles'
    ],

    views: [
        'role.RolesList',
        'role.RoleInfo',
        'role.ManageRoles',
        'role.AddEditRole',
        'role.CopyRole',
        'role.IncludeRoles',
        'role.MemberOfRoles',
        'role.RoleDetailPane',
        'role.RoleDetailPaneHeader',
        'role.RoleDetailPaneTabPanel'
    ],

    refs: [{
        ref: 'roleInfo',
        selector: 'roleinfo'
    }, {
        ref: 'manageRoles',
        selector: 'manageroles'
    }, {
        ref: 'roleProps',
        selector: 'roleinfo roleprops'
    }, {
        ref: 'rolesList',
        selector: 'roleslist'
    }, {
        ref: 'includeRoles',
        selector: 'includeroles'
    }, {
        ref: 'memberOfRoles',
        selector: 'memberofroles'
    }, {
        ref: 'roleDetailPaneTabPanel',
        selector: 'roledetailpanetabpanel'
    },{
        ref:'tenantConfiguration',
        selector:'tenantconfiguration'
    },{
        ref:'accountSettings',
        selector:'accountsettings'
    }],

    init: function() {

        this.control({
            'manageroles': {
                statechange: this.onStateChange
            },
            'manageroles roleslist':{
                selectionchange: this.onRoleSelectionChange
            },
            'manageroles #newrole':{
                click: this.newRole
            },
            'manageroles #newroleldap':{
                click: this.newRoleLdap
            },
            'manageroles #copyrole':{
                click: this.copyRole
            },
            'manageroles #editrole':{
                click: this.editRole
            },
            'manageroles #deleterole':{
                click: this.deleteRole
            },
            'manageroles roleslist textfield[action=searchRole]':{
                change: this.searchRole
            },
            'manageroles roleslist tool[action=refreshRolesList]':{
                click: this.loadRolesListStore
            },
            'editpermissions #savePermissionsForRole':{
                click: this.savePermissionsForRole
            },
            'editpermissions #refreshPermissionForRole':{
                click: this.refreshPermissionForRole
            },
            'includeroles tool[action=includenewrole]':{
                click: this.showRolesToRoleWindow
            },
            'addroleoruserorgroup button[action=addRolesToRole]':{
            	click : this.addChildRolesToRole
            },
            'includeroles tool[action=removememberrole]':{
                click: this.removeMemberRole
            },
            'addeditrole button[action=createRole]':{
                click: this.saveNewRole
            },
            'addeditrole button[action=updateRole]':{
                click: this.updateRole
            },
            'copyrole button[action=copyAndSaveRole]':{
                click: this.copyAndSaveRole
            }
        });
    },

    onStateChange: function(path) {
        if (path.length > 0) {
            var roleId = path[0];
            if (Ext.isNumeric(roleId)) {
                var selected = this.getRolesList().getSelectionModel().getSelection();
                if (selected.length > 0 && selected[0].getId() == roleId) {
                    return; //ignore - role is already selected
                }
                var roleIndex = this.getRolesListStore().findExact('id', Ext.Number.from(roleId));
                if (roleIndex != -1) {
                    this.selectRoleRecord(roleIndex);
                    return;
                } else { //role is not yet loaded
                    this.loadRolesListStore(roleId);
                }
            } else {
                this.application.fireEvent('changeurlpath', []);
            }
        } else {
            this.loadRolesListStore();
        }
    },

    currentRecord: function() {
//        var roleInfo = this.getRoleInfo();
//        return roleInfo ? roleInfo.currentRecord : null;
    	
    	var roleDetailPaneTabPanel = this.getRoleDetailPaneTabPanel();    	
    	return roleDetailPaneTabPanel.currentRecord;
    },
    
    showRolesToRoleWindow : function(){
    	
    	var me = this;
    	
    	var currentRecord = this.currentRecord();
    	
    	 var originalRecordIds=new Ext.util.HashMap();

         if (currentRecord) {
             var rolesStore = this.getIncludeRoles().store;
             
             rolesStore.each(function(rec){
             	originalRecordIds.add(rec.get('id'),rec.get('canonicalName'));
             });
             
             originalRecordIds.add(currentRecord.get('id'),currentRecord.get('canonicalName'));
             
             var availableRolesStore = Ext.create('Security.store.TenantRoles');
             
             var tenantConfiguration = this.getTenantConfiguration();
             var tenantRecord = tenantConfiguration.getTenantRecord();
             
             availableRolesStore.getProxy().setExtraParam('id',tenantRecord.get('id'));
             
             
             availableRolesStore.load({
                 callback: function() {
                     if (rolesStore.data !== undefined) {
                         var selectedRoles = rolesStore.getRange();
//                         var currentRecordIndex = availableRolesStore.find('id',currentRecord.get('id'));
//                         if(currentRecordIndex!=-1)
//                        	 availableRolesStore.removeAt(currentRecordIndex);
                         
                         var adminRoleIndex = availableRolesStore.find('id',1,0,false,true,true);
                         var systemRoleIndex = availableRolesStore.find('id',2,0,false,true,true);
                         
                         
//                         console.log(availableRolesStore.getRange());

                         
                         if(systemRoleIndex!=-1){
                        	 var systemRole = availableRolesStore.getAt(systemRoleIndex);
                        	 originalRecordIds.add(systemRole.get('id'),systemRole.get('canonicalName'));
//                        	 availableRolesStore.removeAt(systemRoleIndex);
                         }
                         
                         if(adminRoleIndex!=-1){
                        	 var adminRole = availableRolesStore.getAt(adminRoleIndex);

                        	 originalRecordIds.add(adminRole.get('id'),adminRole.get('canonicalName'));
//                        	 availableRolesStore.removeAt(adminRoleIndex);
                         }
                         
//                         Ext.each(selectedRoles, function(role) {
//                             var index = availableRolesStore.find('id', role.get('id'));
//                             if(index!=-1)
//                            	 availableRolesStore.removeAt(index);                            
//                         });
                     }
                 }
             });
             
             var addRoleWindow = Ext.create('Security.view.common.AddRoleOrUserOrGroup', {
             	addAction:'addRolesToRole', 
            	title:'Available Roles',
             	originalRecordIds:originalRecordIds,
             	store:availableRolesStore                 
             });
             addRoleWindow.show();

             
         }
    },


    addChildRolesToRole  : function(btn){
    	var me=this;
    	var currentRecord = this.currentRecord();
    	var addWindow=btn.up('window');
    	var rolesList = addWindow.down('grid');
    	var selection = rolesList.getSelectionModel().getSelection();
    	if(selection.length>0){

    		 RoleManager.addChildRolesToRole(currentRecord.data, selection, function(status) {
                if (status) {
                        	 me.getIncludeRoles().getStore().load({
                             	params : {
                             		data : '{"role":'+Ext.encode(currentRecord.data)+',"startRecord":0,"recordCount":-1}'
                             	},
	                             callback: function(records, operation, success) {
	                            	 Ext.Msg.alert("Roles Assigned", "Child roles succesfully assigned.");
	                                 addWindow.close();
	                             }
                        	 });                        	 
                } else {
                    Functions.errorMsg("Could not connect to backend to assign child roles.", "Roles Assignment Failed");
                }
            },this);
    		
    	}
    	else
    		Functions.errorMsg("No role selected to add.", "Selection Error");   
    	
    },
    

    
    
    removeMemberRole: function() {
        var me = this;
        var selectionModel = this.getIncludeRoles().getSelectionModel();
        var selection = selectionModel.getSelection();
        var currentRecord = this.currentRecord();
        
        if (selection.length > 0) {
        	
        	   Ext.Msg.confirm(
        			   "Unassign Role?",
                       'Are you sure you want to unassign roles.',
                       function(btn) {
                           if (btn == 'yes')
                        	   RoleManager.removeChildRolesFromRole(currentRecord.data, selection, function(status) {
                                   if (status) {
                                   	 me.getIncludeRoles().getStore().load({
                                         	params : {
                                   		 		data : '{"role":'+Ext.encode(currentRecord.data)+',"startRecord":0,"recordCount":-1}'
                                         	},
                                             callback: function(records, operation, success) {
                                           	  Ext.Msg.alert("Role Unassigned", "Role was succesfully unassigned from parent role.");  
                                             }
                                    	 });     
                                   } else {
                                       Functions.errorMsg("Could not connect to backend to remove a role for parent role.", "Role Deletion Failed");
                                   }
                               });
                       }
                   );
        	   
        	 
        } else {
            Ext.Msg.alert("Role Deletion", "You must first select one or more roles to delete.");
        }

    },
     
    
    newRole: function() {
        var addWindow = Ext.create('Security.view.role.AddEditRole', {
            title: "Create Role",
            operation :'create'
        });
        addWindow.show();
    },
    
    copyAndSaveRole: function(btn, eventObj) {
    	var me=this;
        var window = btn.up('window');
        var form = window.down('form').getForm();
        var store = this.getRolesListStore();
        var childRoles = this.getIncludeRolesStore();
        var currentRecord = this.currentRecord();
        if (form.isValid()) {
            window.getEl().mask('saving data...');
            var fromRecord = form.getRecord();
            var values = form.getValues();
            var roleName = values.canonicalName;
            window.getEl().unmask();

            RoleManager.checkRolename(roleName, function (valid) {
                if (valid) {
                	RoleManager.createRole(values, function(newRole) {
                		var roleRecord = Ext.create('Security.model.Role',newRole);
                		if(values.copyRoles){
                			RoleManager.addChildRolesToRole(newRole, childRoles.getRange(),function(){
                				
                			});
                		}
                		if(values.copyPermissions){
                			RoleManager.getPermissionsForRole(currentRecord, function(permissions){
                				console.log(permissions);
                				RoleManager.addPermissionsToRole(newRole,permissions,function(){
                					
                				});                				
                			},this);
                		}
                		
		                me.getRolesListStore().load({
		                    callback : function() {
		                        window.close();
		                        var index = me.getRolesListStore().find('id', newRole.id);
		                        me.getRolesList().getSelectionModel().select(index);
		                    }
		                });
		            });
//                	
//                    record = Ext.create('Security.model.Role', values);
//                    record.set('copyOptions', {
//                        copyRoles: values.copyRoles,
//                        copyPermissions: values.copyPermissions,
//                        copyRoleAssignmentPermissions: values.copyRoleAssignmentPermissions,
//                        copyFromRoleID: fromRecord.getId()
//                    });
                    window.close();
//                    this._saveAndSelectRole(record);
                }
                else {
                    form.markInvalid({'canonicalName':'Role already exists.'});
                }
            });
        }
        else {
            Ext.Msg.alert("Error", "The form contains validation errors and cannot be saved.");
        }
    },

    saveNewRole : function(btn, eventObj) {
        var window = btn.up('window');
        var form = window.down('form').getForm();
        var me = this;
        if (form.isValid()) {
            var values = form.getValues();
            var roleName = values.canonicalName;
            RoleManager.checkRolename(roleName, function(valid) {
                if (valid) {
		            RoleManager.createRole(values, function(newRole) {
		                me.getRolesListStore().load({
		                    callback : function() {
		                        window.close();
		                        var index = me.getRolesListStore().find('id', newRole.id);
		                        me.getRolesList().getSelectionModel().select(index);
		                    }
		                });
		            });
                }
                else
                	Functions.errorMsg("A role with same name already exists.", "Duplicate Role");
            });
        } else {
            Ext.Msg.alert("Error", "The form contains validation errors and cannot be saved.");
        }
    },

    updateRole:function (btn, eventObj) {
        var window = btn.up('window');
        var form = window.down('form').getForm();
        var me = this;
        var oldRecord = window.role;
        var index = window.role.index;
        if (form.isValid()) {
            var values = form.getValues();
            var roleName = values.canonicalName;
            if (roleName == oldRecord.get('canonicalName')) {
                RoleManager.updateRole(values, oldRecord.get('id'), function(updatedRole) {
                	var roleRecord=Ext.create('Security.model.Role',updatedRole);
                    me.getRoleProps().update(updatedRole);
                    me.getRolesListStore().load({
                        callback : function() {
                            me.getRolesList().getSelectionModel().select(index);
                            if(me.getRoleInfo())
                            	{
                        		me.getRoleInfo().currentRecord=roleRecord;
                            	}
                        }
                    });
                    window.close();
                });
            } else {
                RoleManager.updateRole(values, oldRecord.get('id'), function(updatedRole) {
                	var roleRecord=Ext.create('Security.model.Role',updatedRole);
                	if(me.getRoleInfo())
	                	{
	            			me.getRoleInfo().currentRecord=roleRecord;
	                	}
                    me.getRoleProps().update(updatedRole);
                    me.getRolesListStore().load();
                    window.close();
                });
            }
        } else {
            Ext.Msg.alert("Error", "The form contains validation errors, role cannot be updated.");
        }
    },

    newRoleLdap: function() {
        Ext.log('new Role ldap');
    },

    copyRole: function(btn, eventObj) {
        var selection = this.getRolesList().getSelectionModel().getSelection();

        if (selection.length) {
            var copyWindow = Ext.create('Security.view.role.CopyRole', {
                title: "Copy Role"
            });
            var form = copyWindow.down('form').getForm();
            form.loadRecord(selection[0]);
            form.setValues({canonicalName: 'Copy of ' + selection[0].get('canonicalName')});
            copyWindow.show();
        }
    },

    editRole: function() {
    	var currentRecord = this.currentRecord();

        if (currentRecord) {
            var addWindow = Ext.create('Security.view.role.AddEditRole', {
                title: "Edit Role",
                operation:'edit',
                role:currentRecord
            });
            addWindow.down('form').getForm().loadRecord(currentRecord);
            addWindow.show();
        }
    },

    deleteRole: function() {
        var store = this.getRolesListStore();
        var selection = this.getRolesList().getSelectionModel().getSelection();
        if (selection.length >= 0) {
            var toBeDeleted = selection[0];
            var me = this;
            Ext.Msg.confirm('Delete Role', "Are you sure you want to delete the role '" + toBeDeleted.get('canonicalName') + "' ?",
                function(btn) {
                    if (btn == 'yes') {
                        RoleManager.deleteRole(toBeDeleted.get('id'), function() {
                            store.remove(toBeDeleted);
                            me.loadRolesListStore(0);
//                            me.selectRoleRecord(0);
                            Ext.Msg.alert('Role Deleted', 'Role "' + toBeDeleted.get('canonicalName') + '" deleted successfully.');
                        });
                    }
                }
            );
        }
    },

    loadRolesListStore: function(selectedRoleId) {
        this.getRolesListStore().load({
                scope:this,
                callback:function(records, operation, success) {
                    if (success !== false) {
                        var roleIndex = 0;
                        if (selectedRoleId) {
                            roleIndex = this.getRolesListStore().findExact('id', Ext.Number.from(selectedRoleId));
                        }
                        this.selectRoleRecord(roleIndex > 0 ? roleIndex : 0);
                    } 
//                    else {
//                        window.location = 'login';
//                    }
                }
            }
        );
    },

    /**
     * Selects a Role instance by record instance or index.
     * @param {Security.model.Role/Number} record A Role instance or its index
     */
    selectRoleRecord: function(record) {
        var selectionModel = this.getRolesList().getSelectionModel();
        if (!selectionModel.isSelected(record)) {
            selectionModel.select(record);
        }
    },

    onRoleSelectionChange: function(selectionModel, selected) {
        var grid = this.getRolesList();
        var manageRolesActions = grid.up('container').down('#manageRolesActions');
        if (selected.length > 0) {
        	var selectedRoleId = selected[0].getId();
            manageRolesActions.down('#copyrole').setDisabled(selected.length === 0);
            manageRolesActions.down('#editrole').setDisabled(selected.length === 0 || selected[0].get('readOnly'));
            manageRolesActions.down('#deleterole').setDisabled(selected.length === 0 || selected[0].get('readOnly'));

            this.getRoleInfo().update(selected[0]);
            this.application.fireEvent('changeurlpath', [selectedRoleId]);
        }
    },

    searchRole: function(field, newValue, oldValue) {
        var store = this.getRolesList().store;
        store.suspendEvents();
        store.clearFilter();
        store.resumeEvents();
        store.filter({
            property: 'canonicalName',
            anyMatch: true,
            value   : newValue
        });
        var filteredRecords = store.getRange();
        if (filteredRecords.length > 0)
            this.selectRoleRecord(0);
        field.focus(false, 50);
    },

    deleteSelectedRecords: function(grid, confirmTitle, confirmMsg, callback) {
        var store = grid.store;
        var selection = grid.getSelectionModel().getSelection();
        if (selection.length >= 0) {
            if (confirmTitle) {
                Ext.Msg.confirm(confirmTitle, confirmMsg,
                    function(btn) {
                        if (btn == 'yes') {
                            for (var selectionIndex in selection) {
                                store.remove(selection[selectionIndex]);
                            }
                            store.sync();
                            if (null !== callback) callback();
                            return true;
                        }
                    }
                );
            } else {
                store.remove(toBeDeleted);
                store.sync();
                return true;
            }
        }
        return false;
    },

//    savePermissionsForRole : function(btn) {
//        var me = this;
//        var currentRecord = this.currentRecord();
//
//        var tabPanel = btn.up('tabpanel');
//        var activeTab = tabPanel.getActiveTab();
//        var checkBoxes = activeTab.getEl().query('input');
//        var selectedPermissions = [];
//        var deselectedPermissions = [];
//        
//        Security.loadingWindow = Ext.widget('progresswindow', {
//             text: 'Saving Permissions...'
//         });
//        
//        Ext.each(checkBoxes, function(box) {
//            if (box.checked)
//                selectedPermissions.push(Ext.decode(box.getAttribute("data-permission")));
//            else
//                deselectedPermissions.push(Ext.decode(box.getAttribute("data-permission")));
//        });
//
//        // Log.debug(selectedPermissions); Log.debug(deselectedPermissions);
//
//        var selectedPermissionsArray = [];
//        var deselectedPermissionsArray = [];
//
//        Ext.each(selectedPermissions, function(newPermission) {
//            if (!RoleManager.permissionsForRole.containsKey(newPermission.id)) {
//                selectedPermissionsArray.push(newPermission);
//            }
//        });
//
//        Ext.each(deselectedPermissions, function(oldPermission) {
//            if (RoleManager.permissionsForRole.containsKey(oldPermission.id)) {
//                deselectedPermissionsArray.push(oldPermission);
//            }
//        });
//
//        RoleManager.addPermissionsToRole(currentRecord.data, selectedPermissionsArray, function() {
//            if (deselectedPermissionsArray.length > 0)
//                RoleManager.removePermissionsFromRole(currentRecord.data, deselectedPermissionsArray, function() {
//                    Ext.Msg.alert('Permissions Changed',
//                                  'Permissions for role "' + currentRecord.get('canonicalName') +
//                                      '" changed successfully.');
//                });
//        });
//        
//        Security.removeLoadingWindow(function() {
//        	Ext.Msg.alert('Permissions Changed',
//                    'Permissions for role "' + currentRecord.get('canonicalName') +
//                        '" changed successfully.');
//        });
//    },
//
//    refreshPermissionForRole : function(btn) {
//        var tabPanel = btn.up('tabpanel');
//        var activeTab = tabPanel.getActiveTab();
//        var currentRecord = this.currentRecord();
//        RoleManager.getPermissionsForRole(currentRecord, function(permissionsOfRole) {
//            RoleManager.RolePermissionMap = permissionsOfRole;
//            tabPanel.fireEvent('tabchange', tabPanel, activeTab);
//        });
//    }
    
    savePermissionsForRole : function(btn) {
        var me = this;
        var roleDetailPaneTabPanel = this.getRoleDetailPaneTabPanel();    	
    	var currentRecord = roleDetailPaneTabPanel.currentRecord;

        var tabPanel = btn.up('tabpanel');
        var activeTab = tabPanel.getActiveTab();
        var checkBoxes = activeTab.getEl().query('input');
        var selectedPermissions = [];
        var deselectedPermissions = [];
        
        Security.loadingWindow = Ext.widget('progresswindow', {
             text: 'Saving Permissions...'
         });
        
        Ext.each(checkBoxes, function(box) {
            if (box.checked)
                selectedPermissions.push(Ext.decode(box.getAttribute("data-permission")));
            else
                deselectedPermissions.push(Ext.decode(box.getAttribute("data-permission")));
        });

        // Log.debug(selectedPermissions); Log.debug(deselectedPermissions);

        var selectedPermissionsArray = [];
        var deselectedPermissionsArray = [];

        Ext.each(selectedPermissions, function(newPermission) {
            if (!RoleManager.permissionsForRole.containsKey(newPermission.id)) {
                selectedPermissionsArray.push(newPermission);
                RoleManager.permissionsForRole.add(newPermission.id,'permission-'+newPermission.id);
            }
        });
                                
        Ext.each(deselectedPermissions, function(oldPermission) {
            if (RoleManager.permissionsForRole.containsKey(oldPermission.id)) {
                deselectedPermissionsArray.push(oldPermission);
                RoleManager.permissionsForRole.removeAtKey(oldPermission.id);
            }
        });


        RoleManager.addPermissionsToRole(currentRecord.data, selectedPermissionsArray, function() {
            if (deselectedPermissionsArray.length > 0)
                RoleManager.removePermissionsFromRole(currentRecord.data, deselectedPermissionsArray, function() {
                    Ext.Msg.alert('Permissions Changed',
                                  'Permissions for role "' + currentRecord.get('canonicalName') +
                                      '" changed successfully.');
                });
        });
        
        Security.removeLoadingWindow(function() {
        	Ext.Msg.alert('Permissions Changed',
                    'Permissions for role "' + currentRecord.get('canonicalName') +
                        '" changed successfully.');
        });
    },

    refreshPermissionForRole : function(btn) {
        var tabPanel = btn.up('tabpanel');
        var activeTab = tabPanel.getActiveTab();
        var currentRecord = null;
        
        var roleDetailPaneTabPanel = this.getRoleDetailPaneTabPanel();    
        if(roleDetailPaneTabPanel)
        	currentRecord = roleDetailPaneTabPanel.currentRecord;
        
        if(this.getAccountSettings())
        	currentRecord = this.getAccountSettings().currentRecord;
    	
        RoleManager.getPermissionsForRole(currentRecord, function(permissionsOfRole) {
            RoleManager.RolePermissionMap = permissionsOfRole;
            tabPanel.fireEvent('tabchange', tabPanel, activeTab);
        });
    }
});

