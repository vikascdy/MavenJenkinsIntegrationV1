// VIEW: Server Context Menu
// The right-click context menu for a Server.
// ----------------------------------------------------------------------------

Ext.define('SM.view.server.ServerContextMenu', {
    extend: 'Ext.menu.Menu',
    alias : 'widget.servercontextmenu',
    server: null,

    initComponent: function(config) {
        //var active = this.server.get('status') == 'active';
        this.items = [{
            text: 'Server Overview',
            iconCls: 'mico-server',
            itemId: 'tab0'
        }, {
            xtype: 'menuseparator'
        }, {
            text: 'Clone Server',
            iconCls: 'mico-copy',
            itemId: 'clone'
            // TODO: Add actual permission checks.
            //disabled: !UserManager.admin
        }, {
            text: 'Delete Server',
            iconCls: 'mico-delete',
            itemId: 'delete'
            //disabled: !UserManager.admin
        }, {
            text: 'Add New Role...',
            iconCls: 'mico-add',
            itemId: 'newrole'
            //disabled: !UserManager.admin
        }, {
            text: 'Create New Node...',
            iconCls: 'mico-new',
            itemId: 'newnode'
            //disabled: !UserManager.admin
        }];
        this.callParent(arguments);
    }
});

