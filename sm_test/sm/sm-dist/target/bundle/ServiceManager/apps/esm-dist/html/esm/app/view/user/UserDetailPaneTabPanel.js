Ext.define('Security.view.user.UserDetailPaneTabPanel', {
    extend: 'Ext.tab.Panel',
    plain: true,
    alias: 'widget.userdetailpanetabpanel',
    initComponent : function() {
        this.items = [
			{
			    title:'Settings',
			    itemId:'profilesettings',
			    padding:5,
			    layout:'fit',
			    items:[]
			},
			{
			    title:'Roles Assigned',
			    itemId:'userroles',
			    padding:5,
			    layout:'fit',
			    items:[]
			},
			{
			    title:'Groups Assigned',
			    itemId:'usergroups',
			    padding:5,
			    layout:'fit',
			    items:[]
			},
			{
			    title:'Permissions',
			    padding:5,
			    layout:'fit',
			    xtype:'container',
			    itemId:'userpermissionspanel'
			}
        ];

        this.listeners = {
            'afterrender' : function() {
                this.fireEvent('tabchange',this,this.getActiveTab());
            }
        };
        this.callParent(arguments);
    },
    update:function (record,authType,readOnly, credentialObj) {
    	
    	if(record){
    			
		    	var me = this;
		    	this.currentRecord = record;
		
		    	me.reset();
		    	
		        var userRolesCont = this.down('#userroles');
		        var userGroupsCont = this.down('#usergroups');
		        var profileSettingsCont = this.down('#profilesettings');
		        
		        var userRoles = Ext.widget({
	                xtype:'userroles',
		            hideHeaders:true
	                });
		        var userGroups = Ext.widget({
	                xtype:'usergroups',
		            hideHeaders:true
                });
		        var profileSettings = Ext.widget({
	                xtype:'profilesettings'
                });
		        
		        
		        userRolesCont.removeAll();
		        userGroupsCont.removeAll();	   
		        profileSettingsCont.removeAll();
		        
		        userRolesCont.add(userRoles);
		        userGroupsCont.add(userGroups);		   
		        profileSettingsCont.add(profileSettings);
		        
		        profileSettings.update(record,authType,credentialObj,function(){
			        userRoles.update(record, function() {
			        	userGroups.update(record, function() {
				            UserManager.getPermissionsForUser(record, function(permissions, response) {
							if(permissions){
				                me.updatePermissions(permissions,function(){
				            		Security.removeLoadingWindow(function(){
				                	});
				                });
							}
							else
							{
								me.down('#userpermissionspanel').removeAll();
								me.down('#userpermissionspanel').add({
											xtype:'component',
											html:response.error
										});   
							}
				            });
			        	},this,readOnly);
			        },this,readOnly);
		        });
    	}

    },
    
    reset : function(){    
    	this.down('#profilesettings').removeAll();
    	this.down('#userroles').removeAll();
        this.down('#usergroups').removeAll();
        this.down('#userpermissionspanel').removeAll();    	
    },
    
    updatePermissions:function(permissions, callback) {
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
	            productGroups:store.getGroups()
	        });
        
        Ext.callback(callback,this,[]);

    }

});