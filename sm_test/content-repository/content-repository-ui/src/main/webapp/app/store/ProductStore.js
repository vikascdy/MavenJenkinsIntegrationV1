
// STORE: Products
// Retrieves all available Products from the backend server.
// ----------------------------------------------------------------------------

Ext.require('SM.proxy.RemoteProxy');

Ext.define('SM.store.ProductStore', {
    extend: 'Ext.data.Store',
    model: 'SM.model.Product',
    sorters: [{
        property : 'name',
        direction: 'ASC'
    }],

    autoLoad: false,

    proxy: {
        type: 'remoteproxy',
        url: JSON_URL + '/config.products',
        actionMethods: 'GET',
        reader: {
            type: 'json'
        }
    }
});

