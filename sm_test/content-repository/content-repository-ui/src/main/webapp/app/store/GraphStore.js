
// STORE: Graph Points
// Retrieves server system data in GraphPoint form, to display on CPU and
// memory graphs.
// ----------------------------------------------------------------------------

Ext.define('SM.store.GraphStore', {
    extend: 'Ext.data.Store',
    model: 'SM.model.GraphPoint',
    lastLoadSuccessful: true,
    
    reload: function(object, property) {
        var store = this;
        if (object.getSystemInfo === undefined)
            throw new Error("Tried to load data for a GraphStore from a source that does not have a getSystemInfo method!");
        object.getSystemInfo(property, function(response) {
            try {
                store.loadData(Ext.decode(response));
                store.lastLoadSuccessful = true;
            } catch (err) {
                Log.error(err);
                store.lastLoadSuccessful = false;
            }
        });
    }
});

