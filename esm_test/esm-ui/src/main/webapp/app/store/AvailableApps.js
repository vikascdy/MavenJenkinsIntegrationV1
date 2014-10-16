Ext.define('Security.store.AvailableApps', {
    extend  :'Ext.data.Store',
    model   :'Security.model.AppsListModel',
    autoLoad:false,
    proxy:{
        type:'ajax',
        url:JSON_SERVICE_SERVLET_PATH + 'esm-service/AppStore.getAvailableApps',
        startParam:'startRecord',
        limitParam:'recordCount',
        reader:{
            type:'json',
            root: 'data'
        },
        writer:{
            type:'json'
        }
    }
	});