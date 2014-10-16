Ext.define('DD.view.parameters.MultiSliderControl', {
    extend:'Ext.slider.Multi',
    alias :'widget.multislidercontrol',
    values: [25, 75],
    increment: 1,
    minValue: 0,
    maxValue: 100,
    config:{
        parameterConfig:null,
        storeRef:null,
        controlConfig:null
    },
    labelSeparator:'',
    labelAlign:'top',
    initComponent : function() {

        this.listeners = {
            'change' : function(slider) {
                var values = slider.getValues();
                var parameterConfig = this.getParameterConfig();
                var configuration = this.getControlConfig();

                Ext.each(parameterConfig, function(config, index) {

                    var widgetConfigObj = config.get('widgetConfigObj');
                    var store = slider.getStoreRef()[index];
                    var dataSetId = widgetConfigObj.dataSetId;
                    var parameter = config.get('value');
                    store.clearFilter();

                    ParameterManager.updateStoreRecords(dataSetId, parameter, store, values, 'range', store.nameField, store.dataField, store.useAggregateData, function() {
                        slider.setFieldLabel(configuration.name + ' (' + values[0] + ' - ' + values[1] + ')');
                    });
                });

            }
        };

        this.callParent(arguments);
    },
    updateConfiguration : function(configObj, parameterConfig, callback) {

        this.setParameterConfig(parameterConfig);

        var minValue = parseInt(configObj.minValue);
        var maxValue = parseInt(configObj.maxValue);
        var defaultMinValue = 0;
        var defaultMaxValue = 0;

        if (configObj.defaultMinValue.length > 0)
            defaultMinValue = parseInt(configObj.defaultMinValue);
        else
            defaultMinValue = minValue;

        if (configObj.defaultMaxValue.length > 0)
            defaultMaxValue = parseInt(configObj.defaultMaxValue);
        else
            defaultMaxValue = maxValue;

        var storeRef = [];

        Ext.each(parameterConfig, function(config) {
            var widgetConfigObj = config.get('widgetConfigObj');
            DataStoreManager.registerStoreInstancesForDataSet(widgetConfigObj.dataSetId, widgetConfigObj.dataSetName, widgetConfigObj, config.get('value'), widgetConfigObj.useAggregateData, function(store) {
                storeRef.push(store);
            });
        });

        this.setFieldLabel(configObj.name);
        this.setStoreRef(storeRef);
        this.setControlConfig(configObj);

        this.setMinValue(minValue);
        this.setMaxValue(maxValue);
        this.setValue([defaultMinValue,defaultMaxValue], true);
        this.setFieldLabel(configObj.name + ' (' + minValue + ' - ' + maxValue + ')');

        Ext.callback(callback, this);
    }
});
