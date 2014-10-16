
// STORE: Nodes
// Retrieves node data from the loaded config file.
// ----------------------------------------------------------------------------

Ext.require('SM.proxy.ConfigProxy');

Ext.define('SM.store.NodeStore', {
    extend: 'SM.store.ConfigStore',
    model : 'SM.model.Node',
    sorters: ['name'],
    searchCriteria: {type: 'Node'}
});

