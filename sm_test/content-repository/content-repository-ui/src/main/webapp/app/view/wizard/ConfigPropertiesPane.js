// VIEW: Config Properties Pane
// Allows the user to set basic configuration properties in the Wizard.
// ----------------------------------------------------------------------------

Ext.define('SM.view.wizard.ConfigPropertiesPane', {
    extend: 'SM.view.wizard.WizardPane',
    title : 'Define Configuration Properties',
    iconCls: 'lrgico-config',
    items : [{
        fieldLabel: 'General Properties',
        labelSeparator: '',
        defaults: {anchor: '100%',labelSeparator: ''},
        items: [{
            xtype: 'textfield',
            name : 'name',
            fieldLabel: 'Configuration Name',
            allowBlank: false,
            maskRe: /[^:]/
        }, {
            xtype: 'combobox',
            name: 'environment',
            fieldLabel: 'Environment',
            allowBlank: false,
            editable: false,
            displayField: 'name',
            valueField: 'name',
            store: Ext.create('Ext.data.Store', {
                fields: ['name'],
                data:[{
                    name: 'Production'
                }, { 
                    name: 'Pre-production'
                }, {
                    name: 'Testing'
                }]
            })
        }, {
            xtype: 'textarea',
            name : 'description',
            fieldLabel: 'Description'
        }]
    }, {
        fieldLabel: 'Number of Servers',
        labelSeparator: '',
        defaults:{labelSeparator: ''},
        itemId: 'distributedOptions',
        hidden: false,
        items: [{
            xtype: 'numberfield',
            fieldLabel: 'Servers',
            name: 'numServers',
            anchor: '40%',
            value: 1,
            minValue: 1,
            maxValue: 10, // FIXME: Change this to some sensible max number of servers
            allowDecimals: false
        }]
    }],

    initComponent: function() {
        this.callParent(arguments);
        var config = ConfigManager.config;
        this.down('[name=name]').setValue(config.get('name'));
        this.down('[name=description]').setValue(config.get('description'));
        if (config.get('clusters').length > 0) {
            var cluster = config.get('clusters')[0];
            this.down('[name=environment]').setValue(cluster.get('environment') || 'Production');
            var numServers = cluster.getChildrenWith({type: 'Server'}).length;
            if (numServers > 0) this.down('[name=numServers]').setValue(numServers);
        }
    },

    saveChanges: function() {
        var values = this.form.getFieldValues();
        var config = ConfigManager.config;
        config.set('name', values.name);
        config.set('description', values.description);
        // Get the first cluster, and create one if it does not exist.
        var cluster;
        if (config.get('clusters').length > 0) {
            cluster = config.get('clusters')[0];
            cluster.set('name', values.name);
            cluster.set('environment', values.environment);
            cluster.set('description', values.description);
        } else {
            cluster = Ext.create('SM.model.Cluster', {
                name: values.name,
                environment: values.environment,
                description: "Default cluster for " + values.name
            });
            config.appendChild(cluster);
        }
        cluster.normalize(config);
        // Get the number of servers for the cluster, and create them accordingly.
        // TODO: Support installation to a specific location for standalone installations.
        var numServers = 1;
//        if (values.installType == 'distributed')
        numServers = values.numServers;
//        else if (values.installType != 'standalone') {
//            Functions.errorMsg("You must select an installation type.");
//            return false;
//        }
        var existingServers = cluster.getChildrenWith({type: 'Server'}).length;
        Log.debug("numServers: " + numServers);
        Log.debug("existingServers: " + existingServers);
        if (numServers < existingServers) {
            var me = this;
            Ext.Msg.confirm(
                "Reset Config?",
                'You have selected a smaller number of servers than is' +
                    ' currently configured. All existing server configurations' +
                    ' will have to be deleted in order to reduce the number of' +
                    ' servers. Do you want to delete all existing server' +
                    ' configurations?',
                function(btn) {
                    if (btn == 'yes') {
                        me.serverIndex = 0;
                        me.serviceIndex = 0;
                        var servers = cluster.getChildrenWith({type: 'Server'});
                        Ext.each(servers, function(s) {
                            cluster.removeChild(s);
                        });
                        for (var i = 0; i < numServers; i++) {
                            cluster.appendChild(Ext.create('SM.model.Server', {name: 'Server ' + (i + 1)}));
                        }
                        SM.reloadAll();
                    }
                }
            );
        } else {
            for (var i = existingServers; i < numServers; i++) {
                cluster.appendChild(Ext.create('SM.model.Server', {name: 'Server ' + (i + 1)}));
                Log.debug("Server created.");
            }
        }
        return true;
    },

    getPrevPane: function() {
        var wizardPage = this.up('wizardpage');
        wizardPage.promptToExit();
        return null;
    },

    getNextPane: function() {
        var wizardPage = this.up('wizardpage');
        wizardPage.serverIndex = 1;
        return Ext.create('SM.view.wizard.DefineServersPane', {flex: 1, index: 1});
    },

    getNextPaneName: function() {
        return "Define Servers";
    }
});

