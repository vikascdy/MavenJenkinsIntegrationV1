Ext.require('Security.proxy.RemoteProxy');

Ext.define('Security.store.OrganizationsList', {
    extend: 'Ext.data.Store',
    storeId: 'organizationsList',
    requires: 'Security.model.Organizations',
    model: 'Security.model.Organizations',
    	
//      remoteFilter:false,
      pageSize: 20,
      autoLoad: false
});