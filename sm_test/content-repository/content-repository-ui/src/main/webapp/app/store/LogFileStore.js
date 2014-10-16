// STORE: Log Files
// Retrieves log files for Services.
// ----------------------------------------------------------------------------

Ext.require('SM.proxy.RemoteProxy');

Ext.define('SM.store.LogFileStore',{
    extend: 'Ext.data.Store',
    model: 'SM.model.LogFile',
    autoLoad: false,

    sorters: ['lastEntry', 'name'],

    constructor: function(config) {
        if (!config.service)
            Ext.Error.raise("A LogFileStore must specify a Configuration Item to get log files for!");
        var status = config.service.get('status');
        if (status != 'offline' && status != 'new') {
            this.proxy = {
                type: 'rest',
                url : JSON_URL + '/logs.list',
                actionMethods: 'GET',
                extraParams: {
                    id: config.service.getId()
                },
                reader: {
                    type: 'json'
                }
            };
        } else {
            this.data = {};
            this.proxy = {
              type: 'memory',
              reader: {
                  type: 'json'
              }
            };
        }
        this.callParent(arguments);
    }
});

