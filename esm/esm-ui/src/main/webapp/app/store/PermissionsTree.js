Ext.define('Security.store.PermissionsTree', {
    extend: 'Ext.data.TreeStore',
    storeId: 'permissionsTreeStore',
    requires: 'Security.model.Permission',
    model: 'Security.model.Permission',
    proxy: {
        type: 'ajax',
        url: 'security-data/permissions/tree'
    },
    autoLoad:false,
    
    listeners: {
        /**
         * Each Security.model.Permission will be automatically decorated with
         * methods/properties of Ext.data.NodeInterface
         * Whenever a Permission node is appended
         * to the tree, this TreeStore will fire an "append" event.
         */
        append: function( thisNode, newChildNode, index, eOpts ) {
            newChildNode.set('iconCls', 'permissions-icon');
            newChildNode.set('text', newChildNode.get('caption'));
            newChildNode.set('checked', newChildNode.get('leaf') ? false : null);
            newChildNode.set('expanded', true);

        }
    }
});
