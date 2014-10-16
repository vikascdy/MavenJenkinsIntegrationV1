
// MIXIN: Auto-update Graphs
// Code used in several different views to automatically update CPU/memory
// graphs.
// ----------------------------------------------------------------------------

Ext.define('SM.mixin.AutoUpdateGraphsMixin', {
    updateInterval: 1000,
    updatingGraphs: false,

    initGraphs: function() {
        this.on('render', function(cmp, eOpts) {
                Log.debug("Updating graphs...");
                cmp.updatingGraphs = true;
                setTimeout(function() {cmp.updateGraphs();}, cmp.updateInterval);
            });
        this.on('beforeDestroy', function(cmp, eOpts) {
                cmp.updatingGraphs = false;
            });
    },
    
    updateGraphs: function() {
        var inst=this; 
        if (inst.updatingGraphs) {
            try {
                var cpuGraph = inst.down('cpugraph');
                var memGraph = inst.down('memgraph');
                cpuGraph.reload();
                memGraph.reload();
                if (cpuGraph.lastLoadSuccessful() && memGraph.lastLoadSuccessful()) {
                    setTimeout(function() {inst.updateGraphs();}, inst.updateInterval);
                } else {
                    Log.warn("Failed to load CPU/MEM data. Stopping graphs.");
                    inst.updatingGraphs = false;
                }
            } catch (err) {
                Log.warn("Error while updating graphs; most likely because window was closed.");
            }
        }
        else
            Log.debug("Stopped updating graphs.");
    }
});

