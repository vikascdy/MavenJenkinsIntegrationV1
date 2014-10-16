
// STORE: Saved Users
// Retrieves all saved users information from the backend server.
// ----------------------------------------------------------------------------

Ext.require('SM.proxy.RemoteProxy');

Ext.define('SM.store.SavedUsersStore', {
    extend: 'Ext.data.Store',
    model: 'SM.model.SavedUsers',
    autoLoad: false,
    autoSync:true,

    proxy: {
        type: 'remoteproxy',
        url: JSON_URL + '/users.list',
        actionMethods: 'GET',
        reader: {
            type: 'json'
        }
    }
});


