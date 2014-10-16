
// STORE: Filtered Error Store
// Lists all errors under a specific ConfigItem. Used for the error lists in
// the UI.
// ----------------------------------------------------------------------------

Ext.require('SM.proxy.CollectiveErrorProxy');

Ext.define('SM.store.FilteredErrorStore', {
    extend: 'Ext.data.Store',
    model : 'SM.model.ErrorLog',
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

    constructor: function(config) {
        config = config || {};
        config.proxy = {
            type: 'errorcollect',
            parentItem: config.parentItem
        };
        this.callParent(arguments);
    },

    setParentItem: function(parentItem) {
        this.proxy = Ext.create('SM.proxy.CollectiveErrorProxy', {
            parentItem: parentItem
        });
    },

    listeners: {
        load: function(store) {
            store.sort();
        }
    }
});

