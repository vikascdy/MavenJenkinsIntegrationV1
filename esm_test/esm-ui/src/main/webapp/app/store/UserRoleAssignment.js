Ext.define('Security.store.UserRoleAssignment', {
    extend: 'Ext.data.Store',
    storeId: 'UserRoleAssignment',
    requires: 'Security.model.UserRoleAssignment',
    model: 'Security.model.UserRoleAssignment'
    ,remoteFilter:true
    ,autoLoad:false
});