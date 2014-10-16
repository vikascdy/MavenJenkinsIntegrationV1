// -----------------------------------------------------------------------------
// Copyright (c) Edifecs Inc. All Rights Reserved.
//
// This software is the confidential and proprietary information of Edifecs Inc.
// ("Confidential Information").  You shall not disclose such Confidential
// Information and shall use it only in accordance with the terms of the license
// agreement you entered into with Edifecs.
//
// EDIFECS MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
// SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED
// WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, OR
// NON-INFRINGEMENT. EDIFECS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY
// LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR
// ITS DERIVATIVES.
// -----------------------------------------------------------------------------

// APP.JS
// The main entry point for the application.
// This defines a global variable called 'Rest' (Service Manager), which will
// store all of the application's objects and classes.
// ----------------------------------------------------------------------------

Ext.Loader.setConfig({enabled:true});

Ext.Loader.setPath({
    'Util': 'app/util',

    'Rest': 'app',
    // Initializes the Generic UX controls
    'Edifecs': EDIFECS_PLUGIN_PATH
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
    'Edifecs.*',
    'Util.Log',
    'Util.Functions',
    'Util.ConfigManager'
]);

Ext.application({
    name: 'Rest',
    appFolder: 'app',

    controllers: [
        'ConfigurationController'
    ],

    launch: function() {
        var me = this;

        // TODO: Remove Log.level=DEBUG in final production version.
        Log.level = Log.DEBUG;

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

        Rest.setPage = function (page) {
            var ctr = Ext.getCmp('json-page-container');
            ctr.removeAll();
            ctr.add(page);
            return true;
        };

        Rest.removeLoadingWindow= function(){
             var loadingEl = Ext.get('site-loading');
               if (loadingEl) loadingEl.hide();
        };

        this.buildViewport();

        ConfigManager.checkIfConnected(function(connected) {

            if (connected) {

                ConfigManager.checkIfClusterNameRequired(function(req) {
                    if (req && !Rest.testMode) {
                        me.showClusterNamePage();
                    } else {
                        UserManager.checkIfFirstRun(function(firstrun) {
                            if (firstrun) {
                                me.showEulaPage();
                            } else {

                                if (Rest.navigationController.validateUrl())
                                    Rest.removeLoadingWindow();
                                else
                                    me.showConfigPage();
                            }
                        }, this);
                    }
                });
            } else {
                me.showClusterNamePage();
            }
        });
    },

    buildViewport: function() {
        var viewport = Ext.create('Ext.container.Viewport', {
            id    : 'json-root-viewport',
            border:0,
            layout: 'fit',
            items : [
                {
                    // An extra root container is used to allow for horizontal
                    // scrolling.
                    // The root container scrolls horizontally, but the page
                    // container
                    // (below the header) scrolls vertically. Pages must specify
                    // their
                    // own minHeight settings.
                    id: 'json-scroll-container',
                    border:0,
                    layout   : 'border',
                    overflowX: 'auto',
                    overflowY: 'hidden',
                    items: [
                        {
                            id    : 'json-header-container',
                            xtype : 'container',
                            layout: 'fit',
                            region: 'north',
                            border: false,
                            minWidth: 960,
                            hidden: true
                        },
                        {
                            id    : 'json-page-container',
                            xtype : 'container',
                            layout: 'fit',
                            region: 'center',
                            flex  : 1,
                            border: false,
                            minWidth : 960,
                            overflowY: 'auto',
                            overflowX: 'hidden'
                        }
                    ]
                }
            ]
        });

        // Store a reference to the viewport.
        Rest.viewport = viewport;
    },

    showClusterNamePage: function(){
        Rest.setPage(Ext.create('Rest.view.configuration.ClusterNamePage'));
         // Hide the loading screen.
        Rest.removeLoadingWindow();
    },

    showErrorPage: function() {
        Rest.setPage(Ext.create('Rest.view.configuration.NoConnectionPage'));

        // Hide the loading screen.
        Rest.removeLoadingWindow();
    }
});
