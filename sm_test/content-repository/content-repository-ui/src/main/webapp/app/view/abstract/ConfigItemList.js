
// ABSTRACT VIEW: Config Item List
// A generic abstract GridPanel view used as the base for all of the other list
// views (ServerList, NodeList, etc.).
// ----------------------------------------------------------------------------

Ext.define('SM.view.abstract.ConfigItemList', {
    extend: 'Ext.grid.Panel',
    alias : 'widget.configitemlist',
    parentItem: null,
    extraCriteria: null,
    itemType: null,
    storeType: null,
    mergeHeaders: false,

    iconColumn: {
        header: '&nbsp;',
        width: 32,
        sortable: false,
        hideable: false,
        renderer: function(value, metadata, record) {
            return Ext.String.format('<div class="icon {0}">&nbsp;</div>', record.getIconCls());
        }
    },

    nameColumn: {
        header: 'Name',
        dataIndex: 'name',
        flex: 3,
        renderer: function(value) {
            return "<a href='#' class='config-link'>" + value + "</a>";
        }
    },

    initComponent: function() {
        this.columns = [
            this.iconColumn,
            this.nameColumn
        ];
        Ext.each(this.extraColumns, function(col) {
            var newCol = Functions.clone(col);
            if (this.showColumns !== undefined &&
                this.showColumns.indexOf(col.dataIndex) < 0)
                newCol.hidden = true;
            this.columns.push(newCol);
        }, this);

        var storeOptions = this.extraCriteria ? {
            parentItem: this.parentItem,
            searchCriteria: Functions.merge({type: this.itemType}, this.extraCriteria)
        } : {
            parentItem: this.parentItem
        };
        this.store = Ext.create(this.storeType, storeOptions);
        if (this.mergeHeaders) Functions.mergePanelHeaders(this);
        this.callParent(arguments);
        this.reload();
    },

    reload: function() {
        if (this.store.setParentItem)
            this.store.setParentItem(this.parentItem);
        this.store.load();
    }
});

