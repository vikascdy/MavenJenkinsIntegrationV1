
// VIEW: New Server Window 
// A popup window for creating new, unassociated Servers for a Cluster.
// ----------------------------------------------------------------------------

Ext.define('SM.view.server.NewServerWindow', {
    extend: 'Ext.window.Window',
    alias : 'widget.newserverwindow',

    title   : 'Create New Server',
    layout  : 'fit',
    autoShow: true,
    modal:true,
    cluster : null,
    width   : 360,
    resizable:false,
     draggable: false,
    /*tbar   : [{
        xtype: 'label',
        text: 'Cluster',
        width: 100
    }, {
        xtype: 'configitempicker',
        searchCriteria: {type: 'Cluster'},
        flex: 1
    }],*/

    items : [{
        xtype : 'serverform',
        bodyPadding:'0 0 0 8',
        flex  : 1,
        margin: 4
    }],

    buttons: [{
        text: "Create Server",
        handler: function(btn) {
            var nsw = btn.up('newserverwindow');
            nsw.createServer();
        }
    }, {
        text: "Cancel",
        handler: function(btn) {
            var nsw = btn.up('newserverwindow');
            nsw.close();
        }
    }],

    createServer: function() {
        try {
            var form = this.down('serverform').getForm();
            if (!form.isValid())
                Ext.Error.raise("Some of the mandatory fields are empty or invalid.");
            var values = form.getFieldValues();
            var cluster = this.cluster;
            //var cluster = this.down('configitempicker').getItem();
            //if (!cluster)
            //    Ext.Error.raise("No Cluster selected.");
            cluster.shouldBeA('Cluster');
            if (cluster.spawnServer(values.name, values.hostName, values.ipAddress, values.messagePort, values.description))
                SM.reloadAll();
            this.close();
        } catch (err) {
            Functions.errorMsg(err.message);
        }
    },

    initComponent: function() {
        this.callParent(arguments);
        if (!this.cluster)
            this.cluster = ConfigManager.config.get('clusters')[0];
        //if (this.cluster)
        //    this.down('configitempicker').setItem(this.cluster);
    }
});


