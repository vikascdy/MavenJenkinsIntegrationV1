Ext.define('DD.controller.DataSourceController', {
    extend: 'Ext.app.Controller',
    views:[
        'datasource.DataSourceWindow',
        'datasource.DataSourceList',
        'datasource.CreateDataSourceForm'
    ],
    models:[
        'DataSourceListModel',
        'DataSourceTypeListModel'
    ],
    stores:[
        'DataSourceListStore',
        'DataSourceTypeListStore'
    ],
    init : function() {
        this.control({
            'createdatasourceform #create':{
                click : this.createDatasource
            }
        });
    },
    createDatasource : function(btn) {
        var form = btn.up('form').getForm();
        if (form.isValid()) {
            DD.loadingWindow = Ext.widget('progresswindow', {
                text: 'Creating Data Source...'
            });
            var values = form.getValues();
            var dataSourceTypeId = values.dataSourceTypeId;
            delete values.dataSourceTypeId;
            DataSourceManager.createDatasource(dataSourceTypeId, values, function() {
                var dataSourceListStore = Ext.StoreManager.lookup('DataSourceListStore');
                dataSourceListStore.load();
                DD.loadingWindow.destroy();
            });
        }
    }
});