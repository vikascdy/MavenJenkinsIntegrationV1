Ext.define('Security.store.RealmProperties', {
    extend: 'Ext.data.Store',
    storeId: 'realmProperties',
    requires: 'Security.model.RealmProperties',
    model: 'Security.model.RealmProperties',
    autoLoad:false
});