try {
    Ext.Loader.setConfig({enabled: true});

    Ext.Loader.setPath({
        'Util': 'app/util',
        'Edifecs':'../ext/edifecs-plugins'
    });

    Ext.application({
        requires: [
            'Ext.ux.IFrame',
            'Edifecs.Notifications',
            'Edifecs.Favourites',
            'Edifecs.DoormatNavigation',
            'Edifecs.DoormatApplicationBar',
            'Util.Functions',
            'Util.ChartManager',
            'Util.LayoutManager',
            'Util.DashboardManager',
            'Util.WidgetManager',
            'Util.ParameterManager',
            'Util.WidgetPropertiesManager',
            'Util.MediaManager',
            'Util.ShapesManager',
            'Util.UserManager',
            'Util.NavigationManager',
            'Util.DataSetManager',
            'Util.DataSourceManager',
            'Util.DataStoreManager'

        ],

        name: 'DD',

        appFolder: 'app' ,

        controllers: [
            'ApplicationBarController',
            'CoreController',
            'LayoutController',
            'DashboardController',
            'WidgetController',
            'DataSourceController',
            'DataSetController',
            'ParameterController',
            'QueryBuilderController'
        ],


        launch: function() {
            var me = this;
            Ext.tip.QuickTipManager.init();
            DD.viewport = null;
            DD.currentPage = null;
            DD.loadingWindow = null;


            DD.removeLoadingWindow = function(callback) {
                if (DD.loadingWindow)
                    DD.loadingWindow.destroy();
                Ext.callback(callback, this);
            };

            DD.setPage = function (page, callback) {

                var ctr = Ext.getCmp('dd-page-container');
                ctr.removeAll();
                ctr.add(page);
                DD.currentPage = page;
                Ext.callback(callback, this);
            };

            me.buildViewport(function(view) {
                DD.viewport = view;
                NavigationManager.initiateRoutes(function() {
                    var loadingEl = Ext.get('site-loading');
                    if (loadingEl) loadingEl.hide();
                });
            });

        },
        buildViewport:function (callback) {

            var viewport = Ext.create('Ext.container.Viewport', {
                id    :'dd-root-viewport',
                layout:'fit',
                border:false,
                items :[
                    {
                        id       :'dd-scroll-container',
                        layout   :'border',
                        overflowX:'auto',
                        overflowY:'hidden',
                        border   :false,
                        items    :[
                            {
	                            xtype: 'DoormatApplicationBar',
	                            id: 'menuToolbar',
	                            logoIcon:  '../resources/images/edifecs-logo.png',
	                            appBarUrl: '/rest/service/xboard-portal-service/getAppBar',
	                            doormatUrl: '/rest/service/xboard-portal-service/getMenus',
	                            border:0,
	                            minWidth : 960,
	                            region: 'north'
	                        },
                            {
                                id    :'dd-page-container',
                                xtype :'container',
                                layout:'fit',
                                region   :'center',
                                style:{
                                    backgroundColor:'#FFF!important'
                                },
                                flex     :1,
                                border   :false,
                                minWidth :1280,
                                overflowX:'hidden'
                            },
                            {
                                xtype: 'component',
                                border: false,
                                style:{
                                    backgroundColor:'#FFF!important'
                                },
                                region: 'south',
                                height:40,
                                padding:'5 20 0 0',
                                cls: 'page-footer',
                                html: '<p>Copyright &copy; 2012, Edifecs Inc</p>'
                            }
                        ]
                    }
                ]
            });
            Ext.callback(callback, this, [viewport]);

        }
    });
} catch (err) {
    console.log("Application Loading Failed : " + err);
}