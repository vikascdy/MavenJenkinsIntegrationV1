Ext.define('Security.view.audit.AuditLog', {
    extend:'Ext.panel.Panel',
    alias:'widget.auditlog',

    requires: [
        'Security.store.PermissionTabs',
        'Security.view.common.EditPermissions',
        'Security.view.common.PageHeader'
    ],

    title: 'Audit Log Settings',
    preventHeader: true,
    margins:{top:0, right:15, bottom:15, left:15},
    border:0,

    layout:{
        type:'vbox',
        align:'stretch',
        defaultMargins:{top:10, right:10, bottom:10, left:10}
    },

    constructor: function() {
        Ext.apply(this, {
            items:[
                {
                    xtype:'pageheader',
                    title:'Audit Log Settings'
                },
                {
                    xtype:'editpermissions',
                    itemId:'permissions',
                    flex:1,
                    treeStoreUrl:'security-data/audit/log/permissions',
                    tabsStore:Ext.create('Security.store.PermissionTabs', {}, this),
                    readOnly:!true,
                    showHeader:false,
                    listeners:{
                        savePermissions:{
                            fn:function (checkedPermissions, uncheckedPermissions) {
                                Ext.log("Save audit log permissions...");
                                Ext.Ajax.request({
                                    url:'security-data/auditLog/permissions',
                                    params:{
                                        checkedPermissions:checkedPermissions,
                                        uncheckedPermissions:uncheckedPermissions
                                    },
                                    success:function (response) {
                                        Ext.log("Changed permissions saved with success.");
                                    }
                                });
                            }
                        }
                    }
                }
            ]
        });
        this.callParent();
    }
});
