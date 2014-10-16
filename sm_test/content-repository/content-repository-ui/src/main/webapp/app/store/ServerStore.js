
// STORE: Servers
// Retrieves server data from the loaded config file.
// ----------------------------------------------------------------------------

Ext.require('SM.proxy.ConfigProxy');

Ext.define('SM.store.ServerStore', {
    extend: 'SM.store.ConfigStore',
    model : 'SM.model.Server',
    sorters: ['name'],
    searchCriteria: {type: 'Server'}
});

