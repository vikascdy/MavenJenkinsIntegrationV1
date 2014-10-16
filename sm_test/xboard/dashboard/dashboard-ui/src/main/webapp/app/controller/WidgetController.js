Ext.define('DD.controller.WidgetController', {
    extend: 'Ext.app.Controller',
    views:[
        'widgets.WidgetTypeView',
        'widgets.WidgetWindow',
        'widgets.WidgetDataWindow',
        'widgets.WidgetLibrary',
        'widgets.SavedWidgets',
        'widgets.SavedWidgetsView',
        'widgets.SaveToLibraryWindow',
        'widgets.SaveToLibraryForm',
        'widgets.WidgetPreviewPanel',

        'widgets.general.ImagePanel',
        'widgets.general.TextPanel',

        'widgets.charts.ChartsGallery',
        'widgets.charts.AreaChart',
        'widgets.charts.BarChart',
        'widgets.charts.ColumnChart',
        'widgets.charts.LineChart',
        'widgets.charts.PieChart',
        'widgets.charts.RadarChart',
        'widgets.charts.ScatterChart',

        'widgets.grid.GridConfigForm',

        'widgets.shapes.ShapesGallery',
        'widgets.shapes.ShapesPanel',
        'widgets.shapes.ShapesWindow',
        'widgets.shapes.DrawComponent'
    ],
    models:['WidgetTypeListModel','WidgetListModel'],
    stores:['WidgetTypeListStore','WidgetListStore','ChartsGalleryStore','ShapesGalleryStore'],
    refs: [
        {
            ref: 'widgetLibrary',
            selector: 'widgetlibrary'
        },
        {
            ref: 'widgetWindow',
            selector: 'widgetwindow'
        },
        {
            ref: 'widgetPreviewPanel',
            selector: 'widgetpreviewpanel'
        }
    ],
    init :function() {
        this.control({
            'widgetlibrary #addToDashboard':{
                'click' : this.addSavedWidgetToDashboard
            },
            'widgetlibrary #customWidget':{
                'click' : this.createCustomWidget
            },
            'chartsgallery > button':{
                click : this.handleSelectedChart
            },

            'widgetwindow #back':{
                click : function(btn) {
                    var widgetWindow = btn.up('window');
                    DataSetManager.showDataSetWindow(function() {
                        widgetWindow.close();
                    });
                }
            },
            'widgetwindow #applyChangesToChart':{
                click : this.applyChangesForChart
            },
            'widgetwindow #applyChangesToGrid':{
                'click':this.applyChangesForGrid
            },
            'widgetwindow #saveInLibrary':{
                click : this.saveWidgetInLibrary
            },
            'widgetwindow #addToDashboard':{
                click : this.addWidgetToDashboard
            },
            'savetolibrarywindow #saveWidget':{
                click : this.saveWidget
            },
            'shapeswindow #addToDashboard':{
                click : this.addShapeToDashboard
            },
            'shapeswindow toolbar > button':{
                click:this.toolbarItemSelectionChange
            },
            'shapeswindow #cancel':{
                click:function(btn) {
                    btn.up('window').close();
                }
            },
            '#gridProperties':{
                'afterrender':function() {
                    var widgetWindow = this.getWidgetWindow();
                    WidgetPropertiesManager.getGridProperties(widgetWindow.fields, null, function(properties) {
                        var widgetPropertiesPanel = widgetWindow.down('#gridProperties');
                        widgetPropertiesPanel.setSource(properties[0], properties[1]);
                        widgetPropertiesPanel.updateLayout(true);
                    });

                }
            },
            'propertiespanel #updateProperties':{
                'click' : this.updateWidgetProperties
            }
        });
    },
    toolbarItemSelectionChange:function(ev) {
        ShapesManager.active = ev;
        ShapesManager.handleShapeSelection(ev, function(source) {
            var shapesWindow = ev.up('window');
            var propertyGrid = shapesWindow.down('propertygrid');
            propertyGrid.setSource(source[0], source[1]);

        });
    },
    addShapeToDashboard:function(btn) {
        var shapesWindow = btn.up('window');
        ShapesManager.handleAddEvent();
        shapesWindow.close();
        ShapesManager.active = null; // reset the active var
    },

    createCustomWidget : function(btn) {
        var widgetLibrary = btn.up('window');
        DataSetManager.showDataSetWindow(function() {
            widgetLibrary.close();
        });
    },

    handleSelectedChart  :function(btn) {

        var widgetWindow = this.getWidgetWindow();
        var widgetPreviewPanel = this.getWidgetPreviewPanel();
        var selected = btn.selectedWidget;
        widgetWindow.selectedWidget = selected;
        widgetWindow.setIsWidgetSaved(false);
        var dataSetId = widgetWindow.dataSetId;
        var fields = widgetWindow.fields;

        var widgetPropertiesPanel = widgetWindow.down('propertiespanel');
        var widgetId = selected.get('id');
        ChartManager.getComponentForId(widgetId, selected.get('type'), true, null, dataSetId, null, fields, function(widget) {
            widgetPreviewPanel.setLoading('Loading Widget Preview...', true);
            widgetPreviewPanel.updateWidgetPreview(widget, function() {
                WidgetManager.generateChartPropertiesForm(widget, fields, function(widgetProperties, fields) {
                    widgetPropertiesPanel.setSource(widgetProperties[0], widgetProperties[1]);
                    widgetPropertiesPanel.updateLayout(true);
                    widgetWindow.storeFields = fields;
                    widgetPreviewPanel.setLoading(false);
                });
            });
        });

    },

    applyChangesForChart : function(btn) {

        var widgetWindow = btn.up('window');
        var widgetPreviewPanel = widgetWindow.down('widgetpreviewpanel');
        var propertiesPanel = widgetWindow.down('propertiespanel');
        var DataSetResultStore = Ext.StoreManager.lookup('DataSetResultStore');

        var selectedWidget = widgetWindow.selectedWidget;
        var dataSetId = widgetWindow.dataSetId;
        var dataSetName = widgetWindow.dataSetName;
        var storeFields = widgetWindow.fields;

        var configObj = propertiesPanel.getSource();

        if (widgetPreviewPanel.getWidget()) {
            for (var i in configObj) {
                if (configObj[i] == null) {
                    Ext.Msg.alert('Invalid Configuration', 'Some fields are mandatory');
                    return;
                }
            }

            DD.loadingWindow = Ext.widget('progresswindow', {
                text: 'Loading Widget Preview...'
            });

            var widgetId = selectedWidget.get('id');

            DataStoreManager.registerStoreInstancesForDataSet(dataSetId, dataSetName, configObj, storeFields, 'chart', function(storeInstance) {
                storeInstance.clearFilter();
                ChartManager.getComponentForId(widgetId, selectedWidget.get('type'), false, configObj, dataSetId, storeInstance, storeFields, function(widget) {
                    widgetPreviewPanel.updateWidgetPreview(widget, function() {
                        widgetWindow.setIsWidgetConfigured(true);
                        DD.removeLoadingWindow(function() {

                        });
                    });
                });
            });
        }
    },

    applyChangesForGrid:function(btn) {

        var widgetWindow = btn.up('window');

        var dataSetId = widgetWindow.dataSetId;
        var dataSetName = widgetWindow.dataSetName;
        var storeFields = widgetWindow.fields;
        var previewPanel = widgetWindow.down('widgetpreviewpanel');


        var gridProperties = widgetWindow.down('#gridProperties');
        var configObj = gridProperties.getSource();
        var selectedColumns = configObj.columns;

        for (var i in configObj) {
            if (configObj[i] == null) {
                Ext.Msg.alert('Invalid Configuration', 'Some fields are mandatory');
                return;
            }
        }

        if (selectedColumns.length > 0) {
            DD.loadingWindow = Ext.widget('progresswindow', {
                text: 'Loading Widget Preview...'
            });
            DataStoreManager.registerStoreInstancesForDataSet(dataSetId, dataSetName, configObj, storeFields, false, function(storeInstance) {
                storeInstance.clearFilter();
                WidgetManager.createGridWidget(configObj, selectedColumns, dataSetId, storeInstance, storeFields, function(grid) {
                    previewPanel.updateWidgetPreview(grid, function() {
                        widgetWindow.setIsWidgetConfigured(true);
                        DD.removeLoadingWindow(function() {

                        });
                    });
                });
            });
        }
        else
            Ext.Msg.alert('No Column Selected', 'Please select columns for grid.');


    },

    saveWidgetInLibrary : function() {
        var widgetWindow = this.getWidgetWindow();
        var dataSetId = widgetWindow.dataSetId;
        var portletId = WidgetManager.activePortlet.id;
        var widgetPreviewPanel = this.getWidgetPreviewPanel();
        var widget = widgetPreviewPanel.getWidget();
        if (widget && widgetWindow.getIsWidgetConfigured()) {

            var portlet = Ext.getCmp(portletId);
            if (portlet) {
                Ext.widget({
                    xtype:'savetolibrarywindow',
                    widgetXtype:widget.xtype,
                    dataSetId:dataSetId,
                    widgetConfig:widget.configObj,
                    storeFields:widget.storeFields
                }).show();
            }
            else
                Ext.Msg.alert('Save Error', 'Failed to save widget to library');
        }
        else
            Ext.Msg.alert('Save Error', 'Please configure before saving.');


    },

    saveWidget : function(btn) {
        var widgetWindow = this.getWidgetWindow();
        var saveToLibraryWindow = btn.up('window');
        var saveToLibraryForm = saveToLibraryWindow.down('form').getForm();
        if (saveToLibraryForm.isValid()) {
            var values = saveToLibraryForm.getValues();
            var widgetConfig = saveToLibraryWindow.widgetConfig;
            var storeFields = saveToLibraryWindow.storeFields;

            widgetConfig['name'] = values.Name;

            if (widgetConfig.storeConfig)
                delete widgetConfig.storeConfig;
            if (widgetConfig.storeData)
                delete widgetConfig.storeData;


            widgetConfig['widgetXtype'] = saveToLibraryWindow.widgetXtype;
            widgetConfig['storeFields'] = Ext.encode(storeFields);

//            console.log(widgetConfig);

            values['Configuration'] = Ext.Object.toQueryString(widgetConfig);
            WidgetManager.createWidget(saveToLibraryWindow.dataSetId, WidgetManager.activeWidget.id, values, function(widgetId) {
                widgetWindow.setSavedWidgetId(widgetId);
                saveToLibraryWindow.close();
                widgetWindow.setIsWidgetSaved(true);
            });
        }
        else
            Ext.Msg.alert('Invalid Widget Info', 'Please provide complete information.');

    },

    addSavedWidgetToDashboard : function(btn) {
        var window = btn.up('window');
        var savedWidgetsList = window.down('savedwidgetsview');
        var selectedWidget = savedWidgetsList.getSelectionModel().getSelection();
        var dashboardElementsTreeStore = Ext.StoreManager.lookup('DashboardElementsTreeStore');
        var rootNode = dashboardElementsTreeStore.getRootNode();

        if (selectedWidget.length == 0)
            Ext.Msg.alert('No Widget Selected', 'Please select a widget to add.');
        else {
            WidgetManager.parseWidgetConfiguration(selectedWidget[0], function(widget) {
                WidgetManager.activePortlet.updateWidget(widget, function() {

                    var controlInfo = selectedWidget[0].get('widgetType')[0];
                    var widgetType = controlInfo.get('name');

                    var treeNode = rootNode.findChild('id', WidgetManager.activePortlet.id + '-node');
                    treeNode.destroy();

                    LayoutManager.addWidgetNodeToTree({
                        widgetName:selectedWidget[0].get('name'),
                        widgetDesc:selectedWidget[0].get('description'),
                        widgetId:selectedWidget[0].get('id'),
                        widgetType : widgetType,
                        type : widgetType,
                        id : WidgetManager.activePortlet.id + '-node',
                        holderId : WidgetManager.activePortlet.id,
                        leaf : true,
                        text : widget.configObj.title + ' (' + widgetType + ')',
                        checked : true
                    });

                    WidgetManager.activePortlet.setSavedWidgetId(selectedWidget[0].get('id'));
                    window.close();
                });
            });
        }
    },


    addWidgetToDashboard : function() {
        var widgetWindow = this.getWidgetWindow();
        var portletId = WidgetManager.activePortlet.id;
        if (widgetWindow.getIsWidgetSaved() && widgetWindow.getIsWidgetConfigured()) {

            var widgetPreviewPanel = widgetWindow.down('widgetpreviewpanel');
            if (widgetPreviewPanel.getWidget()) {

                var portlet = Ext.getCmp(portletId);
                if (portlet) {
                    var item = widgetPreviewPanel.getWidget();
                    portlet.setSavedWidgetId(widgetWindow.getSavedWidgetId());
                    item.dataSetId = widgetWindow.dataSetId;
                    portlet.updateWidget(item);
                    widgetWindow.close();
                }
                else
                    Ext.Msg.alert('Widget Error', 'Failed to add widget to dashboard');
            }

            else
                Ext.Msg.alert('No Widget Selected', 'Please select a widget first to configure.');

        }
        else
            Ext.Msg.alert('Widget Error', 'Please apply changes and save the widget before adding to dashboard.');
    },

    updateWidgetProperties : function(btn) {
        var propertiesPanel = btn.up('propertiespanel');
        var properties=propertiesPanel.getSource();

        for(var i in properties){
           var value=properties[i];
            if(value && value.indexOf(',')!=-1)
            {
                properties[i]=value.split(',');
            }
        }
        console.log(properties);
        WidgetManager.updateWidgetConfiguration(propertiesPanel.getWidgetInfo(), propertiesPanel.getWidgetType(), propertiesPanel.getWidgetHolderId(), properties, function() {

        });
    }
});