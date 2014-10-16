// VIEW: Cluster Context Menu
// The right-click context menu for a Cluster.
// ----------------------------------------------------------------------------

Ext.define('SM.view.cluster.ClusterContextMenu', {
    extend : 'Ext.menu.Menu',
    alias : 'widget.clustercontextmenu',
    cluster : null,

    initComponent : function(config) {
        this.items = [ {
            text : 'View Servers',
            iconCls : 'mico-server',
            itemId : 'tab0'
        }, {
            text : 'View Resources',
            iconCls : 'mico-resource',
            itemId : 'tab1'
        }, {
            text : 'View Nodes',
            iconCls : 'mico-node',
            itemId : 'tab2'
        }, {
            text : 'View Services',
            iconCls : 'mico-service',
            itemId : 'tab3'
        }, {
            xtype : 'menuseparator'
        }, {
            text : 'Create New Server...',
            iconCls : 'mico-new',
            itemId : 'newserver'
        // TODO: Add actual permission checks.
        // disabled: !UserManager.admin
        }, {
            text : 'Create New Resource...',
            iconCls : 'mico-new',
            itemId : 'newresource'
        // disabled: !UserManager.admin
        } ];
        this.callParent([ config ]);
    }
});
