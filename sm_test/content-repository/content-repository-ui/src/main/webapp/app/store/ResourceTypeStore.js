
// STORE: Resource Types
// Retrieves all supported Resource Types from the backend server.
// ----------------------------------------------------------------------------

Ext.require('SM.proxy.RemoteProxy');

Ext.define('SM.store.ResourceTypeStore', {
    extend: 'Ext.data.Store',
    model: 'SM.model.ResourceType',
    sorters: ['name'],
    autoLoad: false,

    proxy: {
        type: 'remoteproxy',
        url: JSON_URL + '/config.resourceTypes',
        actionMethods: 'GET',
        reader: {
            type: 'json'
        }
    },

    loadIfNecessary: function() {
        if (!this.data) this.load();
    }
});

