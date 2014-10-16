
// VIEW: Service Context Menu
// The right-click context menu for a Service.
// ----------------------------------------------------------------------------

Ext.define('SM.view.service.ServiceContextMenu', {
    extend: 'Ext.menu.Menu',
    alias : 'widget.servicecontextmenu',
    service: null,

    initComponent: function(config) {
        var active = this.service.get('status') == 'active';
        var newStatus = this.service.get('status') == 'new';
        this.items = [{
            text: 'Service Properties',
            iconCls: 'mico-service',
            itemId: 'tab0'
        }, {
            xtype: 'menuseparator'
        }, {
            text: 'Clone Service',
            iconCls: 'mico-copy',
            itemId: 'clone'
            // TODO: Add actual permission checks.
            //disabled: !UserManager.admin
        }, {
            text: active ? 'Stop Service' : 'Start Service',
            iconCls: (active?'mico-stop':'mico-start'),
            itemId: 'startorstop',
            hidden: newStatus
        }, {
            text: 'Delete Service',
            iconCls: 'mico-delete',
            itemId: 'delete',
            disabled: /*!UserManager.admin ||*/ !this.service.getParent().isEditable()
        }];
        this.callParent(arguments);
    }
});


