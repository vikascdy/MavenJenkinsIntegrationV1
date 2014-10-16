Ext.define('Util.DataStoreManager', {});

window.DataStoreManager = {

    registeredStore : Ext.create('Ext.util.HashMap'),

    registerStoreInstancesForDataSet : function(dataSetId, dataSetName, configObj, fields, useAggregateData, callback) {

        var nameField = null;
        var dataField = null;
        var widgetName = new Date().getTime().toString();

        if (configObj && configObj.name)
            widgetName = configObj.name;

        var storeName = widgetName + '-' + dataSetName + '-' + dataSetId + '-store';

//        console.log(storeName,dataSetId, dataSetName, configObj, fields, useAggregateData);

        if (useAggregateData) {
            if (configObj.widgetXtype) {

                if (configObj.widgetXtype == 'barchart' || configObj.widgetXtype == 'piechart') {
                    dataField = configObj.Xfields;
                    nameField = configObj.Yfields;
                }
                else {
                    dataField = configObj.Yfields;
                    nameField = configObj.Xfields;
                }
            }
            else {
                if (Ext.isArray(configObj.Yfields)) {
                    dataField = configObj.Yfields[0];
                    nameField = configObj.Xfields;
                }
                else {
                    dataField = Ext.isArray(configObj.Xfields) ? configObj.Xfields[0] : configObj.Xfields;
                    nameField = configObj.Yfields;
                }
            }
        }


        if (!DataStoreManager.registeredStore.containsKey(storeName)) {

            DataSetManager.getDatasetResult(dataSetId, nameField, dataField, useAggregateData, function(result) {
                var storeData = result.get('data');
                Ext.define('Model', {
                    extend: 'Ext.data.Model',
                    fields: fields,
                    idProperty:null
                });
                var store = Ext.create('Ext.data.Store', {
                    storeId:dataSetId,
                    model:Model,
                    data:storeData,
                    nameField:nameField,
                    dataField:dataField,
                    useAggregateData:useAggregateData
                });
                DataStoreManager.registeredStore.add(storeName, store);
                Ext.callback(callback, this, [store]);
            });
        }
        else
            Ext.callback(callback, this, [DataStoreManager.registeredStore.get(storeName)]);
    },

    createFieldArrayForStore : function(meta, callback) {
        var fieldsArray = [];
        for (var i in meta) {
            fieldsArray.push({'name':i,'type':meta[i].type});
        }
        Ext.callback(callback, this, [fieldsArray]);

    }

};

