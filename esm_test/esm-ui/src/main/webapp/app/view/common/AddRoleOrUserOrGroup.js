Ext.define('Security.view.common.AddRoleOrUserOrGroup', {
    extend: 'Ext.window.Window',
    alias : 'widget.addroleoruserorgroup',
    width:350,
    height:450,
    border:false,
    closable:true,
    resizable:false,
    modal:true,
    autoScroll:true,
    closeAction:'destroy',
    layout:{type:'vbox',align:'stretch'},
	disableDuplicate:true,
    initComponent: function() {
    	
        var me = this;
        
//        var columnsArray = [{	                               
//	        header: 'Name',
//	        dataIndex: 'canonicalName',
//	        menuDisabled:true,
//	        flex: 1
//	    }];
//        
//        if(me.enableGrouping)
//        	columnsArray.push({
//    	        dataIndex: 'groupCanonicalName',
//    	        dataIndex: me.groupFieldName ? me.groupFieldName : 'Group Name',
//    	        menuDisabled:true,
//    	        flex: 1
//        	});
        
        this.items=[
						{
							xtype:'basegridlist',
							originalRecordIds:me.originalRecordIds,
							flex:1,
							storeName:me.store,
							enableSearch : false,
							enableCheckboxSel : true,
							extraParam:me.extraParam,
							enableGrouping : me.enableGrouping,
							groupFieldName : me.groupFieldName,
							columns:[{	                               
						        header: 'Name',
						        dataIndex: 'canonicalName',
						        menuDisabled:true,
						        flex: 1
						    }]
						}                   
                ];
                this.buttons=[
                    {
                        text:'Close',
                        handler:function(btn) {
                            btn.up('window').close();
                        }
                    },
                    {
                        text:'Add',
                        id:'addRoleOrGroupOrUser',
                        action:me.addAction
                    }
                ];        
                
                
                this.listeners = {
                		'close' : function(){
						
                			this.down('basegridlist').getStore().removeAll();
							if(Security.viewport.down('organizationroles'))
                		Security.viewport.down('organizationroles').autoGenId=true;
						}                		
                };
        
        this.callParent(arguments);
    }
});
