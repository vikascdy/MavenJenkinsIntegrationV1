
// VIEW: New Node Window 
// A popup window for spawning new Nodes from a Server.
// ----------------------------------------------------------------------------

Ext.define('SM.view.node.NewNodeWindow', {
    extend: 'Ext.window.Window',
    alias : 'widget.newnodewindow',

    title   : 'Create New Node',
    layout  : 'fit',
    autoShow: true,
    modal:true,
    server  : null,
    width   : 360,
    padding:'0 12 0 12',
    resizable:false,
    draggable: false,
    tbar: [{
        xtype: 'label',
        text: 'Server',
        width: 100
    }, {
        xtype: 'configitempicker',
        searchCriteria: {type: 'Server'},
        flex: 1
    }],

    items: [{
        xtype : 'nodeform',
//        padding:'0 0 0 8',
        flex  : 1,
        margin: 4
    }],

    buttons: [{
        text: "Create Node",
        handler: function(btn) {
            var nnw = btn.up('newnodewindow');
            nnw.createNode();
        }
    }, {
        text: "Cancel",
        handler: function(btn) {
            var nnw = btn.up('newnodewindow');
            nnw.close();
        }
    }],

    createNode: function() {
        try {
            var form = this.down('nodeform').getForm();
            if (!form.isValid())
                Ext.Error.raise("Some of the mandatory fields are empty or invalid.");
            var values = form.getFieldValues();
            var server = this.down('configitempicker').getItem();
            if (!server)
                Ext.Error.raise("No Server selected.");
            server.shouldBeA('Server');
            
            if (server.spawnNode(values.name, values.port, values.logLevel, values.description,null))
                SM.reloadAll();
            this.close();
        } catch (err) {
            Functions.errorMsg(err.message);
        }
    },

    initComponent: function() {
        this.callParent(arguments);
        if (this.server)
            this.down('configitempicker').setItem(this.server);
    }
});

