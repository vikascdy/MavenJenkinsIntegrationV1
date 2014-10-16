Ext.define('Security.view.user.UserProfileInfo', {
    extend: 'Ext.container.Container',
    alias : 'widget.userprofileinfo',
    currentRecord:null,
    initComponent: function() {
	    this.items= [
	            {
	            	xtype:'container',
	                margins: {top: 15, right: 15, bottom: 10, left: 0},
	                layout:{type:'hbox'},
	                items:[
		                       {
		                    	   xtype:'component',
		                    	   itemId:'profileHeader',
		                    	   tpl: new Ext.XTemplate(
		                    	            '<div class="info-pane-header"><h2>{name}</h2></div><br/>'
		                    	        )
		                       },
		                       {
		                    	   xtype:'container',
		                    	   itemId:'editButtonCtr'
		                       },
		                       {
		                    	   xtype:'container',
		                    	   margin:'0 0 0 10',
		                    	   itemId:'passwordButtonCtr'
		                       }
		                       
	                       ]
	            },
		         {
		             xtype: 'userprofileprops',
		             itemId: 'userprofileprops',
		             border:0,
		             height:90,
		             margins: {top: 10, right: 15, bottom: 15, left: 0}
		         }
	    ];
	    
	this.callParent(arguments);
    },   

    update: function(record,callback) {
        this.currentRecord = record;
        
        var editButtonCtr = this.down('#editButtonCtr');
        var passwordButtonCtr = this.down('#passwordButtonCtr');
        
        this.down('#profileHeader').update(record.data);
        this.down('#userprofileprops').update(record.data);
        
       
        if(Security.authType!='LDAP'){
	        editButtonCtr.removeAll();
	        editButtonCtr.add({
                xtype: 'button',
                itemId: 'edituser',
                mode:'editProfile',
                tooltip:'Edit information',
                iconCls: 'edit'
            });
	        
	        passwordButtonCtr.removeAll();
	        passwordButtonCtr.add({
                xtype: 'button',
                itemId: 'changePassword',
                tooltip:'Change Password',
                text:'Change Password',
                handler : function(btn){
                	 var editWindow = Ext.create('Security.view.user.EditUserCredentials', {
                         userId:record.get('id'),
                         userInfo:record
                     });
                	 editWindow.show(btn.getEl());
                }
            });
        }
        
        Ext.callback(callback,this,[]);
    }

});