
// ABSTRACT STORE: Ad-hoc Tree Store
// Abstract base class that can be used to construct TreeStores with custom
// Proxies attached, which can pull data from an arbitrary JavaScript function
// without the need to define a separate proxy class.
// ----------------------------------------------------------------------------

Ext.define("SM.store.AdHocTreeStore", {
    extend: 'Ext.data.TreeStore',

    constructor: function(config) {
        var me = this;
        config = config || {};
        config.proxy = Ext.create('Ext.data.proxy.Proxy', {
            read: function(operation, callback, scope) {
                Ext.apply(operation, {
                    resultSet: Ext.create('Ext.data.ResultSet', {
                        records: me.buildChildNodes(operation.node)
                    })
                });

                operation.setCompleted();
                operation.setSuccessful();
                Ext.callback(callback, scope || this, [operation]);
            },

            reader: {type: 'json'}
        });
        this.callParent([config]);
    },

    load: function(options) {
        var me = this;
        options = options || {};
        if (!options.node) {
            me.setRootNode(me.buildRootNode());
        }

        me.callParent(arguments);

        // Propagate to child nodes. This fixes a bug in Ext JS 4.1.0 in which
        // most child nodes aren't automatically loaded. I'm not sure what the
        // exact cause of this bug is, so feel free to check whether this code
        // is actually necessary after any subsequent Ext JS update.
        var node = options.node || me.getRootNode();

        Ext.each(node.childNodes, function(child) {
            var subOpts = Ext.clone(options);
            subOpts.node = child;
            me.load(subOpts);
        });
        
    },
    
    buildChildNodes: function(node) {
        // Override me!
        Ext.Error.raise("Did not override buildChildNodes() of an AdHocTreeStore!");
    },

    buildRootNode: function() {
        // Override me!
        Ext.Error.raise("Did not override buildRootNode() of an AdHocTreeStore!");
    }
});

