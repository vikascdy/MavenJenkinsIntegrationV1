Ext.define('Security.view.user.UserGroups', {
		extend:'Ext.panel.Panel',
	    layout:'fit',
	    flex:1,
        alias:'widget.usergroups',
        requires:[
            'Security.view.user.UserGroupAssignments'
        ],
		config : {
			errors : null
		},
        update:function (record,callback,scope,readOnly) {
            var me=this;
            
            var userGroupsList = Ext.StoreManager.lookup('UserGroupsList');
            userGroupsList.getProxy().setExtraParam("userId", record.get('id') );
            userGroupsList.load({
                scope: this,
                callback: function(records, operation, success) {
			    me.removeAll();
				if(success && me.getErrors()==null){
	                   me.add({
	                        xtype:  'usergroupassignments',
	                        readOnly : readOnly,
	                        itemId: 'rolesassigned',
	                        groups:  records
	                    }); 
	                   
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