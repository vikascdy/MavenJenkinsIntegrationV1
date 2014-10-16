
// STORE: Clusters
// Retrieves Cluster data from the loaded config file.
// ----------------------------------------------------------------------------

Ext.require('SM.proxy.ConfigProxy');

Ext.define('SM.store.ClusterStore', {
    extend: 'SM.store.ConfigStore',
    model : 'SM.model.Cluster',
    sorters: ['name'],
    searchCriteria: {type: 'Cluster'}
});

