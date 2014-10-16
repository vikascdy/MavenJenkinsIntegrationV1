Ext.define('DD.controller.DashboardController', {
    extend: 'Ext.app.Controller',
    views:[
        'dashboard.SaveDashboardWindow',
        'dashboard.SaveDashboardForm',
        'dashboard.DashboardDesignerPane',
        'dashboard.DashboardElementsTree',
        'dashboard.DashboardCanvasHolder'
    ],
    models:[
        'DashboardListModel'
    ],
    stores:[
        'DashboardListStore'
    ],
    refs: [
        {
            ref: 'freeFormLayout',
            selector: 'freeformlayout'
        }
    ],
    init:function() {
        this.control({
            'dashboardcanvasholder #deleteDashboard':{
                click : this.deleteDashboard
            },
            'dashboardcanvasholder #saveDashboard':{
                click : this.showSaveDashboardWindow
            },
            'dashboardcanvasholder #cloneDashboard':{
                click : this.showSaveDashboardWindow
            },
            'savedashboardwindow #saveDashboard':{
                click : this.saveDashboard
            },
            'chart':{
                drillData : this.handleDrillData
            }
        });
    },
    deleteDashboard : function() {
        if (DashboardManager.currentDashboardId) {
            Ext.Msg.confirm(
                'Delete Confirmation',
                'Are you sure you want to delete this dashboard ?',
                function(btn) {
                    if (btn === 'yes') {

                        DD.loadingWindow = Ext.widget('progresswindow', {
                            text: 'Deleting Dashboard "' + DashboardManager.dashboardName + '" ...'
                        });
                        DashboardManager.removeXBoard(DashboardManager.currentDashboardId, function() {
                            DD.removeLoadingWindow(function() {
                                DashboardManager.currentDashboardId = null;
                                Ext.Msg.alert('Dashboard Deleted', 'Dashboard deleted succesfully.');
                                window.location.href = '#/dashboard';
                            });

                        });
                    }
                }
            );


        }
        else
            Ext.Msg.alert('Invalid Dashboard', 'There is no dashboard opened to delete. Please select a dashboard first.');

    },
    showSaveDashboardWindow : function(btn) {

        DashboardManager.getXBoardTypes(function(dashboardType) {
            var saveDashboardWindow = Ext.widget({
                xtype:'savedashboardwindow',
                dashboardType:dashboardType[0],
                mode:btn.mode
            });
            saveDashboardWindow.show(btn.getEl());
        });

    },
    saveDashboard : function(btn) {
        var formWindow = btn.up('window');
        var form = formWindow.down('form').getForm();

        if (form.isValid()) {


            var xBoardProperties = form.getValues();
            formWindow.close();
            var freeFormLayout = this.getFreeFormLayout();

            var dashboardItems = freeFormLayout.getLayout().getLayoutItems();
            var widgetIds = [];
            var widgetsConfiguration = [];
            var parametersConfiguration = [];
            var textWidgetConfiguration = [];
            var embeddedWidgetConfiguration = [];
            var imageWidgetConfiguration = [];

            Ext.each(dashboardItems, function(item) {
                var el=null;
                var HTML=null;

//                console.log(item);
                if (!item.isHidden()) {

                    if (item.xtype == 'portlet' && item.getSavedWidgetId()) {
                        widgetIds.push(item.getSavedWidgetId().toString());
                        widgetsConfiguration.push({
                            widgetId:item.getSavedWidgetId().toString(),
                            gridDimensions:item.getGridDimensions()
                        });
                    }
                    else
                    if (item.xtype == 'parametercontrol' && item.getIsParameterConfigured()) {

                        var dataSetId = [];

                        Ext.each(item.getParameterConfig(), function(dataSet) {
                            dataSetId.push({
                                'value':dataSet.get('value'),
                                'widgetConfigObj':dataSet.get('widgetConfigObj')
                            });
                        });

                        parametersConfiguration.push({
                            parameterXtype:item.getParameterControl().xtype,
                            gridDimensions:item.getGridDimensions(),
                            configObj:item.getConfigObj(),
                            parameterConfig:dataSetId
                        });
                    }
                    else
                    if (item.xtype == 'portlet' && item.getWidget().isTextWidget) {

                        el = item.getWidget().getEl();
                        HTML = el.dom.innerHTML.split('"').join('!@#');
                        textWidgetConfiguration.push({
                            gridDimensions:item.getGridDimensions(),
                            text:HTML
                        });
                    }
                    else
                    if (item.xtype == 'portlet' && item.getWidget().isEmbeddedWidget) {

                        var widget = item.getWidget();
                        var code = widget.code;
                        if (code) {
                            code = code.split('"').join('!@#');
                            code = code.split("'").join('$%^');
                        }
                        embeddedWidgetConfiguration.push({
                            gridDimensions:item.getGridDimensions(),
                            code:code
                        });
                    }else
                    if (item.xtype == 'portlet' && item.getWidget().isImageWidget) {

                        el = item.getWidget().getEl();
                        HTML = el.dom.innerHTML.split('"').join('!@#');
                        imageWidgetConfiguration.push({
                            gridDimensions:item.getGridDimensions(),
                            image:HTML,
                            configObj:item.getWidget().configObj
                        });
                    }

                }
            });


            var dashboardConfiguration = {
                noOfRows:DashboardManager.noOfRows,
                noOfCols:DashboardManager.noOfCols
            };

            var configuration = {
                'dashboardConfiguration':Ext.encode(dashboardConfiguration),
                'widgetsConfiguration' : Ext.encode(widgetsConfiguration),
                'parametersConfiguration' : Ext.encode(parametersConfiguration),
                'textWidgetConfiguration':Ext.encode(textWidgetConfiguration),
                'embeddedWidgetConfiguration':Ext.encode(embeddedWidgetConfiguration),
                'imageWidgetConfiguration':Ext.encode(imageWidgetConfiguration)

            };

            xBoardProperties['Configuration'] = Ext.Object.toQueryString(configuration);

//            console.log(xBoardProperties);
            if (widgetIds.length == 0 && textWidgetConfiguration.length == 0) {
                Ext.Msg.alert('Invalid Widget', 'Dashboard does not have any valid widget.');
            }
            else {
                if (DashboardManager.currentDashboardId && formWindow.mode == 'save') {
                    DD.loadingWindow = Ext.widget('progresswindow', {
                        text: 'Updating Dashboard...'
                    });
                    DashboardManager.updateXBoard(DashboardManager.currentDashboardId, widgetIds, xBoardProperties, function() {
                        DashboardManager.getXBoards(function() {
                            DD.removeLoadingWindow(function() {
                                Ext.Msg.alert('Operation Success', 'XBoard updated successfully');
                            });
                        });
                    });
                }
                else {
                    DD.loadingWindow = Ext.widget('progresswindow', {
                        text: 'Saving Dashboard...'
                    });
                    DashboardManager.createXBoard(formWindow.dashboardTypeId, widgetIds, xBoardProperties, function(dashboardId) {
                        DashboardManager.currentDashboardId = dashboardId;
                        DD.removeLoadingWindow(function() {
                            Ext.Msg.alert('Operation Success', 'XBoard saved successfully');
                            window.location.href = '#/editDashboard/' + dashboardId;
                        });
                    });
                }
            }
        }
        else
            Ext.Msg.alert('Save Error', 'Please specify complete information.');
    },

    handleDrillData : function(origin, field, value) {

        if (origin) {
            var originId = origin.id;
            var freeFormLayout = this.getFreeFormLayout();
            var dashboardItems = freeFormLayout.getLayout().getLayoutItems();

            var originWidget = origin.getWidget();
            var originConfig = originWidget.configObj;
            var originParam = originConfig.title + ' - ' + field;

            Ext.each(dashboardItems, function(item) {

                if (item.xtype == 'portlet' && item.id != originId)
                    if (item.getWidget() && item.getWidget().isDrillingAllowed) {
                        var widget = item.getWidget();
                        var widgetConfig = widget.configObj;
                        Ext.each(widgetConfig.listenerFields, function(listenerField) {
                            if (listenerField == originParam)
                                WidgetManager.drillDataForWidget(widget, field, value);
                        });
                    }
            });
        }


    }

});