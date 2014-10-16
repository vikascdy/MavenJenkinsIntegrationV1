Ext.define('Security.view.tenant.TenantActionMenu', {
    extend:'Ext.menu.Menu',
    alias:'widget.tenantactionmenu',
    plain:true,
    items:[
        {
            text:"Edit Tenant"
        },
        {
            text:"Remove Tenant"
        },
        {
            text:"Add Administrator"
        },
        {
            text:"Add App"
        }
    ]
});