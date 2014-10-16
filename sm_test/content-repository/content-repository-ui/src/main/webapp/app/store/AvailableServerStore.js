
// STORE: Available Servers
// Retrieves data on all unused Servers from the backend server.
// ----------------------------------------------------------------------------

Ext.require('SM.proxy.RemoteProxy');

Ext.define('SM.store.AvailableServerStore', {
    extend: 'Ext.data.Store',
    model: 'SM.model.AvailableServer',
    sorters: ['hostname'],
    autoLoad: false,

    proxy: {
        type: 'remoteproxy',
        url: JSON_URL + '/servers.available',
        actionMethods: 'GET',
        reader: {
            type: 'json'
        }
    }
});

