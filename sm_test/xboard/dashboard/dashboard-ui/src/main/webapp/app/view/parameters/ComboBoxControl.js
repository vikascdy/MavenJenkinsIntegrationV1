Ext.define('DD.view.parameters.ComboBoxControl', {
    extend:'Ext.form.ComboBox',
    alias :'widget.comboboxcontrol',
    editable:false,
    displayField:'name',
    nameField:'name',
    query:'local',
    config:{
        parameterConfig:null,
        store:null,
        storeRef:null
    },
    queryMode:'local',
    multiSelect:true,
    listConfig : {
        getInnerTpl : function() {
            return '<div class="x-combo-list-item"><img src="'
                + Ext.BLANK_IMAGE_URL
                + '"'
                + 'class="chkCombo-default-icon chkCombo" /> {name} </div>';
        }
    },
    initComponent : function() {

        this.listeners = {
            'change' : function(combo, newValue) {
                var parameterConfig = this.getParameterConfig();

                Ext.each(parameterConfig, function(config, index) {
                    var widgetConfigObj=config.get('widgetConfigObj');
                    var store = combo.getStoreRef()[index];
                    var dataSetId = widgetConfigObj.dataSetId;
                    var parameter = config.get('value');

                    store.clearFilter();
                    if (newValue.length > 0) {
                        ParameterManager.updateStoreRecords(dataSetId, parameter, store, newValue, 'single', store.nameField, store.dataField, store.useAggregateData, function() {

                        });
                    }
                    else
                        store.removeAll();
                });
            }
        };

        this.callParent(arguments);
    },
    updateConfiguration : function(configObj, parameterConfig, callback) {


        this.setParameterConfig(parameterConfig);

        var storeRef = [];

        Ext.each(parameterConfig, function(config) {
            var widgetConfigObj=config.get('widgetConfigObj');
            DataStoreManager.registerStoreInstancesForDataSet(widgetConfigObj.dataSetId,widgetConfigObj.dataSetName,widgetConfigObj,config.get('value'),widgetConfigObj.useAggregateData,function(store){
                  storeRef.push(store);
            });
        });

        this.setFieldLabel(configObj.name);
        this.setStoreRef(storeRef);

        var data = [];
        Ext.each(storeRef, function(store, index) {
            var param = configObj.parameter[index];

            Ext.each(store.getRange(), function(rec) {
                data.push({
                    'name':rec.get(param)
                });
            });
        });

        this.setStore(Ext.create('Ext.data.Store', {
            fields:['name'],
            idProperty : 'name',
            data:data
        }));

        if (configObj.defaultValue)
            this.setValue(configObj.defaultValue);

        this.updateLayout();

        Ext.callback(callback, this);
    }
});
