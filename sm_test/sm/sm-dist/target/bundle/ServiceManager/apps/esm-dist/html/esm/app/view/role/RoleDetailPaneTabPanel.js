Ext.define('Security.view.role.RoleDetailPaneTabPanel', {
    extend: 'Ext.tab.Panel',
    plain: true,
    alias: 'widget.roledetailpanetabpanel',
    initComponent : function() {
        this.items = [
            {
                title:'Include Roles',
                itemId:'includeroles',
                padding:5,
                layout:'fit',
                items:[]
            },
            {
                title:'Users With Role',
                itemId:'memberofroles',
                padding:5,
                layout:'fit',
                items:[]
            },
            {
                title:'Role Permissions',
                padding:5,
                layout:'fit',
                xtype:'container',
                itemId:'rolepermissions'
            }
        ];

        this.listeners = {
            'afterrender' : function() {
                this.fireEvent('tabchange',this,this.getActiveTab());
            }
        };
        this.callParent(arguments);
    },
    update:function (record) {
    	
    	if(record){
    			Security.loadingWindow = Ext.widget('progresswindow', {text: 'Loading Role Details...'});
		    	var me = this;
		    	this.currentRecord = record;
		
		        RoleManager.getAllPermissions(function(permissions) {
		            me.updatePermissions(permissions,record);
		        });
		
		        var memberRolesCont = this.down('#includeroles');
		        var memberofrolesCont = this.down('#memberofroles');

		        var memberRoles = Ext.widget({
	                xtype:'includeroles',
		            hideHeaders:true
	                });
		        var memberofroles = Ext.widget({
	                xtype:'memberofroles',
		            hideHeaders:true
                });
		        
		        memberRolesCont.removeAll();
		        memberofrolesCont.removeAll();	
		        
		        memberRolesCont.add(memberRoles);
		        memberofrolesCont.add(memberofroles);		        
		        
		        
		        memberRoles.getStore().load({
		        	params : {
		        		data : '{"role":'+Ext.encode(record.data)+',"startRecord":0,"recordCount":-1}',   
		        	},
		        	callback: function(records, operation, success) {
		        		memberofroles.getStore().load({
				        	params : {
				        		data : '{"roleId":'+Ext.encode(record.get('id'))+',"startRecord":0,"recordCount":-1}'
				        	},
				        	callback: function(records, operation, success) {
				        		Security.removeLoadingWindow(function(){
									
                            	});				        		
				            }
				        });
		            }
		        });
		        
		        
    	}

    },
    
    reset : function(){    
    	this.down('#includeroles').removeAll();
        this.down('#memberofroles').removeAll();
        this.down('#rolepermissions').removeAll();    	
    },
    
    updatePermissions: function(permissions,record) {
        var permissionsContainer = this.down('#rolepermissions');
        permissionsContainer.removeAll();

        var store = Ext.create('Ext.data.Store', {
            model: 'Security.model.Permission',
            autoLoad:true,
            data:permissions,
            groupField:'productCanonicalName'
        });

        permissionsContainer.add({
            xtype:'editpermissions',
            grayed:record.get('readOnly'),
			readOnly:record.get('readOnly'),
            productGroups:store.getGroups(),
            //headerCaption: 'Permissions',
            record:record
        });
    }

});