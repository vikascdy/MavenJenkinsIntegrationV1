Ext.define('Security.view.group.GroupUsers', {
        extend:'Ext.container.Container',
        alias:'widget.groupusers',
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
            var groupsUsersList = Ext.StoreManager.lookup('GroupUsersList');
            groupsUsersList.getProxy().setExtraParam("groupId", record.get('id'));
            groupsUsersList.removeAll();
            groupsUsersList.load({
                scope: this,
                callback: function(records, operation, success) {
				me.removeAll();
				if(success && me.getErrors()==null){
				
                     me.add({
                          xtype:  'groupuserassignments',
                          itemId: 'usersassigned',
                          users:  records,
                          group: record
                      });
                     
        				if( me.down('groupuserassignments').getStore().getTotalCount()<10 )
           					me.down('groupuserassignments').down('#pagingToolbar').hide();
           				else
           					me.down('groupuserassignments').down('#pagingToolbar').show();
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