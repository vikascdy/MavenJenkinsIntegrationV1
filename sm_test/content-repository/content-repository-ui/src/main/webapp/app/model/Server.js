
// MODEL: Server
// Represents a single physical or virtual machine at a specific IP address.
// A Server may belong to a Cluster, and may contain one or more Nodes.
// ----------------------------------------------------------------------------

Ext.define('SM.model.Server', {
    extend: 'SM.model.ConfigItem',
    fields: [
         {name: 'id',          type: 'string', persist: false},
         {name: 'name',        type: 'string'},
         {name: 'hostName',    type: 'string'},
         {name: 'description', type: 'string'},
         {name: 'pid',         type: 'string'},
         {name: 'ipAddress',   type: 'string'},
         {name: 'messagePort', type: 'string'},
         {name: 'status',      type: 'string', persist: false, defaultValue: 'unknown'},
         {name: 'nodes',       type: Functions.childArrayType('SM.model.Node')}
    ],

    getType: function() {return 'Server';},
    
    getServerPID : function(callback){
        var item=this;
        Ext.Ajax.request({
            url:'/json/Agent/default/getAgentPID',
            method:'GET',
            success : function(response){
                var respJson=Ext.decode(response.responseText);
                item.set('pid', respJson.pid);
                Ext.callback(callback,this,[]);
            },
            failure : function(response){
                item.set('pid', null);
                Ext.callback(callback,this,[]);
            }
        });
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

    appendChild: function(child) {
        child.shouldBeA('Node');
        this.shouldNotAlreadyHave(child);
        child.set('status', ('unassociated' == this.get('status')) ? 'unassociated' : 'new');
        this.data.nodes.push(child);
        child.normalize(this);
        return true;
    },

    removeChild: function(child) {
        var childName;
        if ('string' == typeof child)
            childName = child;
        else 
            childName = child.get('name');
        var nodes = this.data.nodes;
        for (var i=nodes.length-1; i>=0; i--) {
            if (nodes[i].get('name') == childName) {
                nodes.splice(i, 1);
                return true;
            }
        }
        return false;
    },

    getChildren: function() {return this.get('nodes');},

    spawnNode: function(name, port, logLevel, description,jvmProperties) {
        var server = this;
        var node = Ext.create('SM.model.Node', {
            serverName: server.get('name'),
            id: server.getId() + ':' + name,
            name: name,
            port: port,
            description: description,
            logLevel: logLevel,
            status: 'new',
            jvmProperties: jvmProperties ? jvmProperties : [],
            services: []
        });
        if (server.appendChild(node))
            return node;
        else
            return null;
    },

    getSystemInfo: function(property, callback) {
        var server = this;

        if (server.get('status') != 'offline' && server.get('status') != 'new') {
            Functions.jsonCommand("UI Service", "servers.systemInfo", {
                'id': server.getId(),
                'property': property
            }, {
                success: function(response){
                    callback(response);
                }
            });
        }
    },

    showPropertiesWindow: function(tab) {
        Ext.widget('serverpropertieswindow', {server: this, activeTab: tab || 0});
    },

    getContextMenu: function() {
        return Ext.create('SM.view.server.ServerContextMenu', {server: this});
    },

    getInfoPane: function() {
        return Ext.create('SM.view.server.ServerInfoPane', {server: this});
    },

    getIconCls: function() {
        return 'ico-server';
    },

    getErrors: function() {
        var errors = [];
        if (this.get('status') == 'offline') {
            errors.push(this.newError('error', 'status', 'Server is offline.'));
        } else if (this.get('status') == 'offline') {
            errors.push(this.newError('error', 'status', 'Server status cannot be determined.'));
        }
        return errors;
    },

    toJSON: function() {
        return {
            'name': this.get('name'),
            'hostName': this.get('hostName'),
            'description': this.get('description'),
            'ipAddress': this.get('ipAddress'),
            'messagePort': this.get('messagePort'),
            'status': this.get('status'),
            'nodes': Ext.Array.map(this.get('nodes'), function(n) {return n.toJSON();})
        };
    }
});

