
// VIEW: Define Servers Pane
// A New Configuration Wizard pane that allows the user to select Roles for a
// Server.
// ----------------------------------------------------------------------------

Ext.define('SM.view.wizard.DefineServersPane', {
    extend: 'SM.view.wizard.WizardPane',
    title : 'Define Server',
    iconCls: 'lrgico-server',
    index : null,
    server: null,
    
    items : [{
        fieldLabel: 'Server Properties',
        labelSeparator: '',
        itemId: 'serverProperties'
    }, {
        fieldLabel: 'Server Roles',
        labelSeparator: '',
        itemId: 'serverRoles',
        margin:'-13 0 0 0',
        helpText: "Roles define lists of services that can be installed on" +
            " servers. Check the boxes to the left of the roles that you" +
            " want to assign to this server."
    }],

    initComponent: function() {
        var config = ConfigManager.config;
        if (this.index === null)
            Functions.errorMsg("The Define Server pane must be provided with a server index!");
        var cluster = config.get('clusters')[0];
        var servers = cluster.getChildrenWith({type: 'Server'});
        this.title = "Define Server " + this.index + " / " + servers.length;
        this.server = servers[this.index-1];
        this.callParent(arguments);
        this.down('#serverProperties').add({
            xtype:  'serverform',
            server: this.server,
            fieldDefaults: {labelSeparator: ''}
        });
        this.down('#serverRoles').add({
            xtype:  'roletree',
            server: this.server,
            height: 200,
            padding:'2 0 0 0'

        });
    },

    saveChanges: function() {
        if (!this.down('serverform').form.isValid()) {
            Functions.errorMsg("One or more form values is invalid.");
            return false;
        }
        this.down('serverform').save();
        return true;
    },

    getPrevPane: function() {
        var wizardPage = this.up('wizardpage');
        if (this.index > 1) {
            wizardPage.serverIndex--;
            return Ext.create('SM.view.wizard.DefineServersPane', {flex: 1, index: wizardPage.serverIndex});
        }
        return Ext.create('SM.view.wizard.ConfigPropertiesPane', {flex: 1});
    },

    getNextPane: function() {
        var wizardPage = this.up('wizardpage');
        var config = ConfigManager.config;
        var cluster = config.get('clusters')[0];
        var servers = cluster.getChildrenWith({type: 'Server'});
        if (this.index < servers.length) {
            wizardPage.serverIndex++;
            return Ext.create('SM.view.wizard.DefineServersPane', {flex: 1, index: wizardPage.serverIndex});
        }
        var services = cluster.getChildrenWith({type: 'Service'});
        if (services.length === 0) {
            Functions.errorMsg("You have not yet assigned any roles to any servers. Assign at least one role to at least one server before continuing.");
            return null;
        }
        wizardPage.serviceIndex = 1;
        return Ext.create('SM.view.wizard.DefineServicesPane', {flex: 1, index: 1});
    },

    getNextPaneName: function() {
        var wizardPage = this.up('wizardpage');
        var config = ConfigManager.config;
        var cluster = config.get('clusters')[0];
        var servers = cluster.getChildrenWith({type: 'Server'});
        if (this.index < servers.length) {
            return Ext.String.format("Define Server {0} / {1}", wizardPage.serverIndex+1, servers.length);
        } else {
            return "Define Services";
        }
    }
});

