Ext.define('Security.view.group.ManageGroups', {
    extend: 'Ext.container.Container',
    alias : 'widget.managegroups',
    border:0,
    minHeight:700,
    requires: [
        'Ext.layout.container.Border',
        'Security.view.group.GroupsList',
        'Security.view.group.GroupInfo',
        'Security.view.common.PageHeader'
    ],
    style : 'background-color: #FFFFFF !important',
    title: 'Manage Groups',
    layout: 'border',
    defaults: {
        collapsible: false,
        split: false
    },
    margins:{top:0, right:15, bottom:15, left:15},
    items: [
        {
            xtype: 'pageheader',
            region:'north',
            title: 'Manage Groups'
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
                    itemId:'manageGroupActions',
                    items:[

                        {
                            iconCls : 'user',
                            tooltip: 'Create New Group',
                            action: 'newgroup',
                            itemId: 'newgroup'
                        },
                        {
                            iconCls : 'edit',
                            tooltip: 'Edit Group',
                            action: 'editgroup',
                            itemId: 'editgroup',
                            disabled:true
                        },
                        {
                            iconCls : 'delete',
                            tooltip: 'Delete Group',
                            action: 'deletegroup',
                            itemId: 'deletegroup',
                            disabled:true
                        }

                    ]
                },
                {
                    xtype: 'groupslist',
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
            xtype:'groupinfo',
            margin:'0 0 0 10',
            region:'center'
        }
    ]

});