Ext.define('Security.controller.AuditController', {
    extend: 'Ext.app.Controller',

    stores: [
        'PermissionsTree'
    ],
    views: [
        'audit.AuditLog',
        'audit.AuditPermissions',
        'audit.PermissionsFilter',
        'audit.RolesFilter',
        'audit.UsersList'
    ],

    refs: [{
        ref: 'permissionsFilter',
        selector: 'permissionsfilter'
    },{
        ref: 'rolesFilter',
        selector: 'rolesfilter'
    },{
        ref: 'usersList',
        selector: 'audituserslist'
    }],

    init: function() {
        this.control({
            'permissionsfilter button[action=clearall]':{
                click: this.onClearAll
            },
            'permissionsfilter': {
                checkchange: this.updatePermissions
            },
            'rolesfilter': {
                selectionchange: this.updateUsers
            }
        });
    },

    onClearAll: function() {
        var checkedRecords = this.getPermissionsFilter().getView().getChecked();
        Ext.Array.each(checkedRecords, function(record){
            record.set('checked', false);
        });
        this.updatePermissions();
    },

    updatePermissions: function() {
        var records = this.getPermissionsFilter().getView().getChecked();
        var permissionIds = [];
        Ext.Array.each(records, function(permission){
            permissionIds.push(permission.get('id'));
        });
        Ext.log('filter roles by: ' + permissionIds);
        this.loadFilteredRoles(permissionIds);
    },

    loadFilteredRoles: function(permissionIds) {
        this.getRolesFilter().suspendEvents(false);
        this.getRolesFilter().store.load({
            scope   : this,
            callback: function(records, operation, success) {
                //the operation object contains all of the details of the load operation
                Ext.log('roles loaded: ' + success);
                Ext.log(records);
                this.getRolesFilter().resumeEvents();
                this.getRolesFilter().getSelectionModel().selectAll(false);
            },
            filters: [{
                property: 'permissionIds',
                value: permissionIds
            }]
        });
    },

    updateUsers: function() {
        var records = this.getRolesFilter().getSelectionModel().getSelection();
        var roleIds = [];
        Ext.Array.each(records, function(role){
            roleIds.push(role.get('id'));
        });
        Ext.log('filter users by: ' + roleIds);
        this.loadFilteredUsers(roleIds);
    },

    loadFilteredUsers: function(roleIds) {
        this.getUsersList().store.load({
            scope   : this,
            callback: function(records, operation, success) {
                //the operation object contains all of the details of the load operation
                Ext.log('users loaded: ' + success);
                Ext.log(records);
            },
            filters: [{
                property: 'roleIds',
                value: roleIds
            }]
        });
    }
});

