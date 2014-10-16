
// ABSTRACT STORE: Config Store
// The parent class of most of the stores used in the Service Manager UI. Uses
// a ConfigProxy to retrieve data, and verifies that a config file is loaded
// before loading data.
// ----------------------------------------------------------------------------

Ext.require('SM.proxy.ConfigProxy');

Ext.define('SM.store.ConfigStore', {
    extend: 'Ext.data.Store',
    autoLoad: false,

    constructor: function(config) {
        config = config || {};
        config.proxy = {
            type: 'config',
            criteria: config.searchCriteria || this.searchCriteria,
            parentItem: config.parentItem
        };
        this.callParent([config]);
    },

    setParentItem: function(parentItem) {
        this.proxy = Ext.create('SM.proxy.ConfigProxy', {
            criteria: this.getProxy().criteria,
            parentItem: parentItem
        });
    }
});

