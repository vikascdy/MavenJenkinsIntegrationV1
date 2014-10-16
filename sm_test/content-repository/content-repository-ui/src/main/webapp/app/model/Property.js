
// MODEL: Property
// Represents a property for a service or resource, as defined in a
// configuration XML file. It is a name-value pair, but restricted by a type
// and possibly by a regex or list of valid values.
// ----------------------------------------------------------------------------

Ext.define('SM.model.Property', {
    extend: 'Ext.data.Model',
    fields: [
         {name: 'name',       type: 'string'},
         {name: 'value',      type: 'string'},
         {name: 'description',type: 'string'},
         {name: 'type',       type: 'string'},
         {name: 'editable',   type: 'boolean'},
         {name: 'regex',      type: 'string'},
         {name: 'listValues', type: 'auto'}
    ],

    createListValuesStore: function() {
        var listValues = this.get('listValues');
        return Ext.create('Ext.data.Store', {
            fields: ['value'],
            data: Ext.Array.map(listValues, function(v) {
                return {value: v};
            }, this)
        });
    },

    getValue: function() {
        return this.get('value');
    },

    setValue: function(value) {
        var error = false;
        value = value.toString();
        switch (this.get('type')) {
        case "string":
            if (this.get('regex')) {
                var regex = new RegExp(this.get('regex'));
                if (!value.match(regex))
                    error = true;
            }
            break;
        case "boolean":
            if (value != "true" && value != "false")
                error = true;
            break;
        case "list":
            if (!Ext.Array.contains(this.get('listValues'), value))
                error = true;
            break;
        }
        if (error)
            Functions.fmerr("Invalid value '{0}' for property '{1}'.", value, this.get("name"));
        this.set("value", value);
    }
});

