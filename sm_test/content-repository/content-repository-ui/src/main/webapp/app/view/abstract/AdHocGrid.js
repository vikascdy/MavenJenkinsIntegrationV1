
// ABSTRACT VIEW: Ad-hoc Grid
// A GridPanel that appears to bypass all of the complexity of Ext JS Models
// and Stores, and requires only a list of fields and a `getData()` function to
// work. It will pull data into an in-memory store using the getData function.
// ----------------------------------------------------------------------------

Ext.define('SM.view.abstract.AdHocGrid', {
    extend: 'Ext.grid.Panel',
    xtype : 'adhocgrid',

    initComponent: function(config) {
        var data = this.getData();
        var fields = this.fields || Ext.Error.raise("An AdHocGrid must have a fields property.");
        var sorters = this.sorters;
        this.store = Ext.create('Ext.data.Store', {
            autoDestroy: true,
            fields: fields,
            sorters: sorters,
            data: data,
            proxy: {
                type: 'memory',
                reader: 'json'
            }
        });
        this.callParent(arguments);
    },

    reload: function() {
        var data = this.getData();
        this.store.removeAll();
        this.store.add(data);
    },

    getData: function() {
        // Override me!
        Ext.Error.raise("You must override getData() for an AdHocGrid to work.");
    }
});

