
// MODEL: Log File
// Represents a log file for a Service, which can be viewed in the LogViewer
// interface.
// ----------------------------------------------------------------------------

Ext.define('SM.model.LogFile', {
    extend: 'Ext.data.Model',

    fields: [
        {name: 'name',        type: 'string'},
        {name: 'id',          type: 'string'},
        {name: 'serviceName', type: 'string'},
        {name: 'lastEntry',   type: Functions.timestampType()},
        {name: 'sizeInKb',    type: 'int'}
    ]
});

