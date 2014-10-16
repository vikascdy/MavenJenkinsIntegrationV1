
// MODEL: Cluster
// Represents a cluster of Servers that can communicate with each other.
// Services can only communicate with other Services and Resources within the
// same Cluster.
// ----------------------------------------------------------------------------

Ext.define('SM.model.Cluster', {
    extend: 'SM.model.ConfigItem',
    fields: [
         {name: 'id',                type: 'string', persist: false},
         {name: 'name',              type: 'string'},
         {name: 'description',       type: 'string'},
         {name: 'environment',       type: 'string'},
         {name: 'resources',         type: Functions.childArrayType('SM.model.Resource')},
         {name: 'servers',           type: Functions.childArrayType('SM.model.Server')}
    ],

    getType: function() {return 'Cluster';},

    appendChild: function(child) {
        child.shouldBeA(['Server', 'Resource']);
        this.shouldNotAlreadyHave(child);
        child.set('status', ('unassociated' == child.status) ? 'unassociated' : 'new');
        switch (child.getType()) {
            case 'Server':
                this.data.servers.push(child);
                break;
            case 'Resource':
                this.data.resources.push(child);
                break;
        }
        child.normalize(this);
        return true;
    },

    removeChild: function(child) {
        var childName;
        if ('string' == typeof child)
            childName = child;
        else 
            childName = child.get('name');
        var servers = this.data.servers;
        for (var i=servers.length-1; i>=0; i--) {
            if (servers[i].get('name') == childName) {
                servers.splice(i, 1);
                return true;
            }
        }
        var resources = this.data.resources;
        for (var j=resources.length-1; j>=0; j--) {
            if (resources[j].get('name') == childName) {
                resources.splice(j, 1);
                return true;
            }
        }
        return false;
    },

    spawnServer: function(name, hostName, ipAddress, messagePort, description) {
        var cluster = this;
        var server = Ext.create('SM.model.Server', {
            id: cluster.getId() + ':' + name,
            name: name,
            hostName: hostName,
            ipAddress: ipAddress,
            messagePort: messagePort,
            description: description,
            status: 'unassociated',
            nodes: []
        });
        return cluster.appendChild(server);
    },

    spawnResource: function(name, restype, description, propertyMap) {
        var cluster = this;
        var resTypeStore = Ext.getStore('ResourceTypeStore');
        if (!resTypeStore)
            Ext.Error.raise("Could not access ResourceTypeStore.");
        var restypeModel = null;
        resTypeStore.data.each(function(rt) {
            if (rt.get('name') == restype) {
                restypeModel = rt;
                return false;
            }
        });
        if (!restypeModel)
            Ext.Error.raise("Could not find a ResourceType named " + restype + ".");
        var resource = Ext.create('SM.model.Resource', {
            id: cluster.getId() + ':' + name,
            name: name,
            restype: restype,
            description: description,
            status: 'new',
            properties: []
        });
        resource.importPropertiesFrom(restypeModel);
        for (var k in propertyMap) {
            if (propertyMap.hasOwnProperty(k)) {
                resource.setProperty(k, propertyMap[k]);
            }
        }
        return cluster.appendChild(resource);
    },

    getChildren: function() {
        return this.get('servers').concat(this.get('resources'));
    },

    showPropertiesWindow: function(tab) {
        Ext.widget('clusterpropertieswindow', {cluster: this, activeTab: tab || 0});
    },

    getContextMenu: function() {
        return Ext.create('SM.view.cluster.ClusterContextMenu', {cluster: this});
    },

    getInfoPane: function() {
        return Ext.create('SM.view.cluster.ClusterInfoPane', {cluster: this});
    },

    getIconCls: function() {
        return 'ico-cluster';
    },

    toJSON: function() {
        return {
            'name': this.get('name'),
            'description': this.get('description'),
            'environment': this.get('environment'),
            'resources': Ext.Array.map(this.get('resources'), function(r) {return r.toJSON();}),
            'servers': Ext.Array.map(this.get('servers'), function(s) {return s.toJSON();})
        };
    }
});

