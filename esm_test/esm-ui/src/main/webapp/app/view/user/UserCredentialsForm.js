Ext.define('Security.view.user.UserCredentialsForm', {
    extend: 'Ext.form.Panel',
    alias : 'widget.usercredentialsform',
	fieldDefaults: {
	    labelAlign: 'left',
	    labelWidth: 150,
	    anchor: '100%'
	},
	bodyPadding:10,
	initComponent : function(){
	var me=this;
	this.items=[
            {
            	hidden:me.mode=='userProfile',
                xtype:'displayfield',
                name:'username',
                value:me.userInfo.username,
                fieldLabel:'Username',
                allowBlank:false
            },
            {
                 xtype:'textfield',
                 vtype:'regeXPassword',
				 name:'password',
				 id:'userPassword',
				 inputType: 'password',
				 fieldLabel:'New Password',
				 allowBlank:false,
				 anchor:'100%',
				 msgTarget :'side',
				 enableFieldFocus:false	                   
             },
             {
                 xtype:'textfield',
                 name:'confirmPassword',
                 id:'userPassword2',
                 msgTarget:'qtip',
                 inputType: 'password',
                 fieldLabel:'Confirm New Password',
                 vtype:'password',
                 anchor:'100%',
                 initialPassField: 'userPassword',
                 allowBlank:false
             }
        ];
	
		this.buttons=[
	         {
	             text:'Cancel',
	             hidden:me.mode=='userProfile',
	             width:80,
	             handler:function() {
	                 this.up('window').close();
	             }
	         },
	         {
	             text:'Update',
	             formBind:true,
	             itemId:'updateCred',
	             ui:'greenbutton',
	             width:80,
	             action: 'updateCred'
	         }
	     ];

        this.callParent(arguments);
    }
});

