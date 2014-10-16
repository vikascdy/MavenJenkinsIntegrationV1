
// MODEL: Node
// Represents a single JVM instance on a Server, running the Service Manager
// system. It contains a set of Services. No relation to the Node.js framework.
// ----------------------------------------------------------------------------

Ext.define('SM.model.Node', {
    extend: 'SM.model.ConfigItem',
    //mixins: ['SM.mixin.StatusControlMixin'],

    //startUrl: JSON_URL + '/nodes.start',
    //stopUrl : JSON_URL + '/nodes.stop',
    
    fields: [
         {name: 'id',            type: 'string', persist: false},
         {name: 'name',          type: 'string'},
         {name: 'description',   type: 'string'},
         {name: 'pid',           type: 'string'},
         {name: 'port',          type: 'string'},
         {name: 'sshPort',       type: 'string'},
         {name: 'messagePort',   type: 'string'},
         {name: 'roleName',      type: 'string'},
         {name: 'logLevel',      type: 'string'},
         {name: 'status',        type: 'string', persist: false, defaultValue: 'unknown'},
         {name: 'jvmProperties', type: 'auto'},
         {name: 'services',      type: Functions.childArrayType('SM.model.Service')},
         {name: 'jobs',          type: Functions.childArrayType('SM.model.Job')}
    ],

    getType: function() {return 'Node';},
    
    getNodePID : function(callback){
        var item=this;
        Ext.Ajax.request({
            url:'/json/Agent/default/getPIDForNode',
            method:'POST',
            params:{
                data : '{"nodeName":'+item.get('name')+'}'
            },
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

    getChildren: function() {
        return this.get('services');
    },

    moveToServer: function(server) {
        server.shouldBeA('Server');
        server.shouldNotAlreadyHave(this);
        var parent = this.getParent();
        var addSuccessful = server.appendChild(this);
        if (addSuccessful && parent)
            parent.removeChild(this);
        return addSuccessful;
    },

    addServiceFromType: function(type, name) {
        type.shouldBeA('ServiceType');
        name = name || ConfigManager.getNextAvailableName(type);
        var configNode = ConfigManager.searchConfigById(this.getId());
        var service = Ext.create('SM.model.Service', {
            id: this.getId() + ':' + name,
            name: name,
            serviceName: type.get('name'),
            serviceVersion: type.get('version'),
            status: 'new',
            nodeName: this.get('name')
        });
        service.importPropertiesFrom(type);
        return this.appendChild(service);
    },

    addJobFromType: function(type, name) {
        type.shouldBeA('JobType');
        name = name || ConfigManager.getNextAvailableName(type);
        var configNode = ConfigManager.searchConfigById(this.getId());
        var job = Ext.create('SM.model.Job', {
            id: this.getId() + ':' + name,
            name: name,
            description: type.get('description'),
            jobName: type.get('name'),
            jobVersion: type.get('version'),
            status: 'Disabled',
            nodeName: this.get('name')
        });
//        service.importPropertiesFrom(type);
        this.addJob(job);
        return job;
    },
    
    addJob: function(child) {
        child.shouldBeA('Job');
        this.shouldNotAlreadyHave(child);
        this.data.jobs.push(child);
        child.normalize(this);
        return true;
    },
    
    appendChild: function(child) {
        child.shouldBeA('Service');
        this.shouldNotAlreadyHave(child);
        child.set('status', ('unassociated' == this.get('status')) ? 'unassociated' : 'new');
        this.data.services.push(child);
        child.normalize(this);
        return true;
    },

    removeChild: function(child) {
        var childName;
        if ('string' == typeof child)
            childName = child;
        else 
            childName = child.get('name');
        var services = this.data.services;
        for (var i=services.length-1; i>=0; i--) {
            if (services[i].get('name') == childName) {
                services.splice(i, 1);
                return true;
            }
        }
        return false;
    },

    rename: function(newName) {
        this.set('name', newName);
        this.normalize();
        return true;
    },

    isEditable: function() {
        return !this.get('roleName');
    },

    applyRole: function(role) {
        this.data.services = [];
        Ext.each(role.getServiceTypes(), function(t) {this.addServiceFromType(t);}, this);
        this.data.roleName = role.get('name');
    },

    getSystemInfo: function(property, callback) {
        var node = this;
        var status = node.get('status');
        if (status != 'offline' && status != 'new' && status != 'unassociated') {
            Functions.jsonCommand("UI Service", "nodes.systemInfo", {
                'id': node.getId(),
                'property': property
            }, {
                success: function(response){
                    callback(response);
                }
            });
        }
    },

    showPropertiesWindow: function(tab) {
        Ext.widget('nodepropertieswindow', {node: this, activeTab: tab || 0});
    },

    getContextMenu: function() {
        return Ext.create('SM.view.node.NodeContextMenu', {node: this});
    },

    getInfoPane: function() {
        return Ext.create('SM.view.node.NodeInfoPane', {node: this});
    },

    getIconCls: function() {
        return 'ico-node';
    },

    getRole: function() {
        var rn = this.get('roleName');
        if (!rn) return null;
        var roles = this.getConfig().getRoles();
        var role = null;
        Ext.each(roles, function(r) {
            if (r.get('name') == rn) {
                role = r;
                return false;
            }
        });
        return role;
    },
    
    getLogLevel: function() {
        return this.get('logLevel');
    },

    toJSON: function() {
        return {
            'name': this.get('name'),
            'description': this.get('description'),
            'port': this.get('port'),
            'sshPort': this.get('sshPort'),
            'messagePort': this.get('messagePort'),
            'roleName': this.get('roleName'),
            'status': this.get('status'),
            'logLevel': this.get('logLevel'),
            'jvmProperties': this.get('jvmProperties'),
            'services': Ext.Array.map(this.get('services'), function(s) {return s.toJSON();}),
            'jobs': Ext.Array.map(this.get('jobs'), function(s) {return s.toJSON();})
        };
    }
});

