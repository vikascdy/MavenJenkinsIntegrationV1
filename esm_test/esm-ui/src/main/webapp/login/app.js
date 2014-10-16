Ext.Loader.setConfig({enabled:true});

Ext.Loader.setPath({
    'Util': '../app/util',
    'Security': 'app'
});
Ext.application({
    name: 'Security',

    appFolder: 'app',

    requires: [
        'Util.Log',
        'Util.Functions',
        'Util.UserManager',
        'Util.TenantManager',
        'Util.SessionManager',
        'Util.NavigationManager',
        'Util.PasswordManager',
        'Util.PasswordMeter'
    ],

    controllers: [
        'CoreController'
    ],

    setPageTitle: function(title) {
        document.title = 'Edifecs Login : ' + title;
    },

    launch: function() {
    	
    	var me=this;
        Ext.Ajax.on('requestexception', this.onRequestException, this);
        Ext.QuickTips.init();
        Ext.form.Field.prototype.msgTarget = 'side';
        
        Security.page = null;
        Security.viewport = null;        
        Security.loadingWindow = null;
        Security.isEmailServiceEnabled = false;

        Security.removeLoadingWindow = function(callback) {
            if (Security.loadingWindow)
            	Security.loadingWindow.destroy();
            Ext.callback(callback, this);
        };
        
        Security.setPage = function(page) {
            var ctr = Ext.getCmp('login-page-container');
            ctr.removeAll();
            ctr.add(page);
            Security.page = page;
            return true;
        };
        
        
        me.checkForEmailService(function(status){
        	if(status!=null){
	        	me.buildViewport();            
	            NavigationManager.initiateRoutes(function() {
	            });
        	}
        	else
        		 Functions.errorMsg('Failed to receive service status','Error');
        });
                
    },

    buildViewport: function() {
        Security.viewport = Ext.create('Ext.container.Viewport', {
            id    :'login-root-viewport',
            layout:'fit',
            border:false,
            items :[
                {
                    id       :'login-scroll-container',
                    layout   :'border',
                    border   :false,
                    overflowX: 'auto',
                    overflowY: 'hidden',
                    items    :[
                        {
                            id    : 'login-page-container',
                            border:0,
                            xtype : 'container',
                            layout: 'fit',
                            region: 'center',
                            flex  : 1,
                            minWidth : 960,
                            overflowY: 'auto',
                            overflowX: 'hidden'
                        }
                    ]
                }
            ]
        });
    },

    onRequestException: function (conn, response, options) {
        if (response.status == 505) {
            Ext.Msg.show({
                title:'Internal Server Error',
                msg:'HTTP Version Not supported.',
                buttons: Ext.Msg.OK,
                icon: Ext.Msg.ERROR,
                closable: false,
                modal: true,
                draggable: false
            });
        }
        else
        if (response.status == 404) {
            options.failure(response);
        }
        else
        if (response.status == 403) {
            Ext.Msg.show({
                title:'Access Denied',
                msg: 'Your session expired. You will be redirected to the login page.',
                buttons: Ext.Msg.OKCANCEL,
                icon: Ext.Msg.WARNING,
                closable: false,
                modal: true,
                draggable: false,
                fn: function(btn) {
                    if (btn == 'ok') {
                        if (SecurityConfig.integration.SESSION_TIMEOUT_URL) {
                            var contextPathLength = SecurityConfig.integration.CONTEXT_PATH.length;
                            var relativePath = document.location.pathname.substring(contextPathLength);
                            var relativeUrl = relativePath + document.location.hash;
                            window.location = SecurityConfig.integration.SESSION_TIMEOUT_URL + '?' + Ext.urlEncode({url: relativeUrl});
                        } else {
                            window.location.reload();
                        }
                    }
                    else
                        return;
                }
            });
        }
        else
        if (response.status == 500) {
            Ext.Msg.show({
                title:'Internal Server Error',
                msg:'A FATAL internal error occurred.<br>Please contact the Site Administrator for assistance.',
                buttons: Ext.Msg.OK,
                icon: Ext.Msg.ERROR,
                closable: false,
                modal: true,
                draggable: false
            });
        }
        //handle other error statuses
    },
    
    checkForEmailService : function(callback){
    	Functions.jsonCommand("esm-service", "isEmailServiceAvailable", {}, {
			success : function(response) {
				Security.isEmailServiceEnabled = response;
				Ext.callback(callback,this,[response]);
			},
			failure : function(response){
				Ext.callback(callback,this,[null]);
			}
		});
    	
    }
});
