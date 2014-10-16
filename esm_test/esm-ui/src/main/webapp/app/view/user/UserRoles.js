Ext.define('Security.view.user.UserRoles', {
        extend:'Ext.panel.Panel',
        layout:'fit',
        flex:1,
        alias:'widget.userroles',
        requires:[
            'Security.view.user.UserRoleAssignments'
        ],
		config : {
			errors : null
		},
        update:function (record,callback,scope, readOnly) {
            var me=this;
            
            var userRolesList = Ext.StoreManager.lookup('UserAssignedRolesList');
            userRolesList.getProxy().setExtraParam("userId", record.get('id') );
            userRolesList.load({
                scope: this,
                callback: function(records, operation, success) {
			    me.removeAll();
				if(success && me.getErrors()==null){
	                   me.add({
	                        xtype:  'userroleassignments',
	                        readOnly : readOnly,
	                        itemId: 'rolesassigned',
	                        roles:  records
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