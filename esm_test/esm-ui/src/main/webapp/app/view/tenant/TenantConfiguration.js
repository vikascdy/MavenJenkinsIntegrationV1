Ext.define('Security.view.tenant.TenantConfiguration', {
    extend: 'Ext.container.Container',
    alias : 'widget.tenantconfiguration',
    border:0,
    layout   : 'border',
    overflowX: 'auto',
    overflowY: 'hidden',
    defaults:{
        margins:{top:0, right:0, bottom:15, left:0}
    },
    config:{
    	tenantId:null,
        tenantRecord:null,
        disableLinking:false
    },
    title:'Tenant Configuration',
    items: [
        {
            region      :   'west',
            width       :   220,
            xtype       :   'LeftMenu',
            bodyPadding:15,
            id      	:   "configTenantMenu",
            itemId      :   "configTenantMenu",
            url         :   'resources/json/tenant-config-json.json',
            menuType    :   "type2"
        },
        {
            id    : 'Home-page-container',
            xtype : 'container',
            layout: 'fit',
            region: 'center',
            flex  : 1,
            minWidth : 960,
            overflowY: 'auto',
            overflowX: 'hidden',
            bodyPadding:20,
            margin:'0 0 0 10',
            items: [
//                {
//                    xtype:'tenantoverview'
//                }
            ]
        }
    ]

});
