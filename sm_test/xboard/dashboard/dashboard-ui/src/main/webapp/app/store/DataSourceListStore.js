Ext.define('DD.store.DataSourceListStore', {
    extend  :'Ext.data.Store',
    model:'DD.model.DataSourceListModel',
    autoLoad:false,
    proxy:{
        type:'ajax',
        url:JSON_SERVLET_PATH + 'getDatasources'
    },
    listeners : {
        load : function(store, records) {
            Ext.each(records, function(rec) {
                var datastoreType = rec.get('datastoreType');
                rec.set('datasourceTypeId',datastoreType.id);
                var parameters = rec.get('parameters');
                Ext.each(parameters, function(param) {
                    if (param.parameterDef.name == 'Name') {
                        rec.set('name',param.value)
                    }
                    if (param.parameterDef.name == 'Category') {
                        rec.set('category',param.value)
                    }
                    if (param.parameterDef.name == 'Description') {
                        rec.set('description',param.value)
                    }
                    rec.commit();
                });
            })
        }
    }
});
