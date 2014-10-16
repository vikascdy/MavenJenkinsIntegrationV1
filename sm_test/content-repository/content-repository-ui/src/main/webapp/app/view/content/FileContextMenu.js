
// VIEW: File Context Menu
// The right-click context menu for files on a Content Repository Tree.
// ----------------------------------------------------------------------------

Ext.define('SM.view.content.FileContextMenu', {
    extend: 'Ext.menu.Menu',
    alias : 'widget.filecontextmenu',
    node  : null,

    initComponent: function(config) {
        this.items = [{
            text: 'Download',
            iconCls: 'mico-save',
            itemId: 'download'
        }, {
            text: 'View/Edit',
            iconCls: 'mico-edit',
            itemId: 'edit'
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

