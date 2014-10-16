
// VIEW: Cluster List
// A Grid that lists all Clusters under a specific parent item.
// ----------------------------------------------------------------------------

Ext.define('SM.view.cluster.ClusterList', {
    extend: 'SM.view.abstract.ConfigItemList',
    alias : 'widget.clusterlist',
    itemType : 'Cluster',
    storeType: 'SM.store.ClusterStore',
    title    : 'Clusters',
    
    extraColumns: [{
        header: 'Environment',
        dataIndex: 'environment',
        flex: 2
    }]
});

