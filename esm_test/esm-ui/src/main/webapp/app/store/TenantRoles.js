Ext.require('Security.proxy.RemoteProxy');

Ext.define('Security.store.TenantRoles', {
    extend: 'Ext.data.Store',
    storeId: 'tenantRoles',
    requires: 'Security.model.Role',
    model: 'Security.model.Role',
    remoteSort : true,
    pageSize: 20,
    autoLoad: false,
    remoteFilter: true,
    buffered:true,
    proxy: {
        type: 'remoteproxy',
        startParam:'startRecord',
        limitParam:'recordCount',
        url: JSON_SERVICE_SERVLET_PATH + 'esm-service/role.getRolesForTenant',
        reader: {
            type: 'json',
            root: 'data.resultList',
            totalProperty: 'data.total'
        },
        writer: {
            type: 'json'
        }
    }
});