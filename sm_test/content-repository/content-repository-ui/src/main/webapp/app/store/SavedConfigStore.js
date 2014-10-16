
// STORE: Saved Configs
// Retrieves all saved config files from the backend server.
// ----------------------------------------------------------------------------

Ext.require('SM.proxy.RemoteProxy');

Ext.define('SM.store.SavedConfigStore', {
    extend: 'Ext.data.Store',
    model: 'SM.model.SavedConfig',
    sorters: [{
        property : 'active',
        direction: 'DESC'
    }, {
        property : 'lastModified',
        direction: 'DESC'
    }, {
        property : 'name',
        direction: 'ASC'
    }],

    autoLoad: false,

    proxy: {
        type: 'remoteproxy',
        url: JSON_URL + '/config.list',
        actionMethods: 'GET',
        reader: {
            type: 'json'
        }
    }
});


