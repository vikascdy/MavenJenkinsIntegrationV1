
// MODEL: Service
// Represents a specific Service running on a Node.
// ----------------------------------------------------------------------------

Ext.define('SM.model.Service', {
    extend: 'SM.model.ConfigItem',
    mixins: ['SM.mixin.PropertiesMixin', 'SM.mixin.StatusControlMixin'],

    startUrl: JSON_URL + '/services.start',
    stopUrl : JSON_URL + '/services.stop',
    
    fields: [
         {name: 'id',             type: 'string', persist: false},
         {name: 'name',           type: 'string'},
         {name: 'description',    type: 'string'},
         {name: 'serviceName',    type: 'string'},
         {name: 'serviceVersion', type: 'string'},
         {name: 'status',         type: 'string', persist: false, defaultValue: 'unknown'},
         {name: 'reqServices',    type: 'auto'}, // Should be a map of ServiceDependency names to Service IDs.
         {name: 'reqResources',   type: 'auto'}, // Should be a map of ResourceDependency names to Resource IDs.
         {name: 'properties',     type: Functions.childArrayType('SM.model.Property')}
    ],

    getType: function() {return 'Service';},

    getServiceType: function() {
        var product = this.getConfig().getProduct();
        var me=this;
        if (!product) {
            // ConfigManager.getCurrentProduct(function(prod){
                product=ConfigManager.product;
                Log.info(product);
                if (!product) {
                    Ext.Error.raise("No Product is associated with this Config;" +
                        " cannot determine Service Types.");
                } else {
                    me.serviceTypeList(product);
                }
            // });
        }
        return this.serviceTypeList(product);
       
    },
    
    serviceTypeList: function(product){
        var type = null;
        try {
            Ext.Array.some(product.get('serviceTypes'), function(t) {
                if ((t.get('name')    == this.get('serviceName')) &&
                    (t.get('version') == this.get('serviceVersion'))) {
                    type = t;
                    return true;
                }
                return false;
            }, this);
            return type;
        } catch(err) {
            return null;
        }
    },

    getServiceDependencies: function() {
        var services = this.get('reqServices');
        if (!services || services.constructor !== Object) {
            services = {};
            this.set('reqServices', services);
        }
        var type = this.getServiceType();
        if (!type) return [];
        var deps = [];
        Ext.each(type.get('serviceDependencies'), function(dep) {
            if (services.hasOwnProperty(dep.get('name'))) {
                var id = services[dep.get('name')];
                var invalid = false;
                var service = null;
                if (id) service = ConfigManager.searchConfigById(id);
                if (service && (service.getType() != 'Service' ||
                                service.get('serviceName') != dep.get('typeName') ||
                                service.get('serviceVersion') != dep.get('typeVersion'))) {
                    service = null;
                    invalid = true;
                }
                deps.push({
                    name: dep.get('name'),
                    typeName: dep.get('typeName'),
                    typeVersion: dep.get('typeVersion'),
                    service: service,
                    serviceId: id,
                    invalid: invalid
                });
            }
        }, this);
        return deps;
    },

    getResourceDependencies: function() {
        var resources = this.get('reqResources');
        if (!resources || resources.constructor !== Object) {
            resources = {};
            this.set('reqResources', resources);
        }
        var type = this.getServiceType();
        if (!type) return [];
        var deps = [];

        Ext.each(type.get('resourceDependencies'), function(dep) {
            if (resources.hasOwnProperty(dep.get('name'))) {
                var id = resources[dep.get('name')];
                var invalid = false;
                var res = null;
                if (id) res = ConfigManager.searchConfigById(id);
                if (res && ( res.getType() != 'Resource' ||
                             res.get('restype') != dep.get('type') )) {
                    res = null;
                    invalid = true;
                }

                deps.push({
                    name: dep.get('name'),
                    restype: dep.get('type'),
                    resource: res,
                    invalid: invalid
                });            
            } else {
                deps.push({
                    name: dep.get('name'),
                    restype: dep.get('type'),
                    resource: null
                });   
            }
        }, this);
        return deps;
    },
    
    isServiceRequired: function() {
        var type = this.getServiceType();
        return type.get('required');
    },
    
    isServiceUnmovable: function() {
        var type = this.getServiceType();
        return type.get('unmovable');
    },

    setServiceForDependency: function(depName, service) {
        var services = this.get('reqServices');
        if (!services || services.constructor !== Object) {
            services = {};
            this.set('reqServices', services);
        }
        if (!service) {
            if (services.hasOwnProperty(depName))
                delete services[depName];
            return;
        }
        service.shouldBeA('Service');
        var type = this.getServiceType();
        if (!type)
            Functions.fmerr("Unable to find ServiceType '{0}-{1}'.", this.get('serviceName'), this.get('serviceVersion'));
        var deps = Ext.Array.filter(type.get('serviceDependencies'),
            function(dep) {return dep.get('name') == depName;});
        if (deps.length < 1)
            Functions.fmerr("The Service '{0}' has no Service dependency named '{1}'.", this.get('name'), depName);
        var dep = deps[0];
        if (service.get('serviceName') != dep.get('typeName') ||
            service.get('serviceVersion') != dep.get('typeVersion'))
            Functions.fmerr("For Service dependency '{0}' on Service '{1}': Expected a Service of type" +
                "'{2}-{3}', but got a Service of type '{4}-{5}'.",
                depName, this.get('name'), dep.get('typeName'), dep.get('typeVersion'),
                service.get('serviceName'), service.get('serviceVersion'));
        services[depName] = service.getId();
        Log.debug("Filled dependency '{0}' for '{1}-{2}'.", depName, service.get('serviceName'), service.get('serviceVersion'));
    },
    
    setResourceTypeForDependency: function(serviceResourceName, resource) {
        var resources = this.get('reqResources');
        if (!resources || resources.constructor !== Object) {
            resources = {};
            this.set('reqResources', resources);
        }
        if (!resource) {
            if (resources.hasOwnProperty(serviceResourceName))
                delete resources[serviceResourceName];
            return;
        }
        var serviceType = this.getServiceType();
        if (!serviceType) {
            Functions.fmerr("Unable to find a ServiceType named '{0}'.", this.get('serviceName'));
        }
        var deps = Ext.Array.filter(serviceType.get('resourceDependencies'), function(dep) {
            return dep.get('name') == serviceResourceName;
        });
        if (deps.length != 1) {
            Functions.fmerr("The Service '{0}' has no Resource dependency named '{1}'.",
                    this.get('name'), serviceResourceName);
        }
        var dep = deps[0];
        if (resource.get('restype') != dep.get('type')) {
            Functions.fmerr("For Resource dependency '{0}' on Service '{1}': Expected a Resource of type" +
                    "'{2}', but got a Resource of type '{3}'.",
                    serviceResourceName, this.get('name'), dep.get('type'), resource.get('restype'));
        }

        resources[serviceResourceName] = resource.getId();
    },

    getChildren: function() {return [];},

    appendChild: function(child) {
        Log.warn("Attempted to add a child item to a Service!");
    },

    removeChild: function(child) {
        Log.warn("Attempted to remove a child item from a Service!");
    },

    moveToNode: function(node) {
        node.shouldBeA('Node');
        node.shouldNotAlreadyHave(this);
        var parent = this.getParent();
        if (!parent.isEditable())
            Ext.Error.raise("Cannot move a Service belonging to a Role.");
        if (!node.isEditable())
            Functions.fmerr("The Node '{0}' has the Role '{1}', and its Service list cannot be changed.",
                node.get('name'), node.get('roleName'));
        var addSuccessful = node.appendChild(this);
        if (addSuccessful && parent)
            parent.removeChild(this);
        SM.reloadAll();
        return addSuccessful;
    },

    askToDelete: function() {
        if (!this.getParent().isEditable()) {
            Functions.errorMsg("Cannot delete a Service belonging to a Role." +
                " Delete the Node containing the Service instead.");
            return false;
        }
        this.callParent(arguments);
    },

    showPropertiesWindow: function(tab) {
        Ext.widget('servicepropertieswindow', {service: this, activeTab: tab});
    },

    getContextMenu: function() {
        console.log(this);
        return Ext.create('SM.view.service.ServiceContextMenu', {service: this});
    },

    getInfoPane: function() {
        return Ext.create('SM.view.service.ServiceInfoPane', {service: this});
    },

    getIconCls: function() {
        return 'ico-service';
    },

    getErrors: function() {
        var errors = [];
        Ext.each(this.getServiceDependencies(), function(dep) {
            if (!dep.service || dep.invalid === true) {
                if (dep.invalid)
                    errors.push(this.newError('error', 'config', "The Service dependency '" + dep.name +
                        "' references an object that is not a valid Service."));
                else
                    errors.push(this.newError('error', 'config', 'Missing Service dependency: ' + dep.name));
            }
        }, this);
        Ext.each(this.getResourceDependencies(), function(dep) {
            if (!dep.resource || dep.invalid === true) {
                if (dep.invalid)
                    errors.push(this.newError('error', 'config', "The Resource dependency '" + dep.name +
                        "' references an object that is not a valid Resource."));
                else
                    errors.push(this.newError('error', 'config', 'Missing Resource dependency: ' + dep.name));
            }
        }, this);
        return errors;
    },

    toJSON: function() {

        var propmap = {};
        Ext.each(this.get('properties'), function(prop) {
            propmap[prop.get('name')] = prop.get('value');
        });
        var reqServices = this.get('reqServices');
        var reqResources = this.get('reqResources');
        return {
            'name': this.get('name'),
            'description': this.get('description'),
            'serviceName': this.get('serviceName'),
            'serviceVersion': this.get('serviceVersion'),
            'status': this.get('status'),
            'reqServices': reqServices === '' ? {} : reqServices,
            'reqResources': reqResources === '' ? {} : reqResources,
            'properties': propmap
        };
    }
});

