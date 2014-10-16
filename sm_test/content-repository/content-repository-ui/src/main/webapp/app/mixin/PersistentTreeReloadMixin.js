
// MIXIN: Persistent Tree Reload
// Allows a TreePanel to refresh while maintaining the same expanded/collapsed
// nodes. Also provides a `reload` function for the TreePanel which will be
// triggered when SM.reloadAll() is called.
// ----------------------------------------------------------------------------

Ext.define("SM.mixin.PersistentTreeReloadMixin", {

    reload: function(callback, scope) {
        var tree = this;
        tree.setLoading("Updating...");
        setTimeout(function() {
            tree.reloadNow.apply(tree);
            tree.setLoading(false);
            Ext.callback(callback, scope);
        }, 10);
    },

    reloadNow: function() {
        var map = {};
        this._collectNodes(this.getRootNode(), map);
        var tree = this;
        this.getStore().load({
            callback: function(records, operation, success) {
                tree._expandIfNecessary(tree.getRootNode(), map);
            },
            scope: tree 
        });
    },

    _collectNodes: function(node, map) {
        map[node.getId()] = node.isExpanded();
        node.eachChild(function(n) {
            if (!n.isLeaf())
                this._collectNodes(n, map);
        }, this);
    },

    _expandIfNecessary: function(node, map) {
        var expanded = map[node.getId()];
        var tree = this;
        if (expanded === true) {
            node.expand(false, function() {
                node.eachChild(function(n) {
                    tree._expandIfNecessary(n, map);
                }, tree);
            });
        } else if (expanded === false) {
            node.collapse();
        }
    }
});


