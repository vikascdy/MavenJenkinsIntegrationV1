
// MODEL: Config
// Represents a configuration file, the highest level of organization in the
// Service Manager.
// ----------------------------------------------------------------------------

Ext.define('SM.model.Config', {
    extend: 'SM.model.ConfigItem',
    fields: [
         {name: 'id',             type: 'string'},
         {name: 'name',           type: 'string'},
         {name: 'version',        type: 'version'},
         {name: 'description',    type: 'string'},
         {name: 'timestamp',      type: 'int'},
         {name: 'productName',    type: 'string'},
         {name: 'productVersion', type: 'string'},
         {name: 'clusters',       type: Functions.childArrayType('SM.model.Cluster')}
    ],

    getType: function() {return 'Config';},

    getIdSegment: function() {return this.get('name')+'-'+this.get('version');},

    timestamp: function() {
        this.set('timestamp', Date.now());
    },

    appendChild: function(child) {
        child.shouldBeA('Cluster');
        this.shouldNotAlreadyHave(child);
        this.data.clusters.push(child);
        this.normalize();
        child.normalize(this);
        return true;
    },

    removeChild: function(child) {
        var childName;
        if ('string' == typeof child)
            childName = child;
        else 
            childName = child.get('name');
        var clusters = this.data.clusters;
        for (var i=clusters.length-1; i>=0; i--) {
            if (clusters[i].get('name') == childName) {
                clusters.splice(i, 1);
                return true;
            }
        }
        return false;
    },

    getChildren: function() {
        return this.get('clusters');
    },

    getIconCls: function() {
        return 'ico-config';
    },

    getProduct: function() {
        var store = Ext.getStore('ProductStore');
        var index = store.findBy(function(record) {
            return (record.get('name')    == this.get('productName')) &&
                   (record.get('version') == this.get('productVersion'));
        }, this);
        if (index < 0) return null;
        else           return store.getAt(index);
    },

    getRoles: function() {
        var product = this.getProduct();
        if (product) return product.get('roles');
        else         return [];
    },

    toJSON: function() {
        return {
            'name': this.get('name'),
            'version': this.get('version'),
            'description': this.get('description'),
            'timestamp': this.get('timestamp'),
            'productName': this.get('productName'),
            'productVersion': this.get('productVersion'),
            'clusters': Ext.Array.map(this.get('clusters'), function(c) {return c.toJSON();})
        };
    }
});

