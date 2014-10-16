
// MODEL: Resource
// An item in a Cluster that does not fall into the usual "server-node-service"
// hierarchy, such as a database or an email server.
// ----------------------------------------------------------------------------

Ext.define('SM.model.Resource', {
    extend: 'SM.model.ConfigItem',
    mixins: ['SM.mixin.PropertiesMixin'],
    fields: [
         {name: 'id',           type: 'string', persist: false},
         {name: 'name',         type: 'string'},
         {name: 'description',  type: 'string'},
         {name: 'restype',      type: 'string'},
         {name: 'status',       type: 'string', persist: false, defaultValue: 'unknown'},
         {name: 'properties',   type: Functions.childArrayType('SM.model.Property')}
    ],

    getType: function () {return 'Resource';},
    
    getChildren: function() {return [];},

    appendChild: function(child) {
        Log.warn("Attempted to add a child item to a Resource!");
    },

    removeChild: function(child) {
        Log.warn("Attempted to remove a child item from a Resource!");
    },

    moveToCluster: function(cluster) {
        cluster.shouldBeA('Cluster');
        cluster.shouldNotAlreadyHave(this);
        var parent = this.getParent();
        var addSuccessful = cluster.appendChild(this);
        if (addSuccessful && parent)
            parent.removeChild(this);
        return addSuccessful;
    },

    showPropertiesWindow: function(tab) {
        Ext.widget('resourcepropertieswindow', {resource: this, activeTab: tab});
    },

    getContextMenu: function() {
        return Ext.create('SM.view.resource.ResourceContextMenu', {resource: this});
    },

    getInfoPane: function() {
        return Ext.create('SM.view.resource.ResourceInfoPane', {resource: this});
    },

    getIconCls: function() {
        var restype = this.get('restype');
        var iconTable = {
            "Database": 'ico-database',
            "Email Server": 'ico-mailserver',
            "Artifact": 'ico-artifact',
            "Application Server": 'ico-appserver',
            "JMS Queue": 'ico-jmsqueue'
        };
        return iconTable[restype] || 'ico-resource';
    },

    toJSON: function() {
        var propmap = {};
        Ext.each(this.get('properties'), function(prop) {
            propmap[prop.get('name')] = prop.get('value');
        });
        return {
            'name': this.get('name'),
            'description': this.get('description'),
            'restype': this.get('restype'),
            'status': this.get('status'),
            'properties': propmap
        };
    }
});

