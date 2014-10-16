Ext.define('Security.view.group.GroupRoles', {
        extend:'Ext.container.Container',
        alias:'widget.grouproles',
        requires:[
            'Security.view.group.GroupRoleAssignments'
        ],
        minHeight:150,
        layout:'fit',
        flex:1,
		config : {
			errors : null
		},
        update:function (record,callback,scope) {
            var me=this;
            var groupsRolesList = Ext.StoreManager.lookup('GroupRolesList');
            groupsRolesList.getProxy().setExtraParam("groupId", record.get('id'));
            groupsRolesList.load({
                scope: this,
                callback: function(records, operation, success) {
				me.removeAll();
				if(success && me.getErrors()==null){
	            	   
	                   me.add({
	                        xtype:  'grouproleassignments',
	                        itemId: 'rolesassigned',
	                        roles:  records,
	                        group: record
	                    }); 
	                   
                   
           				if( me.down('grouproleassignments').getStore().getTotalCount()<10 )
           					me.down('grouproleassignments').down('#pagingToolbar').hide();
           				else
           					me.down('grouproleassignments').down('#pagingToolbar').show();
							Ext.callback(callback,this);
           			}
					else{
							me.add({
								xtype:'container',
								html:me.getErrors()
							});
							Ext.callback(callback,this);
						}				
	                   
                }
            });
        }
    }
);