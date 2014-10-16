Ext.define('DD.view.dataset.DataSetList', {
    title: 'Available Data Sets',
    extend:'Ext.grid.Panel',
    alias:'widget.datasetlist',
    store: 'DataSetListStore',
    border:false,
    columns: [
        { text: 'Name',  dataIndex: 'name', flex: 1 },
        { text: 'Description', dataIndex: 'description', flex: 1 },
        { text: 'Query', dataIndex: 'query', flex: 2 },
        {
            xtype:'actioncolumn',
            menuDisabled:true,
            width:20,
            items: [
                {
                    icon: 'resources/images/delete.png',
                    tooltip: 'Delete Data Set',
                    handler: function(grid, rowIndex, colIndex) {
                        var rec = grid.getStore().getAt(rowIndex);
                        Ext.Msg.confirm(
                            'Delete Confirmation',
                            'Are you sure you want to delete this data set ?',
                            function(btn) {
                                if (btn === 'yes') {
                                    DD.loadingWindow = Ext.widget('progresswindow', {
                                        text: 'Deleting Data Set...'
                                    });
                                    DataSetManager.removeDataset(rec.get('id'), function(results) {
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
        {
            xtype:'actioncolumn',
            menuDisabled:true,
            width:20,
            items: [
                {
                    icon: 'resources/images/data.png',
                    margin:'0 0 0 10',
                    tooltip: 'Query Data',
                    handler: function(grid, rowIndex, colIndex) {
                        var rec = grid.getStore().getAt(rowIndex);
                        DD.loadingWindow = Ext.widget('progresswindow', {
                            text: 'Loading Query Result...'
                        });
                        DataSetManager.getDatasetPreview(rec.get('id'), function(results) {
                            var queryResultWindow = Ext.widget({
                                xtype:'queryresultwindow',
                                query:rec.get('query'),
                                queryResult:Ext.encode(results.get('data'))
                            });
                            queryResultWindow.showEditor();
                            DD.loadingWindow.destroy();
                            queryResultWindow.show();

                        });

                    }
                }
            ]
        }
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
            this.getStore().getProxy().url = JSON_SERVLET_PATH + 'getDatasets';
            this.getStore().load();
        }
    }
});