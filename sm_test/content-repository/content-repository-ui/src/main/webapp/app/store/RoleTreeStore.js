
// STORE: Role Tree Nodes
// Retrieves tree node data from a RoleTreeProxy, in order to get a list of
// available Roles and the ServiceTypes they define.
// ----------------------------------------------------------------------------

Ext.define('SM.store.RoleTreeStore', {
    extend: 'SM.store.AdHocTreeStore',
    model: 'SM.model.TreeNode',
    sorters: ['text'],
    autoLoad: false,
    server: null,

    buildChildNodes: function(node) {
        var config = ConfigManager.config;
        var server = this.server;
        var type   = node.get('type');
        var object = node.get('object');
        var data;
        if (type == 'Product') {
            data = Ext.Array.map(config.getRoles(), function(role) {
                var checked = false;
                if (server) {
                    checked = server.getChildrenWith({
                        type: 'Node',
                        roleName: role.get('name')
                    }).length > 0;
                }
                return {
                    type: 'Role',
                    text: role.get('name'),
                    object: role,
                    leaf: false,
                    server: server,
                    checked: checked,
                    iconCls: 'ico-node'
                };
            });
        } else if (type == 'Role') {
            data = Ext.Array.map(object.getServiceTypes(), function(sType) {
                return {
                    type: 'ServiceType',
                    text: sType.get('name') + ' (' + sType.get('version') + ')',
                    object: sType,
                    leaf: true,
                    iconCls: 'ico-service'
                };
            });
        } else
            data = [];

        return Ext.Array.map(data, function(item) {
            var n = Ext.create('SM.model.TreeNode', item);
            n.set('server', server); // Ext 4.1 is a little pickier about constructors setting only defined model attributes.
            Ext.data.NodeInterface.decorate(n);
            return n;
        });
    },

    buildRootNode: function(node) {
        var config = ConfigManager.config;
        if (config) {
            return {
                text: config.get('productName') + '-' + config.get('productVersion') ,
                type: 'Product',
                object: null
            };
        } else {
            return {
                text: '[No Product]',
                type: 'Product',
                object: null
            };
        }
    }
});


