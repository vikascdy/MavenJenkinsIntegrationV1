
// VIEW: New Cluster Window 
// A popup window for creating new Clusters.
// ----------------------------------------------------------------------------

Ext.define('SM.view.cluster.NewClusterWindow', {
    extend: 'Ext.window.Window',
    alias : 'widget.newclusterwindow',

    title   : 'Create New Cluster',
    layout  : 'fit',
    autoShow: true,
    modal:true,
    width   : 360,
    resizable:false,
    draggable: false,
    items   : [{
        xtype : 'clusterform',
        flex  : 1
    }],

    buttons: [{
        text: "Create Cluster",
        handler: function(btn) {
            var ncw = btn.up('newclusterwindow');
            ncw.createCluster();
        }
    }, {
        text: "Cancel",
        handler: function(btn) {
            var ncw = btn.up('newclusterwindow');
            ncw.close();
        }
    }],

    createCluster: function() {
        try {
            var form = this.down('clusterform').getForm();
            if (!form.isValid())
                Ext.Error.raise("One or more of the form values is invalid or missing.");
            var values = form.getFieldValues();
            var cluster = Ext.create('SM.model.Cluster', values);
            ConfigManager.config.appendChild(cluster);
            SM.reloadAll();
            this.close();
        } catch (err) {
            Functions.errorMsg(err.message);
        }
    }
});



