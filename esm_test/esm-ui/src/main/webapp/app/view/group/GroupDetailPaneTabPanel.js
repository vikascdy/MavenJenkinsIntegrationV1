Ext.define('Security.view.group.GroupDetailPaneTabPanel', {
    extend: 'Ext.tab.Panel',
    plain: true,
    alias: 'widget.groupdetailpanetabpanel',
    initComponent : function() {
        this.items = [
            {
                title:'Roles Assigned',
                itemId:'grouproles',
                padding:5,
                layout:'fit',
                items:[]
            },
            {
                title:'Users Included',
                itemId:'groupusers',
                padding:5,
                layout:'fit',
                items:[]
            },
            {
                title:'Organizations Included',
                itemId:'grouporganizations',
                padding:5,
                layout:'fit',
                items:[]
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
    			Security.loadingWindow = Ext.widget('progresswindow', {text: 'Loading Group Details...'});
		    	var me = this;
		    	this.currentRecord = record;
		
		        var groupRolesCont = this.down('#grouproles');
		        var groupUsersCont = this.down('#groupusers');
		        var groupOrgsCont = this.down('#grouporganizations');
		        
		        var groupRoles = Ext.widget({
	                xtype:'grouproles',
		            hideHeaders:true
	                });
		        var groupUsers = Ext.widget({
	                xtype:'groupusers',
		            hideHeaders:true
                });
		        var groupOrgs = Ext.widget({
	                xtype:'grouporganizations',
		            hideHeaders:true
                });
		        
		        
		        groupRolesCont.removeAll();
		        groupUsersCont.removeAll();
		        groupOrgsCont.removeAll();	
		        
		        groupRolesCont.add(groupRoles);
		        groupUsersCont.add(groupUsers);		
		        groupOrgsCont.add(groupOrgs);		
		        
		        
		        groupRoles.update(record, function() {
		        	groupUsers.update(record, function() {
		        		groupOrgs.update(record, function() {
		        			Security.removeLoadingWindow(function(){
                        	});
			            });
		            });
		        });
    	}
    },
    
    reset : function(){    
    	this.down('#grouproles').removeAll();
        this.down('#groupusers').removeAll();
    },
});