Ext.define('Util.ParameterManager', {});

window.ParameterManager = {

    parametersList : Ext.create('Ext.util.HashMap'),

    generateParametersList : function(callback) {

        var freeFormLayout = DD.currentPage.down('freeformlayout');
        var dashboardItems = freeFormLayout.getLayout().getLayoutItems();

        Ext.each(dashboardItems, function(item) {
            if (item.xtype == 'portlet')
                if (item.getWidget() && (!item.getWidget().isTextWidget || !item.getWidget().isImageWidget) && item.getWidget().configObj.parameterFields) {
                    var widget = item.getWidget();
                    DataSetManager.findDataSetInStore(widget.dataSetId, function(dataSet) {
                        if (dataSet) {
                            ParameterManager.addParametersToDashboard(widget, widget.configObj.parameterFields, item.getWidgetTitle(), widget.dataSetId, dataSet.get('name'), function() {

                            });
                        }
                    });
                }
        });

        Ext.callback(callback, this);

    },

    addParametersToDashboard : function(widget, parametersList, title, dataSetId, dataSetName, callback) {

        var parameterName = null;
        Ext.each(parametersList, function(param) {
            parameterName = title + ' - ' + param;
            var configObj = {
                param:param,
                widgetConfigObj:{
                    Xfields:widget.configObj.Xfields,
                    Yfields:widget.configObj.Yfields,
                    widgetXtype:widget.configObj.widgetXtype,
                    dataSetId:dataSetId,
                    dataSetName:dataSetName,
                    title:title,
                    useAggregateData:widget.useAggregateData
                }
            };
            if (!ParameterManager.parametersList.containsKey(parameterName)) {
                ParameterManager.parametersList.add(parameterName, configObj);
            }

        });
        Ext.callback(callback, this, []);
    },

    generateConfigurationForControl : function(control, callback) {
        var config = [];
        switch (control.xtype) {
            case 'singleslidercontrol' :
                config = [
                    {
                        fieldLabel:'Default Value',
                        name:'defaultValue',
                        xtype:'numberfield'

                    },
                    {
                        fieldLabel:'Minimum Value',
                        xtype:'numberfield',
                        name:'minValue',
                        allowBlank:false
                    },
                    {
                        fieldLabel:'Maximum Value',
                        xtype:'numberfield',
                        name:'maxValue',
                        allowBlank:false
                    }
                ];
                break;
            case 'singleverticalslidercontrol' :
                config = [
                    {
                        fieldLabel:'Default Value',
                        name:'defaultValue',
                        xtype:'numberfield'

                    },
                    {
                        fieldLabel:'Minimum Value',
                        xtype:'numberfield',
                        name:'minValue',
                        allowBlank:false
                    },
                    {
                        fieldLabel:'Maximum Value',
                        xtype:'numberfield',
                        name:'maxValue',
                        allowBlank:false
                    },
                    {
                        xtype:'textfield',
                        hidden:true,
                        value:true
                    }
                ];
                break;
            case 'multislidercontrol' :
                config = [
                    {
                        fieldLabel:'Default Min Value',
                        name:'defaultMinValue',
                        xtype:'numberfield'

                    },
                    {
                        fieldLabel:'Default Max Value',
                        name:'defaultMaxValue',
                        xtype:'numberfield'

                    },
                    {
                        fieldLabel:'Minimum Value',
                        xtype:'numberfield',
                        name:'minValue',
                        allowBlank:false
                    },
                    {
                        fieldLabel:'Maximum Value',
                        xtype:'numberfield',
                        name:'maxValue',
                        allowBlank:false
                    }
                ];
                break;
            case 'multisingleslidercontrol' :
                config = [
                    {
                        fieldLabel:'Default Value',
                        name:'defaultValue',
                        xtype:'numberfield'

                    },
                    {
                        fieldLabel:'Minimum Value',
                        xtype:'numberfield',
                        name:'minValue',
                        allowBlank:false
                    },
                    {
                        fieldLabel:'Maximum Value',
                        xtype:'numberfield',
                        name:'maxValue',
                        allowBlank:false
                    },
                    {
                        xtype:'textfield',
                        hidden:true,
                        value:true
                    }
                ];
                break;
            case 'comboboxcontrol' :
                config = [
                    {
                        fieldLabel:'Default Value',
                        name:'defaultValue',
                        xtype:'textfield'

                    }
                ];
                break;
            case 'checkboxgroupcontrol' :
                config = [];
                break;
            case 'numberfieldcontrol' :
                config = [
                    {
                        fieldLabel:'Minimum Value',
                        xtype:'numberfield',
                        name:'minValue',
                        allowBlank:false
                    },
                    {
                        fieldLabel:'Maximum Value',
                        xtype:'numberfield',
                        name:'maxValue',
                        allowBlank:false
                    },
                    ,
                    {
                        fieldLabel:'Comparison',
                        xtype:'combo',
                        store:Ext.create('Ext.data.Store', {
                            fields:['name','value'],
                            data:[
                                {name:'Less then <',value:'<'},
                                {name:'Less then equal to <=',value:'<='},
                                {name:'Equal to ==',value:'=='},
                                {name:'Greater then >',value:'>'},
                                {name:'Greater then equal to >=',value:'>='}
                            ]
                        }),
                        displayField:'name',
                        valueField:'value',
                        name:'comparison',
                        value:'==',
                        editable:false,
                        allowBlank:false
                    }
                ];
                break;

        }
        Ext.callback(callback, this, [config]);

    },

    showConfigurationWindow:function(me, btn) {
        ParameterManager.generateConfigurationForControl(me.getParameterControl(), function(config) {
            var parametersConfigurationWindow = Ext.widget({
                xtype:'parametersconfigurationwindow',
                parameterControlHolder:me,
                parameter:me.getParameterControl(),
                parameterConfig:config
            });
            parametersConfigurationWindow.show(btn.getEl());
        });
    },

    filterDataset : function(datasetId, parameters, callback) {

        Ext.Ajax.request({
            url:JSON_SERVLET_PATH + 'filterDataset',
            method:'POST',
            params:{
                data : '{"datasetId":' + datasetId + ',"parameters":' + Ext.encode(parameters) + '}'
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

    filterDatasetWithAggregation : function(datasetId, parameters, fieldMeta, callback) {

        Ext.Ajax.request({
            url:JSON_SERVLET_PATH + 'filterDatasetWithAggregation',
            method:'POST',
            params:{
                data : '{"datasetId":' + datasetId + ',"parameters":' + Ext.encode(parameters) + ',"fieldMeta":' + Ext.encode(fieldMeta) + '}'
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

    updateStoreRecords : function(datasetId, parameter, store, values, valueType, nameField, dataField, useAggregateData, callback) {

        var parameters = [];
        parameters.push({
            fieldName:parameter,
            type:valueType,
            values:values
        });
        if (useAggregateData) {
            var fieldMeta = {
                name : nameField,
                data : dataField
            };
            ParameterManager.filterDatasetWithAggregation(datasetId, parameters, fieldMeta, function(filterRecords) {
                store.removeAll();
                store.loadData(filterRecords.data);
                Ext.callback(callback, this, []);
            });
        }
        else
            ParameterManager.filterDataset(datasetId, parameters, function(filterRecords) {
                store.removeAll();
                store.loadData(filterRecords.data);
                Ext.callback(callback, this, []);
            });

    }

};

