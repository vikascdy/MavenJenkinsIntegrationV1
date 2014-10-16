Ext.define('Security.view.group.GroupOrganizations', {
        extend:'Ext.container.Container',
        alias:'widget.grouporganizations',
        requires:[
            'Security.view.group.GroupOrgAssignments'
        ],
        minHeight:150,
        layout:'fit',
        flex:1,
		config : {
			errors : null
		},
        update:function (record,callback,scope) {            
            var me=this;
            var groupOrgsList = Ext.StoreManager.lookup('GroupOrgsList');
            groupOrgsList.getProxy().setExtraParam("id", record.get('id'));
            groupOrgsList.removeAll();
            groupOrgsList.load({
                scope: this,
                callback: function(records, operation, success) {

				me.removeAll();
				if(success && me.getErrors()==null){
                	 
                     me.add({
                          xtype:  'grouporgassignments',
                          itemId: 'orgassigned',
                          users:  records,
                          group: record
                      });
                     
        				if( me.down('grouporgassignments').getStore().getTotalCount()<10 )
           					me.down('grouporgassignments').down('#pagingToolbar').hide();
           				else
           					me.down('grouporgassignments').down('#pagingToolbar').show();
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