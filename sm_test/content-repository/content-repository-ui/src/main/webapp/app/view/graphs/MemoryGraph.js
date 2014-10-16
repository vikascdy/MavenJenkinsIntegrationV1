
// VIEW: Memory Graph
// A Chart showing the memory usage of a specific Node or Server over time.
// ----------------------------------------------------------------------------

Ext.require('Ext.chart.*');

Ext.define('SM.view.graphs.MemoryGraph', {
    extend: 'Ext.chart.Chart',
    alias : 'widget.memgraph',
    
    width: 400,
    height: 160,
    //animate: true,
    shadow: false,
    theme: 'Blue',
    axes: [{
        type: 'Numeric',
        minimum: 0.0,
        maximum: 100.0,
        position: 'left',
        fields: ['value']
    }, {
        type: 'Numeric',
        minimum: 0,
        maximum: 50,
        position: 'bottom',
        fields: ['time']
    }],

    series: [{
        type: 'line',
        axis: 'left',
        xField: 'time',
        yField: 'value',
        fill:'true',
        markerConfig: {
            size: 0,
            radius: 0
        }
    }],

    source: null,
    systemProperty: 'memSnapshots',
    initComponent: function() {
        if (ConfigManager.usingDefaultConfig) {
            this.store = Ext.create('SM.store.GraphStore');
            this.callParent(arguments);
            this.reload();
        }
    },

    lastLoadSuccessful: function() {
        return this.store.lastLoadSuccessful;
    },
    
    reload: function() {
        if (ConfigManager.usingDefaultConfig) {
            if (this.source !== null && this.source !== undefined)
                this.store.reload(this.source, this.systemProperty);
            else
                Log.warn("Loaded a Memory Graph with no source data!");
        }
    }
});

