Ext.define('Security.view.user.AddUser', {
    extend: 'Ext.window.Window',
    alias : 'widget.adduser',

    requires: [
        'Ext.form.Panel',
        'Ext.form.FieldSet'
    ],

    title:'Create User',
    width:350,
    height:500,
    layout:'fit',
    resizable:false,
    modal:true,
    initComponent : function(){
    	
   	
    this.items=[
        {
            xtype:'form',

            id:'adduserform',
            fieldDefaults: {
                labelAlign: 'left',
                labelWidth: 110,
                anchor: '100%'
            },
            bodyPadding:10,
            items:[
                {
                    xtype:'fieldset',
                    title:'User Info',
                    items: [
                        {
                            xtype:'textfield',
                            name:'username',
                            maxLength:60,
                            fieldLabel:'Username',
                            allowBlank:false,
                            regex: /^[a-zA-Z]+$/,
                            regexText: 'Invalid chars',
                        },
                        {
                            xtype:'passwordmeter',
                            config:{
	                            name:'password',
	                            id:'userPassword',
	                            inputType: 'password',
	                            fieldLabel:'Password',
	                            vtype:'password',
	                            allowBlank:false,
	                            enableFieldFocus:false
                            }
                        },
                        {
                            xtype:'textfield',
                            name:'confirmPassword',
                            id:'userPassword2',
                            maxLength:20,
                            inputType: 'password',
                            fieldLabel:'Confirm Password',
                            vtype:'password',
                            initialPassField: 'userPassword',
                            allowBlank:false
                        },
                        {
                            xtype: 'checkbox',
                            inputValue:true,
                            uncheckedValue:false,
                            name: 'active',
                            checked:true,
                            boxLabel: 'Active',
                            fieldLabel: 'Status'
                        }
                    ]
                },
                {
                    xtype:'fieldset',
                    title:'Contact Info',
                    items:[
                        {
                            xtype:'textfield',
                            name:'firstName',
                            maxLength:20,
                            regex: /^[a-zA-Z]+$/,
                            regexText: 'Invalid chars',
                            fieldLabel:'First Name',
                            allowBlank:false
                        },
                        {
                            xtype:'textfield',
                            name:'middleName',
                            maxLength:20,
                            regex: /^[a-zA-Z]+$/,
                            regexText: 'Invalid chars',
                            fieldLabel:'Middle Name',
                            defaultValue:''
                        },
                        {
                            xtype:'textfield',
                            name:'lastName',
                            maxLength:19,
                            regex: /^[a-zA-Z]+$/,
                            regexText: 'Invalid chars',
                            fieldLabel:'Last Name',
                            allowBlank:false
                        },
                        {
                            xtype:'textfield',
                            name:'salutation',
                            maxLength:20,
							regex: /^[a-zA-Z]+$/,
                            regexText: 'Invalid chars',
                            fieldLabel:'Title',
                            allowBlank:true
                        },
                        {
                            xtype:'textfield',
                            name:'emailAddress',
                            fieldLabel:'Email',
                            maxLength:100,
                            vtype: 'email',
                            allowBlank:false
                        }
                    ]
                }
            ]
        }
    ];
    this.buttons=[
        {
            text:'Cancel',
            width:80,
            handler:function() {
                this.up('window').close();
            }
        },
        {
            text:'Save',
            ui:'greenbutton',
            width:80,
            action: 'saveNewUser'
        }
    ];
    
    this.callParent(arguments);
    }

});

