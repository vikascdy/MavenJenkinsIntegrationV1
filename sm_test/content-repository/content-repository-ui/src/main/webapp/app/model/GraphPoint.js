
// MODEL: Graph Point
// Used to represent points on CPU and memory graphs for Nodes and Servers.
// ----------------------------------------------------------------------------

Ext.define('SM.model.GraphPoint', {
    extend: 'Ext.data.Model',
    fields: [
         {name: 'time',  type: 'int'},
         {name: 'value', type: 'float'}
    ]
});

