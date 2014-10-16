
// STORE: Job
// Retrieves Job data from the loaded config file.
// ----------------------------------------------------------------------------

Ext.require('SM.proxy.ConfigProxy');

Ext.define('SM.store.JobStore', {
    extend: 'SM.store.ConfigStore',
    model : 'SM.model.Job',
    sorters: ['jobName', 'name'],
    searchCriteria: {type: 'Job'}    
});

