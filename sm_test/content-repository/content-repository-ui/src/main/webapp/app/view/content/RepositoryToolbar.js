
// VIEW: Repository Toolbar
// Toolbar for the file browser of the ContentRepositoryPage.
// ----------------------------------------------------------------------------

Ext.define('SM.view.content.RepositoryToolbar', {
    extend: 'Ext.toolbar.Toolbar',
    alias : 'widget.repositorytoolbar',

    items: [{
        text   : 'New Folder',
        itemId : 'newFolder',
        iconCls: 'mico-new'
    }, {
        text   : 'Upload File',
        itemId : 'upload',
        iconCls: 'mico-upload'
    }, '-', {
        text   : 'Copy/Move',
        itemId : 'copy',
        iconCls: 'mico-copy'
    }, {
        text   : 'Rename',
        itemId : 'rename',
        iconCls: 'mico-rename'
    }, {
        text   : 'Delete',
        itemId : 'delete',
        iconCls: 'mico-delete'
    }, '-', {
        text   : 'Download',
        itemId : 'download',
        iconCls: 'mico-save'
    }, {
        text   : 'Properties',
        itemId : 'properties',
        iconCls: 'mico-file'
    }, {
        text   : 'View/Edit',
        itemId : 'edit',
        iconCls: 'mico-edit'
    }, '-', {
        text   : 'Refresh',
        itemId : 'refresh',
        iconCls: 'mico-refresh'
    }]
});


