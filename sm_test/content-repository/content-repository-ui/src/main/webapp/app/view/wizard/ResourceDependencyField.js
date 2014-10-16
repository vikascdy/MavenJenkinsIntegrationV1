
// VIEW: Resource Dependency Field
// A pseudo-form-field that allows the user to select a Resource to fulfill
// a Service's dependency. It also provides a button that allows users to add
// new Resources. It must be provided with a Service (for which the dependency
// is being fulfilled), the name of a ResourceType, and the name of the
// dependency.
// ----------------------------------------------------------------------------

Ext.define('SM.view.wizard.ResourceDependencyField', {
    extend: 'Ext.form.FieldContainer',
    alias : 'widget.resourcedependencyfield',
    layout: 'hbox',
    service: null,
    resourceType: null,
    dependencyName: null,

    getSelectedResource: function() {
        return this.down('configitempicker').getItem();
    },
    
    initComponent: function() {
        var config = ConfigManager.config;
        var cluster = null;
        if (config.get('clusters').length > 0)
            cluster = config.get('clusters')[0];
        this.fieldLabel = this.dependencyName;
        var resType = this.resourceType;
        this.items = [{
            xtype: 'configitempicker',
            flex: 1,
            includeNull: true,
            parentItem: cluster || config,
            searchCriteria: {
                type: 'Resource',
                restype: this.resourceType
            }
        }, {
            xtype: 'button',
            width: 26,
            padding: '4 2 4 2',
            iconCls: 'ico-add',
            tooltip: 'Create New Resource',
            handler: function(btn) {
                var nrw = Ext.widget('newresourcewindow', {
                    selector: btn.up('resourcedependencyfield').down('configitempicker')
                });

                nrw.down('#create').setText('Save and Select');
                var cbox = nrw.down('#restypeSelector');
                cbox.select(resType);
            }
        }];
        this.callParent(arguments);
        var resourceDeps = this.service.getResourceDependencies();
        var deps = Ext.Array.filter(resourceDeps, function(dep) {
            return dep.name == this.dependencyName;
        }, this);
        if (deps.length > 0) {
            this.down('configitempicker').setItem(deps[0].resource);
        } else {
            Log.warn("No Resource Dependency for Service '" + this.service.get('name') +
                "' named '" + this.dependencyName + "'.");
        }
    }
});

