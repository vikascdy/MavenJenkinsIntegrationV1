Ext.define('DD.view.datasource.CreateDataSourceForm', {
    extend:'Ext.form.Panel',
    title: 'Create New Data Source',
    bodyPadding: 5,
    alias:'widget.createdatasourceform',
    layout: 'anchor',
    border:false,
    defaults: {
        anchor: '100%'
    },

    defaultType: 'textfield',
    items: [
        {
            fieldLabel: 'Data Source Type',
            name: 'dataSourceTypeId',
            xtype:'combo',
            editable:false,
            store: 'DataSourceTypeListStore',
            queryMode: 'local',
            allowBlank:false,
            displayField: 'name',
            valueField: 'id',
            listeners : {
                render : function() {
                    this.getStore().load();
                },
                change:function(combo, newValue) {
                    DD.loadingWindow = Ext.widget('progresswindow', {
                        text: 'Generating Data Source Configuration...'
                    });
                    DataSourceManager.getDatasourceTypeById(newValue, function(dataSource) {
                        DataSourceManager.generateDataSourceConfigForm(dataSource, function(config) {
                            combo.up('form').updateDataSourceTypeConfig(config);
                        });
                    });
                }
            }
        },
        {
            xtype:'fieldset',
            hidden:true,
            itemId:'dataSourceTypeConfig',
            title:'Data Source Configuration',
            defaultType: 'textfield',
            defaults: {anchor: '100%'},
            layout: 'anchor'
        }
    ],

    buttons: [
        {
            text: 'Create',
            itemId:'create',
            formBind: true,
            disabled: true

        }
    ],

    updateDataSourceTypeConfig : function(config) {
        var dataSourceTypeConfig = this.down('#dataSourceTypeConfig');
        dataSourceTypeConfig.removeAll();
        dataSourceTypeConfig.add(config);
        DD.removeLoadingWindow(function() {
            dataSourceTypeConfig.show();
        });

    }
});