Ext.define('Util.DashboardManager', {});

window.DashboardManager = {

    noOfRows : 35,
    noOfCols : 50,
    targetGridCellCount:5,
    isEditMode:false,
    currentDashboardId:null,
    dashboardName:'Dashboard',
    progressBarStatus : null,

    getXBoardTypes : function(callback) {

        Ext.Ajax.request({
            url:JSON_SERVLET_PATH + 'getXBoardTypes',
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

    getXBoards : function(callback) {

        var dashboardListStore = Ext.StoreManager.lookup('DashboardListStore');
        dashboardListStore.load({
            scope: this,
            callback: function(records, operation, success) {
                Ext.callback(callback, this, [records,dashboardListStore]);
            }
        });

    },


    createXBoard : function(xBoardTypeId, widgetIds, xBoardProperties, callback) {

        Ext.Ajax.request({
            url:JSON_SERVLET_PATH + 'createXBoard',
            method:'POST',
            params:{
                data : '{"xBoardTypeId":"' + xBoardTypeId + '","widgetIds":' + Ext.encode(widgetIds) + ',"xBoardProperties":' + Ext.encode(xBoardProperties) + '}'
            },
            success : function(response) {
                var respJson = Ext.decode(response.responseText);
                if (respJson.success != false) {
                    Ext.callback(callback, this, [respJson]);
                }
                else
                    DD.removeLoadingWindow(function() {
                        Ext.Msg.alert('Operation Failed', respJson.error);
                    });
            },
            failure : function(response) {
                var respJson = Ext.decode(response.responseText);
                DD.removeLoadingWindow(function() {
                    Ext.Msg.alert('Operation Failed', respJson.error);
                });
            }
        });

    },

    updateXBoard : function(xBoardId, widgetIds, xBoardProperties, callback) {

        Ext.Ajax.request({
            url:JSON_SERVLET_PATH + 'updateXBoard',
            method:'POST',
            params:{
                data : '{"xBoardId":"' + xBoardId + '","widgetIds":' + Ext.encode(widgetIds) + ',"xBoardProperties":' + Ext.encode(xBoardProperties) + '}'
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

    removeXBoard : function(xBoardId, callback) {

        Ext.Ajax.request({
            url:JSON_SERVLET_PATH + 'removeXBoard',
            method:'POST',
            params:{
                data : '{"xBoardId":"' + xBoardId + '"}'
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

    parseXBoardConfiguration : function(xBoardId, callback) {

        DashboardManager.progressBarStatus = Ext.getCmp('progressBarStatus');
        DashboardManager.progressBarStatus.wait({
//            text:'Initializing....',
            scope:this
        });
//        progressBarStatus.wait({
//            interval: 500, //bar will move fast!
////            duration: 50000,
////            increment: 15,
//            text: 'Parsing Configuration...',
//            scope: this,
//            fn: function() {
//                progressBarStatus.updateText('Done!');
//            }
//        });

        var dashboardListStore = Ext.StoreManager.lookup('DashboardListStore');
        var dashboardElementsTreeStore = Ext.StoreManager.lookup('DashboardElementsTreeStore');
        var rootNode = dashboardElementsTreeStore.getRootNode();
        var widgetListStore = Ext.StoreManager.lookup('WidgetListStore');

        var xBoardObj = null;
        var index = dashboardListStore.find('id', xBoardId);
        if (index != -1) {

            xBoardObj = dashboardListStore.getAt(index);
            var dashboardCanvasHolder = DD.currentPage.down('dashboardcanvasholder');


            dashboardCanvasHolder.setDashboardName(xBoardObj.get('name'), function() {
                DashboardManager.dashboardName = xBoardObj.get('name');
                DashboardManager.currentDashboardId = xBoardObj.get('id');
                rootNode.set('text', xBoardObj.get('name'));
                rootNode.commit();

            });

            Ext.define('ParameterConfig', {
                extend:'Ext.data.Model',
                fields:['value','widgetConfigObj']
            });
//            console.log(xBoardObj);

            var configurationObj = Ext.Object.fromQueryString(xBoardObj.get('configuration'));
            var widgetsList = Ext.decode(configurationObj['widgetsConfiguration']);
            var parametersList = Ext.decode(configurationObj['parametersConfiguration']);
            var textWidgetList = Ext.decode(configurationObj['textWidgetConfiguration']);
            var embeddedWidgetList = Ext.decode(configurationObj['embeddedWidgetConfiguration']);
            var imageWidgetList = Ext.decode(configurationObj['imageWidgetConfiguration']);


            var dashboardConfiguration = Ext.decode(configurationObj['dashboardConfiguration']);


            widgetListStore.removeAll();
            rootNode.removeAll();

            DashboardManager.noOfRows = dashboardConfiguration.noOfRows;
            DashboardManager.noOfCols = dashboardConfiguration.noOfCols;

            DashboardManager.progressBarStatus.updateText('Drawing Dashboard Canvas...');
            dashboardCanvasHolder.resizeCanvas(dashboardConfiguration.noOfRows, dashboardConfiguration.noOfCols, function(canvas) {
//                console.log(canvas);

                DashboardManager.progressBarStatus.updateText('Adding Text To Dashboard...');
                DashboardManager.addTextToDashboard(textWidgetList, canvas, function() {
                    DashboardManager.progressBarStatus.updateText('Adding Embedded Widgets To Dashboard...');
                    DashboardManager.addEmbeddedWidgetsToDashboard(embeddedWidgetList, canvas, function() {
                        DashboardManager.progressBarStatus.updateText('Adding Images To Dashboard...');
                        DashboardManager.addImageWidgetsToDashboard(imageWidgetList, canvas, function() {
                            DashboardManager.progressBarStatus.updateText('Adding Widgets To Dashboard...');
                            Ext.Function.defer(function () {
                                DashboardManager.addWidgetsToDashboard(xBoardObj.get('widgetIds'),widgetsList, parametersList, canvas, function() {
                                    DashboardManager.progressBarStatus.reset();
                                    Ext.callback(callback, this, [xBoardObj]);

                                });
                            }, 1000);
                        });
                    });
                });
            });
        }
        else {
            window.location.href = '#/dashboard';
        }
    },

    addTextToDashboard : function(textWidgetList, canvas, callback) {

        Ext.each(textWidgetList, function(text) {

            var textCoordinates = text.gridDimensions;
            var HTML = text.text.split('!@#').join('"');

            DashboardManager.calculateWidgetWidthHeight(canvas, textCoordinates, function(widgetWidth, widgetHeight, widgetTopOffset, widgetLeftOffset) {
                LayoutManager.addPortlet(canvas, null, null, null, 'text', widgetWidth, widgetHeight, function(portlet) {
                    var parsedTextObj = {
                        xtype:'component',
                        isTextWidget:true,
                        html:HTML
                    };

                    portlet.setMargin(Ext.String.format('{0} 0 0 {1}', widgetTopOffset, widgetLeftOffset));
                    portlet.setGridDimensions(text.gridDimensions);
                    portlet.updateWidget(parsedTextObj, function(widget) {

                        var text = portlet.getWidget().getEl().dom.innerText;
                        if (DashboardManager.isEditMode) {

                            LayoutManager.addWidgetNodeToTree({
                                widgetType : 'text',
                                type : 'text',
                                id : portlet.id + '-node',
                                holderId : portlet.id,
                                leaf : true,
                                text : text + ' (Text)',
                                checked : true
                            });
                        }
                    });
                });
            });
        });

        Ext.callback(callback, this, []);

    },

    addEmbeddedWidgetsToDashboard : function(embeddedWidgetList, canvas, callback) {

        Ext.each(embeddedWidgetList, function(widget) {

            var widgetCoordinates = widget.gridDimensions;
            var code = widget.code.split('!@#').join('"');
            code = code.split("$%^").join("'");

            DashboardManager.calculateWidgetWidthHeight(canvas, widgetCoordinates, function(widgetWidth, widgetHeight, widgetTopOffset, widgetLeftOffset) {
                LayoutManager.addPortlet(canvas, null, null, null, 'embedded', widgetWidth, widgetHeight, function(portlet) {

                    WidgetManager.embeddedCode = code;

                    var parsedEmbeddedObj = {
                        xtype: "uxiframe",
                        code:code,
                        isEmbeddedWidget:true,
                        layout:'fit',
                        src: 'resources/eg-iframe.html'
                    };


                    portlet.setMargin(Ext.String.format('{0} 0 0 {1}', widgetTopOffset, widgetLeftOffset));
                    portlet.setGridDimensions(widget.gridDimensions);
                    portlet.updateWidget(parsedEmbeddedObj, function(widget) {

                        if (DashboardManager.isEditMode) {

                            LayoutManager.addWidgetNodeToTree({
                                widgetType : 'embedded',
                                type : 'embedded',
                                id : portlet.id + '-node',
                                holderId : portlet.id,
                                leaf : true,
                                text : '(Embedded)',
                                checked : true
                            });
                        }
                    });
                });
            });
        });

        Ext.callback(callback, this, []);

    },
    addImageWidgetsToDashboard : function(imageWidgetList, canvas, callback) {

        Ext.each(imageWidgetList, function(widget) {

            var widgetCoordinates = widget.gridDimensions;
            var image = widget.image.split('!@#').join('"');
            image = image.split("$%^").join("'");

            DashboardManager.calculateWidgetWidthHeight(canvas, widgetCoordinates, function(widgetWidth, widgetHeight, widgetTopOffset, widgetLeftOffset) {
                LayoutManager.addPortlet(canvas, null, null, null, 'image', widgetWidth, widgetHeight, function(portlet) {

                    var parsedEmbeddedObj = {
                        xtype:'container',
                        isImageWidget:true,
                        layout:'fit',
                        configObj:widget.configObj,
                        html:image
                    };


                    portlet.setMargin(Ext.String.format('{0} 0 0 {1}', widgetTopOffset, widgetLeftOffset));
                    portlet.setGridDimensions(widget.gridDimensions);
                    portlet.updateWidget(parsedEmbeddedObj, function(widget) {

                        if (DashboardManager.isEditMode) {

                            LayoutManager.addWidgetNodeToTree({
                                widgetType : 'image',
                                type : 'image',
                                id : portlet.id + '-node',
                                holderId : portlet.id,
                                leaf : true,
                                text : '(Image)',
                                checked : true
                            });
                        }
                    });
                });
            });
        });

        Ext.callback(callback, this, []);

    },

    addWidgetsToDashboard : function(widgetIds,widgetsList, parametersList, canvas, callback) {

       widgetIds=widgetIds.toString().split(',');

        Ext.each(widgetsList, function(widget, index) {

                var widgetCoordinates = widget.gridDimensions;
                var widgetId = null;

                if (Ext.Array.contains(widgetIds,widget.widgetId)) {
                    WidgetManager.getWidget(widget.widgetId, function(widgetObj) {


                        widgetId = widget.widgetId;
//                            console.log('1) Getting Widget Configuration ',widgetObj);
                        DashboardManager.getWidgetXtype(widgetObj, function(updatedWidgetObj) {

//                                 console.log('2) Getting Updated Widget Configuration ',updatedWidgetObj);
                            var widgetRecord = Ext.create('DD.model.WidgetListModel',
                                updatedWidgetObj
                            );

                            WidgetManager.parseWidgetConfiguration(widgetRecord, function(parsedWidgetObj) {

//                                    console.log('3) Parsed Widget Configuration ',parsedWidgetObj);
                                var controlInfo = widgetRecord.get('widgetType')[0];
                                var widgetType = controlInfo.get('name');


                                DashboardManager.calculateWidgetWidthHeight(canvas, widgetCoordinates, function(widgetWidth, widgetHeight, widgetTopOffset, widgetLeftOffset) {
                                    LayoutManager.addPortlet(canvas, null, null, controlInfo.data, widgetType, widgetWidth, widgetHeight, function(portlet) {
//                                            console.log('4) Adding Portlet ',portlet);

                                        portlet.setSavedWidgetId(widget.widgetId);
                                        portlet.setGridDimensions(widget.gridDimensions);

                                        portlet.setMargin(Ext.String.format('{0} 0 0 {1}', widgetTopOffset, widgetLeftOffset));

                                        portlet.updateWidget(parsedWidgetObj, function(widget) {

                                            if (DashboardManager.isEditMode) {
                                                LayoutManager.addWidgetNodeToTree({
                                                    widgetId:widgetId,
                                                    widgetType : widgetType,
                                                    widgetName:widgetObj.name,
                                                    widgetDesc:widgetObj.description,
                                                    type : widgetType,
                                                    id : portlet.id + '-node',
                                                    holderId : portlet.id,
                                                    leaf : true,
                                                    text : widget.configObj.title + ' (' + widgetType + ')',
                                                    checked : true
                                                });
                                            }

                                        });
                                    });
                                });
                            });
                        });
                    });
                }

                if (index == widgetsList.length - 1)
                    DashboardManager.progressBarStatus.updateText('Adding Parameters To Dashboard...');
                Ext.Function.defer(function () {
                    DashboardManager.addParametersToDashboard(parametersList, canvas, callback);
                }, 3000);

            }
        )
            ;
    },

    addParametersToDashboard : function(parametersList, canvas, callback) {

        Ext.each(parametersList, function(parameter) {

            var parameterCoordinates = parameter.gridDimensions;

            var parameterType = Ext.widget({
                xtype:parameter.parameterXtype
            });

            var dataSetArray = [];

            Ext.each(parameter.parameterConfig, function(parameterConfig) {
                var widgetConfigObj = parameterConfig.widgetConfigObj;
                DataSetManager.loadDataSetStoreById(widgetConfigObj.dataSetId, function(dataSet) {

                    DataStoreManager.registerStoreInstancesForDataSet(widgetConfigObj.dataSetId, dataSet[0].get('name').toString(), widgetConfigObj, [
                        {name:parameterConfig.value,type:'auto'}
                    ], true, function(dataStore) {

                        dataSetArray.push(
                            Ext.create('ParameterConfig', {
                                'value':parameterConfig.value,
                                'widgetConfigObj':widgetConfigObj
                            })
                        );
                    });
                });
            });

            Ext.Function.defer(function () {

                parameterType.updateConfiguration(parameter.configObj, dataSetArray, function() {

                    var targetCell = Ext.get('canvasGrid-cell-' + parameterCoordinates.startRow + '-' + parameterCoordinates.startCol);

                    DashboardManager.calculateWidgetWidthHeight(canvas, parameterCoordinates, function(widgetWidth, widgetHeight, widgetTopOffset, widgetLeftOffset) {
                        LayoutManager.addParameterControl(canvas, null, null, {controlType:parameter.parameterXtype}, widgetWidth, widgetHeight, function(parameterControl, name) {

                            parameterControl.setIsParameterConfigured(true);
                            parameterControl.setConfigObj(parameter.configObj);
                            parameterControl.setGridDimensions(parameter.gridDimensions);

                            parameterControl.setMargin(Ext.String.format('{0} 0 0 {1}', widgetTopOffset, widgetLeftOffset));

                            parameterControl.updateParameterControl(parameterType, function(control) {

                                if (DashboardManager.isEditMode) {
                                    LayoutManager.addWidgetNodeToTree({
                                        widgetType : 'Control',
                                        type : 'Control',
                                        id : parameterControl.id + '-node',
                                        holderId : parameterControl.id,
                                        leaf : true,
                                        text : control.getFieldLabel() + ' (Control)',
                                        checked : true
                                    });
                                }

                            });

//
                        });
                    });

                });
//                            DD.removeLoadingWindow(function() {  //
//                            });
            }, 1000);
        });
        DashboardManager.progressBarStatus.updateText('Done !');
        Ext.callback(callback, this, []);

    }
    ,


    calculateWidgetWidthHeight : function(freeFormLayout, widgetCoordinates, callback) {
        var widgetHeight = null;
        var widgetWidth = null;
        var widgetTopOffset = null;
        var widgetLeftOffset = null;

        LayoutManager.getCellWidthHeight(freeFormLayout, function(cellWidth, cellHeight) {

            widgetHeight = (widgetCoordinates.endRow - widgetCoordinates.startRow + 1) * cellHeight;
            widgetWidth = (widgetCoordinates.endCol - widgetCoordinates.startCol + 1) * cellWidth;

            widgetTopOffset = widgetCoordinates.startRow * cellHeight;
            widgetLeftOffset = widgetCoordinates.startCol * cellWidth;

        });
//        console.log(widgetWidth, widgetHeight, widgetTopOffset, widgetLeftOffset);
        Ext.callback(callback, this, [widgetWidth, widgetHeight,widgetTopOffset,widgetLeftOffset]);
    }
    ,

    getWidgetXtype : function(widget, callback) {

        Ext.each(widget.parameters, function(param) {
            if (param.parameterDef.name == 'Name')
                widget['name'] = param.value;
            else
            if (param.parameterDef.name == 'Description')
                widget['description'] = param.value;
            else
            if (param.parameterDef.name == 'Configuration') {
                widget['widgetXtype'] = Ext.Object.fromQueryString(param.value).widgetXtype;
            }
        });
        Ext.callback(callback, this, [widget]);
    }
}
    ;

