Ext.define('Security.store.InstalledApps', {
    extend  :'Ext.data.Store',
    model   :'Security.model.AppsListModel',
    autoLoad:false,
    proxy:{
        type:'ajax',
        url:JSON_SERVICE_SERVLET_PATH + 'esm-service/AppStore.getInstalledApps',
        startParam:'startRecord',
        limitParam:'recordCount',
        reader:{
            type: 'json',
            root: 'data'
        },
        writer:{
            type:'json'
        }
    }
	});