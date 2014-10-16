Ext.define('DD.view.parameters.NumberFieldControl', {
    extend:'Ext.form.field.Number',
    alias :'widget.numberfieldcontrol',
    config:{
        parameterConfig:null,
        comparison:null,
        storeRef:null
    },
    initComponent : function() {

        this.listeners = {
            'change' : function(combo, newValue) {

                var parameterConfig = this.getParameterConfig();
                Ext.each(parameterConfig, function(config, index) {

                    var widgetConfigObj = config.get('widgetConfigObj');
                    var store = combo.getStoreRef()[index];
                    var dataSetId = widgetConfigObj.dataSetId;
                    var parameter = config.get('value');
                    store.clearFilter();


                    ParameterManager.updateStoreRecords(dataSetId, parameter, store, [newValue.toString()], 'single', store.nameField, store.dataField, store.useAggregateData, function() {

                    });
                });


//                Ext.each(stores, function(store, index) {
//                    if (store) {
//                        store.clearFilter(true);
//                        store.filter([
//                            {filterFn: function(item) {
//                                if (parameters)
//                                    if (combo.getComparison() == '<=')
//                                        return item.get(parameters[index]) <= newValue;
//                                    else
//                                    if (combo.getComparison() == '<')
//                                        return item.get(parameters[index]) < newValue;
//                                    else
//                                    if (combo.getComparison() == '>')
//                                        return item.get(parameters[index]) > newValue;
//                                    else
//                                    if (combo.getComparison() == '>=')
//                                        return item.get(parameters[index]) >= newValue;
//                                    else
//                                        return item.get(parameters[index]) == newValue;
//                            }}
//                        ])
//                    }
//                });
            }
        };

        this.callParent(arguments);
    },
    updateConfiguration : function(configObj, parameterConfig, callback) {

        this.setParameterConfig(parameterConfig);


        var storeRef = [];

        Ext.each(parameterConfig, function(config) {
            var widgetConfigObj = config.get('widgetConfigObj');
            DataStoreManager.registerStoreInstancesForDataSet(widgetConfigObj.dataSetId, widgetConfigObj.dataSetName, widgetConfigObj, config.get('value'), widgetConfigObj.useAggregateData, function(store) {
                storeRef.push(store);
            });
        });

        this.setFieldLabel(configObj.name);
        this.setStoreRef(storeRef);

        this.setComparison(configObj.comparison);

        this.updateLayout();

        Ext.callback(callback, this);
    }
});
