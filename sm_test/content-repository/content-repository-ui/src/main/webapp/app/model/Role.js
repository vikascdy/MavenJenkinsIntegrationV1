
// MODEL: Role
// Represents a predefined set of Services that can be installed on a Node.
// When a Node is assigned a Role, no other Services can be added to or
// removed from the Node.
// ----------------------------------------------------------------------------

Ext.define('SM.model.Role', {
    extend: 'Ext.data.Model',

    fields: [
        {name: 'name',           type: 'string'},
        {name: 'productName',    type: 'string'},
        {name: 'productVersion', type: 'string'},
        {name: 'serviceTypeIds', type: 'auto'}
    ],

    getProduct: function() {
        var store = Ext.getStore('ProductStore');
        var index = store.findBy(function(record) {
            return (record.get('name')    == this.get('productName')) &&
                   (record.get('version') == this.get('productVersion'));
        }, this);
        if (index < 0) return null;
        else           return store.getAt(index);
    },

    getServiceTypes: function() {
        Log.warn("Getting service types for role...");
        Log.warn(this);
        Log.warn("name: " + this.get('name') + ", productName: " + this.get('productName') + ", productVersion: " + this.get('productVersion'));
        var types = this.getProduct().get('serviceTypes');
        return Ext.Array.map(this.get('serviceTypeIds'), function(id) {
            var type = null;
            Ext.Array.some(types, function(t) {
                if ((t.get('name')    == id.name) &&
                    (t.get('version') == id.version)) {
                    type = t;
                    return true;
                }
                return false;
            });
            if (type) return type;
            else Functions.fmerr("The Service Type '{0}-{1}' specified in the Role '{2}' does not exist.",
                    id.name, id.version, this.get('name'));
        }, this);
    },

    getIconCls: function() {
        return 'ico-newnode';
    }
});

