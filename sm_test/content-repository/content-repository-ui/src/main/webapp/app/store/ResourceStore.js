
// STORE: Resources
// Retrieves Resource data from the loaded config file.
// ----------------------------------------------------------------------------

Ext.require('SM.proxy.ConfigProxy');

Ext.define('SM.store.ResourceStore', {
    extend: 'SM.store.ConfigStore',
    model : 'SM.model.Resource',
    sorters: ['restype', 'name'],
    searchCriteria: {type: 'Resource'}
});

