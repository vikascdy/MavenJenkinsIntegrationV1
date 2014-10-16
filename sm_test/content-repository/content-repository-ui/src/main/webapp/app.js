// APP.JS
// The main entry point for the application.
// This defines a global variable called 'SM' (Service Manager), which will
// store all of the application's objects and classes.
// ----------------------------------------------------------------------------

// The base URL for all ServiceManager JSON API calls.
// This is a global variable.
window.JSON_URL = '/rest/service/UI%20Service';
window.JSON_UPLOAD_URL = '/rest/service/UI%20Service';

try {
    Ext.Loader.setConfig({enabled:true});
    Ext.Loader.setPath({
        'Util': 'app/util',
        // Initializes the Generic UX controls
        'Edifecs': '../edifecs-plugins'
    });

    Ext.require([
        'Ext.data.*',
        'Ext.grid.*',
        'Ext.container.*',
        'Ext.tree.*',
        'Ext.panel.*',
        'Ext.button.*',
        'Ext.form.*',
        'Ext.tip.*',
        'Edifecs.DoormatNavigation',
        'Edifecs.DoormatApplicationBar',
        'Edifecs.Notifications',
        'Edifecs.Favourites',
        'Edifecs.MiniGrid',
        'Edifecs.CustomButtonGroup',
        'Util.Log',
        'Util.Functions',
		'Util.UserManager',
        'Util.ConfigManager',
        'Util.NavigationManager',
        'Util.JCRLocale',
        'SM.view.abstract.*'
    ]);

    Ext.application({
        name: 'SM',
        appFolder: 'app',

        controllers: [
            'CoreController',
            'ClustersController',
            'ServersController',
            'ResourcesController',
            'NodesController',
            'MenuController',
            'ServicesController',
            'LogsController',
            'GraphsController',
            'WizardController',
            'ContentController'
        ],

        launch: function() {
            var me = this;
            
                    // TODO: Remove Log.level=DEBUG in final production version.
                    Log.level = Log.DEBUG;
                    SM.testMode = null;
                    SM.firstRun = false;
                    SM.changesSavedStatus = true;
                    SM.checkServiceManagerChanges = null;
                    SM.page = null;
                    SM.isConfigLoaded = false;
                    SM.disableUnsavedChanges = false;
                    SM.environmentName = null;
                    SM.loadingWindow=null;
        
                    // Print the app name and current Ext JS version.
                    // TODO: Remove the "Development Prototype Build" part for
                    // production.
                    Log.info("Edifecs\u00AE Service Manager, Development Prototype Build");
                    try {
                        var version = Ext.versions.core.version;
                        Log.info("Ext JS Version: {0}", version);
                    } catch (err) {
                        Log.warn("Could not determine Ext JS version.");
                    }
                    Log.debug("Debug logging enabled.");
        
                    // Overriding form textfields to avoid leading blank spaces and
                    // allowing spaces in between and after text
                    Ext.override(Ext.form.TextField, {
                        stripCharsRe: /^\s+/g
                    });
        
                    
                    SM.removeLoadingWindow = function(callback) {
                        if (SM.loadingWindow)
                            SM.loadingWindow.destroy();
                        Ext.callback(callback, this);
                    };
                    
                    // Ends user session, clear cookies and redirects to the login page
                    SM.displayLoginPage= function(){
                        Functions.redirectToLogin();
                    };
        
                    // Define a function to set the current full-screen page view.
                    SM.setPage = function(page, force) {
                    	
        
                        // Checking the URL parameter for testmode and setting the test
                        // flag true/false accordingly
//                        SM.testMode = SM.checkTestMode('testmode');
        
//                        SM.setTestMode(SM.testMode);
        
                        if(force === undefined) { 
                            if(SM.checkServiceManagerChanges) {
                                force=false;
                                SM.checkServiceManagerChanges=null;
                            }
                        }
        
                        if(SM.disableUnsavedChanges) {
                            force=true;
                            SM.disableUnsavedChanges=false;
                        }
        
                        if (!force && SM.page) {
//                            console.log(SM.page,SM.page.unsavedChanges,SM.changesSavedStatus);
                            if (SM.page && SM.page.unsavedChanges && SM.changesSavedStatus) {
                                Ext.Msg.confirm(
                                    "Unsaved Changes",
                                    "The page you are on has unsaved changes." +
                                    " If you leave this page, you may lose these changes." +
                                    "<br /><br />Are you sure you want to leave this page?",
                                    function(btn) {
                                        if (btn == 'yes') {
                                            Functions.destroyModalWindow();
                                            SM.setPage(page, true);
                                        }
                                    }
                                );
                                return false;
                            }
                        }
        
                        var ctr = Ext.getCmp('sm-page-container');
                        ctr.removeAll();
        
                        ctr.add(page);
        
                        SM.page = page;

                        return true;
                    };
        
                    // Define a function to load a saved config file and display the
                    // Service Manager page.
                    SM.loadAndViewConfig = function(savedConfig) {
        
                        var loadingWindow = Ext.widget('progresswindow', {
                            text: 'Loading ' + savedConfig.get('name') + '...'
                        });
                        var callback = function() {
                            SM.isConfigLoaded=true;
                            SM.setPage(Ext.create('SM.view.core.ServiceManagerPage'));
                            loadingWindow.destroy();
                        };
        
                        if (savedConfig.get('active'))
                            ConfigManager.loadDefaultConfig(callback);
                        else
                            ConfigManager.loadSavedConfig(savedConfig.get('name'), savedConfig.get('version'), callback);
                    };
        
                    // Define a function to reload all trees and grids.
                    SM.reloadAll = function(flag) {
                        Ext.each(Ext.ComponentManager.all.getValues(), function(cmp) {
                            if (cmp.reload !== undefined){
                                if (cmp.xtype=='configtree' && flag=='noRefresh') {}
                                else{
                                    cmp.reload();
                                }
                            }
                        });
                        SM.page.unsavedChanges=true;
                        ConfigManager.getRequiredServiceTypes();  
                        
                 	   //A hack to explicitly refresh config tree as it does not
                 	   //reflects changes in single refresh at first time although
                 	   //store contains correct data
                 	   
                 	   Ext.defer(function() {
         	        	   var configtree=SM.viewport.down('configtree');
         	               if(configtree){
         	                   configtree.getRootNode().collapse(true);
         	                   configtree.getRootNode().expand(true);
         	               }
                 	   },1000);
                 	   
                    };
        
                    SM.reloadAllWithStatuses = function() {
                        ConfigManager.updateStatuses(false, SM.reloadAll());
                    };
        
                    // Register an event handler with the <body> element to warn the
                    // user when trying to navigate away from a page that may have
                    // unsaved changes.
                    // [This event handler is referenced in index.html.]
                    SM.unloadHandler = function() {
                        if (SM.page && SM.page.unsavedChanges) {
                            return "The page you are on has unsaved changes." +
                                   " If you leave this page, you may lose these changes.";
                        } else {
                            return undefined;
                        }
                    };
        
                    // Define a function to return the parameter value for the param
                    // passed to it.
                    SM.checkTestMode = function(parameter) {
                        var hash=location.hash.substring(2, location.hash.length);
                        var params = hash.split("?");
                        if(params.length==1)
                            return false;
                        var param_value = params[1].substring(params[1].indexOf('=') + 1);
                        if(param_value!="true")
                            return false;
        
                        return true;    
                    };
        
                    SM.setTestMode = function(testModeStatus) {
                        Functions.jsonCommand("UI Service", "setTestMode",
                                              {testmode: SM.testMode}, {
                            success: function() {
                                if(SM.testMode)
                                    Log.info("Test mode enabled.");
                                else
                                    Log.info("Production mode enabled.");
                            }
                        });
                    };
        
                    SM.getTestMode = function() {
                        Ext.Ajax.request({
                            url: JSON_URL + '/getTestMode',
                            success: function(result, request) {
                                var respJson = Ext.decode(result.responseText);
                            },
                            failure: function() {
                                Log.error("Cannot access test mode");
                            }
                        });
                    };
        
                    
                 // Construct the viewport and navigation.
                    me.buildViewport(function(view) {
                        // Store a reference to the viewport.
                        SM.viewport = view;
                        NavigationManager.initiateRoutes(function() {
                            var loadingEl = Ext.get('site-loading');
                            if (loadingEl) loadingEl.hide();
                        });
                    });
        
        
                
                    //SessionManager.setSessionTimeout();

        },

        buildViewport: function(callback) {
        	
        	Functions.getSiteLogo(function(siteLogo){
        	
		            var viewport = Ext.create('Ext.container.Viewport', {
		                id    : 'sm-root-viewport',
		                border:0,
		                layout: 'fit',
		                items : [{
		                    // An extra root container is used to allow for horizontal scrolling.
		                    // The root container scrolls horizontally, but the page container
		                    // (below the header) scrolls vertically. Pages must specify their
		                    // own minHeight settings.
		                    id: 'sm-scroll-container',
		                    border:0,
		                    layout   : 'border',
		                    overflowX: 'auto',
		                    overflowY: 'hidden',
		                    items: [{
		                            xtype: 'DoormatApplicationBar',
		                            id: 'menuToolbar',
		                            logoIcon:  siteLogo,
		                            appBarUrl: '/rest/service/xboard-portal-service/getAppBar',
		                            doormatUrl: '/rest/service/xboard-portal-service/getMenus',
		                            border:0,
		                            minWidth : 960,
		                            region: 'north'
		                        }, {
		                            id    : 'sm-page-container',
		                            xtype : 'container',
		                            layout: 'fit',
		                            region: 'center',
		                            flex  : 1,
		                            border: false,
		                            minWidth : 960,
		                            overflowY: 'auto',
		                            overflowX: 'hidden'
		                    }]
		                }]
		            });
		
		            Ext.callback(callback,this,[viewport]);
            
        	});
        }
    });
} catch (err) {
    Functions.loadingErr(err);
}
