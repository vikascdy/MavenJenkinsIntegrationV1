
// VIEW: Define Services Pane
// A New Configuration Wizard pane that allows the user to assign properties
// fulfill dependencies for a Service.
// ----------------------------------------------------------------------------

Ext.define('SM.view.wizard.DefineServicesPane', {
    extend : 'SM.view.wizard.WizardPane',
    title  : 'Define Service',
    iconCls: 'lrgico-service',
    index  : null,
    service: null,

    items  : [{
    	fieldLabel: '',
    	labelSeparator: '',
        itemId: 'serviceDescription'
    },
    {
        fieldLabel: 'Service Properties',
        labelSeparator: '',
        itemId: 'serviceProperties'
    }, {
        fieldLabel: 'Dependencies',
        labelSeparator: '',
        itemId: 'dependencies',
        padding:'0',
        margin:'5 0 0 0',
        defaults: {
            xtype: 'fieldcontainer',
            labelAlign: 'top',
            labelSeparator: '',
            cls: 'formpage-category',
            labelCls: 'formpage-category-header',
            layout: 'form',
            anchor: '100%',
            bodyPadding:'0'
        },
        items: [{
            fieldLabel: 'Service Dependencies',
            itemId: 'serviceDependencies',
            helpText: "This service requires other services in order to " +
                " function. Use the dropdown boxes to select the specific" +
                " instances of this service's required services to connect to."
        }, {
            fieldLabel: 'Resource Dependencies',
            itemId: 'resourceDependencies',
            helpText: "This service requires external resources in order to" +
                " function. Use the dropdown boxes to select existing" +
                " resources, or use the buttons to the right of the boxes to" +
                " add new resources of the required type."
        }]
    }],

    initComponent: function() {
        var config = ConfigManager.config;
        if (this.index === null)
            Functions.errorMsg("The Define Service pane must be provided with a service index!");
        var cluster = config.get('clusters')[0];
        var services = cluster.getChildrenWith({type: 'Service'});        
        this.service = services[this.index-1];
        var serviceType= this.service.getServiceType();
        
        this.title = "Define Service (" + this.index + " / " + services.length + "): " + this.service.get("name") + ' - ' + this.service.get("serviceVersion");
        this.callParent(arguments);
        

        // Service Description
        this.down('#serviceDescription').add({        	
        	  xtype: 'displayfield',
              anchor:'100%',
              value:serviceType.get('description') ? serviceType.get('description') : 'No description available.'
        });

        // Service Properties Form
        this.down('#serviceProperties').add({
            xtype:  'propertiesform',
            object: this.service,
            fieldDefaults: {labelSeparator: ''},
            preventHeader: true,
            border: false,
            autoSave: false,
            bodyPadding:'0 0 0 0',
            margin:'3 0 0 0'
        });

        // Dependency Lists
        var hasServiceDeps = false;
        var hasResourceDeps = false;
        this.serviceDepFields  = [];
        this.resourceDepFields = [];
        var serviceDeps = this.service.getServiceDependencies();
        if (serviceDeps.length > 0) {
            hasServiceDeps = true;
            Ext.each(serviceDeps, function(dep) {
                var field = Ext.create('SM.view.wizard.ServiceDependencyField', {
                    service: this.service,
                    serviceName: dep.typeName,
                    serviceVersion: dep.typeVersion,
                    dependencyName: dep.name
                });
                this.down('#serviceDependencies').add(field);
                this.serviceDepFields.push(field);
            }, this);
        } else {
            //this.down('#serviceDependencies').hide();
        	// For some reason, hiding the Service Dependencies panel makes the
        	// Resource Dependencies panel disappear as well. Instead, we're
        	// going to remove the label from the empty panel, providing
        	// essentially the same effect.
        	this.down('#serviceDependencies').setFieldLabel("");
        }
        var resourceDeps = this.service.getResourceDependencies();
        if (resourceDeps.length > 0) {
            hasResourceDeps = true;
            Ext.each(resourceDeps, function(dep) {
                var field = Ext.create('SM.view.wizard.ResourceDependencyField', {
                    service: this.service,
                    resourceType: dep.restype,
                    dependencyName: dep.name
                });
                this.down('#resourceDependencies').add(field);
                this.resourceDepFields.push(field);
            }, this);
        } else {
            this.down('#resourceDependencies').hide();
        }
        if (!hasServiceDeps && !hasResourceDeps) {
            this.down('#dependencies').hide();
        }
    },

    saveChanges: function() {
        if (!this.down('propertiesform').form.isValid()) {
            Functions.errorMsg("One or more form values is invalid.");
            return false;
        }
        this.down('propertiesform').save();
        Ext.each(this.serviceDepFields, function(field) {
            this.service.setServiceForDependency(field.dependencyName, field.getSelectedService());
        }, this);
        Ext.each(this.resourceDepFields, function(field) {
            this.service.setResourceTypeForDependency(field.dependencyName, field.getSelectedResource());
        }, this);
        return true;
    },

    getPrevPane: function() {
        var wizardPage = this.up('wizardpage');
        if (this.index > 1) {
            wizardPage.serviceIndex--;
            return Ext.create('SM.view.wizard.DefineServicesPane', {flex: 1, index: wizardPage.serviceIndex});
        }
        return Ext.create('SM.view.wizard.DefineServersPane', {flex: 1, index: wizardPage.serverIndex});
    },

    getNextPane: function() {
        var wizardPage = this.up('wizardpage');
        var config = ConfigManager.config;
        var cluster = config.get('clusters')[0];
        var services = cluster.getChildrenWith({type: 'Service'});
        if (this.index < services.length) {
            wizardPage.serviceIndex++;
            return Ext.create('SM.view.wizard.DefineServicesPane', {flex: 1, index: wizardPage.serviceIndex});
        }
        return Ext.create('SM.view.wizard.ValidationPane', {flex: 1});
    },

    getNextPaneName: function() {
        var wizardPage = this.up('wizardpage');
        var config = ConfigManager.config;
        var cluster = config.get('clusters')[0];
        var services = cluster.getChildrenWith({type: 'Service'});
        if (this.index < services.length) {
            return Ext.String.format('Define Service {0} / {1}', wizardPage.serviceIndex+1, services.length);
        } else {
            return "Validate Configuration";
        }
    }
});

