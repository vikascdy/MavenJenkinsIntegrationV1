
// MODEL: Available Server
// Represents a single physical or virtual machine at a specific IP address
// which may not yet have the Service Manager Agent or any Nodes installed.
// These servers are candidates for becoming Servers on the Config tree.
// ----------------------------------------------------------------------------

Ext.define('SM.model.AvailableServer', {
    extend: 'Ext.data.Model',

    fields: [
        {name: 'hostname',  type: 'string'},
        {name: 'ipAddress', type: 'string'},
        {name: 'os',        type: 'string'},
        {name: 'arch',      type: 'string'},
        {name: 'cpuCores',  type: 'int'},
        {name: 'cpuMHz',    type: 'int'},
        {name: 'memMB',     type: 'int'}
    ]
});

