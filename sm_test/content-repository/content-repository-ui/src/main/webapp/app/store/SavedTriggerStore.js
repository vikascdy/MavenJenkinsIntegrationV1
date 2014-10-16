
Ext.require('SM.proxy.RemoteProxy');

Ext.define('SM.store.SavedTriggerStore', {
    extend: 'Ext.data.Store',
    model: 'SM.model.JobTrigger',
    autoLoad: false
});


