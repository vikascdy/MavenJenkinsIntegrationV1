Ext.define('Security.view.user.ManageUsers', {
    extend: 'Ext.container.Container',
    alias : 'widget.manageusers',
    border:0,
    minHeight:700,
    requires: [
        'Ext.layout.container.Border',
        'Security.view.user.UsersList',
        'Security.view.user.UserInfo',
        'Security.view.common.PageHeader'
    ],

    title: 'Manage User Accounts',
    layout: 'border',
    style : 'background-color: #FFFFFF !important',
    defaults: {
        collapsible: false,
        split: false
    },
    margins:{top:0, right:15, bottom:15, left:15},
    initComponent : function(){
    	
    	
	var me=this;
    	
    this.items = [
        {
            xtype: 'pageheader',
            region:'north',
            title: 'Manage User Accounts'
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
                    columns: 6,
                    itemId:'manageUserActions',
                    items:[

                        {
                            iconCls : 'user',
                            tooltip: 'Create New User',
                            action: 'newuser',
                            itemId: 'newuser'
                        },
                        {
                            iconCls : 'upload',
                            tooltip: 'Import Users',
                            action: 'bulkusers',
                            itemId: 'bulkusers'
                        },
                        {
                            iconCls : 'edit',
                            tooltip: 'Edit User',
                            action: 'edituser',
                            itemId: 'edituser',
                            disabled:true
                        },
                        {
                            iconCls: 'rename',
                            tooltip: 'Update Credentials',
                            action: 'updateuser',
                            itemId: 'updateuser',
                            disabled:true
                        },
//                         TODO: Re-enable this feature in 1.5
//                        {
//                            iconCls : 'delete',
//                            tooltip: 'Delete User',
//                            action: 'deleteuser',
//                            itemId: 'deleteuser',
//                            disabled:true
//                        },
                        {
                            iconCls : 'email',
                            tooltip: 'Send Password Reset Email',
                            action: 'emailuser',
                            itemId: 'emailuser',
                            disabled:true,
                            hidden : !Security.isEmailServiceEnabled
                        }

                    ]
                },
                {
                    xtype: 'userslist',
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
            xtype:'userinfo',
            margin:'0 0 0 10',
            region:'center',
        }
    ];
    
    this.callParent(arguments);
    
    }

});