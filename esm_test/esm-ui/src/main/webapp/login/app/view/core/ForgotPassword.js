Ext.define('Security.view.core.ForgotPassword', {
    extend : 'Ext.container.Container',
    alias : 'widget.forgotpassword',
    title : 'Forgot Password',
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
                hidden : true,
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
                                 name:'emailAddress',
                                 fieldLabel:'Email',
                                 maxLength:100,
                                 vtype: 'email',
                                 allowBlank:false,
                                 cls : 'login-field',
                                 labelCls : 'login-label',
                                 fieldCls : 'login-text',
                                 labelAlign : 'top',
		                         listeners : {
		                            afterrender : function(field) {
		                                field.focus(false, 200);
		                            },
		                            specialkey : function(field, e) {
		                                if (field.up('form').getForm().isValid() && e.getKey() == Ext.EventObject.ENTER) {
		                                    var btn = field.up('form').down('#resetPassword');
		                                    btn.fireEvent('click', btn,me);
		                                }
		                            }
		                         }
                    }, 
                    {
                        xtype : 'component',
                        html:'Reset Password',
                        cls : 'login-link',
                        itemId : 'resetPassword',
                        margin:'15 0 0 0',
                        listeners : {
                            render : function(c) {
                                c.getEl().on('click', function(e) {
                                   var btn = c.up('form').down('#resetPassword');
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
                    }]
                } ]
            } ]
        } ];

        this.callParent(arguments);
    }
});
