
// VIEW: Service Dependency Field
// A pseudo-form-field that allows the user to select a Service to fulfill
// another Service's dependency. It must be provided with a Service (for which
// the dependency is being fulfilled) and the name of a ServiceType (to specify
// the dependency).
// ----------------------------------------------------------------------------

Ext.define('SM.view.wizard.ServiceDependencyField', {
    extend: 'Ext.form.FieldContainer',
    alias : 'widget.servicedependencyfield',
    layout: 'hbox',
    service: null,
    serviceName: null,
    serviceVersion: null,
    dependencyName: null,
    
    getSelectedService: function() {
        return this.down('configitempicker').getItem();
    },

    initComponent: function() {
        var config = ConfigManager.config;
        var cluster = null;
        if (config.get('clusters').length > 0)
            cluster = config.get('clusters')[0];
        this.fieldLabel = this.dependencyName;
        this.items = [{
            xtype: 'configitempicker',
            flex: 1,
            includeNull: true,
            parentItem: cluster || config,
            searchCriteria: {
                type: 'Service',
                serviceName: this.serviceName,
                serviceVersion: this.serviceVersion
            }
        }];
        this.callParent(arguments);
        var serviceDeps = this.service.getServiceDependencies();
        var deps = Ext.Array.filter(serviceDeps, function(dep) {
            return dep.name == this.dependencyName;
        }, this);
        if (deps.length > 0) {
            this.down('configitempicker').setItem(deps[0].service||null);
        } else {
            Log.warn("No Service Dependency for Service '" + this.service.get('name') +
                "' named '" + this.dependencyName + "'.");
        }
    }
});

