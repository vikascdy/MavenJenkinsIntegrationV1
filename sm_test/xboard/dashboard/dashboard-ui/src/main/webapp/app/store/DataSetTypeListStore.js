Ext.define('DD.store.DataSetTypeListStore', {
    extend  :'Ext.data.Store',
    model:'DD.model.DataSetTypeListModel',
    autoLoad:false,
    proxy:{
        type:'ajax',
        url:JSON_SERVLET_PATH + 'getDatasetTypes'
    }

});
