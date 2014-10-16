// VIEW: Product List
// A Grid that lists all Products available.
// ----------------------------------------------------------------------------

Ext.define('SM.view.core.ProductList', {
    extend: 'Ext.grid.Panel',
    alias : 'widget.productlist',

    store : 'ProductStore',
    title : 'Products',
    preventHeader: true,
    hideHeaders: true,

    columns: [
        {
            text: 'Name',
            dataIndex: 'name',
            flex: 3
        },
        {
            text: 'Version',
            dataIndex: 'version',
            flex: 2
        }
    ],

        initComponent: function(config) {
            this.callParent(config);
            this.store.load();
        }
    });

