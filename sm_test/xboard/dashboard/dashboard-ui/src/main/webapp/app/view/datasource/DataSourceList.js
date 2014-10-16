Ext.define('DD.view.datasource.DataSourceList', {
    title: 'Available Data Source',
    extend:'Ext.grid.Panel',
    alias:'widget.datasourcelist',
    store: 'DataSourceListStore',
    border:false,
    columns: [
        { text: 'Name',  dataIndex: 'name', flex: 1  },
        { text: 'Category', dataIndex: 'category', flex: 1 },
        { text: 'Description', dataIndex: 'description', flex: 2  },
        {
            xtype:'actioncolumn',
            menuDisabled:true,
            width:20,
            items: [
                {
                    icon: 'resources/images/delete.png',
                    tooltip: 'Delete Data Source',
                    handler: function(grid, rowIndex, colIndex) {
                        var rec = grid.getStore().getAt(rowIndex);
                        Ext.Msg.confirm(
                            'Delete Confirmation',
                            'Are you sure you want to delete this data source ?',
                            function(btn) {
                                if (btn === 'yes') {
                                    DD.loadingWindow = Ext.widget('progresswindow', {
                                        text: 'Deleting Data Source...'
                                    });
                                    DataSourceManager.removeDatasource(rec.get('id'), function(results) {
                                        grid.getStore().load();
                                        DD.loadingWindow.destroy();
                                    });
                                }
                            }
                        );
                    }
                }
            ]
        },
    ],
    tools:[
        {
            type:'refresh',
            tooltip:'Refresh List',
            handler : function() {
                this.up('grid').getStore().load();
            }
        }
    ],
    listeners : {
        'render' : function() {
            this.getStore().load();
        }
    }
});