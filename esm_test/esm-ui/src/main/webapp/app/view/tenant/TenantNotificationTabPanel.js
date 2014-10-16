Ext.define('Security.view.tenant.TenantNotificationTabPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.tenantnotificationtabpanel',
    border:0,
    bodyPadding:'0 20 15 15',
    initComponent : function() {

        this.items = [

            {
                xtype:'component',
                itemId:'heightReference'
            }

        ];

        this.callParent(arguments);
    }
});