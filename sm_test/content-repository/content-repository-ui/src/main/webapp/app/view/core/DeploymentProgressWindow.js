
// VIEW: Deployment Progress Window
// Shows a message and progress bar that updates as the backend processes the
// deployment of a new Config.
// ----------------------------------------------------------------------------

Ext.define('SM.view.core.DeploymentProgressWindow', {
    extend: 'Ext.window.Window',
    alias : 'widget.deploymentprogresswindow',

    stage : 0,
    stages: 4,
    stageMessages: [
        "Sending deployment request to server...",
        "Bringing down the backend service...",
        "Waiting for the backend service to come back online...",
        "Finalizing deployment...",
        "Done."
    ],

    title : 'Deploying Configuration...',
    width : 460,
    height: 180,
    modal : true,
    closable: false,
    resizable: false,
    autoShow: true,
    bodyPadding: 16,
    layout: 'anchor',
    defaults: {
        anchor: '100%'
    },

    items : [{
        xtype : 'component',
        plain : true,
        html  : '<p>Please wait for deployment to complete.</p>'
    }, {
        xtype : 'progressbar',
        itemId: 'bar',
        margin: '32 0 8 0'
    }],

    buttons: [{
        xtype : 'button',
        itemId: 'okButton',
        text  : 'OK',
        width : 80,
        disabled: true,
        handler: function(btn) {
        	SM.reloadAllWithStatuses();
        	SM.reloadAll();
        	btn.up('window').close();
        }
    }],

    initComponent: function(config) {
        this.callParent(arguments);
        this.setStage(0);
    },

    setStage: function(stage) {
        if (stage > this.stages || stage < 0) {
            Log.error("Invalid stage: {0}. Maximum is {1}.", stage, this.stages);
        } else if (stage == this.stages) {
            this.down('#okButton').enable();
        }
        this.stage = stage;
        this.down('#bar').updateProgress(
            (stage*1.0)/this.stages,
            this.stageMessages[stage],
            true
        );
    },

    setFinished: function() {
        this.setStage(this.stages);
    }
});

