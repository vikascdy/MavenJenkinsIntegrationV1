Ext.define('DD.store.DashboardListStore', {
    extend  :'Ext.data.Store',
    model   :'DD.model.DashboardListModel',
    autoLoad:false,
    proxy:{
        type:'ajax',
        url:JSON_SERVLET_PATH + 'getXBoards'
    },
    listeners : {
        load : function(store, records) {
            Ext.each(records, function(rec) {
                var imageUrl = null;

                Ext.each(rec.get('parameters'), function(param) {
                    if (param.parameterDef.name == 'Name')
                        rec.set('name', param.value);
                    else
                    if (param.parameterDef.name == 'Description')
                        rec.set('description', param.value);
                    else
                    if (param.parameterDef.name == 'Category') {
                        rec.set('category', param.value);
                    }
                    else
                    if (param.parameterDef.name == 'Configuration') {
                        rec.set('configuration', param.value);
                    }

                });
                rec.commit();
            });
        }
    }
});


