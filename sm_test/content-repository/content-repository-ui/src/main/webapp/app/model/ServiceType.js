
// MODEL: Service Type
// Represents an abstract Service, which may be installed in any number of
// Nodes throughout the network.
// ----------------------------------------------------------------------------

Ext.define('SM.model.ServiceType', {
    extend: 'SM.model.ConfigItem',
    mixins: ['SM.mixin.PropertiesMixin'],
    fields: [
         {name: 'name',                 type: 'string'},
         {name: 'version',              type: 'string'},
         {name: 'className',            type: 'string'},
         {name: 'description',          type: 'string'},
         {name: 'maxInstances',         type: 'int'},
         {name: 'required',        		type: 'boolean'},
         {name: 'unmovable',       		type: 'boolean'},
         {name: 'properties',           type: Functions.childArrayType('SM.model.Property')},
         {name: 'serviceDependencies',  type: Functions.childArrayType('SM.model.ServiceDependency')},
         {name: 'resourceDependencies', type: Functions.childArrayType('SM.model.ResourceDependency')}
    ],

    getType: function () {return 'ServiceType';},

    getParent: function() {return null;},

    getChildren: function() {return [];},

    appendChild: function(child) {
        Log.warn("Attempted to add a child item to a ServiceType!");
    },

    removeChild: function(child) {
        Log.warn("Attempted to remove a child item from a ServiceType!");
    },

    getIconCls: function() {
        return 'ico-servicetype';
    },
    
    toJSON: function(){

        var propmap = {};
        var servDepMap = {};
        var resDepMap = {};
        
        Ext.each(this.get('properties'), function(prop) {
            propmap[prop.get('name')] = prop.get('value');
        });
        Ext.each(this.get('serviceDependencies'), function(servDep) {
        	servDepMap[servDep.get('name')] = servDep.get('value');
        });
        Ext.each(this.get('resourceDependencies'), function(resDep) {
        	resDepMap[resDep.get('name')] = resDep.get('value');
        });

        return {
            'name': this.get('name'),
            'version': this.get('version'),
            'className': this.get('className'),
            'description': this.get('description'),
            'maxInstances': this.get('maxInstances'),
            'required': this.get('required'),
            'unmovable': this.get('unmovable'),
            'properties': propmap,
            'serviceDependencies': servDepMap,
            'resourceDependencies': resDepMap
        };    	
    }
});

