
// VIEW: Validation Pane
// Validates the current config file and displays the list of errors. Will only
// allow the user to proceed if there are no errors.
// ----------------------------------------------------------------------------

Ext.define('SM.view.wizard.ValidationPane', {
    extend: 'SM.view.wizard.WizardPane',
    title : 'Validate Configuration',
    iconCls: 'lrgico-validate',
    numErrors: -1,

    items: [
        {
            xtype: 'container',
            anchor:'100%',
            margin:'-30 0 0 15',
            layout:'hbox',
            items: [
                {
                flex:1,
                xtype:'tbspacer'
            },
                {
                xtype : 'button',
                itemId: 'validateAgain',
                text  : 'Validate Again',
                margin:'0 5 0 0',
                handler: function(btn) {
                    btn.up('wizardpane').doValidation();
                }
            }, {
                xtype : 'button',
                itemId: 'configEditor',
                text  : 'Switch to Config Editor',
                margin:'0 10 0 0',
                handler: function(btn) {

                    btn.up('wizardpage').promptToSwitch();
                }
            }]
        },
        {
        itemId: 'validationWorking',
        items: [{
            xtype: 'progressbar'
        }, {
            xtype: 'component',
            height: 64,
            html: "<h2 class='wizard-validation-banner'>Validating...</h2>"
        }]
    }, {
        itemId: 'validationDone',
        hidden: true,
        items: [{
            xtype: 'component',
            itemId: 'validationDoneDisplay',
            height: 64,
            data: {
                result: 'Not Started',
                numErrors: 0,
                numWarnings: 0
            },
            tpl: "<h2 class='wizard-validation-banner'>Validation {result}</h2>" +
                 "<p>Errors: {numErrors}</p>" +
                 "<p>Warnings: {numWarnings}</p>"
        }]
    }, {
        fieldLabel: 'Errors',
        anchor: '100%',
        itemId: 'errors',
            margin:'-13 0 0 0'
    }],
    
    doValidation: function() {
        this.down('#validationDone').hide();
        this.down('#validationWorking').show();
        this.down('progressbar').wait();
		
        ConfigManager.validateConfig(function() {
            var store = Ext.create('SM.store.FilteredErrorStore',
                                   {parentItem: ConfigManager.config});
            store.load({
                scope   : this,
                callback: function(records, operation, success) {
                    var numErrors = records.length;
                    var numWarnings = 0;
                    this.down('#validationWorking').hide();
                    this.down('#validationDone').show();
                    this.down('#validationDoneDisplay').update({
                        result: numErrors===0 ? 'Successful' : 'Failed',
                        numErrors: numErrors,
                        numWarnings: numWarnings
                    });
                    this.down('progressbar').reset();
                    this.numErrors = numErrors;
                    this.up('wizardpage').validationErrors = numErrors;
                    SM.reloadAll();

                }
            });
        }, this, true);
    },

    initComponent: function() {
        this.callParent(arguments);
        this.down('#errors').add({
            xtype: 'errorlist',
            object: ConfigManager.config,
            height: 360,
            preventHeader: true,
            padding:'2 0 0 0'
        });
        this.doValidation();
    },

    saveChanges: function() {
        if (this.numErrors !== 0) {
            Functions.errorMsg("Cannot proceed to deployment: there are still unresolved" +
                " validation errors. Resolve these errors before continuing.");
            return false;
        }
        return true;
    },

    getPrevPane: function() {
        var wizardPage = this.up('wizardpage');
        return Ext.create('SM.view.wizard.DefineServicesPane', {flex: 1, index: wizardPage.serviceIndex});
    },

    getNextPane: function() {
        return Ext.create('SM.view.wizard.DeploymentPane', {flex: 1});
    },

    getNextPaneName: function() {
        return "Deploy to Server(s)";
    }
});

