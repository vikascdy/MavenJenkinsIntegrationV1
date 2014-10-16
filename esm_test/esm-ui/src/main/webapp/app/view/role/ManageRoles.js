Ext.define('Security.view.role.ManageRoles', {
    extend: 'Ext.panel.Panel',
    alias : 'widget.manageroles',
    minHeight:700,
    requires: [
        'Security.view.common.PageHeader'
    ],

    title: 'Manage roles',
    preventHeader: true,
    border:0,

    layout:{
        type:'border'
    },
    margins:{top:0, right:15, bottom:15, left:15},
    defaults: {
        collapsible: false,
        split: false
    },
    items: [
        {
            xtype: 'pageheader',
            title:'Manage Roles',
            region:'north'

        },
        {
            xtype:'container',
            region:'west',
            layout:{
                type:'vbox'
            },
            items:[
                {
                    xtype:'buttongroup',
                    columns:4,
                    itemId:'manageRolesActions',
                    items:[
                        {
                            iconCls : 'user',
                            tooltip: 'Create New Role',
                            itemId: 'newrole',
                            hidden:!SecurityConfig.permissions.roles.add
                        },
                        {
                            iconCls : 'edit',
                            tooltip: 'Edit Role',
                            action: 'editrole',
                            itemId: 'editrole',
                            disabled:true
                        },
                        {
                            iconCls : 'copy',
                            tooltip: 'Copy Role',
                            action: 'copyrole',
                            itemId: 'copyrole',
                            disabled:true
                        },
                        {
                            iconCls : 'delete',
                            tooltip: 'Delete Role',
                            action: 'deleterole',
                            itemId: 'deleterole',
                            disabled:true
                        }
                    ]
                },
                {
                    xtype: 'roleslist',
                    margin:'10 0 0 0',
                    flex:1,
                    width: 330,
                    minSize: 100,
                    maxSize: 250,
                    collapsible:false
                }
            ]
        },
        {
            xtype:'roleinfo',
            margin:'0 0 0 10',
            region:'center'
        }
    ]

});