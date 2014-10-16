Ext.define('Security.view.audit.AuditPermissions', {
    extend: 'Ext.container.Container',
    alias : 'widget.auditpermissions',
    title: 'Audit Permissions',
    minHeight:700,
    requires: [
        'Security.view.audit.PermissionsFilter',
        'Security.view.audit.UsersList',
        'Security.view.audit.RolesFilter'
    ],

    layout: {
        type: 'vbox'
        ,align: 'stretch'
    },
    margins:{top:0, right:15, bottom:15, left:15},
    border:0,

    items: [
        {
            xtype:'pageheader',
            title:'Audit Permissions'
        },
        {
            xtype:'container',
            flex:1,
            border:0,
            layout:{
                type:'hbox'
                ,align:'stretch'
            },
            defaults:{
                padding:{top:10, right:10, bottom:10, left:10}
            },
            items:[
                {
                    xtype: 'permissionsfilter',
                    width: 270
                },
                {
                    xtype: 'audituserslist',
                    flex: 1,
                    margin:'0 20 0 20'
                },
                {
                    xtype: 'rolesfilter',
                    width: 220
                }
            ]
        }
    ]

});
