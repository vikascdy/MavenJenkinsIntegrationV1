Ext.define('Security.store.OptionalRealmProperties', {
    extend: 'Ext.data.Store',
    storeId: 'optionalRealmProperties',
    requires: 'Security.model.RealmProperties',
    model: 'Security.model.RealmProperties'
});