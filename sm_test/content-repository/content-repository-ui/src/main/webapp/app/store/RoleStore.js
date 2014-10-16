
// STORE: Roles
// Retrieves all available Roles from the backend server.
// ----------------------------------------------------------------------------

Ext.define('SM.store.RoleStore', {
    extend: 'Ext.data.Store',
    model: 'SM.model.Role',
    sorters: ['name'],
    autoLoad: true,

    proxy: Ext.create('Ext.data.proxy.Proxy', {
        read: function(operation, callback, scope) {
            var records = ConfigManager.config.getRoles();
            Ext.apply(operation, {
                resultSet: Ext.create('Ext.data.ResultSet', {
                    records: records,
                    total  : records.length,
                    loaded : true
                })
            });

            operation.setCompleted();
            operation.setSuccessful();
            Ext.callback(callback, scope || this, [operation]);
        }
    })
});

