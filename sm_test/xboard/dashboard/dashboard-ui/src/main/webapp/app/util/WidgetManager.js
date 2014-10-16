Ext.define('Util.WidgetManager', {});

window.WidgetManager = {


    activePortlet : null,
    activeWidget : null,
    embeddedCode:null,
    isWidgetWizardActive:false,
    currentWidgetParsedId:null,
    parsingWidgetsMap  : Ext.create('Ext.util.HashMap'),

    removeWidgetFromCanvas : function(callback) {
        var activePortlet = WidgetManager.activePortlet;
        if (activePortlet.getSavedWidgetId() == null && !WidgetManager.isWidgetWizardActive) {
            activePortlet.removePortlet();
        }
        Ext.callback(callback, this);
    },

    createWidget : function(datasetId, widgetTypeId, widgetProperties, callback) {

        Ext.Ajax.request({
            url:JSON_SERVLET_PATH + 'createWidget',
            method:'POST',
            isUpload:datasetId == null,
            headers: datasetId != null ? {} : {'Content-type':'multipart/form-data'},
            params:{
                data : '{"datasetId":"' + datasetId + '","widgetTypeId":' + widgetTypeId + ',"widgetProperties":' + Ext.encode(widgetProperties) + '}'
            },
            success : function(response) {
                var respJson = Ext.decode(response.responseText);
                if (respJson.success != false) {
                    Ext.Msg.alert('Operation Success', 'Widget created successfully');
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

    updateWidget : function(widgetId, widgetProperties, callback) {

        Ext.Ajax.request({
            url:JSON_SERVLET_PATH + 'updateWidget',
            method:'POST',
            params:{
                data : '{"widgetId":' + widgetId + ',"widgetProperties":' + Ext.encode(widgetProperties) + '}'
            },
            success : function(response) {
                var respJson = Ext.decode(response.responseText);
                if (respJson.success != false) {
                    Ext.Msg.alert('Operation Success', 'Widget updated successfully');
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


    getWidget : function(widgetId, callback) {

        Ext.Ajax.request({
            url:JSON_SERVLET_PATH + 'getWidget',
            method:'POST',
            params:{
                data : '{"widgetId":' + widgetId + '}'
            },
            success : function(response) {
                var respJson = Ext.decode(response.responseText);
                if (respJson.success != false) {
                    Ext.callback(callback, this, [respJson]);
                }
                else {
                    DD.removeLoadingWindow(function() {
                        if (respJson.error == null)
                            Ext.Msg.alert('Operation Failed', 'Unable to get widget.');
                        else
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


    removeWidget : function(widgetId, callback) {

        Ext.Ajax.request({
            url:JSON_SERVLET_PATH + 'removeWidget',
            method:'POST',
            params:{
                data : '{"widgetId":' + widgetId + '}'
            },
            success : function(response) {
                var respJson = Ext.decode(response.responseText);
                if (respJson.success != false) {
                    Ext.Msg.alert('Operation Success', 'Widget deleted successfully');
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

    getWidgets : function(filter, callback) {

        var widgetListStore = Ext.StoreManager.lookup('WidgetListStore');
        widgetListStore.load({
            scope: this,
            callback: function(records, operation, success) {
                widgetListStore.clearFilter();
                widgetListStore.filter('widgetCategory', filter);
                Ext.callback(callback, this, [records]);
            }
        });
    },

    showWidgetLibrary : function(callback) {

        var widgetLibrary = Ext.widget({
            xtype:'widgetlibrary'
        });
        widgetLibrary.show();
        Ext.callback(callback, this, []);
    },


    findWidgetType : function(widgetType, portlet, e, widget, callback) {

        WidgetManager.activePortlet = portlet;
        WidgetManager.activeWidget = widget;

        switch (widgetType) {
            case 'chart':
                WidgetManager.showWidgetLibrary(function() {
                    Ext.callback(callback, this, [
                        Ext.widget({xtype:'panel',
                            border:false
                        })
                    ]);
                });
                break;
            case 'shape':
                ShapesManager.showShapesGallery(function() {
                    var shapesPanel = Ext.widget({xtype:'shapespanel',border:false});
                    Ext.callback(callback, this, [shapesPanel]);
                });
                break;
            case 'grid':
                WidgetManager.showWidgetLibrary(function() {
                    Ext.callback(callback, this, [
                        Ext.widget({xtype:'panel',border:false})
                    ]);
                });
                break;
            case 'text':
                MediaManager.showTextEditor(portlet, e, function() {
                    Ext.callback(callback, this, [
                        Ext.widget({xtype:'textpanel'})
                    ]);
                });
                break;
            case 'image':
                MediaManager.showImageGallery(portlet, e, function() {
                    Ext.callback(callback, this, [
                        Ext.widget({xtype:'imagepanel'})
                    ]);
                });
                break;
            case 'embedded':
                MediaManager.showCodeEmbeddedWindow(portlet, e, function() {
                    Ext.callback(callback, this, [
                        Ext.widget({xtype:'panel',isEmbeddedWidget:true})
                    ]);
                });
                break;
            default:
                Ext.callback(callback, this, []);
        }

    },

    getChartConfiguration : function() {

        return {
            xtype:'container',
            layout:{
                type:'vbox',
                align:'stretch'
            },
            items:[
                {
                    xtype:'container',
                    margin:'10 0 0 0',
                    flex:1,
                    layout:{
                        type:'hbox',
                        align:'stretch'
                    },
                    items:[
                        {
                            xtype:'widgetpreviewpanel',
                            margin:'0 10 0 0',
                            flex:1
                        },
                        {
                            xtype:'container',
                            flex:1,
                            layout:{type:'vbox',align:'stretch'},
                            items:[
                                {
                                    xtype:'container',
                                    margin:'0 0 5 0',
                                    height:25,
                                    layout:{type:'hbox',align:'stretch'},
                                    items:[
                                        {
                                            xtype:'component',
                                            html:'<h3>Widget Properties</h3>'
                                        },
                                        {
                                            xtype:'tbspacer',
                                            flex:1
                                        },
                                        {
                                            xtype:'button',
                                            iconCls:'apply',
                                            text:'Apply Changes',
                                            itemId:'applyChangesToChart'
                                        }
                                    ]
                                },
                                {
                                    xtype:'propertiespanel',
                                    itemId:'widgetPropertiesPanel',
                                    flex:1
                                }
                            ]
                        }
                    ]
                }
            ]
        };
    },

    getGridConfiguration : function() {

        return {
            xtype:'container',
            layout:{
                type:'hbox',
                align:'stretch'
            },
            items:[
                {
                    xtype:'widgetpreviewpanel',
                    margin:'0 10 0 0',
                    flex:1,
                    itemId:"gridPreview"
                },

                {
                    xtype:'container',
                    flex:1,
                    layout:{type:'vbox',align:'stretch'},
                    items:[
                        {
                            xtype:'container',
                            margin:'0 0 5 0',
                            height:25,
                            layout:{type:'hbox',align:'stretch'},
                            items:[
                                {
                                    xtype:'component',
                                    html:'<h3>Grid Properties</h3>'
                                },
                                {
                                    xtype:'tbspacer',
                                    flex:1
                                },
                                {
                                    xtype:'button',
                                    iconCls:'apply',
                                    text:'Apply Changes',
                                    itemId:'applyChangesToGrid'
                                }
                            ]
                        },
                        {
                            xtype:'propertiespanel',
                            itemId:'gridProperties',
                            flex:1
                        }
                    ]
                }
            ]
        };
    },

    createGridWidget : function(configObj, columns, dataSetId, gridStore, storeFields, callback) {

        var customModel = Ext.define('CustomModel', {
            extend:'Ext.data.Model',
            fields:columns
        });


        var grid = Ext.create('Ext.grid.Panel', {
            configObj:configObj,
            store:gridStore,
            collapsible:configObj.collapsible == "true",
            dataSetId:dataSetId,
            border:configObj.border == "true",
            autoScroll:configObj.autoScroll == "true",
            hideHeaders:configObj.hideHeaders == "true",
            storeFields:storeFields,
            isDrillingAllowed:true,
            scaffold: {
                target: customModel,
                oneStorePerModel: false,
                deletable: false,
                buttons: []
            }
        });

        Ext.callback(callback, this, [grid]);
    },

    generateChartPropertiesForm : function(widget, fields, callback) {
        var chartType = widget.xtype;
        WidgetPropertiesManager.getChartProperties(chartType, fields, function(widgetProperties) {
            Ext.callback(callback, this, [widgetProperties,fields]);
        });
    },

    parseWidgetConfiguration : function(selectedWidget, callback) {

        var dataSetId = selectedWidget.get('datasetId');
        var widgetConfiguration = null;
        var widget = null;

        Ext.each(selectedWidget.get('parameters'), function(param) {
            if (param.parameterDef.name == 'Configuration') {
                widgetConfiguration = Ext.Object.fromQueryString(param.value);
            }
        });
//         console.log("Loading Data Set By ID");
        DataSetManager.loadDataSetStoreById(dataSetId, function(dataSetDef) {
            var widgetType = selectedWidget.get('widgetXtype');

            var storeFields = Ext.decode(widgetConfiguration.storeFields);

            var useAggregateData = true;

            if (selectedWidget.get('widgetXtype') == 'gridpanel')
                useAggregateData = false;

//         console.log("Registering Data Set To Store");

            DataStoreManager.registerStoreInstancesForDataSet(dataSetId, dataSetDef[0].get('name'), widgetConfiguration, storeFields, useAggregateData, function(dataStore) {

                if (selectedWidget.get('widgetXtype') == 'gridpanel') {
                    WidgetManager.createGridWidget(widgetConfiguration, widgetConfiguration.columns, dataSetId, dataStore, storeFields, function(grid) {
                        widget = grid;
                    });
                }
                else
                    widget = Ext.widget({
                        xtype:widgetType,
                        dataSetId:dataSetId,
                        configObj:widgetConfiguration,
                        useSampleStore:false,
                        storeInstance:dataStore,
                        storeFields:storeFields
                    });

                if (widget)
                    Ext.callback(callback, this, [widget]);
            });
        });

    },

    updateWidgetConfiguration : function(widgetInfo, widgetType, widgetHolderId, properties, callback) {

        var storeConfig = {};

        var portlet = Ext.getCmp(widgetHolderId);
        if (portlet && portlet.getSavedWidgetId() != null) {

            var widget = portlet.getWidget();
            if (widgetType == 'chart') {


                var storeInstance = widget.getStore();
                storeConfig['fields'] = storeInstance.model.getFields();

                ChartManager.getComponentForId(widget.xtype, widgetType, false, properties, widget.getStore().storeId, storeInstance, storeInstance.model.getFields(), function(chart) {

                    DD.loadingWindow = Ext.widget('progresswindow', {
                        text: 'Updating Widget Properties...'
                    });
                    storeInstance.clearFilter();

                    properties['widgetXtype']=widget.xtype;
                    properties['storeFields']=Ext.encode(storeInstance.model.getFields());

                    var widgetProperties = {
                        Name : widgetInfo.widgetName,
                        Description:widgetInfo.widgetDesc,
                        Configuration:Ext.Object.toQueryString(properties)
                    };
                    portlet.updateWidget(chart, function() {

                        WidgetManager.updateWidget(widgetInfo.widgetId, widgetProperties, function() {
                            DD.removeLoadingWindow(function() {

                            });
                        });

                    });
                });

            }
        }
        Ext.callback(callback, this);
    },

    drillDataForWidget : function(widget, field, value) {
        var store = widget.getStore();
        var dataSetId = store.storeId;
        widget.setLoading('Refreshing Data');
        ParameterManager.updateStoreRecords(dataSetId, field, store, [value.toString()], 'single', store.nameField, store.dataField, store.useAggregateData, function() {
            widget.setLoading(false);
        });

    }

};

