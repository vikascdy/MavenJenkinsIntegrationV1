Ext.define('Security.view.core.ResetPassword', {
    extend : 'Ext.container.Container',
    alias : 'widget.resetpassword',
    title : 'Login',
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
                hidden:true,
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
                        	 hidden:true,
                        	 name:'token',
                        	 value:me.token
                         },  
                         {
                        	 xtype:'textfield',
      	                     vtype:'regeXPassword',
                        	 name:'newPasswd',
                             id:'newPasswd',
                             inputType: 'password',
                             fieldLabel:'New Password',
                             vtype:'password',
                             allowBlank:false,
                             cls : 'login-field',
                             labelCls : 'login-label',
                             fieldCls : 'login-text',
                             labelAlign : 'top',
                             enableFieldFocus:true                                                             	 
                         },
                         {
                        flex : 1,
                        name : 'confirmNewPasswd',
                        id : 'newPasswd2',
                        maxLength:20,
                        vtype:'password',    
                        msgTarget:'qtip',
                        inputType : 'password',
                        initialPassField: 'newPasswd',
                        fieldLabel : 'Confirm New Password',
                        allowBlank:false,
                        cls : 'login-field',
                        labelCls : 'login-label',
                        fieldCls : 'login-text',
                        labelAlign : 'top',
                        listeners : {
                            specialkey : function(field, e) {
                                if (field.up('form').getForm().isValid() && e.getKey() == Ext.EventObject.ENTER) {
                                    var btn = field.up('form').down('#updatePassword');
                                    btn.fireEvent('click', btn,me);
                                }
                            }
                        }
                    }, {
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
                    },{
                        xtype : 'component',
                        html:'Back to Login',
                        cls : 'login-link',
                        margin:'15 0 0 0',
                        listeners : {
                            render : function(c) {
                                c.getEl().on('click', function(e) {
                                	window.location.href='#!/LoginPage';
                                }, c);
                            }
                        }
                    } ]
                } ]
            } ]
        } ];

        this.callParent(arguments);
    }
});
