
// MODEL: Resource Type
// Represents a general type of Resource. Every Resource created must be of one
// of the defined types.
// ----------------------------------------------------------------------------

Ext.define('SM.model.ResourceType', {
    extend: 'Ext.data.Model',
    mixins: ['SM.mixin.PropertiesMixin'],
    fields: [
         {name: 'name',        type: 'string'},
         {name: 'description', type: 'string'},
         {name: 'properties',  type: Functions.childArrayType('SM.model.Property')}
    ]
});

