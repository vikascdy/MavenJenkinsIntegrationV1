
// STORE: Server Error Logs
// Retrieves runtime error logs from the server.
// ----------------------------------------------------------------------------

Ext.define('SM.store.ServerErrorStore', {
    extend: 'Ext.data.Store',
    model: 'SM.model.ErrorLog',
    autoLoad: false,

    sorters: [{
        sorterFn: function(e1, e2) {
            severityKeys = {
                'fatal'  : 1,
                'error'  : 2,
                'warning': 3
            };
            s1 = severityKeys[e1.get('severity')] || 91;
            s2 = severityKeys[e2.get('severity')] || 91;
            return s1 - s2;
        }
    }, 'type', 'message'],

    proxy: {
        type: 'ajax',
        url: JSON_URL + '/config.errorLogs',
        actionMethods: {
				read:'GET'
				},
        reader: {
            type: 'json'
        }
    }
});

