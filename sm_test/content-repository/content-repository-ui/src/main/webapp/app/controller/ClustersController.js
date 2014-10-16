
// CONTROLLER: Clusters Controller
// Manages loading and displaying data related to Clusters.
// ----------------------------------------------------------------------------

Ext.define('SM.controller.ClustersController', {
    extend: 'Ext.app.Controller',
    requires: ['Ext.window.MessageBox'],
    
    stores: ['ClusterStore'],
    models: ['Cluster'],
    
    views: [
        'cluster.ClusterList',
        'cluster.ClusterForm',
        'cluster.ClusterContextMenu',
        'cluster.ClusterPropertiesWindow',
        'cluster.ClusterInfoPane',
        'cluster.NewClusterWindow'
    ],

    init: function() {
        var contextMenu = function(handler) {
            return {click: function(mitem) {
                var cluster = mitem.up('clustercontextmenu').cluster;
                return handler(cluster, mitem);
            }};
        };
        this.control({
            'clusterlist': {
                itemdblclick: Functions.showPropertiesWindow,
                itemcontextmenu: Functions.showContextMenu
            },
            'clusterlist #newcluster': {
                click: function(button) {
                    Ext.widget('newclusterwindow', {
                        server: button.up('clusterlist').parentItem
                    });
                }
            },
            'clustercontextmenu > #tab0': contextMenu(function(cluster) {
                cluster.showPropertiesWindow(0);
            }),
            'clustercontextmenu > #tab1': contextMenu(function(cluster) {
                cluster.showPropertiesWindow(1);
            }),
            'clustercontextmenu > #tab2': contextMenu(function(cluster) {
                cluster.showPropertiesWindow(2);
            }),
            'clustercontextmenu > #tab3': contextMenu(function(cluster) {
                cluster.showPropertiesWindow(3);
            }),
            'clustercontextmenu > #delete': contextMenu(function(cluster){cluster.askToDelete();}),
            'clustercontextmenu > #newserver': contextMenu(function(cluster) {
                Ext.widget('newserverwindow', {cluster: cluster});
            }),
            'clustercontextmenu > #newresource': contextMenu(function(cluster) {
                Ext.widget('newresourcewindow', {cluster: cluster});
            })
        });
    }
});

