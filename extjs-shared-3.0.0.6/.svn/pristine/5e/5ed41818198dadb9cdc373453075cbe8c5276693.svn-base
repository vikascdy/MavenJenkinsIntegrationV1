Ext.define('Edifecs.LeftMenu', {
    extend          :   'Ext.tree.Panel',
    alias           :   'widget.LeftMenu',
    padding         :   0,
    minWidth        :   220,
    layout          :   'fit',
    border          :   false,
    bodyBorder      :   false,
    rootVisible     :   false,
    useArrows       :   true,
    ui              :   "edifecs-leftmenu",
    // user defined variables
    url             :   "",
    previousNode    :   null,
    currentView     :   null,
    menuType        :   "type1",
    selectedFlag    :   false,
    // overriding superclass template method
    initComponent   :   function () {

        // create store for tree Panel
        this.store = this.createTreeStore(this.url, this);

        if (this.menuType.toUpperCase() == "TYPE1") {
            this.addCls("leftMenuType1");
            this.addBodyCls("leftMenuBodyType1");
        }
        else if (this.menuType.toUpperCase() == "TYPE2") {
            this.addCls("leftMenuType2");
            this.addBodyCls("leftMenuBodyType2");
        }

        // set left menu height according to browser height
        this.setHeight(this.getBrowserHeight());
        this.callParent(arguments);
    },
    afterRender     :   function () {
        // create tree view object
        this.currentView = this.getView();

        this.on('beforeitemclick',function(currentNode,record,item,index,e){
            if(!record.get('expandable'))
                return false;
        });

        // events defined for left menu
        this.on('afteritemexpand', function (currentNode) {
            // apply selected class for selected node
            this.currentView.addRowCls(this.previousNode, "selectedCls");
        });

        this.on('itemexpand', function (currentNode) {
            // apply style for child nodes upto multiple levels
            this.iterateChildGroups(currentNode, this);
        });

        this.on('itemclick', function (currentView, currentRecord, currentRowItem, currentIndex, evt) {
            // single click expand and collapse
            currentRecord.isExpanded() ? currentRecord.collapse() : currentRecord.expand();
            var currentNode = currentView.getTreeStore().getNodeById(currentRecord.internalId);

            // select node when click on it
            if (!currentNode.hasChildNodes()) {
                currentView.addRowCls(currentRowItem, "selectedCls");
                if (this.previousNode != null) {
                    if (currentNode != this.previousNode)
                        currentView.removeRowCls(this.previousNode, "selectedCls");
                }
                currentView.addRowCls(currentNode.parentNode, "boldFont");
                this.previousNode = currentNode;
            }
        });

        this.callParent(arguments);
        return;
    },

    // get browser height
    getBrowserHeight  :     function () {
        if (Ext.isIE7 || Ext.isIE8)
            return document.documentElement.offsetHeight;
        else
            return window.innerHeight;
    },

    // iterate child groups
    iterateChildGroups  :   function (currentNode, me) {
        if (currentNode.hasChildNodes() && currentNode.isExpanded()) {
            if (currentNode.parentNode != null && currentNode.parentNode.isRoot())
                    currentNode.set('cls', 'expandedParentNode '+ currentNode.internalId);
            currentNode.eachChild(function (childNode) {
                if (childNode.isLeaf() && !childNode.parentNode.isRoot()) {
                    if (childNode.isLast())
                        childNode.set('cls', 'leafLastNode '+ currentNode.internalId);
                    else
                        childNode.set('cls', 'leafNode '+ currentNode.internalId);
                    if (childNode.raw.active && !me.selectedFlag) {
                        me.currentView.addRowCls(childNode, "selectedCls");
                        me.previousNode = childNode;
                        me.selectedFlag = true;
                    }
                }
                else
                    me.iterateChildGroups(childNode, me);
            });
        }
    },

    // set parent node selected
    setParentSelected   :   function (node, clsStatus) {
        if (node != null) {
            if (clsStatus)
                this.currentView.addRowCls(node.parentNode, "boldFont");
            else
                this.currentView.removeRowCls(node.parentNode, "boldFont");
        }
    },

    // create tree store
    createTreeStore     :   function (url, me) {
        var treeStore = Ext.create('Ext.data.TreeStore',
            {
                proxy    :{
                    type:'ajax',
                    url :url //user defined url
                },
                listeners:{
                    load:function () {
                        var rootNode = me.getRootNode();
                        rootNode.eachChild(function (currentNode) {
                            me.iterateChildGroups(currentNode, me);
                        });
                    }
                }
            });
        return treeStore;
    },

    // private, clean up 
    onDestroy   :   function () {
        this.removeAll();
        this.callParent(arguments);
    }
});