Ext.define('DD.view.dataset.CreateDataSetForm', {
    extend:'Ext.form.Panel',
    title: 'Create New Data Set',
    bodyPadding: 5,
    alias:'widget.createdatasetform',
    layout: 'anchor',
    border:false,
    defaults: {
        anchor: '100%'
    },
    maxHeight:500,
    dataSetType:'simple',
    defaultType: 'textfield',
    items: [
        {
            xtype: 'radiogroup',
            submitValue:false,
            columns: 2,
            vertical: true,
            items: [
                { boxLabel: 'Simple', name: 'dataSetType', inputValue: 'simple',checked:true },
                { boxLabel: 'Composite', name: 'dataSetType', inputValue: 'composite'}
            ],
            listeners : {
                'change' : function(radio, newValue) {
                    var panel = radio.up('panel');
                    panel.updateConfigurationForm(newValue.dataSetType);
                }
            }
        },
        {
            xtype:'container',
            layout:'anchor',
            defaults:{anchor: '100%'},
            margin:'5 0 0 0',
            itemId:'typeConfigurationHolder'
        }
    ],

    buttons: [
        {
            text: 'Create',
            margin:'5 0 0 0',
            itemId:'create',
            formBind: true,
            disabled: true
        }
    ],

    updateConfigurationForm : function(newValue) {
        var panel = this;
        var typeConfigurationHolder = panel.down('#typeConfigurationHolder');
        var configForm = null;
        if (newValue == 'simple') {
            panel.dataSetType = 'simple';
            configForm = panel.getSimpleConfigurationForm();
        }
        else
        if (newValue == 'composite') {
            panel.dataSetType = 'composite';
            configForm = panel.getCompositeConfigurationForm();
        }

        typeConfigurationHolder.removeAll();
        typeConfigurationHolder.add(configForm);

    },

    getSimpleConfigurationForm : function() {
        return  [
            {
                xtype:'container',
                margin:'0 0 6 0',
                layout:{
                    type:'hbox',
                    align:'stretch'
                },
                items:[
                    {
                        xtype:'combo',
                        editable:false,
                        fieldLabel: 'Data Source',
                        name:'datasourceId',
                        store: 'DataSourceListStore',
                        flex:1,
                        queryMode: 'local',
                        allowBlank:false,
                        displayField: 'name',
                        valueField: 'id',
                        listeners : {
                            render : function() {
                                this.getStore().load();
                            },
                            change:function(combo, newValue) {
                                var store = combo.getStore();
                                var datasetType = combo.up('form').down('#datasetType');
                                var datasetTypeId = combo.up('form').down('#datasetTypeId');
                                var index = store.find('id', newValue);
                                if (index != -1) {
                                    var rec = store.getAt(index);
                                    DataSetManager.getDatasetTypesForDatasourceType(rec.get('datasourceTypeId'), function(dataSetTypes) {
                                        var dataSetTypeListStore = Ext.StoreManager.lookup('DataSetTypeListStore');
                                        dataSetTypeListStore.removeAll();
                                        dataSetTypeListStore.loadData(dataSetTypes);
                                        DD.loadingWindow = Ext.widget('progresswindow', {
                                            text: 'Generating Data Set Configuration...'
                                        });
                                        DataSetManager.getDatasetTypeById(dataSetTypes[0].id, function(dataSetTypeDef) {
                                            DataSetManager.generateDataSetConfigForm(dataSetTypeDef, function(config) {
                                                combo.up('form').updateDataSetTypeConfig(config, function() {
                                                    datasetTypeId.setValue(dataSetTypes[0].id);
                                                    datasetType.setValue(dataSetTypes[0].name);
                                                    DD.removeLoadingWindow(function() {
                                                        datasetType.show();
                                                    });
                                                });

                                            });
                                        });
                                    });
                                }

                            }
                        }
                    },
                    {
                        xtype:'button',
                        text:'New Data Source',
                        margin:'0 0 0 6',
                        listeners:{
                            click:function() {
                                var datasetWindow = this.up('window');
                                var datasourcewindow = Ext.widget({
                                    xtype:'datasourcewindow'
                                });
                                datasourcewindow.show();
                                datasetWindow.close();
                            }
                        }
                    }
                ]
            },
            {
                fieldLabel: 'Data Set Type',
                itemId: 'datasetType',
                xtype:'displayfield',
                hidden:true
            },
            {
                name: 'datasetTypeId',
                itemId: 'datasetTypeId',
                xtype:'textfield',
                hidden:true
            },
            {
                xtype:'fieldset',
                hidden:true,
                itemId:'dataSetTypeConfig',
                title:'Data Set Configuration',
                defaultType: 'textfield',
                defaults: {anchor: '100%'},
                layout: 'anchor'
            }
        ];
    },

    getCompositeConfigurationForm : function() {
        return  [

            {
                xtype:'combo',
                editable:false,
                fieldLabel: 'Select Data Set',
                name:'datasetIds',
                flex:1,
                anchor: '100%',
                store: 'DataSetListStore',
                queryMode: 'local',
                allowBlank:false,
                displayField: 'name',
                valueField: 'id',
                multiSelect:true,
                forceSelection : true,
                listConfig : {
                    getInnerTpl : function() {
                        return '<div class="x-combo-list-item"><img src="'
                            + Ext.BLANK_IMAGE_URL
                            + '"'
                            + 'class="chkCombo-default-icon chkCombo" /> {name} </div>';
                    }
                },
                listeners : {
                    render : function() {
                        this.getStore().load();
                    }
                }
            },
            {
                xtype:'fieldset',
                itemId:'dataSetTypeConfig',
                title:'Data Set Configuration',
                defaultType: 'textfield',
                defaults: {anchor: '100%'},
                layout: 'anchor',
                items:[
                    {
                        xtype:'textfield',
                        fieldLabel:'Name',
                        name:'Name',
                        allowBlank:false
                    },
                    {
                        xtype:'textarea',
                        fieldLabel:'Query',
                        name:'Query',
                        allowBlank:false
                    },
                    {
                        xtype:'textarea',
                        fieldLabel:'Description',
                        name:'Description'
                    }
                ]
            }
        ];

    },

    updateDataSetTypeConfig : function(config, callback) {
        var dataSetTypeConfig = this.down('#dataSetTypeConfig');
        dataSetTypeConfig.removeAll();
        dataSetTypeConfig.add(config);
        dataSetTypeConfig.show();
        Ext.callback(callback, this);
    },
    listeners : {
        'render' : function() {
            this.dataSetType='simple';
            this.updateConfigurationForm('simple');
        }
    }
});