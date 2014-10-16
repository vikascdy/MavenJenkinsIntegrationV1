Ext.define('Security.store.SiteTenant', {
    extend: 'Ext.data.Store',
    requires: 'Security.model.SiteTenants',
    model: 'Security.model.SiteTenants',
    
//    remoteFilter:false,
//    pageSize: 20,
    autoLoad: false,

    groupField : 'canonicalName'
});
