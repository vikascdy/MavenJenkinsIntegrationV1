Ext.define('Security.view.user.AccountSettings', {
    extend: 'Ext.panel.Panel',
    alias : 'widget.accountsettings',
    title:null,
    currentRecord:null,
    padding:0,
    layout:'border',
    defaults : {
        style:{
            backgroundColor:'#FFF!important'
        }
    },
    initComponent: function() {
	    this.items= [
        {
            xtype:'container',
            region:'north',
            items:[
                  {
                	   xtype:'container',
                	   padding:'20 0 0 20',
                	   layout:'hbox',
                	   items:[
                	          
                              {
                                  xtype:'label',
                                  height:30,
                                  itemId:'userNameHeader',
                                  text:'No User Selected',
                                  cls:'detailPaneHeading'
                               },
		                       {
		                    	   xtype:'container',
		                    	   margin:'4 0 0 10',
		                    	   id:'editUserProfile',
		                    	   itemId:'editButtonCtr'
		                       },
		                       {
		                           xtype: 'button',
		                           hidden:true,
		                           itemId: 'edituser'
		                       }
			               ]
                },
                {
                    xtype:'accountsettingsprop',
                    height:100,
                    padding:'0 20 0 20'
                }
            ]
        },
        {
            xtype:'userdetailpanetabpanel',
            itemId:'settingsTabPanel',
            margin:'20 0 0 0',
            region:'center',
            flex:1
        }
    ];
    
	this.callParent(arguments);
	
    },      

    update: function(record, readOnly, callback) {
    	var me=this;
        this.currentRecord = record;
        this.readOnly = readOnly;
        
    	UserManager.getUserAuthType(record.get('id'),function(authType, credentialObj){
		        var editButtonCtr = me.down('#editButtonCtr');
		        
		        var nameHeader = record.get('name')+' ('+record.get('username')+')';
		        
		        me.down('#userNameHeader').setText(nameHeader);
		        
		        if(me.down('accountsettingsprop'))
				{
		        	me.down('accountsettingsprop').update(record.data); 
					me.down('accountsettingsprop').show();  					
				}
		       
		        if(me.down('userdetailpanetabpanel'))
		        	me.down('userdetailpanetabpanel').update(record,authType,readOnly, credentialObj);
	       
		        if(authType!='LDAP'){
			        editButtonCtr.removeAll();
					if(record.data.username!='system' && record.data.username!='admin' ){
			        editButtonCtr.add({
		                xtype: 'component',
		                html:'<img src="resources/images/edit.png" style="cursor:pointer" class="editUser" id="manageTenant-editUser" />',
		                listeners :{
		                	'afterrender' : function(){
		                		 this.getEl().on('click', function(e, t, opts) {
		                             e.stopEvent();
		                             var btn = me.down('#edituser');
		                             btn.fireEvent('click',btn);
		                         }, null, {delegate: '.editUser'});
		                	}
		                }
		            });	        
		        }
				}
		        
		        Ext.callback(callback,this,[]);
		        
    	});
    },
    
    reset : function(){
		this.down('#userNameHeader').setText('No User Selected');
        this.down('accountsettingsprop').hide();  
        this.down('userdetailpanetabpanel').reset();
    }

});
