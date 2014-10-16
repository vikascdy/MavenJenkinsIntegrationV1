Ext.define('DD.store.DataSourceTypeListStore', {
    extend  :'Ext.data.Store',
    model:'DD.model.DataSourceTypeListModel',
    autoLoad:false,
    proxy:{
        type:'ajax',
        url:JSON_SERVLET_PATH + 'getDatasourceTypes'
    }

});
