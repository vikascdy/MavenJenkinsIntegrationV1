
// MODEL: Product
// Represents a predefined set of Roles that can be used to create a complete
// configuration.
// ----------------------------------------------------------------------------

Ext.define('SM.model.Product', {
    extend: 'Ext.data.Model',

    fields: [
        {name: 'name',         type: 'string'},
        {name: 'version',      type: 'string'},
        {name: 'description',  type: 'string'},
        {name: 'releaseNotes', type: 'string'},
        {name: 'roles',        type: Functions.childArrayType('SM.model.Role')},
        {name: 'serviceTypes', type: Functions.childArrayType('SM.model.ServiceType')}
    ]
});

