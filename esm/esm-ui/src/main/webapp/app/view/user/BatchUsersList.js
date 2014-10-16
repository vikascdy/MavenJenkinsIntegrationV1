Ext.define('Security.view.user.BatchUsersList', {
    extend: 'Ext.window.Window',
    alias : 'widget.batchuserslist',
    title:'Users List',
    width:500,
    height:450,
    border:false,
    defaults     : { flex : 1 },
    closable:true,
    resizable:false,
    modal:true,
    layout:'fit',
    autoScroll:true,
    initComponent: function() {
    	
        var me = this;
        
    	var userStore = Ext.create('Ext.data.Store',{
             fields :[ 
                       {name:'username', type:'string'},
                       {name:'lineNumber', type:'integer'},
                       {name:'name', type:'string'},
                       {name:'value', type:'string'},
                       {name:'valid', type:'boolean'},
                       {name:'error', type:'string'},
                      ],
             sortOnLoad: true,
             sorters: { property: 'lineNumber', direction : 'ASC' },
             autoLoad: false,
	         	proxy:{
	         		type:'memory',
	         		 reader: {
	                      type: 'json'
	                  }
	         	}
         });

    	userStore.removeAll();
    	
        Ext.each(me.result,function(user){
        	var userStr=user['line'].split(',');
        	userStore.add({
        		username:userStr[0],
        		name:userStr[2]+' '+userStr[3]+' '+userStr[4],
        		lineNumber:user['lineNumber'],
        		value:user['line'],
        		valid:user['valid'],
        		error:user['valid']==true ? 'Valid' : user['error']
        	});
        });
        
        
        
        
        this.items=[
                    {
                    xtype: 'grid',
                    autoScroll:true,
                    selModel: {
                    	showHeaderCheckbox : false,
                    	checkOnly:true,
                        selType: 'checkboxmodel',
                        mode: 'MULTI',
                        listeners : {
                        	beforeselect : function(selModel,record){
                        		if(!record.get('valid'))
                        			{
                            		Functions.errorMsg("User with errors cannot be selected.", "Invalid Selection");
                            		return false;
                        			}
                        		else
                        			return true;
                        		
                        	}
                        }
                    },
                    store: userStore,
                    listeners : {
                    	render : function(){
                    		var selectionModel = this.getSelectionModel();
                    		this.getStore().each(function(rec){
                    			if(rec.get('valid')){
                            		selectionModel.select([rec],true,true);
                    			}
                    		});
                    	}
                    },
                    columns : [
                               {
                                   header: 'Status',
                                   width:60,
                                   dataIndex: 'valid',
                                   menuDisabled:true,
                                   renderer : function(v,m,r){
                            		   m.tdAttr = 'data-qtip="' + r.get('error')+'"' ;                               	   
                                   	   return Ext.String.format("<div class='{0} icon16' style='cursor:pointer;' ></div>", v ? 'success-status' : 'fail-status');
                                   }
                               },
                               {
                                   header: 'Line No.',
                                   dataIndex: 'lineNumber',
                                   align:'center',
                                   menuDisabled:true,
                                   flex:1
                               },
                               {
                                   header: 'User Name',
                                   dataIndex: 'username',
                                   menuDisabled:true,
                                   flex:2
                               },
                               {
                                   header: 'Name',
                                   dataIndex: 'name',
                                   menuDisabled:true,
                                   flex: 2
                               }
                           ],
                    flex:1
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
                        text:'Import',
                        itemId:'importUsers'
                    }
                ];        
        
        this.callParent(arguments);
    }
});
