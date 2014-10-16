Ext.define('Util.WidgetPropertiesManager', {});

window.WidgetPropertiesManager = {

    getWidgetProperties : function(widgetType, portlet, callback) {
        var widgetProperties = null;
        switch (widgetType) {
            case undefined:
                widgetProperties = WidgetPropertiesManager.getDashboardProperties();
                Ext.callback(callback, this, [widgetProperties]);
                break;
            case 'chart':
                WidgetPropertiesManager.populateChartProperties(portlet, function(widgetProperties) {
                    Ext.callback(callback, this, [widgetProperties]);
                });
                break;
            case 'grid':
                WidgetPropertiesManager.getGridProperties(null, portlet, function(widgetProperties) {
                    Ext.callback(callback, this, [widgetProperties]);
                });
                break;
            case 'text':
                WidgetPropertiesManager.getTextProperties(portlet, function(widgetProperties) {
                    Ext.callback(callback, this, [widgetProperties]);
                });
                break;
            case 'image':
                WidgetPropertiesManager.getImageProperties(portlet, function(widgetProperties) {
                    Ext.callback(callback, this, [widgetProperties]);
                });
                break;


        }

    },
    getChartProperties:function(chartType, fields, callback) {

        var formConfig = null;

        var paramList = [];
        ParameterManager.generateParametersList(function() {
            Ext.each(ParameterManager.parametersList.getKeys(), function(key) {
                var keyObj = ParameterManager.parametersList.get(key);
                paramList.push({
                    name:key,
                    value:keyObj.param,
                    storeRef:keyObj.store,
                    widgetId:keyObj.widgetId,
                    dataSetId:keyObj.dataSetId
                });
            });
        });

        if (chartType == 'areachart' || chartType == 'linechart' || chartType == 'scatterchart')
            chartType = 'columnchart';
        if (chartType == 'radarchart')
            chartType = 'piechart';
        var a = new Array();

        switch (chartType) {
            case 'barchart' :

                a[0] = {
                    "title":null,
                    "Xfields":null,
                    "Yfields":null,
                    "parameterFields": "",
                    "listenerFields": "",
                    "XfieldLabel":"",
                    "YfieldLabel":"",
                    "legend":"false",
                    "legendPosition":"",
                    "theme":"Base"


                };
                a[1] = {
                    title:{
                        displayName:"Title <span style='color:red'>*</span>"
                    },
                    theme:{
                        editor: new Ext.form.field.ComboBox({
                            editable: false,
                            store: ['Base','Green','Sky','Red','Purple','Blue','Category1','Category2','Category3','Category4','Category5','Category6']
                        }),
                        displayName:'Theme'
                    },
                    Xfields: {
                        editor: new Ext.form.field.ComboBox({
                            store: Ext.create('Ext.data.Store', {
                                fields:['name'],
                                data:fields
                            }),
                            queryMode:'local',
                            editable: false,
                            displayField:'name',
                            valueField:'name',
                            allowBlank:false,
                            multiSelect:true,
                            forceSelection : true,
                            listConfig : {
                                getInnerTpl : function() {
                                    return '<div class="x-combo-list-item"><img src="'
                                        + Ext.BLANK_IMAGE_URL
                                        + '"'
                                        + 'class="chkCombo-default-icon chkCombo" /> {name} </div>';
                                }
                            }
                        }),

                        displayName:"Data Field <span style='color:red'>*</span>"

                    },
                    Yfields: {
                        editor: new Ext.form.field.ComboBox({
                            store: Ext.create('Ext.data.Store', {
                                fields:['name'],
                                data:fields
                            }),
                            displayField:'name',
                            allowBlank:false,
                            editable: false,
                            valueField:'name',
                            forceSelection : true,
                            queryMode:'local'
                        }),
                        displayName:"Name Field <span style='color:red'>*</span>"

                    },
                    parameterFields: {
                        editor: new Ext.form.field.ComboBox({
                            store: Ext.create('Ext.data.Store', {
                                fields:['name'],
                                data:fields
                            }),
                            queryMode:'local',
                            editable: false,
                            displayField:'name',
                            valueField:'name',
                            multiSelect:true,
                            forceSelection : true,
                            listConfig : {
                                getInnerTpl : function() {
                                    return '<div class="x-combo-list-item"><img src="'
                                        + Ext.BLANK_IMAGE_URL
                                        + '"'
                                        + 'class="chkCombo-default-icon chkCombo" /> {name} </div>';
                                }
                            }
                        }),

                        displayName:"Parameter Fields"

                    },
                    listenerFields: {
                        editor:    {
                            allowBlank:false,
                            xtype:'combo',
                            store:Ext.create('Ext.data.Store', {
                                fields:['name','value','storeRef','widgetId','dataSetId'],
                                data:paramList
                            }),
                            displayField:'name',
                            valueField:'name',
                            editable:false,
                            name:'parameter',
                            forceSelection:true,
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
                            listeners : {
                                select : function(combo, records) {
                                    var invalidListeners = [];
                                    Ext.each(records, function(record) {
                                        var flag = 0;
                                        Ext.each(fields, function(field) {
                                            if (field.name == record.get('value')) {
                                                flag = 1;
                                            }
                                        });
                                        if (flag == 0)
                                            invalidListeners.push(record.get('name'));
                                    });
                                    if (invalidListeners.length > 0) {
                                        var invalidListenerList = '';
                                        Ext.each(invalidListeners, function(listener) {
                                            invalidListenerList += invalidListenerList + '-> ' + listener + '<br/>'
                                        });
                                        invalidListenerList = invalidListenerList.substr(0, invalidListenerList.length - 2);
                                        Functions.errorMsg('Warning', 'Following listeners may not work as they do not map to any field in this widget <br/>' + invalidListenerList);
                                    }
                                }
                            }
                        },
                        displayName:"Listeners Fields"
                    },
                    "XfieldLabel":{
                        displayName:'Y Axis Label'
                    },
                    "YfieldLabel":{
                        displayName:'X Axis Label'
                    },
                    "legend":{
                        displayName:'Legend',
                        editor: new Ext.form.field.ComboBox({
                            editable:false,
                            store: ['true','false']

                        })
                    },
                    legendPosition: {
                        displayName:"Legend Position",
                        editor: new Ext.form.field.ComboBox({
                            editable:false,
                            store: ['right','left','top','bottom']

                        })
                    }
                };
                formConfig = a;
                break;
            case 'columnchart' :
                a[0] = {
                    "title":null,
                    "Xfields":null,
                    "Yfields": null,
                    "parameterFields": "",
                    "listenerFields": "",
                    "XfieldLabel":"",
                    "YfieldLabel":"",
                    "legend":"false",
                    "legendPosition":"",
                    "theme":"Base"

                };
                a[1] = {
                    title:{
                        displayName:"Title <span style='color:red'>*</span>"
                    },
                    theme:{
                        editor: new Ext.form.field.ComboBox({
                            editable: false,
                            store: ['Base','Green','Sky','Red','Purple','Blue','Category1','Category2','Category3','Category4','Category5','Category6']
                        }),
                        displayName:'Theme'
                    },
                    Xfields: {
                        editor: new Ext.form.field.ComboBox({
                            store: Ext.create('Ext.data.Store', {
                                fields:['name'],
                                data:fields
                            }),
                            displayField:'name',
                            allowBlank:false,
                            editable: false,
                            valueField:'name',
                            forceSelection : true,
                            queryMode:'local'
                        }),
                        displayName:"Name Field <span style='color:red'>*</span>"
                    },
                    Yfields: {
                        editor: new Ext.form.field.ComboBox({
                            store: Ext.create('Ext.data.Store', {
                                fields:['name'],
                                data:fields
                            }),
                            queryMode:'local',
                            allowBlank:false,
                            editable: false,
                            displayField:'name',
                            valueField:'name',
                            multiSelect:true,
                            forceSelection : true,
                            listConfig : {
                                getInnerTpl : function() {
                                    return '<div class="x-combo-list-item"><img src="'
                                        + Ext.BLANK_IMAGE_URL
                                        + '"'
                                        + 'class="chkCombo-default-icon chkCombo" /> {name} </div>';
                                }
                            }
                        }),
                        displayName:"Data Field <span style='color:red'>*</span>"

                    },
                    parameterFields: {
                        editor: new Ext.form.field.ComboBox({
                            store: Ext.create('Ext.data.Store', {
                                fields:['name'],
                                data:fields
                            }),
                            queryMode:'local',
                            editable: false,
                            displayField:'name',
                            valueField:'name',
                            multiSelect:true,
                            forceSelection : true,
                            listConfig : {
                                getInnerTpl : function() {
                                    return '<div class="x-combo-list-item"><img src="'
                                        + Ext.BLANK_IMAGE_URL
                                        + '"'
                                        + 'class="chkCombo-default-icon chkCombo" /> {name} </div>';
                                }
                            }
                        }),

                        displayName:"Parameter Fields"

                    },
                    listenerFields: {
                        editor:    {
                            allowBlank:false,
                            xtype:'combo',
                            store:Ext.create('Ext.data.Store', {
                                fields:['name','value','storeRef','widgetId','dataSetId'],
                                data:paramList
                            }),
                            displayField:'name',
                            valueField:'name',
                            editable:false,
                            name:'parameter',
                            forceSelection:true,
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
                            listeners : {
                                select : function(combo, records) {
                                    var invalidListeners = [];
                                    Ext.each(records, function(record) {
                                        var flag = 0;
                                        Ext.each(fields, function(field) {
                                            if (field.name == record.get('value')) {
                                                flag = 1;
                                            }
                                        });
                                        if (flag == 0)
                                            invalidListeners.push(record.get('name'));
                                    });
                                    if (invalidListeners.length > 0) {
                                        var invalidListenerList = '';
                                        Ext.each(invalidListeners, function(listener) {
                                            invalidListenerList += invalidListenerList + '-> ' + listener + '<br/>'
                                        });
                                        invalidListenerList = invalidListenerList.substr(0, invalidListenerList.length - 2);
                                        Functions.errorMsg('Warning', 'Following listeners may not work as they do not map to any field in this widget <br/>' + invalidListenerList);
                                    }
                                }
                            }
                        },
                        displayName:"Listeners Fields"

                    },
                    "XfieldLabel":{
                        displayName:'X Axis Label'
                    },
                    "YfieldLabel":{
                        displayName:'Y Axis Label'
                    },
                    "legend":{
                        displayName:'Legend',
                        editor: new Ext.form.field.ComboBox({
                            editable:false,
                            store: ['true','false']

                        })
                    },
                    legendPosition: {
                        displayName:"Legend Position",
                        editor: new Ext.form.field.ComboBox({
                            editable:false,
                            store: ['right','left','top','bottom']

                        })
                    }
                };
                formConfig = a;
                break;

            case 'piechart' :
                a[0] = {
                    "title":null,
                    "Xfields":null,
                    "Yfields": null,
                    "parameterFields": "",
                    "listenerFields": "",
                    "legend":"true",
                    "legendPosition":"right",
                    "theme":"Base",
                    "donut":0

                };
                a[1] = {
                    title:{
                        displayName:"Title <span style='color:red'>*</span>"
                    },
                    theme:{
                        editor: new Ext.form.field.ComboBox({
                            editable: false,
                            store: ['Base','Green','Sky','Red','Purple','Blue','Category1','Category2','Category3','Category4','Category5','Category6']
                        }),
                        displayName:'Theme'
                    },
                    Yfields: {
                        editor: new Ext.form.field.ComboBox({
                            store: Ext.create('Ext.data.Store', {
                                fields:['name'],
                                data:fields
                            }),
                            displayField:'name',
                            allowBlank:false,
                            editable: false,
                            valueField:'name',
                            forceSelection : true,
                            queryMode:'local'
                        }),
                        displayName:"Name Field <span style='color:red'>*</span>"
                    },
                    Xfields: {
                        editor: new Ext.form.field.ComboBox({
                            store: Ext.create('Ext.data.Store', {
                                fields:['name'],
                                data:fields
                            }),
                            displayField:'name',
                            allowBlank:false,
                            editable: false,
                            valueField:'name',
                            forceSelection : true,
                            queryMode:'local'
                        }),
                        displayName:"Data Field <span style='color:red'>*</span>"
                    },
                    parameterFields: {
                        editor: new Ext.form.field.ComboBox({
                            store: Ext.create('Ext.data.Store', {
                                fields:['name'],
                                data:fields
                            }),
                            queryMode:'local',
                            editable: false,
                            displayField:'name',
                            valueField:'name',
                            multiSelect:true,
                            forceSelection : true,
                            listConfig : {
                                getInnerTpl : function() {
                                    return '<div class="x-combo-list-item"><img src="'
                                        + Ext.BLANK_IMAGE_URL
                                        + '"'
                                        + 'class="chkCombo-default-icon chkCombo" /> {name} </div>';
                                }
                            }
                        }),

                        displayName:"Parameter Fields"

                    },
                    listenerFields: {
                        editor:    {
                            allowBlank:false,
                            xtype:'combo',
                            store:Ext.create('Ext.data.Store', {
                                fields:['name','value','storeRef','widgetId','dataSetId'],
                                data:paramList
                            }),
                            displayField:'name',
                            valueField:'name',
                            editable:false,
                            name:'parameter',
                            forceSelection:true,
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
                            listeners : {
                                select : function(combo, records) {
                                    var invalidListeners = [];
                                    Ext.each(records, function(record) {
                                        var flag = 0;
                                        Ext.each(fields, function(field) {
                                            if (field.name == record.get('value')) {
                                                flag = 1;
                                            }
                                        });
                                        if (flag == 0)
                                            invalidListeners.push(record.get('name'));
                                    });
                                    if (invalidListeners.length > 0) {
                                        var invalidListenerList = '';
                                        Ext.each(invalidListeners, function(listener) {
                                            invalidListenerList += invalidListenerList + '-> ' + listener + '<br/>'
                                        });
                                        invalidListenerList = invalidListenerList.substr(0, invalidListenerList.length - 2);
                                        Functions.errorMsg('Warning', 'Following listeners may not work as they do not map to any field in this widget <br/>' + invalidListenerList);
                                    }
                                }
                            }
                        },
                        displayName:"Listeners Fields"

                    },
                    "legend":{
                        displayName:'Legend',
                        editor: new Ext.form.field.ComboBox({
                            editable:false,
                            store: ['true','false']

                        })
                    },
                    legendPosition: {
                        displayName:"Legend Position",
                        editor: new Ext.form.field.ComboBox({
                            editable:false,
                            store: ['right','left','top','bottom']

                        })
                    },
                    donut: {
                        displayName:"Do Nut"
                    }
                };
                formConfig = a;
                break;
        }

        Ext.callback(callback, this, [a]);

    },
    populateChartProperties:function(portlet, callback) {

        var formConfig = null;
        var a = new Array();
        var chart = portlet.getWidget();

        if (chart) {
            var chartType = chart.xtype;
            var fields = chart.getStore().model.getFields();

            if (chartType == 'areachart' || chartType == 'linechart' || chartType == 'scatterchart')
                chartType = 'columnchart';
            if (chartType == 'radarchart')
                chartType = 'piechart';


            var paramList = [];
            ParameterManager.generateParametersList(function() {
                Ext.each(ParameterManager.parametersList.getKeys(), function(key) {
                    var keyObj = ParameterManager.parametersList.get(key);
                    paramList.push({
                        name:key,
                        value:keyObj.param,
                        storeRef:keyObj.store,
                        widgetId:keyObj.widgetId,
                        dataSetId:keyObj.dataSetId
                    });
                });
            });

            var listenersList = Ext.Array.clone(paramList);
            Ext.each(listenersList, function(listener, index) {
                Ext.each(chart.configObj.parameterFields, function(param) {
                    var paramName = chart.configObj.title + ' - ' + param;
                    if (listener && listener.name == paramName)
                        listenersList.splice(index, 1);
                });
            });

            switch (chartType) {
                case 'barchart' :

                    a[0] = {
                        "title":chart.configObj.title,
                        "Xfields":Yfields,
                        "Yfields":chart.configObj.Yfields.toString(),
                        "parameterFields": chart.configObj.parameterFields.toString(),
                        "listenerFields": chart.configObj.listenerFields.toString(),
                        "XfieldLabel":chart.configObj.XfieldLabel,
                        "YfieldLabel":chart.configObj.YfieldLabel,
                        "legend":chart.configObj.legend,
                        "legendPosition":chart.configObj.legendPosition,
                        "theme":chart.configObj.theme


                    };
                    a[1] = {
                        title:{
                            displayName:'Title'
                        },
                        theme:{
                            editor: new Ext.form.field.ComboBox({
                                editable:false,

                                store: ['Base','Green','Sky','Red','Purple','Blue','Category1','Category2','Category3','Category4','Category5','Category6']
                            }),
                            displayName:'Theme'
                        },
                        Xfields: {
                            editor: new Ext.form.field.ComboBox({
                                store: Ext.create('Ext.data.Store', {
                                    fields:['name'],
                                    data:fields
                                }),
                                queryMode:'local',
                                editable: false,
                                displayField:'name',
                                valueField:'name',
                                multiSelect:true,
                                forceSelection : true,
                                valueToSet:chart.configObj.Xfields,
                                listConfig : {
                                    getInnerTpl : function() {
                                        return '<div class="x-combo-list-item"><img src="'
                                            + Ext.BLANK_IMAGE_URL
                                            + '"'
                                            + 'class="chkCombo-default-icon chkCombo" /> {name} </div>';
                                    }
                                }
                            }),

                            displayName:"Data Field"

                        },
                        Yfields: {
                            editor: new Ext.form.field.ComboBox({
                                store: Ext.create('Ext.data.Store', {
                                    fields:['name'],
                                    data:fields
                                }),
                                displayField:'name',
                                editable: false,
                                valueField:'name',
                                forceSelection : true,
                                queryMode:'local'
                            }),
                            displayName:"Name Field"
                        },
                        listenerFields: {
                            editor:    {
                                allowBlank:false,
                                xtype:'combo',
                                store:Ext.create('Ext.data.Store', {
                                    fields:['name','value','storeRef','widgetId','dataSetId'],
                                    data:listenersList
                                }),
                                displayField:'name',
                                valueField:'name',
                                editable:false,
                                name:'parameter',
                                forceSelection:true,
                                queryMode:'local',
                                multiSelect:true,
                                valueToSet:chart.configObj.listenerFields,
                                listConfig : {
                                    getInnerTpl : function() {
                                        return '<div class="x-combo-list-item"><img src="'
                                            + Ext.BLANK_IMAGE_URL
                                            + '"'
                                            + 'class="chkCombo-default-icon chkCombo" /> {name} </div>';
                                    }
                                },
                                listeners : {
                                    select : function(combo, records) {
                                        var invalidListeners = [];
                                        Ext.each(records, function(record) {
                                            var flag = 0;
                                            Ext.each(fields, function(field) {
                                                if (field.name == record.get('value')) {
                                                    flag = 1;
                                                }
                                            });
                                            if (flag == 0)
                                                invalidListeners.push(record.get('name'));
                                        });
                                        if (invalidListeners.length > 0) {
                                            var invalidListenerList = '';
                                            Ext.each(invalidListeners, function(listener) {
                                                invalidListenerList += invalidListenerList + '-> ' + listener + '<br/>'
                                            });
                                            invalidListenerList = invalidListenerList.substr(0, invalidListenerList.length - 2);
                                            Functions.errorMsg('Warning', 'Following listeners may not work as they do not map to any field in this widget <br/>' + invalidListenerList);
                                        }
                                    },
                                    listeners : {
                                        expand : function(combo) {
                                            combo.setValue(combo.valueToSet);
                                        }
                                    }
                                }
                            },
                            displayName:"Listeners Fields"
                        },
                        parameterFields: {
                            editor: new Ext.form.field.ComboBox({
                                store: Ext.create('Ext.data.Store', {
                                    fields:['name'],
                                    data:fields
                                }),
                                queryMode:'local',
                                editable: false,
                                displayField:'name',
                                valueField:'name',
                                multiSelect:true,
                                forceSelection : true,
                                valueToSet:chart.configObj.ParameterFields,
                                listConfig : {
                                    getInnerTpl : function() {
                                        return '<div class="x-combo-list-item"><img src="'
                                            + Ext.BLANK_IMAGE_URL
                                            + '"'
                                            + 'class="chkCombo-default-icon chkCombo" /> {name} </div>';
                                    }
                                },
                                listeners : {
                                    expand : function(combo) {
                                        combo.setValue(combo.valueToSet);
                                    }
                                }
                            }),

                            displayName:"Parameter Fields"

                        },

                        "XfieldLabel":{
                            displayName:'Y Axis Label'
                        },
                        "YfieldLabel":{
                            displayName:'X Axis Label'
                        },
                        "legend":{
                            displayName:'Legend',
                            editor: new Ext.form.field.ComboBox({
                                editable:false,
                                store: ['true','false']

                            })
                        },
                        legendPosition: {
                            displayName:"Legend Position",
                            editor: new Ext.form.field.ComboBox({
                                editable:false,
                                store: ['right','left','top','bottom']

                            })
                        }
                    };
                    formConfig = a;
                    break;
                case 'columnchart' :
                    a[0] = {
                        "title":chart.configObj.title,
                        "Xfields":chart.configObj.Xfields,
                        "Yfields":chart.configObj.Yfields.toString(),
                        "parameterFields":chart.configObj.parameterFields.toString(),
                        "listenerFields": chart.configObj.listenerFields.toString(),
                        "XfieldLabel":chart.configObj.XfieldLabel,
                        "YfieldLabel":chart.configObj.YfieldLabel,
                        "legend":chart.configObj.legend,
                        "legendPosition":chart.configObj.legendPosition,
                        "theme":chart.configObj.theme

                    };
                    a[1] = {
                        title:{
                            displayName:'Title'
                        },
                        theme:{
                            editor: new Ext.form.field.ComboBox({
                                editable:false,
                                store: ['Base','Green','Sky','Red','Purple','Blue','Category1','Category2','Category3','Category4','Category5','Category6']
                            }),
                            displayName:'Theme'
                        },
                        Xfields: {
                            editor: new Ext.form.field.ComboBox({
                                store: Ext.create('Ext.data.Store', {
                                    fields:['name'],
                                    data:fields
                                }),
                                displayField:'name',
                                editable: false,
                                valueField:'name',
                                forceSelection : true,
                                queryMode:'local'
                            }),
                            displayName:"Name Field"
                        },
                        Yfields: {
                            editor: new Ext.form.field.ComboBox({
                                store: Ext.create('Ext.data.Store', {
                                    fields:['name'],
                                    data:fields
                                }),
                                queryMode:'local',
                                editable: false,
                                displayField:'name',
                                valueField:'name',
                                multiSelect:true,
                                forceSelection : true,
                                valueToSet:chart.configObj.Yfields,
                                listConfig : {
                                    getInnerTpl : function() {
                                        return '<div class="x-combo-list-item"><img src="'
                                            + Ext.BLANK_IMAGE_URL
                                            + '"'
                                            + 'class="chkCombo-default-icon chkCombo" /> {name} </div>';
                                    }
                                },
                                listeners : {
                                    expand : function(combo) {
                                        combo.setValue(combo.valueToSet);
                                    }
                                }
                            }),
                            displayName:"Data Field"

                        },
                        listenerFields: {
                            editor:    {
                                allowBlank:false,
                                xtype:'combo',
                                store:Ext.create('Ext.data.Store', {
                                    fields:['name','value','storeRef','widgetId','dataSetId'],
                                    data:listenersList
                                }),
                                displayField:'name',
                                valueField:'name',
                                editable:false,
                                name:'parameter',
                                forceSelection:true,
                                queryMode:'local',
                                multiSelect:true,
                                valueToSet:chart.configObj.listenerFields,
                                listConfig : {
                                    getInnerTpl : function() {
                                        return '<div class="x-combo-list-item"><img src="'
                                            + Ext.BLANK_IMAGE_URL
                                            + '"'
                                            + 'class="chkCombo-default-icon chkCombo" /> {name} </div>';
                                    }
                                },
                                listeners : {
                                    select : function(combo, records) {
                                        var invalidListeners = [];
                                        Ext.each(records, function(record) {
                                            var flag = 0;
                                            Ext.each(fields, function(field) {
                                                if (field.name == record.get('value')) {
                                                    flag = 1;
                                                }
                                            });
                                            if (flag == 0)
                                                invalidListeners.push(record.get('name'));
                                        });
                                        if (invalidListeners.length > 0) {
                                            var invalidListenerList = '';
                                            Ext.each(invalidListeners, function(listener) {
                                                invalidListenerList += invalidListenerList + '-> ' + listener + '<br/>'
                                            });
                                            invalidListenerList = invalidListenerList.substr(0, invalidListenerList.length - 2);
                                            Functions.errorMsg('Warning', 'Following listeners may not work as they do not map to any field in this widget <br/>' + invalidListenerList);
                                        }
                                    },
                                    expand : function(combo) {
                                        combo.setValue(combo.valueToSet);
                                    }
                                }
                            },
                            displayName:"Listeners Fields"
                        },
                        parameterFields: {
                            editor: Ext.create('Ext.form.field.ComboBox', {
                                store: Ext.create('Ext.data.Store', {
                                    fields:['name'],
                                    data:fields
                                }),
                                editable: false,
                                displayField:'name',
                                valueField:'name',
                                multiSelect:true,
                                valueToSet:chart.configObj.parameterFields,
                                listConfig : {
                                    getInnerTpl : function() {
                                        return '<div class="x-combo-list-item"><img src="'
                                            + Ext.BLANK_IMAGE_URL
                                            + '"'
                                            + 'class="chkCombo-default-icon chkCombo" /> {name} </div>';
                                    }
                                },
                                listeners : {
                                    expand : function(combo) {
                                        combo.setValue(combo.valueToSet);
                                    }
                                }
                            }),
                            displayName:"Parameter Fields"
                        },
                        "XfieldLabel":{
                            displayName:'X Axis Label'
                        },
                        "YfieldLabel":{
                            displayName:'Y Axis Label'
                        },
                        "legend":{
                            displayName:'Legend',
                            editor: new Ext.form.field.ComboBox({
                                editable:false,
                                store: ['true','false']

                            })
                        },
                        legendPosition: {
                            displayName:"Legend Position",
                            editor: new Ext.form.field.ComboBox({
                                editable:false,
                                store: ['right','left','top','bottom']

                            })
                        }
                    };
                    formConfig = a;
                    break;

                case 'piechart' :
                    a[0] = {
                        "title":chart.configObj.title,
                        "Xfields":chart.configObj.Xfields,
                        "Yfields": chart.configObj.Yfields,
                        "parameterFields": chart.configObj.parameterFields.toString(),
                        "listenerFields": chart.configObj.listenerFields.toString(),
                        "legend":chart.configObj.legend,
                        "legendPosition":chart.configObj.legendPosition,
                        "theme":chart.configObj.theme

                    };
                    a[1] = {
                        title:{
                            displayName:'Title'
                        },
                        theme:{
                            editor: new Ext.form.field.ComboBox({
                                editable:false,
                                store: ['Base','Green','Sky','Red','Purple','Blue','Category1','Category2','Category3','Category4','Category5','Category6']
                            }),
                            displayName:'Theme'
                        },
                        Yfields: {
                            editor: new Ext.form.field.ComboBox({
                                store: Ext.create('Ext.data.Store', {
                                    fields:['name'],
                                    data:fields
                                }),
                                displayField:'name',
                                editable: false,
                                valueField:'name',
                                forceSelection : true,
                                queryMode:'local'
                            }),
                            displayName:"Name Field"
                        },
                        Xfields: {
                            editor: new Ext.form.field.ComboBox({
                                store: Ext.create('Ext.data.Store', {
                                    fields:['name'],
                                    data:fields
                                }),
                                displayField:'name',
                                editable: false,
                                valueField:'name',
                                forceSelection : true,
                                queryMode:'local'
                            }),
                            displayName:"Angle Field"
                        },
                        listenerFields: {
                            editor:    {
                                allowBlank:false,
                                xtype:'combo',
                                store:Ext.create('Ext.data.Store', {
                                    fields:['name','value','storeRef','widgetId','dataSetId'],
                                    data:listenersList
                                }),
                                displayField:'name',
                                valueField:'name',
                                editable:false,
                                name:'parameter',
                                forceSelection:true,
                                queryMode:'local',
                                multiSelect:true,
                                valueToSet:chart.configObj.listenerFields,
                                listConfig : {
                                    getInnerTpl : function() {
                                        return '<div class="x-combo-list-item"><img src="'
                                            + Ext.BLANK_IMAGE_URL
                                            + '"'
                                            + 'class="chkCombo-default-icon chkCombo" /> {name} </div>';
                                    }
                                },
                                listeners : {
                                    select : function(combo, records) {
                                        var invalidListeners = [];
                                        Ext.each(records, function(record) {
                                            var flag = 0;
                                            Ext.each(fields, function(field) {
                                                if (field.name == record.get('value')) {
                                                    flag = 1;
                                                }
                                            });
                                            if (flag == 0)
                                                invalidListeners.push(record.get('name'));
                                        });
                                        if (invalidListeners.length > 0) {
                                            var invalidListenerList = '';
                                            Ext.each(invalidListeners, function(listener) {
                                                invalidListenerList += invalidListenerList + '-> ' + listener + '<br/>'
                                            });
                                            invalidListenerList = invalidListenerList.substr(0, invalidListenerList.length - 2);
                                            Functions.errorMsg('Warning', 'Following listeners may not work as they do not map to any field in this widget <br/>' + invalidListenerList);
                                        }
                                    },
                                    listeners : {
                                        expand : function(combo) {
                                            combo.setValue(combo.valueToSet);
                                        }
                                    }
                                }
                            },
                            displayName:"Listeners Fields"
                        },
                        parameterFields: {
                            editor: new Ext.form.field.ComboBox({
                                store: Ext.create('Ext.data.Store', {
                                    fields:['name'],
                                    data:fields
                                }),
                                queryMode:'local',
                                editable: false,
                                displayField:'name',
                                valueField:'name',
                                multiSelect:true,
                                forceSelection : true,
                                valueToSet:chart.configObj.parameterFields,
                                listConfig : {
                                    getInnerTpl : function() {
                                        return '<div class="x-combo-list-item"><img src="'
                                            + Ext.BLANK_IMAGE_URL
                                            + '"'
                                            + 'class="chkCombo-default-icon chkCombo" /> {name} </div>';
                                    }
                                },
                                listeners : {
                                    expand : function(combo) {
                                        combo.setValue(combo.valueToSet);
                                    }
                                }
                            }),

                            displayName:"Parameter Fields"

                        },
                        "legend":{
                            displayName:'Legend',
                            editor: new Ext.form.field.ComboBox({
                                editable:false,
                                store: ['true','false']

                            })
                        },
                        legendPosition: {
                            displayName:"Legend Position",
                            editor: new Ext.form.field.ComboBox({
                                editable:false,
                                store: ['right','left','top','bottom']

                            })
                        }
                    };
                    formConfig = a;
                    break;
            }
        }

        Ext.callback(callback, this, [a]);

    },
    getTextProperties:function(portlet, callback) {

        var widget = portlet.getWidget();
        var textEditorWindow = Ext.widget({xtype:'texteditorwidnow'});


        var a = new Array();
        a[0] = {
            text:widget.getEl().dom.innerText
        };
        a[1] = {
            text: {
                editor: new Ext.form.field.Text({
                    value:widget.getEl().dom.innerHTML,
                    listeners :{
                        focus :{
//                            console.log("True");
//                            alert("true");
                        }
                    }
                }),
                displayName:"Text"
            }

        };
        Ext.callback(callback, this, [a]);
    },
    getDashboardProperties:function() {
        var a = new Array();
        a[0] = {
            Name:DashboardManager.dashboardName,
            rows:DashboardManager.noOfRows,
            columns:DashboardManager.noOfCols
        };
        a[1] = {

        };
        return  a
    },
    getGridProperties:function(fields, portlet, callback) {

        var grid = null;
        var columns = [];

        if (portlet) {
            grid = portlet.getWidget();
            fields = grid.getStore().model.getFields();
            Ext.each(grid.configObj.columns, function(col) {
                columns.push({
                    name:col
                });
            });
        }

        var paramList = [];
        ParameterManager.generateParametersList(function() {
            Ext.each(ParameterManager.parametersList.getKeys(), function(key) {
                var keyObj = ParameterManager.parametersList.get(key);
                paramList.push({
                    name:key,
                    value:keyObj.param,
                    storeRef:keyObj.store,
                    widgetId:keyObj.widgetId,
                    dataSetId:keyObj.dataSetId
                });
            });
        });

        var a = new Array();
        a[0] = {
            "title":portlet ? grid.configObj.title : null,
            "autoScroll": portlet ? grid.configObj.autoScroll == "true" : true,
            "collapsible":portlet ? grid.configObj.collapsible == "true" : false,
            "hideHeaders": portlet ? grid.configObj.hideHeaders == "true" : false,
            "border":portlet ? grid.configObj.border == "true" : false,
            "columns":portlet ? columns : null,
            "parameterFields":portlet ? grid.configObj.parameterFields : "",
            "listenerFields":portlet ? grid.configObj.listenerFields : ""
        };
        a[1] = {
            title:{
                displayName:"Title <span style='color:red'>*</span>"
            },
            autoScroll:{
                displayName:'Auto Scroll'
            },
            collapsible:{
                displayName:'Collapsible'
            },
            hideHeaders:{
                displayName:'Hide Headers'
            },
            border:{
                displayName:'Border'
            },
            columns: {
                editor: new Ext.form.field.ComboBox({
                    store: Ext.create('Ext.data.Store', {
                        fields:['name'],
                        data:fields
                    }),
                    queryMode:'local',
                    editable: false,
                    displayField:'name',
                    valueField:'name',
                    multiSelect:true,
                    forceSelection : true,
                    value : columns,
                    listConfig : {
                        getInnerTpl : function() {
                            return '<div class="x-combo-list-item"><img src="'
                                + Ext.BLANK_IMAGE_URL
                                + '"'
                                + 'class="chkCombo-default-icon chkCombo" /> {name} </div>';
                        }
                    }
                }),
                displayName:"Columns <span style='color:red'>*</span>"
            },
            parameterFields: {
                editor: new Ext.form.field.ComboBox({
                    store: Ext.create('Ext.data.Store', {
                        fields:['name'],
                        data:fields
                    }),
                    queryMode:'local',
                    editable: false,
                    displayField:'name',
                    valueField:'name',
                    multiSelect:true,
                    forceSelection : true,
                    listConfig : {
                        getInnerTpl : function() {
                            return '<div class="x-combo-list-item"><img src="'
                                + Ext.BLANK_IMAGE_URL
                                + '"'
                                + 'class="chkCombo-default-icon chkCombo" /> {name} </div>';
                        }
                    }
                }),

                displayName:"Parameter Fields"
            },
            listenerFields: {
                editor:    {
                    allowBlank:false,
                    xtype:'combo',
                    store:Ext.create('Ext.data.Store', {
                        fields:['name','value','storeRef','widgetId','dataSetId'],
                        data:paramList
                    }),
                    displayField:'name',
                    valueField:'name',
                    editable:false,
                    name:'parameter',
                    forceSelection:true,
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
                    listeners : {
                        select : function(combo, records) {
                            var invalidListeners = [];
                            Ext.each(records, function(record) {
                                var flag = 0;
                                Ext.each(fields, function(field) {
                                    if (field.name == record.get('value')) {
                                        flag = 1;
                                    }
                                });
                                if (flag == 0)
                                    invalidListeners.push(record.get('name'));
                            });
                            if (invalidListeners.length > 0) {
                                var invalidListenerList = '';
                                Ext.each(invalidListeners, function(listener) {
                                    invalidListenerList += invalidListenerList + '-> ' + listener + '<br/>'
                                });
                                invalidListenerList = invalidListenerList.substr(0, invalidListenerList.length - 2);
                                Functions.errorMsg('Warning', 'Following listeners may not work as they do not map to any field in this widget <br/>' + invalidListenerList);
                            }
                        }
                    }
                },
                displayName:"Listeners Fields"
            }
        };
        Ext.callback(callback, this, [a]);
    },
    getImageProperties:function(portlet, callback) {

        var image = portlet.getWidget();
        var a = new Array();
        a[0] = {
            Text:image.configObj.info.name,
            Description:image.configObj.info.description
        };
        a[1] = {
            Description: {
                editor: Ext.widget('textarea')
            }
        };
        Ext.callback(callback, this, [a]);
    }
};

