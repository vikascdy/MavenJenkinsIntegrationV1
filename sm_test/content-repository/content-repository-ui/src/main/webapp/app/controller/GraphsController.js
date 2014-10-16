
// CONTROLLER: Graphs
// Manages system-resource graphs for Nodes and Servers.
// ----------------------------------------------------------------------------

Ext.define('SM.controller.GraphsController', {
    extend: 'Ext.app.Controller',
    
    stores: ['GraphStore'],
    models: ['GraphPoint'],
    
    views: [
        'graphs.CpuGraph',
        'graphs.MemoryGraph'
    ]
});

