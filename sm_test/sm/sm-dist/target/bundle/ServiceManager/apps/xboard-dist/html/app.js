Ext.Loader.setConfig({enabled: true});

Ext.Loader.setPath({
    'Util': 'app/util',
    'Edifecs':'edifecs-plugins'
});

Ext.application({
    requires: [
        'Edifecs.DoormatNavigation',
        'Edifecs.DoormatApplicationBar',
        'Edifecs.Notifications',
        'Edifecs.Favourites',
        'Edifecs.FormGenerator',
        'Util.Functions',
        'Util.UserManager',
        'Util.Log',
        'Edifecs.WidgetUtils'
    ],

    name: 'Portal',

    appFolder: 'app',

    controllers: [
        'PortalController'
    ],
    
    setPage: function(token) {

    	if (token === "!/about") {
    		var ctr = Ext.getCmp('portal-page-container');
    		ctr.removeAll();
    		ctr.add({
    			id:     'about-page',
                xtype:  'about',
                layout: 'fit'
            });
        } else if (token === "!/samples") {
            var ctr = Ext.getCmp('portal-page-container');

            ctr.removeAll();
            ctr.add({
                id:     'samples-page',
                xtype:  'samples',
                layout: 'fit'
            });
        }else {
        	var ctr = Ext.getCmp('portal-page-container');
        	ctr.removeAll();
        	ctr.add({
    			id:     'portal-page',
                xtype:  'landingpage',
				configurationUrl:JSON_SERVICE_SERVLET_PATH + 'xboard-portal-service/getSectionedFeatureItems',
				//configurationUrl:'resources/json/LandingPage.json',
				heading:'ESM'
            });
        }
    },

    launch: function() {
    	var me = this;
        Portal.viewport = null;
        
        Functions.getSiteLogo(function(siteLogo){       	
        

		        // Check to see if user is logged in
		        Functions.jsonCommand("esm-service", "isSubjectAuthenticated", {}, {
		            success: function(response) {
		
		            	if (response === true) {
		            		
			                // Initialize the UI
			                Portal.viewport = Ext.create('Ext.container.Viewport', {
			                    id    : 'portal-root-viewport',
			                    layout: 'fit',
			                    border: false,
			                    items : [{
			                        id:        'portal-scroll-container',
			                        layout:    'border',
			                        overflowX: 'auto',
			                        overflowY: 'hidden',
			                        border:    false,
			                        items: [{
			                            id:        'portal-header-container',
			                            overflowX: 'visible',
			                            overflowY: 'visible',
			                            region:    'north',
			                            minWidth:  960,
			                            xtype:     'DoormatApplicationBar',
			                            logoIcon:  siteLogo,
			                            appBarUrl:       JSON_SERVICE_SERVLET_PATH + 'xboard-portal-service/getAppBar',
			                            doormatUrl:      JSON_SERVICE_SERVLET_PATH + 'xboard-portal-service/getMenus',
//										helpUrl : 'https://gitlab/platform/repo/blob/develop/README.md',
			                            border:    false
			                        }, {
			                            id:     'portal-page-container',
			                            border:0,
			                            xtype : 'container',
			                            layout: 'fit',
			                            region: 'center',
			                            flex  : 1,
			                            minWidth : 960,
			                            overflowY: 'auto',
			                            overflowX: 'hidden'
			                        }, {
			                            xtype:  'component',
			                            border: false,
			                            style: {
			                                backgroundColor:'#FFF!important'
			                            },
			                            region:  'south',
			                            height:  40,
			                            padding: '10 10 0 20',
			                            cls:     'page-footer',
			                            html:    '<p>Copyright &copy; 2012-2014, Edifecs, Inc.</p>'
			                        }]
			                    }]
			                });
			                
			                // Default Initialization
			                var token = Ext.util.History.getToken();
		
		            		me.setPage(token);
		            		
		            		Ext.util.History.init();
		            		Ext.util.History.on('change', function(token) {
		            			me.setPage(token);
		                    });
		
		            	}
		            	else
		            	{
		            		Functions.redirectToLogin();
		            	}
		            },
		            failure: function(response) {
		                Functions.redirectToLogin();
		            }
		        });
        
        });
    }
});