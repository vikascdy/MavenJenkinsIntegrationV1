Ext.require('Security.proxy.RemoteProxy');

Ext.define('Security.store.TenantUsers', {
    extend: 'Ext.data.Store',
    storeId: 'tenantUsers',
    requires: 'Security.model.User',
    model: 'Security.model.User',
    remoteSort : true,
    pageSize: 20,
    autoLoad: false,
    remoteFilter: true,
    buffered:true,
    leadingBufferZone:500,

    proxy: {
        type: 'remoteproxy',

        url: JSON_SERVICE_SERVLET_PATH + 'esm-service/user',
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
