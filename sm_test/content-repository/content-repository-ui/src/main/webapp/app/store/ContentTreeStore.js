
// STORE: Content Tree Store
// Loads the nodes of the ContentRepositoryTree from the server.
// ----------------------------------------------------------------------------

Ext.require('SM.proxy.RemoteProxy');

Ext.define("SM.store.ContentTreeStore", {
    extend: 'Ext.data.TreeStore',
    model : 'SM.model.ContentNode',
    sorters: ['leaf', 'name'],
    autoLoad: false,
    foldersOnly: false,

    iconMap: {
        'file'  : 'ico-file',
        'folder': null,
        'system': 'ico-node'
    },
    
    proxy: {
        type: 'remoteproxy',
        url: JSON_URL + '/content.listFiles',
        actionMethods: {
        	create:'POST',
        	read :'POST',
        	update:'POST',
        	destroy:'POST'
        	
        },
        reader: {
            type: 'json'
        }
    },

    root: {
        name: 'Repository',
        expanded: true,
        id: '/'
    },

    listeners: {
        load: function(store, node, records) {
            Ext.each(records, function(record) {
                if (record.get('name') == 'jcr:system' || record.get('name') == 'rep:policy') {
                    record.remove();
                } else {
                    var iconCls = store.iconMap[record.get('typeName')];
                    record.set('iconCls', (iconCls === undefined) ? 'ico-unknown' : iconCls);
                }
            });
        }
    },

    constructor: function(config) {
        config = config || {};
        config.iconMap = this.iconMap;
        if (config.foldersOnly) {
            this.proxy.extraParams = {foldersOnly: true};
        }
        this.callParent(Ext.Array.toArray(config));
    }
});

