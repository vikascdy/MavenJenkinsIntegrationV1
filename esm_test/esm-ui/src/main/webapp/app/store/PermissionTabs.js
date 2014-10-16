Ext.define('Security.store.PermissionTabs', {
    extend: 'Ext.data.Store',
    model: 'Security.model.Permission',
    requires: 'Security.model.Permission',
    proxy: {
        type: 'ajax',
        url: 'security-data/permissionsAudit/rootPermissions',
        reader: {
            type: 'json'
        }
    }
});
