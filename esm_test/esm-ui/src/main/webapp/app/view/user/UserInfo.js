Ext.define('Security.view.user.UserInfo', {
    extend: 'Ext.container.Container',
    alias : 'widget.userinfo',

    requires: [
        'Security.store.PermissionTabs',
        'Security.view.user.UserProps',
        'Security.view.user.UserRoles',
        'Security.view.user.UserGroups'
    ],

    currentRecord:null

    ,layout : {
        type:'vbox',
        align:'stretch',
        defaultMargins: {top: 0, right: 15, bottom: 0, left: 15}
    }

    ,items: [
        {
            xtype: 'userprops',
            itemId: 'userprops',
            border:0,
            height:90,
            margins: {top: 0, right: 15, bottom: 15, left: 16}
        },
        {
        	xtype:'container',
        	layout:'hbox',
        	items:[
			        {
			            xtype: 'userroles',
			            itemId: 'userroles',
			            margins:{top:0, right:15, bottom:0, left:0},
						width: "49%",
			            border:0
			        },
			        {
			            xtype: 'usergroups',
			            itemId: 'usergroups',
						margins:{top:0, right:0, bottom:0, left:0},
						width: "49%",
			            border:0
			        }
			       ]
        },
        {
        	xtype:'container',
        	itemId:'showPermissionBtn',
        	hidden:true,
        	items:[
						{
							xtype:'button',
							width:150,
							margin:'20 0 10 0',
							ui:'bluebutton',
							enableToggle:true,
							text:'Show Permissions',
							listeners : {
								'toggle' : function(btn,pressed){
									var permissionPanel = btn.up('userinfo').down('#userpermissionspanel');
									if(pressed) {
										permissionPanel.show();
										this.setText('Hide Permissions');
									}
									else
									{
										permissionPanel.hide();
										this.setText('Show Permissions');
									}
										
								}
							}
						}
        	       ]
        },
        {
            xtype:'container',
            hidden:true,
            itemId:'userpermissionspanel',
            layout:'fit',
            flex:1,
            border :0
        }
    ],

    updatePermissions: function(permissions) {
        var permissionsContainer = this.down('#userpermissionspanel');
        if(permissionsContainer)
        	permissionsContainer.removeAll();
        var store = Ext.create('Ext.data.Store', {
            model: 'Security.model.Permission',
            autoLoad:true,
            data:permissions,
            groupField:'productCanonicalName'
        });

        if(permissionsContainer)
	        permissionsContainer.add({
	            xtype:'editpermissions',
	            readOnly:true,
	            productGroups:store.getGroups(),
	            headerCaption: 'Permissions'
	        });

    },

    update: function(record) {
        var me = this;
        this.currentRecord = record;
        var data = record ? record.data : {};
        this.down('#showPermissionBtn').show();
        this.down('#userprops').update(data);
        this.down('#userroles').update(record, function() {
        	me.down('#usergroups').update(record, function() {
	            UserManager.getPermissionsForUser(record, function(permissions) {
	                me.updatePermissions(permissions);
	            });
        	},this);
        },this);
    }
});