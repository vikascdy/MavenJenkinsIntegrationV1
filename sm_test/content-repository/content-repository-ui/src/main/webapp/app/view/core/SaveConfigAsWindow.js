// VIEW: Save As Config Window
// Displayed when the user saves a config file. Allows the user to change the
// name of the file and view all existing config files.
// ----------------------------------------------------------------------------

Ext.define('SM.view.core.SaveConfigAsWindow', {
    extend: 'Ext.window.Window',
    alias : 'widget.saveconfigaswindow',
    title: 'Save Configuration As',
    iconCls: 'ico-file',
    width: 450,
    height: 330,
    autoShow: true,
    modal: true,
    onSuccessfulSave: null,
    resizable:false,
    buttons: [{
        text: 'Save',
        handler: function(btn) {
            var form = btn.up('saveconfigaswindow').down('configform');
            if (!form.getForm().isValid()) {
                Functions.errorMsg("One or more of the form values is invalid or missing.");
            } else {
                // Update the configuration's name/version.
                btn.up('saveconfigaswindow').down('configform').save();
                // Check for an existing configuration with the same name.
                var name = ConfigManager.config.get('name');
                var version = ConfigManager.config.get('version');
                var preExistingConfig = Ext.getStore('SavedConfigStore').findBy(function(record, id) {
                    return record.get('name') == name && record.get('version') == version;
                });
                // Save the configuration.
                var doSave = function() {
                    ConfigManager.saveConfig(function() {
                        // After saving, call the 'onSuccessfulSave' callback, if it exists.
                        Ext.callback(btn.up('saveconfigaswindow').onSuccessfulSave, this, [name, version]);
                        btn.up('saveconfigaswindow').close();
                        SM.page.unsavedChanges = false;
                    });
                };
                if (preExistingConfig != -1) {
                    Ext.Msg.confirm(
                        "Overwrite Config?",
                        'A configuration file with the name "' + name + '" and version "' + version + '" already exists.' +
                            ' Do you want to overwrite it?',
                        function(btn) {
                            if (btn == 'yes') doSave();
                        }
                    );
                } else {
                    doSave();
                }
            }
        }
    }, {
        text: 'Reset',
        handler: function(btn) {
            btn.up('saveconfigaswindow').down('configform').getForm().setValues(ConfigManager.config.data);
        }
    }, {
        text: 'Cancel',
        handler: function(btn) {
            btn.up('saveconfigaswindow').close();
        }
    }],

    initComponent: function() {
        Ext.getStore('SavedConfigStore').load();
        var me = this;
        this.items = [{
            xtype:'fieldset',
            height:40,
            layout: 'anchor',
            defaults: {
                anchor: '100%'
            },
            border:false,
            items:{
                xtype:'radiogroup',
                items:[{
                    boxLabel:'New Version',
                    name:'saveAsType',
                    inputValue: 'saveVersion',
                    checked: 'true'
                },{
                    boxLabel:'New Configuration',
                    name:'saveAsType',
                    inputValue: 'saveConfig'
                }],
                listeners: {
                    afterRender : function(radioGrp, value) {
                        var pane = radioGrp.up('saveconfigaswindow').down('configform');
                        pane.child('textfield').setDisabled(true);
                    },
                    change: function(radioGrp, value) {
                        var pane = me.down('configform');
                        var button = this.getValue().saveAsType;
                        var configName = pane.child('textfield');
                        var configNameValue = configName.getValue();
                        if (button == 'saveConfig') {
                            configName.setDisabled(false);
                            configName.setValue('Copy of ' + configNameValue);
                        }
                        else
                        if (button == 'saveVersion') {
                            configName.setDisabled(true);
                            pane.getForm().setValues(ConfigManager.config.data);
                        }
                    }
                }
            }
        }, {
            xtype: 'configform',
            bodyPadding:15,
            border: 0,
            height: 250,
            config: ConfigManager.config
        }];
        this.callParent(arguments);
    }
});

