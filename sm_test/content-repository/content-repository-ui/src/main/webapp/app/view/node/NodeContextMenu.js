// VIEW: Node Context Menu
// The right-click context menu for a Node.
// ----------------------------------------------------------------------------

Ext.define('SM.view.node.NodeContextMenu', {
    extend: 'Ext.menu.Menu',
    alias : 'widget.nodecontextmenu',
    node  : null,

    initComponent: function(config) {
        this.items = [{
            text: 'Node Overview',
            iconCls: 'mico-node',
            itemId: 'tab0'
        }, {
            xtype: 'menuseparator'
        }, {
            text: 'Clone Node',
            iconCls: 'mico-copy',
            itemId: 'clone'
            // TODO: Add actual permission checks.
            //disabled: !UserManager.admin
        }, {
            text: 'Delete Node',
            iconCls: 'mico-delete',
            itemId: 'delete'
            //disabled: !UserManager.admin
        }, {
            text: 'Install New Services...',
            iconCls: 'mico-add',
            itemId: 'install',
            disabled: /*!UserManager.admin ||*/ !this.node.isEditable()
        }];
        this.callParent(arguments);
    }
});

