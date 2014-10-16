
// VIEW: Cluster Properties Window
// Wraps several Cluster information views in a tabbed window.
// ----------------------------------------------------------------------------

Ext.define('SM.view.cluster.ClusterPropertiesWindow', {
    extend: 'Ext.window.Window',
    alias : 'widget.clusterpropertieswindow',

    title    : 'Cluster Properties',
    layout   : 'fit',
    autoShow : true,
    cluster  : null,
    activeTab: 0,
    resizable:false,
    draggable: false,
    modal:true,
    initComponent: function() {
        this.items = [{
            xtype: 'tabpanel',
            width: 400,
            height: 400,
            activeTab: this.activeTab,
            items: [{
                xtype: 'serverlist',
                parentItem: this.cluster
            }, {
                xtype: 'resourcelist',
                parentItem: this.cluster
            }, {
                xtype: 'nodelist',
                parentItem: this.cluster
            }, {
                xtype: 'servicelist',
                parentItem: this.cluster
            }]
        }];

        this.buttons = [{
            text: 'Close',
            scope: this,
            handler: this.close
        }];
        
        this.callParent(arguments);
    }
});


