Ext.define('Security.view.core.ChangePassword', {
    extend : 'Ext.container.Container',
    alias : 'widget.changepassword',
    title : 'Change Password',
    autoScroll : false,
    initComponent : function() {
    	var me=this;
        this.items = [ {
            xtype : 'container',
            cls : 'login-page',
            layout : {
                type : 'absolute'
            },
            items : [ {
                xtype : 'component',
                cls : 'login-logo'
            }, {
                xtype : 'label',
                itemId : 'messageHolder',
                cls : 'login-message',
                text:'As per the password policy, you are requested to change the password at first login.',
                hideMode : 'visibility'
            }, {
                xtype : 'container',
                id : 'login-panel-1',
                cls : 'login-form',
                plain : 'true',
                layout : {
                    type : 'absolute'
                },
                items : [ {
                    xtype : 'component',
                    cls : 'login-form-header'
                }, {
                    xtype : 'form',
                    border : false,
                    defaultType : 'textfield',
                    layout : {
                        type : 'vbox'
                    },
                    items : [ 
						{
						    	xtype:'textfield',
	      	                    vtype:'regeXPassword',
						        name:'password',
						        id:'userPassword',
						        inputType: 'password',
						        fieldLabel:'New Password',
						        allowBlank:false,
						        cls : 'login-field',
	                            labelCls : 'login-label',
	                            fieldCls : 'login-text',
	                            labelAlign : 'top',
	                            enableFieldFocus:true     
						},
						{
						    xtype:'textfield',
						    name:'confirmPassword',
						    msgTarget:'qtip',
						    id:'userPassword2',
						    inputType: 'password',
						    fieldLabel:'Confirm New Password',
						    vtype:'password',
						    initialPassField: 'userPassword',
						    allowBlank:false,
	                        cls : 'login-field',
	                        labelCls : 'login-label',
	                        fieldCls : 'login-text',
	                        labelAlign : 'top',
						    listeners : {
						        specialkey : function(field, e) {
						            if (field.up('form').getForm().isValid() && e.getKey() == Ext.EventObject.ENTER) {
						                	var btn = null;                            
						                	btn = me.down('#updatePassword');
						        			btn.fireEvent('click', btn,me);
						            	}
						            }
						        }
						},
						{
                        xtype : 'component',
                        html:'Change Password',
                        cls : 'login-link',
                        itemId : 'updatePassword',
                        margin:'15 0 0 0',
                        listeners : {
                        	render : function(c) {
                                c.getEl().on('click', function(e) {
                                   var btn = c.up('form').down('#updatePassword');
                                    btn.fireEvent('click', btn,me);
                                }, c);
                            }
                        }
                    }
				  ]
                } ]
            } ]
        } ];

        this.callParent(arguments);
    }
});
