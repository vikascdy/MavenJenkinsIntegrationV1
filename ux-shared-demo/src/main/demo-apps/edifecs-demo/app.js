//Main app.js file for application initialization

try {
    Ext.Loader.setPath({
        'Util'   :'app/util',
        // Initializes the Generic UX controls
        'Edifecs':'../edifecs-plugins'
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
        'Util.Functions',
        'Edifecs.LeftMenu',
        'Edifecs.ApplicationBar',
        'Edifecs.Navigation',
        'Edifecs.Favorites',
        'Edifecs.Notifications',
        'Edifecs.MiniGrid'
    ]);

    Ext.application({
        name       :'PP', //namespace for application
        appFolder  :'app',
        controllers:[
            'CoreController',
            'MenuController'
        ],
        launch     :function () {
            PP.setPage = function (page) {
                var ctr = Ext.getCmp('pp-page-container');
                ctr.removeAll();
                ctr.add(page);
                return true;
            };

            this.buildViewport();
            PP.setPage(Ext.create('PP.view.core.UXDemoPortal'));

            var loadingEl = Ext.get('site-loading');
            if (loadingEl) loadingEl.hide();
        },
        buildViewport:function () {
            var viewport = Ext.create('Ext.container.Viewport', {
                id    :'pp-root-viewport',
                layout:'fit',
                border:false,
                items :[
                    {
                        id       :'pp-scroll-container',
                        layout   :'border',
                        overflowX:'auto',
                        overflowY:'hidden',
                        border   :false,
                        bodyStyle:'background-color: #ffffff;',
                        items    :[
                            //For header
                            {
                                id    :'pp-header-container',
                                xtype           :    'ApplicationBar',
                                appBarUrl       :    'json/applicationBar.json',
                                region:'north',
                                border:false

                            },
                            {
                                id    	 :	'pp-page-container',
                                xtype 	 :	'container',
                                layout	 :	'fit',
                                region   :	'center',
                                flex     :	1,
                                border   :	false,
                                minWidth :	960,
                                padding  :	'0 10 20 0',
                                overflowY:	'auto',
                                overflowX:	'hidden'
                            }
                        ]
                    }
                ]
            });
        }
    });
} catch (err) {
    Functions.errorMsg(err);
}