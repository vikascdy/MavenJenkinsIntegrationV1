
// VIEW: File History List
// A grid that lists all previous versions of a file and allows the user to
// download or view any version.
// ----------------------------------------------------------------------------

Ext.define("SM.view.content.FileHistoryList", {
    extend: 'Ext.grid.Panel',
    alias : 'widget.filehistorylist',
    requires: ['SM.store.FileHistoryStore'],

    title: 'History',
    iconCls: 'ico-files',

    node: null,

    columns: [{
        header: 'Version',
        dataIndex: 'version',
        flex: 1
    }, {
        xtype: 'datecolumn',
        header: 'Date',
        dataIndex: 'dateUpdated',
        flex: 1
    }, {
        header: 'User',
        dataIndex: 'lastEditor',
        flex: 1
    }, {
        xtype: 'actioncolumn',
        sortable: false,
        width: 48,
        items: [{
            iconCls: 'mico-save',
            tooltip: 'Download',
            handler: function(grid, rowIndex, colIndex, item) {
                grid.fireEvent('downloadIcon', grid.up('filehistorylist').node, grid.getStore().getAt(rowIndex).get('version'));
            }
        },{
            iconCls: 'mico-edit',
            tooltip: 'View',
            handler: function(grid, rowIndex, colIndex, item) {
                grid.fireEvent('editIcon', grid.up('filehistorylist').node, grid.getStore().getAt(rowIndex).get('version'));
            }
        }]
    }],

    initComponent: function(config) {
        if (this.node)
            this.store = Ext.create('SM.store.FileHistoryStore', {path: this.node.get('id')});
        this.callParent(arguments);
        if (this.node)
            this.store.load();
    }
});

