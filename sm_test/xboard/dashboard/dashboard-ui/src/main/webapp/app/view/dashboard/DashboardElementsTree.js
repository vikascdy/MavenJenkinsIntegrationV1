Ext.define('DD.view.dashboard.DashboardElementsTree', {
    extend:'Ext.tree.Panel',
    alias:'widget.dashboardelementstree',
    store: 'DashboardElementsTreeStore',
    rootVisible: true,
    lines:false,
    useArrows:true,
    viewConfig: {
        plugins: {
            ptype: 'treeviewdragdrop',
            dragText: 'Drag and drop to reorganize',
            containerScroll: true
        }
    },
    dropConfig:{
        onNodeOver : function(n, dd, e, data) {

            var pt = this.getDropPoint(e, n, dd);
            var node = n.node;

            // auto node expand check
            if (!this.expandProcId && pt == "append" && node.hasChildNodes() && !n.node.isExpanded()) {
                this.queueExpand(node);
            } else if (pt != "append") {
                this.cancelExpand();
            }

            var returnCls = this.dropNotAllowed;

            if (data.node.parentNode.id == n.node.id)  //Test if drop node is the same as drag node parent
                if (this.isValidDropPoint(n, pt, dd, e, data)) {
                    if (pt) {
                        var el = n.ddel;
                        var cls;
                        if (pt != "above" && pt != "below") {  //Do not allow append 'above' or 'below'
                            returnCls = "x-tree-drop-ok-append";
                            cls = "x-tree-drag-append";
                        }
                        if (this.lastInsertClass != cls) {
                            Ext.fly(el).replaceClass(this.lastInsertClass, cls);
                            this.lastInsertClass = cls;
                        }
                    }
                }

            if (data.node.parentNode.id == n.node.parentNode.id)  //Test if drop node is a sibling of drag node
                if (this.isValidDropPoint(n, pt, dd, e, data)) {
                    if (pt) {
                        var el = n.ddel;
                        var cls;
                        if (pt == "above") {
                            returnCls = n.node.isFirst() ? "x-tree-drop-ok-above" : "x-tree-drop-ok-between";
                            cls = "x-tree-drag-insert-above";
                        } else if (pt == "below") {
                            returnCls = n.node.isLast() ? "x-tree-drop-ok-below" : "x-tree-drop-ok-between";
                            cls = "x-tree-drag-insert-below";
                        } else {
                            returnCls = this.dropNotAllowed;
                            // cls = "x-tree-drag-append";

                        }
                        if (this.lastInsertClass != cls) {
                            Ext.fly(el).replaceClass(this.lastInsertClass, cls);
                            this.lastInsertClass = cls;
                        }
                    }
                }
            return returnCls;
        },
        onNodeDrop : function(n, dd, e, data) {
            var point = this.getDropPoint(e, n, dd);
            var targetNode = n.node;
            targetNode.ui.startDrop();
            if (!this.isValidDropPoint(n, point, dd, e, data)) {
                targetNode.ui.endDrop();
                return false;
            }
            //  add check to see if drop target is the parent node
            if (data.node.parentNode.id != n.node.parentNode.id && data.node.parentNode.id != n.node.id)
                return false;

            // first try to find the drop node
            var dropNode = data.node || (dd.getTreeNode ? dd.getTreeNode(data, targetNode, point, e) : null);
            var dropEvent = {
                tree : this.tree,
                target: targetNode,
                data: data,
                point: point,
                source: dd,
                rawEvent: e,
                dropNode: dropNode,
                cancel: !dropNode
            };
            var retval = this.tree.fireEvent("beforenodedrop", dropEvent);

            if (retval === false || dropEvent.cancel === true || !dropEvent.dropNode || ((targetNode.childNodes.length > 0) && (targetNode != dropNode.parentNode))) {
                targetNode.ui.endDrop();
                return false;
            }
            // allow target changing
            targetNode = dropEvent.target;
            if (point == "append" && !targetNode.isExpanded()) {
                targetNode.expand(false, null, function() {
                    this.completeDrop(dropEvent);
                }.createDelegate(this));
            } else {
                this.completeDrop(dropEvent);
            }
            return true;
        }
    }
});