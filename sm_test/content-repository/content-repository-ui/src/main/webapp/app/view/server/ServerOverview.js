
// VIEW: Server Overview
// A form listing the properties of a server, such as hostname, OS, system
// load, etc.
// ----------------------------------------------------------------------------

Ext.define('SM.view.server.ServerOverview', {
    extend: 'Ext.form.Panel',
    alias : 'widget.serveroverview',

    title : 'Server Overview',
    bodyPadding: '5',
    width: 400,
    layout: 'anchor',
    defaults: {
        anchor: '100%'
    },
    server: null,
    draggable: false,
    modal:true,
    defaultType: 'displayfield',
    items: [{
        fieldLabel: 'Name',
        name: 'name'
    },{
        fieldLabel: 'Hostname',
        name: 'hostName'
    },{
        fieldLabel: 'IP Address',
        name: 'ipAddress'
    },{
        fieldLabel: 'Message Port',
        name: 'messagePort'
    },{
        fieldLabel: 'Cluster',
        name: 'clusterName'
    },{
        fieldLabel: 'Status',
        name: 'status'
    },{
        fieldLabel: 'OS',
        name: 'soOSField'
    },{
        fieldLabel: 'Architecture',
        name: 'soArchField'
    }],

    initComponent: function() {
        this.callParent(arguments);
        
        // Set up the fields.
        if (this.server !== null)
        {
            this.loadRecord(this.server);
            this.getForm().findField('clusterName').setRawValue(this.server.getParent().get('name'));
            var osField = this.getForm().findField('soOSField');
            var archField = this.getForm().findField('soArchField');
            this.server.getSystemInfo("os", function(s) {osField.setRawValue(s);});
            this.server.getSystemInfo("arch", function(s) {archField.setRawValue(s);});
        }
        else
            Log.warn("Loaded a Server Overview tab with no server data!");
    }
});
