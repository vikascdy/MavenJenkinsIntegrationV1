Ext.Loader.setConfig({enabled:true});

Ext.Loader.setPath({
    'Util': 'app/util',
    'Security': 'app',

    // Initializes the Generic UX controls
    'Edifecs': EDIFECS_PLUGIN_PATH,
    'Widgets': EDIFECS_WIDGET_PATH
});



Ext.application({
    name: 'Security',

    appFolder: 'app',

    requires: [
        'Util.Log',
        'Util.Functions',
        'Util.SecurityConfig',
        'Util.UserManager',
        'Util.GroupManager',
        'Util.RoleManager',
		'Util.AppManager',
        'Util.TenantManager',
        'Util.SiteManager',
        'Util.OrganizationManager',
        'Util.SecurityRealmManager',
        'Util.SessionManager',
        'Util.NavigationManager',
        'Util.PasswordManager',
        'Util.PasswordMeter',
        'Edifecs.DoormatNavigation',
        'Edifecs.DoormatApplicationBar',
        'Edifecs.Notifications',
        'Edifecs.Favourites',
        'Edifecs.MiniGrid',
        'Edifecs.LeftMenu',
        'Edifecs.CustomButtonGroup',
        'Security.proxy.RemoteProxy',
        'Edifecs.FormGenerator'
    ],

    controllers: [
        'CoreController',
        'RolesController',
        'UsersController',
        'TenantsController',
        'OrganizationController',
        'SecurityRealmController',
        'GroupsController',
        'SiteController',
        'AppController'
    ],

    setPageTitle: function(title) {
        document.title = 'Security Manager : ' + title;
    },

    launch: function() {
        var me = this;
		Ext.override(Ext.form.TextField, {stripCharsRe: /(^\s+|)/g});

        Ext.QuickTips.init();

        Security.loadingWindow = null;
        Security.authType = null;
        Security.isEmailServiceEnabled = false;

        Security.removeLoadingWindow = function(callback) {
            if (Security.loadingWindow)
                Security.loadingWindow.destroy();
            Ext.callback(callback, this);
        };
        
        
        Security.selectTreeNode = function(page, callback){
        	var leftMenuTree = Security.viewport.down('LeftMenu');
        	if(leftMenuTree && page.treeId != undefined){

        		var treeView = leftMenuTree.getView();    		
        		var selection = leftMenuTree.getSelectionModel().getSelection();
        		var rootNode = leftMenuTree.getRootNode();
        		
        		var newTreeNode = leftMenuTree.getStore().getNodeById(page.treeId);
        		var selectedTreeNodes = treeView.getSelectedNodes();
        		
        		if(selectedTreeNodes && newTreeNode){
        			
        			rootNode.cascadeBy(function (node) {
        				leftMenuTree.getSelectionModel().deselect(node);	
        				treeView.removeRowCls(node, "selectedCls");
        	        });
        			
        			leftMenuTree.getSelectionModel().select(newTreeNode);
                    treeView.addRowCls(newTreeNode, "selectedCls");                    
        		}        		
        		leftMenuTree.updateLayout();
        	}
        	Ext.callback(callback,this,[]);
        };


        Functions.getSiteLogo(null, function(siteLogo){

            // Check to see if user is logged in
            Functions.jsonCommand("esm-service", "isSubjectAuthenticated", {}, {
                success: function(response) {

                    if (response === true) {

                        Ext.Date.defaultFormat = SecurityConfig.configs.DATE_TIME_FORMAT;
                        Ext.Ajax.on('requestcomplete', me.onRequestComplete, me);
                        Security.viewport = null;
                        Security.page = null;

                        Security.setPage = function(page,callback) {
                            var ctr = Ext.getCmp('Security-page-container');
                            ctr.removeAll();
                            ctr.add(page);
                            Security.page = page;
                            if (!Ext.getCmp('menuToolbar')) {
                                Log.debug("Adding header.");
                                var headerCtr = Ext.getCmp('Security-header-container');
                                headerCtr.removeAll();
                                if (!(page instanceof Security.view.core.Home))
                                    headerCtr.add({
                                        xtype: 'DoormatApplicationBar',
                                        id: 'menuToolbar',
                                        logoIcon:  siteLogo,
                                        appBarUrl: DOORMAT_APP_BAR_URL,
                                        doormatUrl: DOORMAT_DOORMAT_URL
//                                        helpUrl : DOORMAT_HELP_URL
                                    });
                                headerCtr.show();
                            }
                            Ext.callback(callback,this,[]);
                        };

                        me.checkForEmailService(function(status){
                            if(status!=null){
                                me.buildViewport(function(){
                                    NavigationManager.initiateRoutes(function(){});
                                });
                            }
                            else
                                Functions.errorMsg('Failed to receive service status','Error');
                        });

                        var loadingEl = Ext.get('site-loading');
                        if (loadingEl) loadingEl.hide();
                    }
                    else
                        Functions.redirectToLogin();
                },
                failure: function(response) {
                    Functions.redirectToLogin();
                }
            });

        });
    },

    buildViewport: function(callback) {

        Security.viewport = Ext.create('Ext.container.Viewport', {
            id    : 'Security-root-viewport',
            renderTo:Ext.getBody(),
            layout: 'fit',
            border:0,
            items : [{
                id: 'Security-scroll-container',
                border:0,
                layout   : 'border',
                overflowX: 'auto',
                overflowY: 'hidden',
                items: [{
                    id    : 'Security-header-container',
                    border:0,
                    xtype : 'container',
                    layout: 'fit',
                    minWidth : 960,
                    region: 'north'
                }, {
                    id    : 'Security-page-container',
                    border:0,
                    xtype : 'container',
                    layout: 'fit',
                    region: 'center',
                    flex  : 1,
                    minWidth : 960,
                    overflowY: 'auto',
                    overflowX: 'hidden'
                }]
            }]
        });

        Ext.callback(callback,this,[]);
    },
    
    onRequestComplete: function (conn, response, options) {
    	
    	var json = Ext.decode(response.responseText);

    	if(json && json.error && json.error.class=='com.edifecs.epp.security.exception.AuthenticationFailureException')
        {
            Ext.MessageBox.show({
                title: 'Remote Error',
                msg: 'Your session expired. Please Re-login !',
                buttons: Ext.MessageBox.YES,
                buttonText:{
                    yes: "Login"
                },
                fn: function(){
                    window.location = LOGIN_PAGE_PATH + '?redirectURL=' + DEFAULT_REDIRECT;
                }
            });
        }
        
    },

    onRequestException: function (conn, response, options) {
   	
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
                            //url=%2Fsecurity%2Findex.html%23%21%2Froles';
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

