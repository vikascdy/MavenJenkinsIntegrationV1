
// STORE: File History Store
// Loads the history of a specific file from the server.
// ----------------------------------------------------------------------------

Ext.define('SM.store.FileHistoryStore', {
    extend: 'Ext.data.Store',
    
    fields: [
        {name: 'name',        type: 'string'},
        {name: 'version',     type: 'string'},
        {name: 'dateUpdated', type: 'date'},
        {name: 'lastEditor',  type: 'string'}
    ],

    sorters: ['lastUpdated'],

    constructor: function(config) {
        if (!config.path)
            Ext.Error.raise("A FileHistoryStore must specify a `path` config.");
        this.proxy = {
            type: 'ajax',
            url: JSON_URL + '/content.fileHistory',
            actionMethods: 'GET',
            extraParams: {
                path: config.path
            },
            reader: {
                type: 'json'
            }
        };
        this.callParent(arguments);
    }
});

