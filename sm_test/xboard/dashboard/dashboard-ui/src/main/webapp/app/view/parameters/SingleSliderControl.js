Ext.define('DD.view.parameters.SingleSliderControl', {
    extend:'Ext.slider.Single',
    alias :'widget.singleslidercontrol',
    value: 50,
    increment: 1,
    minValue: 0,
    maxValue: 100,
    config:{
        parameterConfig:null,
        vertical:false,
        storeRef:null,
        controlConfig:null
    },
    labelSeparator:'',
    labelAlign:'top',
    initComponent : function() {

        this.listeners = {
            'change' : function(slider, newValues, thumb) {

                var parameterConfig = this.getParameterConfig();
                var configuration = this.getControlConfig();

                Ext.each(parameterConfig, function(config, index) {

                    var widgetConfigObj = config.get('widgetConfigObj');
                    var store = slider.getStoreRef()[index];
                    var dataSetId = widgetConfigObj.dataSetId;
                    var parameter = config.get('value');
                    store.clearFilter();
                    ParameterManager.updateStoreRecords(dataSetId, parameter, store, [newValues.toString()], 'single', store.nameField, store.dataField, store.useAggregateData, function() {
                        slider.setFieldLabel(configuration.name + ' (' + newValues + ')');
                    });
                });

            }
        };

        this.callParent(arguments);
    },
    updateConfiguration : function(configObj, parameterConfig, callback) {

        this.setParameterConfig(parameterConfig);
//
        var minValue = parseInt(configObj.minValue);
        var maxValue = parseInt(configObj.maxValue);

        var storeRef = [];

        Ext.each(parameterConfig, function(config) {
            var widgetConfigObj = config.get('widgetConfigObj');
            DataStoreManager.registerStoreInstancesForDataSet(widgetConfigObj.dataSetId, widgetConfigObj.dataSetName, widgetConfigObj, config.get('value'), widgetConfigObj.useAggregateData, function(store) {
                storeRef.push(store);
            });
        });


        this.setStoreRef(storeRef);
        this.setControlConfig(configObj);

        this.setMinValue(minValue);
        this.setMaxValue(maxValue);


        if (configObj.defaultValue.length>0) {
            var defaultValue = parseInt(configObj.defaultValue);
            this.setValue(defaultValue);
            this.setFieldLabel(configObj.name + ' (' + defaultValue + ')');
        }
        else{
            this.setValue(minValue);
            this.setFieldLabel(configObj.name);
        }

        this.setVertical(configObj.vertical);

        Ext.callback(callback, this);
    }
});
