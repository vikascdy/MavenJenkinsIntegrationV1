
// STORE: Templates
// Retrieves all config file templates from the backend server.
// ----------------------------------------------------------------------------

Ext.require('SM.proxy.RemoteProxy');

Ext.define('SM.store.TemplateStore', {
    extend: 'Ext.data.Store',
    model: 'SM.model.SavedConfig',
    sorters: [{
        property : 'name',
        direction: 'ASC'
    }],

    autoLoad: false,

    proxy: {
        type: 'remoteproxy',
        url: JSON_URL + '/config.templateList',
        actionMethods: 'GET',
        reader: {
            type: 'json'
        }
    }
});

