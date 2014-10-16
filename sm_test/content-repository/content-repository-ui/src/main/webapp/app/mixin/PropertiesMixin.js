
// MIXIN: Properties
// Used for any model that has a list of Properties.
// ----------------------------------------------------------------------------

Ext.define('SM.mixin.PropertiesMixin', {
    
    getPropertyModel: function(name) {
        var propModel = null;
        Ext.each(this.get('properties'), function(prop) {
            if (prop.get('name') == name) {
                propModel = prop;
                return false;
            }
        });
        return propModel;
    },

    getProperty: function(name) {
        var prop = this.getPropertyModel(name);
        if (prop)
            return prop.getValue();
        else
            Functions.fmerr("No property named {0} for {1}.", name, this.toString());
    },

    setProperty: function(name, value) {
        var prop = this.getPropertyModel(name);
        if (prop)
            prop.setValue(value);
        else
            Functions.fmerr("No property named {0} for {1}.", name, this.toString());
    },

    importPropertiesFrom: function(other) {
        var me = this;
        Ext.each(other.get('properties'), function(prop) {
            me.get('properties').push(Ext.create('SM.model.Property', Functions.clone(prop.data)));
        });
    }

});

