
// VIEW: Content Repository Tree
// The core of the repository browser interface. A tree of files and folders
// that is dynamically loaded from the backend server.
// ----------------------------------------------------------------------------

Ext.define("SM.view.content.ContentRepositoryTree", {
    extend: 'Ext.tree.Panel',
    mixins: ['SM.mixin.PersistentTreeReloadMixin'],
    alias : 'widget.repositorytree',

    title : 'Repository Browser',
    model : 'SM.model.ContentNode',
    useArrows: true,
    rootVisible: false,
    foldersOnly: false,
    columnView: true,

    columns: [{
        xtype: 'treecolumn',
        header: 'File',
        dataIndex: 'name',
        flex: 5
    }, {
        header: 'Version',
        dataIndex: 'version',
        flex: 1,
        renderer: function(value, metadata, record) {
            return (record.get('directory') ? "" : value);
        }
    }//, 
//    {
//        header: 'MIME Type',
//        dataIndex: 'mimeType',
//        flex: 1,
//        renderer: function(value, metadata, record) {
//            return value || (record.get('directory') ? "" : "-");
//        }
//    }
    ],

    initComponent: function() {
        if (this.foldersOnly)
            this.columnView = false;
        if (!this.columnView) {
            this.columns = [this.columns[0]];
            this.hideHeaders = true;
        }
        this.store = Ext.create('SM.store.ContentTreeStore', {foldersOnly: this.foldersOnly});
        this.callParent(arguments);
    }
});

