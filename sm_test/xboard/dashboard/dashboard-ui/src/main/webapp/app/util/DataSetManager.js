Ext.define('Util.DataSetManager', {});

window.DataSetManager = {


    showDataSetWindow : function(callback) {

        var dataSetWindow = Ext.widget({
            xtype:'datasetwindow'
        });
        dataSetWindow.show();
        Ext.callback(callback, this, []);
    },

    getDatasets : function(callback) {
        var dataSetListStore = Ext.StoreManager.lookup('DataSetListStore');
        dataSetListStore.removeAll();
        dataSetListStore.load({
            scope: this,
            callback: function(records, operation, success) {
                Ext.callback(callback, this, [records]);
            }
        });
    },

    findDataSetInStore : function(dataSetId, callback) {
        var dataSetListStore = Ext.StoreManager.lookup('DataSetListStore');
        var index = dataSetListStore.find('id', dataSetId);
        if (index != -1) {
            Ext.callback(callback, this, [dataSetListStore.getAt(index)]);
        }
        else
            Ext.callback(callback, this, null);
    },

    loadDataSetStoreById : function(datasetId, callback) {

        var dataSetListStore = Ext.StoreManager.lookup('DataSetListStore');
        dataSetListStore.getProxy().url = JSON_SERVLET_PATH + 'getDatasetById';
        dataSetListStore.getProxy().actionMethods = {
            read:'POST'
        };
        dataSetListStore.getProxy().extraParams = {
            data : '{"datasetId":' + datasetId + '}'
        };
        dataSetListStore.removeAll();
        dataSetListStore.load({
            scope: this,
            callback: function(records, operation, success) {
                Ext.callback(callback, this, [records]);
            }
        });
    },

    getDatasetTypeById : function(datasetTypeId, callback) {

        Ext.Ajax.request({
            url:JSON_SERVLET_PATH + 'getDatasetTypeById',
            method:'POST',
            params:{
                data : '{"datasetTypeId":' + datasetTypeId + '}'
            },
            success : function(response) {
                var respJson = Ext.decode(response.responseText);
                if (respJson.success != false) {
                    Ext.callback(callback, this, [respJson]);
                }
                else
                    Ext.Msg.alert('Operation Failed', respJson.error);
            },
            failure : function(response) {
                var respJson = Ext.decode(response.responseText);
                Ext.Msg.alert('Operation Failed', respJson.error);
            }
        });

    },

    createDataset : function(datasourceId, datasetTypeId, datasetProperties, callback) {

        Ext.Ajax.request({
            url:JSON_SERVLET_PATH + 'createDataset',
            timeout:600000, //10 minutes
            method:'POST',
            params:{
                data : '{"datasourceId":' + datasourceId + ',"datasetTypeId":' + datasetTypeId + ',"datasetProperties":' + Ext.encode(datasetProperties) + '}'
            },
            success : function(response) {
                var respJson = Ext.decode(response.responseText);
                if (respJson.success != false) {
                    Ext.Msg.alert('Operation Success', 'Data set created successfully');
                    Ext.callback(callback, this, [respJson]);
                }
                else {
                    DD.removeLoadingWindow(function() {
                        Ext.Msg.alert('Operation Failed', respJson.error);
                    });
                }
            },
            failure : function(response) {
                var respJson = Ext.decode(response.responseText);
                DD.removeLoadingWindow(function() {
                    Ext.Msg.alert('Operation Failed', respJson.error);
                });
            }
        });

    },

    removeDataset : function(datasetId, callback) {

        Ext.Ajax.request({
            url:JSON_SERVLET_PATH + 'removeDataset',
            method:'POST',
            params:{
                data : '{"datasetId":' + datasetId + '}'
            },
            success : function(response) {
                var respJson = Ext.decode(response.responseText);
                if (respJson.success != false) {
                    Ext.Msg.alert('Operation Success', 'Data set deleted successfully');
                    Ext.callback(callback, this, [respJson]);
                }
                else {
                    DD.removeLoadingWindow(function() {
                        Ext.Msg.alert('Operation Failed', respJson.error);
                    });
                }
            },
            failure : function(response) {
                var respJson = Ext.decode(response.responseText);
                DD.removeLoadingWindow(function() {
                    Ext.Msg.alert('Operation Failed', respJson.error);
                });
            }
        });

    },

    createCompositeDataset : function(datasetIds, datasetProperties, callback) {

        Ext.Ajax.request({
            url:JSON_SERVLET_PATH + 'createCompositeDataset',
            timeout:600000, //10 minutes
            method:'POST',
            params:{
                data : '{"datasetIds":' + Ext.encode(datasetIds) + ',"datasetProperties":' + Ext.encode(datasetProperties) + '}'
            },
            success : function(response) {
                var respJson = Ext.decode(response.responseText);
                if (respJson == null || respJson.success != false) {
                    DD.removeLoadingWindow(function() {
                        Ext.Msg.alert('Operation Success', 'Data set created successfully');
                        Ext.callback(callback, this, [respJson]);
                    });
                }
                else {
                    DD.removeLoadingWindow(function() {
                        Ext.Msg.alert('Operation Failed', respJson.error);
                    });
                }
            },
            failure : function(response) {
                var respJson = Ext.decode(response.responseText);
                DD.removeLoadingWindow(function() {
                    Ext.Msg.alert('Operation Failed', respJson.error);
                });
            }
        });

    },


    getDatasetTypesForDatasourceType : function(datasourceTypeId, callback) {

        Ext.Ajax.request({
            url:JSON_SERVLET_PATH + 'getDatasetTypesForDatasourceType',
            method:'POST',
            params:{
                data : '{"datasourceTypeId":' + datasourceTypeId + '}'
            },
            success : function(response) {
                var respJson = Ext.decode(response.responseText);
                if (respJson.success != false) {
                    Ext.callback(callback, this, [respJson]);
                }
                else
                    Ext.Msg.alert('Operation Failed', respJson.error);
            },
            failure : function(response) {
                var respJson = Ext.decode(response.responseText);
                Ext.Msg.alert('Operation Failed', respJson.error);
            }
        });

    },

    getDatasetMeta : function(datasetId, callback) {

        Ext.Ajax.request({
            url:JSON_SERVLET_PATH + 'getDatasetMeta',
            method:'POST',
            params:{
                data : '{"datasetId":' + datasetId + '}'
            },
            success : function(response) {
                var respJson = Ext.decode(response.responseText);
                if (respJson.success != false) {
                    DataStoreManager.createFieldArrayForStore(respJson.meta.properties, function(fields) {
                        Ext.callback(callback, this, [fields]);
                    });
                }
                else
                    Ext.Msg.alert('Operation Failed', respJson.error);
            },
            failure : function(response) {
                var respJson = Ext.decode(response.responseText);
                Ext.Msg.alert('Operation Failed', respJson.error);
            }
        });

    },

    getDatasetResult : function(datasetId, nameField, dataField, useAggregateData, callback) {

        var dataSetResultStore = Ext.StoreManager.lookup('DataSetResultStore');
        var fieldMeta = {};
        if (!useAggregateData) {

            dataSetResultStore.getProxy().extraParams = {
                data : '{"datasetId":' + datasetId + '}'
            };
            dataSetResultStore.getProxy().url = JSON_SERVLET_PATH + 'getDatasetResult';
        }
        else
        if (useAggregateData && dataField == null) {
            fieldMeta = {
                name : nameField
            };
            dataSetResultStore.getProxy().extraParams = {
                data : '{"datasetId":' + datasetId + ',"fieldMeta":' + Ext.encode(fieldMeta) + '}'
            };
            dataSetResultStore.getProxy().url = JSON_SERVLET_PATH + 'getParameterNames';
        }
        else {
            fieldMeta = {
                name : nameField,
                data : dataField
            };
            dataSetResultStore.getProxy().extraParams = {
                data : '{"datasetId":' + datasetId + ',"fieldMeta":' + Ext.encode(fieldMeta) + '}'
            };
            dataSetResultStore.getProxy().url = JSON_SERVLET_PATH + 'getAggregatedDatasetResult';
        }

        dataSetResultStore.load({
            scope: this,
            callback: function(records, operation, success) {
                if (records) {
                    Ext.callback(callback, this, [records[0]]);
                }
                else
                    DD.removeLoadingWindow(function() {
                        Ext.Msg.alert('Operation Failed', 'Could not retrieve data set information.');
                    });

            }
        });

    },

    getAggregatedDatasetResult : function(datasetId, fieldMeta, callback) {

        var dataSetResultStore = Ext.StoreManager.lookup('DataSetResultStore');
        dataSetResultStore.getProxy().url = JSON_SERVLET_PATH + 'getAggregatedDatasetResult',
            dataSetResultStore.getProxy().extraParams = {
                data : '{"datasetId":' + datasetId + ',"fieldMeta":' + fieldMeta + '}'
            };
        dataSetResultStore.load({
            scope: this,
            callback: function(records, operation, success) {
                if (records)
                    Ext.callback(callback, this, [records[0]]);
                else
                    DD.removeLoadingWindow(function() {
                        Ext.Msg.alert('Operation Failed', 'Could not retrieve data set information.');
                    });

            }
        });

    },

    getDatasetPreview : function(datasetId, callback) {

        var dataSetResultStore = Ext.StoreManager.lookup('DataSetResultStore');
        dataSetResultStore.getProxy().url = JSON_SERVLET_PATH + 'getDatasetPreview',
            dataSetResultStore.getProxy().extraParams = {
                data : '{"datasetId":' + datasetId + '}'
            };
        dataSetResultStore.load({
            scope: this,
            callback: function(records, operation, success) {
                if (records)
                    Ext.callback(callback, this, [records[0]]);
                else
                    DD.removeLoadingWindow(function() {
                        Ext.Msg.alert('Operation Failed', 'Could not retrieve data set information.');
                    });

            }
        });

    },

    generateDataSetConfigForm : function(dataSetTypeDef, callback) {

        var configItems = [];
        var properties = dataSetTypeDef.properties;

        Ext.each(properties, function(prop) {
            configItems.push({
                xtype:(prop.name == 'Query' || prop.name == 'Description') ? 'textarea' : 'textfield',
                name:prop.name,
                fieldLabel:prop.name,
                allowBlank:!prop.isRequired
            });

            if (prop.name == 'Query') {
                configItems.push({
                    xtype:'container',
                    layout:'hbox',
                    margin:'0 0 7 0',
                    items:[
                        {
                            xtype:'tbspacer',
                            flex:1
                        },
                        {
                            xtype:'button',
                            text:'Query Builder',
                            itemId:'queryBuilder',
                            handler : function() {
                                var queryBuilderWindow = Ext.widget({xtype:'querybuilderwindow'});
                                queryBuilderWindow.show(this.getEl());
                            }
                        }
                    ]
                });
            }
        });

        Ext.callback(callback, this, [configItems]);

    }

};

