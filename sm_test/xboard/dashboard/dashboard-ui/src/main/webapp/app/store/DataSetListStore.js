Ext.define('DD.store.DataSetListStore', {
    extend  :'Ext.data.Store',
    model:'DD.model.DataSetListModel',
    autoLoad:true,
    proxy:{
        type:'ajax',
        url:JSON_SERVLET_PATH + 'getDatasets'
    },
    listeners : {
        load : function(store, records) {
            Ext.each(records, function(rec) {
                var parameters = rec.get('parameters');
                Ext.each(parameters, function(param) {
                    if (param.parameterDef.name == 'Name') {
                        rec.set('name', param.value)
                    }
                    if (param.parameterDef.name == 'Description') {
                        rec.set('description', param.value)
                    }
                    if (param.parameterDef.name == 'Query') {
                        rec.set('query', param.value)
                    }
                    rec.commit();
                });
            })
        }
    }
});


