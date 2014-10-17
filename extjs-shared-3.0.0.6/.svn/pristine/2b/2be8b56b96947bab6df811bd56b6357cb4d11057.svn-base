Ext.define('Edifecs.TriStateTree', {
    extend: 'Ext.tree.Panel',
    alias: 'widget.TriStateTree',
    rootVisible: false,
    useArrows: true,
    requires: [
        'Ext.data.TreeStore'
    ],
    cls: 'tristatetree',
    disabledCls: 'x-tree-checkbox-checked-disabled',
    jsonURL: '',
    jsonData: '',
    jsonFields: '',
    gridColumns: '',
    listeners: {
        'checkchange': function (node, check) {
            var me = this;
            node.set('cls', '');
            me.updateParentCheckedStatus(node);

            if (node.hasChildNodes())
                node.eachChild(this.setChildrenCheckedStatus);
        },
        'load': function (current, node, records) {
            var me = this;
            if (node.hasChildNodes()) {
                node.cascadeBy(function (n) {
                    var value = n.get('checked') ? true : false;
                    if (value)
                        me.updateParentCheckedStatus(n);
                });
            }
        }
    },

    // Propagate change downwards (for all children of current node).
    setChildrenCheckedStatus: function (current) {

        if (current.parentNode) {       // if not root checked
            var parent = current.parentNode;
            current.set('checked', parent.get('checked'));
        }

        if (current.hasChildNodes())
            current.eachChild(arguments.callee);
    },

    // Propagate change upwards (if all siblings are the same, update parent).
    updateParentCheckedStatus: function (current) {

        var me = this, currentChecked = current.get('checked'), currentId = current.get('id');

        if (current.parentNode) {
            var parent = current.parentNode;
            var checkedCount = 0;
            var checkedCountChildren = 0;
            parent.eachChild(function (n) {
                checkedCount += (n.get('checked') ? 1 : 0);
            });

            current.eachChild(function (n) {
                checkedCountChildren += (n.get('checked') ? 1 : 0);
            });

            // Children have same value if all of them are checked or none is checked.
            var allMySiblingsHaveSameValue = (checkedCount == parent.childNodes.length) || (checkedCount == 0);
            var allMyChildrenHaveSameValue = (checkedCountChildren == current.childNodes.length) || (checkedCountChildren == 0);

            var setParentVoid = true;

            // check if current node has any visible state.
            if (me.isThirdState(current) || currentChecked)
                setParentVoid = false;

            // if not - clear parent`s class
            if (setParentVoid)
                me.unsetThirdState(parent);

            if (allMySiblingsHaveSameValue)         // All  the Siblings  are same, so apply value to the parent.
            {
                var checkedValue = (checkedCount == parent.childNodes.length);
                parent.set('checked', checkedValue);
                me.unsetThirdState(parent);
                if (parent == me.getRootNode())             // modify  Root based on it`s Children Have Same Value
                {
                    if (allMyChildrenHaveSameValue)
                        me.unsetThirdState(me.getRootNode());
                    else
                        me.setThirdState(me.getRootNode());
                }
            }
            else {
                me.setThirdState(me.getRootNode());         // Not all  the children are same, so set root node to third state.
                if (checkedCount)                           // At least one sibling is checked, so set parent node to third state.
                    me.setThirdState(parent);
                else
                    parent.set('checked', false);
            }

            me.updateParentCheckedStatus(parent);
        }
    },

    isThirdState: function (node) {
        return  node.get('cls') == this.disabledCls;
    },

    setThirdState: function (node) {
        node.set('cls', this.disabledCls);
        node.set('checked', false);
    },

    unsetThirdState: function (node) {
        node.set('cls', '');
    },

    getRecords: function () {
        var me = this;
        try {
            var grid_selections = me.getView().getChecked(), result = [];
            Ext.Array.each(grid_selections, function (rec) {
                var pushdata = false;
                if (rec.get('id')) {
                    if (rec.get('leaf') === true)
                        pushdata = true;
                    else
                        pushdata = false;
                    if (pushdata)
                        result.push(rec);
                }
            });
            return result;
        } catch (e) {
            console.log('error in accessPanel getRecords ' + e);
        }
    },

    setSelections: function (ids,expandStatus) {
        var me = this;
        // check RootNode or do cascade checking
        me.getRootNode().cascadeBy(function (node) {
                if(ids.indexOf(node.get('id'))>-1)
                {
                    node.set('checked', true);
                    if(node.hasChildNodes())
                    {
                        node.cascadeBy(function (n) {
                            n.set('checked', true);
                        });
                    }
                }

            me.updateParentCheckedStatus(node);

            if(node.get('checked'))
            {
                if(!node.isExpanded() && expandStatus)
                {
                    while(!node.isRoot())
                    {
                        node.parentNode.expand(false);
                        node=node.parentNode;
                    }
                }
            }
        });
    },

    getTreeStore: function (url, treeFields, data) {
        var treeStore;
        if (url != undefined) {
            treeStore = Ext.create('Ext.data.TreeStore',
                {
                    fields: treeFields,
                    proxy: {
                        type: 'ajax',
                        url: url
                    }
                });
            treeStore.load();
        }
        else {
            treeStore = Ext.create('Ext.data.TreeStore',
                {
                    fields: treeFields,
                    root: data
                });
        }
        return treeStore;
    },



    initComponent: function () {
        var me = this;
        var store = me.getTreeStore(me.jsonURL, me.jsonFields, me.jsonData);

        Ext.apply(this, {
            store: store,
            columns: me.gridColumns
        });

        this.callParent(arguments);
    }
});