
// VIEW: Resource Context Menu
// The right-click context menu for a Resource.
// ----------------------------------------------------------------------------

Ext.define('SM.view.resource.ResourceContextMenu', {
    extend: 'Ext.menu.Menu',
    alias : 'widget.resourcecontextmenu',
    resource: null,

    initComponent: function(config) {
        this.items = [{
            text: 'Resource Properties',
            iconCls: 'mico-resource',
            itemId: 'tab0'
        }, {
            xtype: 'menuseparator'
        }, {
            text: 'Delete Resource',
            iconCls: 'mico-delete',
            itemId: 'delete'
            // TODO: Add actual permission checks.
            //disabled: !UserManager.admin
        }];
        this.callParent(arguments);
    }
});

