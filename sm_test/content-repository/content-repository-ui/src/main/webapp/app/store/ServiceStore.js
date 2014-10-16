
// STORE: Services
// Retrieves Service data from the loaded config file.
// ----------------------------------------------------------------------------

Ext.require('SM.proxy.ConfigProxy');

Ext.define('SM.store.ServiceStore', {
    extend: 'SM.store.ConfigStore',
    model : 'SM.model.Service',
    sorters: ['serviceName', 'name'],
    searchCriteria: {type: 'Service'}
});

