
// VIEW: Folder Context Menu
// The right-click context menu for folders on a Content Repository Tree.
// ----------------------------------------------------------------------------

Ext.define('SM.view.content.FolderContextMenu', {
    extend: 'Ext.menu.Menu',
    alias : 'widget.foldercontextmenu',
    node  : null,

    initComponent: function(config) {
        this.items = [{
            text: 'Expand All',
            iconCls: 'mico-down',
            itemId: 'expand'
        }, {
            text: 'Collapse All',
            iconCls: 'mico-up',
            itemId: 'collapse'
        }, {
            xtype: 'menuseparator'
        }, {
            text: 'Upload File Here',
            iconCls: 'mico-upload',
            itemId: 'upload'
        }, {
            text: 'New Subfolder',
            iconCls: 'mico-new',
            itemId: 'newFolder'
        }, {
            xtype: 'menuseparator'
        }, {
            text: 'Copy/Move',
            iconCls: 'mico-copy',
            itemId: 'copy'
        }, {
            text: 'Delete',
            iconCls: 'mico-delete',
            itemId: 'delete'
        }, {
            text: 'Rename',
            iconCls: 'mico-rename',
            itemId: 'rename'
        }, {
            xtype: 'menuseparator'
        }, {
            text: 'Properties',
            iconCls: 'mico-file',
            itemId: 'properties'
        }];
        this.callParent(arguments);
    }
});


