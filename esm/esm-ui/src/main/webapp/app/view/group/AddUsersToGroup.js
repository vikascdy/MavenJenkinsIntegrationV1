Ext.define('Security.view.group.AddUsersToGroup', {
    extend: 'Ext.window.Window',
    alias : 'widget.adduserstogroup',
    title:'Available Users',
    width:350,
    height:450,
    border:false,
    closable:true,
    resizable:false,
    modal:true,
    autoScroll:true,
    closeAction:'destroy',
	layout:{type:'vbox',align:'stretch'},
    initComponent: function() {
    	
        var me = this;
        
        this.items=[
						{
							xtype:'basegridlist',
							originalRecordIds:me.originalRecordIds,
							flex:1,
							storeName:me.store,
							enableSearch : true,
							enableCheckboxSel : true,
							columns:[{	                               
                                header: 'Name',
                                dataIndex: 'name',
                                menuDisabled:true,
                                flex: 1
                            }] 
						}
                    
                    
//                    {
//    				xtype:'form',
//    				height:30,
//    				layout:{type:'hbox',align:'stretch'},
//    				items:[
//	    				       {
//	    				    	   xtype:'textfield',
//	    				    	   itemId:'searchStr',
//	    				    	   flex:1,
//	    				    	   emptyText:'Search Users'
//	    				       },
//	    				       {
//	    				    	   xtype:'button',
//	    				    	   action:'searchUsers',
//	    				    	   tooltip:'Search User',
//	    				    	   iconCls:'search'
//	    				       }
//    				       ]
//        			},
//                    {
//	                    xtype: 'grid',
//	                    flex:1,
//	                    autoScroll:true,
//	                    selModel: {
//	                        selType: 'checkboxmodel',
//	                        mode: 'MULTI'
//	                    },
//	                    store: 'UsersSearchList',
//	                    columns : [
//	                               {
//	                                   header: 'Name',
//	                                   dataIndex: 'name',
//	                                   menuDisabled:true,
//	                                   flex: 1
//	                               }
//	                           ],
//	                    flex:1
//                    },
//                    {
//                   	  dock: 'bottom',
//                       xtype: 'pagingtoolbar',
//                       store: 'UsersSearchList',  
//                       dock: 'bottom',
//                       displayInfo: false,
//                       listeners: {
//                           beforechange: function(paging, page) {
//                        	   me.down('grid').getStore().getProxy().setExtraParam('seed', me.down('#searchStr').getValue());
//                           }
//                       }
//                   }
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
                        action:'addUsersToGroup'
                    }
                ];        
                
                this.listeners = {
                		'close' : function(){
                			this.down('basegridlist').getStore().removeAll();
                		}
                		
                };
        
        this.callParent(arguments);
    }
});
