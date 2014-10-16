Ext.define('Security.view.tenant.TenantDetailPane', {
	extend:'Ext.panel.Panel',
    alias:'widget.tenantdetailpane',
    padding:0,
    layout:'border',
    defaults : {
        style:{
            backgroundColor:'#FFF!important'
        }
    },
    initComponent : function() {
        this.items = [
            {
                xtype:'tenantdetailpaneheader',
                minHeight:120,
                region:'north'
            },
            {
                xtype:'tenantdetailpanetabpanel',
                region:'center'
            }
        ];
        this.callParent(arguments);
    }
});