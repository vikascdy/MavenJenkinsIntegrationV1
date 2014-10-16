
// MODEL: Service Dependency
// Represents a named Service dependency for a Service. The required
// ServiceType is identified by a name and a version.
// ----------------------------------------------------------------------------

Ext.define('SM.model.ServiceDependency', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'name', type: 'string'},
        {name: 'typeName', type: 'string'},
        {name: 'typeVersion', type: 'string'}
    ]
});

