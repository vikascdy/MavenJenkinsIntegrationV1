
// VIEW: JVM System Information
// A panel showing graphs of a JVM's CPU and memory usage.
// ----------------------------------------------------------------------------

Ext.define('SM.view.node.JvmSystemInformation', {
    extend: 'Ext.form.Panel',
    mixins: ['SM.mixin.AutoUpdateGraphsMixin'],
    alias : 'widget.jvmsysteminfo',

    title : '<span>JVM Statistics</span>',
    iconCls: 'ico-performance',
    width : 400,
    layout: 'vbox',
    node  : null,
    padding:'10',
    initComponent: function() {
        this.items = [{
            xtype: 'panel',
            title: 'CPU Usage',
            layout: 'fit',
            flex: 1,
            items: [{
                xtype: 'cpugraph',
                source: this.node
            }]
        },{
            xtype: 'panel',
            title: 'Memory Usage',
            layout: 'fit',
            flex: 1,
            margin:'0 0 0 5',
            items: [{
                xtype: 'memgraph',
                source: this.node
            }]
        }];
        
        this.initGraphs();
        this.callParent(arguments);
    }
});

