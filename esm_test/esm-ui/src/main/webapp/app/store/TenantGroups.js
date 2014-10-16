Ext.define('Security.store.TenantGroups', {
    extend: 'Ext.data.Store',
    storeId: 'tenantGroups',
    requires: 'Security.model.Group',
    model: 'Security.model.Group',
    remoteSort : true,
    pageSize: 20,
    autoLoad: false,
    remoteFilter: true,
    buffered:true,
   
    proxy: {
        type: 'remoteproxy',
        startParam:'startRecord',
        limitParam:'recordCount',

        url: JSON_SERVICE_SERVLET_PATH + 'esm-service/group.getGroupsForTenant',
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