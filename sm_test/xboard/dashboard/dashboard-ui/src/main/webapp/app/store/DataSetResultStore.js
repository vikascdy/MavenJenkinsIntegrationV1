Ext.define('DD.store.DataSetResultStore', {
    extend  :'Ext.data.Store',
    model:'DD.model.DataSetResultModel',
    autoLoad:false,
    proxy:{
        type:'ajax',
        url:JSON_SERVLET_PATH + 'getDatasetResult',
        timeout:600000,
        actionMethods : {
            read:'POST'
        },
        reader :{
            type:'json'
        }
    }
});


