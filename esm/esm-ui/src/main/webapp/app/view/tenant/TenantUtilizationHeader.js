Ext.define('Security.view.tenant.TenantUtilizationHeader', {
    extend:'Ext.container.Container',
    alias:'widget.tenantutilizationheader',
    padding :20,
    layout:{type:'vbox',align:'stretch'},
    style:'backgroundColor:#ffffff',
    items:[
        {
            xtype:'component',
            html:'<h1>Tenant Utilization</h1>'
        } ,
        {
            xtype: 'tenantutilization',
            flex:1,
            itemId:'tenantutil1',
            margin: '10 0 0 0'
        }
    ]

});