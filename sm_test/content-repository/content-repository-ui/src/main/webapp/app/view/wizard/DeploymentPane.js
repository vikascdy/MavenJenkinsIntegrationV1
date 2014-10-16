
// VIEW: Deployment Pane
// Allows the user to deploy a finished configuration file and proceed to the
// Configuration Editor (ServiceManagerPage).
// ----------------------------------------------------------------------------

Ext.define('SM.view.wizard.DeploymentPane', {
    extend: 'SM.view.wizard.WizardPane',
    title : 'Deploy to Server(s)',
    iconCls: 'lrgico-deploy',

    items: [{
        itemId: 'preDeployment',
        items: [{
            xtype: 'component',
            html: "<p>Now that your configuration is complete, you can deploy it" +
                " to the specified server(s) with the below button. <b>Warning:" +
                " this may overwrite any existing installations performed through" +
                " this interface!</b></p>"
        }, {
            xtype: 'button',
            cls: 'big-button',
            margin: 8,
            text: 'Deploy Configuration',
            height: 32,
            width: 220,
            handler: function(btn) {
                btn.up('wizardpane').doDeployment();
            }
        }, {
            xtype: 'component',
            height: 64
        }, {
            xtype: 'component',
            html: "<p>Alternatively, if you do not want to deploy this configuration" +
                " yet, you can click the below button to switch to the advanced" +
                " Configuration Editor and make more in-depth modifications to this" +
                " configuration before deploying it.</p>"
        }, {
            xtype: 'button',
            cls: 'big-button',
            margin: 8,
            text: 'Switch to Config Editor',
            height: 32,
            width: 220,
            handler: function(btn) {
                btn.up('wizardpage').promptToSwitch();
            }
        }]
    }, {
        itemId: 'postDeployment',
        hidden: true,
        items: [{
            xtype: 'component',
            html: "<p>Your configuration has been deployed! Click the below button to" +
                " proceed to the advanced Configuration Editor, which will allow you" +
                " to fine-tune and monitor this deployment.</p>"
        }, {
            xtype: 'button',
            cls: 'big-button',
            margin: 8,
            text: 'Switch to Config Editor',
            height: 32,
            width: 220,
            handler: function(btn) {
            	location.href='#!/service/' + ConfigManager.config.get('name')+ '/' +ConfigManager.config.get('version')+ '/' + ConfigManager.config.get('active');
            }
        }]
    }],
    
    doDeployment: function() {
        ConfigManager.applyConfig(function() {
            this.down('#preDeployment').hide();
            this.down('#postDeployment').show();
            SM.disableUnsavedChanges=true;
            SM.reloadAll();
        }, this);
    },

    saveChanges: function() {
        return true;
    },

    getPrevPane: function() {
        return Ext.create('SM.view.wizard.ValidationPane', {flex: 1});
    },

    getNextPane: function() {
        this.up('wizardpage').promptToSwitch();
        return null;
    },

    getNextPaneName: function() {
        return "Switch to Config Editor";
    }
});

