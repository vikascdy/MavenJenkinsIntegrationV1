
// VIEW: System Information
// A panel showing graphs of a Server's CPU and memory usage.
// ----------------------------------------------------------------------------

Ext.define('SM.view.server.SystemInformation', {
    extend: 'Ext.form.Panel',
    mixins: ['SM.mixin.AutoUpdateGraphsMixin'],
    alias : 'widget.systeminfo',

    title : 'Server Statistics',
    iconCls: 'ico-performance',
    width : 400,
    layout: 'vbox',
    server: null,
    padding:'10',
    initComponent: function() {
        this.items = [{
            xtype: 'panel',
            title: 'CPU Usage',
            layout: 'fit',
            flex: 1,
            items: [{
                xtype: 'cpugraph',
                source: this.server
            }]
        },{
            xtype: 'panel',
            title: 'Memory Usage',
            layout: 'fit',
            margin:'0 0 0 5',
            flex: 1,
            items: [{
                xtype: 'memgraph',
                source: this.server
            }]
        }];

        this.initGraphs();
        this.callParent(arguments);
    }
});

