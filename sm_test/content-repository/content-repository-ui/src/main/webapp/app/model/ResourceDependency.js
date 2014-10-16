
// MODEL: Resource Dependency
// Represents a named Resource dependency for a Service. It consists only of a
// name and a Resource Type.
// ----------------------------------------------------------------------------

Ext.define('SM.model.ResourceDependency', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'name', type: 'string'},
        {name: 'type', type: 'string'}
    ]
});

