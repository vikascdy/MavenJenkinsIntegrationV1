Ext.require('Security.proxy.RemoteProxy');

Ext.define('Security.store.TenantOrgs', {
    extend: 'Ext.data.Store',
    storeId: 'tenantOrgs',
    requires: 'Security.model.Organizations',
    model: 'Security.model.Organizations',
    remoteSort : true,
    pageSize: 20,
    autoLoad: false,
    remoteFilter: true,
    buffered:true,
    proxy: {
        type: 'remoteproxy',
        startParam:'startRecord',
        limitParam:'recordCount',
        url: JSON_SERVICE_SERVLET_PATH + 'esm-service/organization.getOrganizationsForTenant',
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