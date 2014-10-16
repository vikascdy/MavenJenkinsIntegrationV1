Ext.define('Security.view.tenant.ManageTenants', {
    extend: 'Security.view.common.CommonManagePage',
    alias : 'widget.managetenants',
    detailPage:{xtype:'tenantdetailpane'},
    configurationUrl:'resources/json/ManageTenantsJson.json'

});
