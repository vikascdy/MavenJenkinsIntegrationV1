Ext.define('DD.controller.DataSetController', {
    extend: 'Ext.app.Controller',
    views:[
        'dataset.DataSetWindow',
        'dataset.DataSetList',
        'dataset.CreateDataSetForm'
    ],
    models:[
        'DataSetTypeListModel',
        'DataSetListModel',
        'DataSetResultModel'
    ],
    stores:[
        'DataSetTypeListStore',
        'DataSetListStore',
        'DataSetResultStore'
    ],
    refs: [
        {
            ref: 'datasetlist',
            selector: 'dataSetList'
        }
    ],
    init : function() {
        this.control({
            'createdatasetform #create':{
                click : this.createDataset
            },
            'datasetwindow #back':{
                click : function(btn) {
                    var dataSetWindow = btn.up('window');
                    WidgetManager.showWidgetLibrary(function() {
                        dataSetWindow.close();
                    });
                }
            },
            'datasetwindow #proceed':{
                click : function(btn) {
                    var dataSetWindow = btn.up('window');
                    var dataSetList = dataSetWindow.down('datasetlist');
                    var selection = dataSetList.getSelectionModel().getSelection();
                    if (selection.length > 0) {

                        DataSetManager.getDatasetMeta(selection[0].get('id'), function(fields) {
                            var widgetWindow = Ext.widget({
                                xtype:'widgetwindow',
                                dataSetId:selection[0].get('id'),
                                dataSetName:selection[0].get('name'),
                                fields:fields
                            });
                            widgetWindow.addConfigurationToWidgetType(WidgetManager.activePortlet.widgetType, function() {
                                dataSetWindow.close();
                                widgetWindow.show();
                            });
                        });
                    }
                    else {
                        Ext.Msg.alert('Invalid Selection', 'Please select a valid data set from list.');
                    }

                }
            }
        });
    },
    createDataset : function(btn) {
        var formPanel = btn.up('form');
        var form = formPanel.getForm();
        if (form.isValid()) {
            DD.loadingWindow = Ext.widget('progresswindow', {
                text: 'Creating Data Set...'
            });
            var values = form.getValues();

            delete values.dataSetType;

            if (formPanel.dataSetType == 'simple') {
                var datasourceId = values.datasourceId;
                var datasetTypeId = values.datasetTypeId;
                delete values.datasourceId;
                delete values.datasetTypeId;
                DataSetManager.createDataset(datasourceId, datasetTypeId, values, function(result) {
                    var dataSetListStore = Ext.StoreManager.lookup('DataSetListStore');
                    dataSetListStore.load();
                    DD.loadingWindow.destroy();
                });
            }
            else {
                var datasetProperties = {
                    Name:values.Name,
                    Query:encodeURIComponent(Ext.encode(JSON.minify(values.Query))),
                    Description:values.Description
                };
                DataSetManager.createCompositeDataset(values.datasetIds, datasetProperties, function(result) {
                    var dataSetListStore = Ext.StoreManager.lookup('DataSetListStore');
                    dataSetListStore.load();
                    DD.loadingWindow.destroy();
                });
            }
        }
    }
});