
// VIEW: Node Overview
// A form listing the properties of a node, such as name, server, etc.
// ----------------------------------------------------------------------------

Ext.define('SM.view.node.NodeOverview', {
    extend: 'Ext.form.Panel',
    alias : 'widget.nodeoverview',

    title : 'Node Overview',
    bodyPadding: '5',
    width: 400,
    layout: 'anchor',
    defaults: {
        anchor: '100%'
    },
    node: null,
    modal:true,
    draggable: false,
    defaultType: 'displayfield',
    items: [{
        fieldLabel: 'Name',
        name: 'name'
    },{
        fieldLabel: 'Comm. Port',
        name: 'port'
    },{
        fieldLabel: 'SSH Port',
        name: 'sshPort'
    },{
        fieldLabel: 'Message Port',
        name: 'messagePort'
    },{
        fieldLabel: 'Status',
        name: 'status'
    }],

    initComponent: function() {
        this.callParent(arguments);
        
        // Set up the fields.
        if (this.node !== null && this.node !== undefined)
            this.loadRecord(this.node);
        else
            Log.warn("Loaded a Node Overview tab with no node data!");
    }
});
