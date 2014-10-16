
// MIXIN: Grid Filter
// Adds a top bar to a Grid with a text field that allows the Grid to be
// incrementally filtered as the user types in the field.
// ----------------------------------------------------------------------------

Ext.define('SM.mixin.GridFilterMixin', {
    tbar: [{
        xtype: 'textfield',
        emptyText: 'Filter',
        itemId: 'filter',
        enableKeyEvents: true,
        flex: 1,
        listeners: {
            keyup: function(field, e) {
                var grid = field.up('gridpanel');
                grid.getStore().clearFilter();
                if (field.getValue()) {
                    grid.getStore().filterBy(function(record) {
                        return record.get('name').toLowerCase().indexOf(field.getValue().toLowerCase()) != -1;
                    });
                }
            }
        }
    }]
});

