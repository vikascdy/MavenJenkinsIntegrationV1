Ext.define('Security.store.TenantsList', {
    extend: 'Ext.data.Store',
    storeId: 'tenantsList',
    requires: 'Security.model.Tenants',
    model: 'Security.model.Tenants',
    
//    remoteFilter:false,
//    pageSize: 20,
    autoLoad: false

    
});
