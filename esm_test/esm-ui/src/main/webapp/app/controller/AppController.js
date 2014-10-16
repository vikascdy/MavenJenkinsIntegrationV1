Ext.define('Security.controller.AppController', {
    extend: 'Ext.app.Controller',
    model: ['AppsListModel'],
    stores: [
        'AppsListStore',
		'AvailableApps',
		'InstalledApps',
        'SearchAppsListStore'
    ],
    views: [
        'app.AppsList',
        'app.ManageApps',
        'app.AppConfiguration',
		'app.ManageSiteApps',
		'app.AppInfoWindow',
		'app.AppOverviewPage'
    ],

    init: function() {
        this.control({

        });
    }
});