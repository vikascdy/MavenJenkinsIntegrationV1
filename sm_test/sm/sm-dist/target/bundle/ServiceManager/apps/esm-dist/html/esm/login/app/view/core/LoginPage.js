Ext.define('Security.view.core.LoginPage', {
    extend : 'Ext.container.Container',
    alias : 'widget.loginpage',
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
                cls : 'login-logo',
				listeners : {
						   afterrender : function(logo){
								me.getTenantName(function(name){
									var tenantName = null;
									
									if(name!="localhost")
										tenantName = name;										
									
									Functions.getSiteLogo(tenantName, function(siteLogo){
										logo.getEl().setStyle('backgroundImage', 'url('+siteLogo+')');	
										});
								});
							}
				}	
            }, {
                xtype : 'label',
                id : 'login-message',
                cls : 'login-message',
                hidden : 'true',
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
                    maxWidth:310,
                    border : false,
                    defaultType : 'textfield',
                    layout : {
                        type : 'vbox',
                        align:'stretch'
                    },
                    items : [
                        {
                            flex : 1,
                            name : 'domain',
                            id : 'domain',
                            hidden: true,
                            fieldLabel : 'Domain',
                            cls : 'login-field',
                            labelCls : 'login-label',
                            fieldCls : 'login-text',
                            labelAlign : 'top',
                            enableKeyEvents:true,
                            listeners : {
                               afterrender : function(){
                                    me.checkDomain();
                                },
                                keyup : function(){
                                    UserManager.clearLoginMessage();
                                }
                            }
                        },
                        {
                            flex : 1,
                            name : 'organization',
                            id : 'organization',
                            itemId : 'organization',
                            hidden: false,
                            fieldLabel : 'Organization',
                            cls : 'login-field',
                            labelCls : 'login-label',
                            fieldCls : 'login-text',
                            labelAlign : 'top',
                            enableKeyEvents:true,
                            listeners : {
                                keyup : function(){
                                    UserManager.clearLoginMessage();
                                }
                            }
                        },
                        {
                            flex : 1,
                            name : 'username',
                            fieldLabel : 'Username',
                            cls : 'login-field',
							id : 'username',
                            labelCls : 'login-label',
                            fieldCls : 'login-text',
                            labelAlign : 'top',
                            enableKeyEvents:true,
                            listeners : {
                            	keyup : function(){
                                    UserManager.clearLoginMessage();
                                }
                            }
                        }, {
                            flex : 1,
                            name : 'password',
                            id : 'password',
                            inputType : 'password',
                            fieldLabel : 'Password',
                            cls : 'login-field',
                            labelCls : 'login-label',
                            fieldCls : 'login-text',
                            labelAlign : 'top',
                            enableKeyEvents:true,
                            listeners : {
                                specialkey : function(field, e) {
                                    if (field.up('form').getForm().isValid() && e.getKey() == Ext.EventObject.ENTER) {
                                        var btn = field.up('form').down('#login');
                                        btn.fireEvent('click', btn);
                                    }
                                },
                                keyup : function(){
                                    UserManager.clearLoginMessage();
                                }
                            }
                        }, {
                            xtype:'container',
                            width:290,
                            flex:1,
                            layout:{type:'hbox',align:'middle'},
                            items:[
                                {
                                    xtype : 'checkboxfield',
                                    labelCls:'rememberMe-label',
                                    labelAlign:'right',
                                    labelSeparator:'',
                                    fieldLabel:'Remember me',
                                    itemId:'rememberMe'
                                },
                                {
                                    xtype : 'tbspacer',
                                    flex:1
                                },
                                {
                                    xtype : 'image',
                                    cls : 'login-button',
                                    id : 'login-button',
                                    itemId : 'login',
                                    listeners : {
                                        render : function(c) {
                                            c.getEl().on('click', function(e) {
                                                var btn = c.up('form').down('#login');
                                                btn.fireEvent('click', btn);
                                            }, c);
                                        }
                                    }
                                }
                            ]
                        },
                        {
                            xtype : 'component',
                            html:'Forgot Password ?',
                            hidden : !Security.isEmailServiceEnabled,
                            cls : 'login-link',
                            itemId : 'forgotPassword',
                            listeners : {
                                render : function(c) {
                                    c.getEl().on('click', function(e) {
                                        window.location.href='#!/ForgotPassword';
                                    }, c);

                                    me.checkDomain(function(tenantName){
                                           
                                    });
                                }
                            }
                        } ]
                } ]
            } ]
        } ];

        this.callParent(arguments);
    },

    checkDomain : function(callback)
       {
                     
					var urlString = window.location.hostname;
                    var uString=urlString.toString();
                    var uLen=uString.length;
                    var subUrl = urlString.split('.');
                    var length = subUrl.length;                   
                    var domainField = Ext.getCmp('domain');     
					var organizationField =Ext.getCmp('organization');					
                    var first = subUrl[0].toString();
                    
                    if( length >=2 && isNaN(first))
                   {
                                 domainField.setValue(first);
								 domainField.hide();      
								 organizationField.focus(false,500);
                    								 
                   }               

                    else
                    {
                                    domainField.setValue("");
                                    domainField.show();
                                    domainField.focus(false,500);
                    }

        Ext.callback(callback,this,[name]);

    },
	
	getTenantName : function(callback)	
	{
					var urlString = window.location.hostname;
                    var uString=urlString.toString();
                    var uLen=uString.length;
                    var subUrl = urlString.split('.');
                    var length = subUrl.length;  
					var tenantName = null;
					
					if(length>1)
						tenantName = subUrl[0]
					
					Ext.callback(callback,this,[tenantName]);
                    
	}
	

});
