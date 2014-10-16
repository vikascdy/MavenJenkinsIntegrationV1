Ext.define('Security.store.GroupsList', {
    extend: 'Ext.data.Store',
    storeId: 'groupsList',
    requires: 'Security.model.TenantUserGroups',
    model: 'Security.model.TenantUserGroups',
    remoteSort : true,
    pageSize: 20,
    autoLoad: false,
    remoteFilter: true,
    buffered:true
});