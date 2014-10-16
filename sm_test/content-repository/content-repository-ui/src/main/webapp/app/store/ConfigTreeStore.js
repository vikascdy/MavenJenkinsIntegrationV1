
// STORE: Config Tree Nodes
// Retrieves tree node data from the config file in memory, and appends the
// necessary data to display the nodes in the tree view.
// ----------------------------------------------------------------------------

Ext.define('SM.store.ConfigTreeStore', {
    extend: 'SM.store.AdHocTreeStore',
    model: 'SM.model.TreeNode',
    sorters: ['type', 'text'],
    autoLoad: false,

    buildChildNodes: function(node) {

        var object = node.get('object') || ConfigManager.config;
        var data;
        if (object) {
            data = Ext.Array.map(object.getChildren(), function(item) {
                return {
                    type: item.getType(),
                    text: item.get('name'),
                    status: item.get('status'),
                    object: item,
                    leaf: (item.getType() == 'Service' || item.getType() == 'Resource'),
                    expanded: node.get('expanded'),
                    iconCls: item.getIconCls()
                };
            });
        } else {
            data = [];
        }

        return Ext.Array.map(data, function(item) {
            var n = Ext.create('SM.model.TreeNode', item);
            Ext.data.NodeInterface.decorate(n);
            if (item.object.dirty)
                n.setDirty();
            return n;
        });
    },

    buildRootNode: function(node) {
        var config = ConfigManager.config;

        if (config) {
            return {
                text: config.get('name'),
                type: 'Config',
                object: config,
                iconCls: config.getIconCls()
            };
        } else {
            return {
                text: '[No Config Loaded]',
                type: 'Config'
            };
        }
    }
});

