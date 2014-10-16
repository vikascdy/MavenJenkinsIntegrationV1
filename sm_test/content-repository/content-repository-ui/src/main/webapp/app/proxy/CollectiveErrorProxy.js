
// PROXY: Collective Error Proxy
// Collects all errors, both server-side and client-side, related to a given
// ConfigItem or any of its children.
// ----------------------------------------------------------------------------

Ext.define('SM.proxy.CollectiveErrorProxy', {
    extend: 'Ext.data.proxy.Proxy',
    alias: 'proxy.errorcollect',
 
    constructor: function(config) {
        config = config || {};
        this.callParent(arguments);
        this.parentItem = config.parentItem;
    },
    
    read: function(operation, callback, scope) {
        if (!this.parentItem)
            this.parentItem = ConfigManager.config;
        if (!this.parentItem)
            Ext.Error.raise("No config file available to load from!");

        var errors = [];
        var serverStore = Ext.getStore('ServerErrorStore');
		serverStore.removeAll();
        serverStore.load();
        serverStore.data.each(function(err) {
            if (err.shouldShowFor(this.parentItem))
            	
                errors.push(err);
        }, this);
		
	
        Ext.each(ConfigManager.getValidationErrors(), function(err) {
            if (err.shouldShowFor(this.parentItem))
                errors.push(err);
        }, this);
        this._recursiveCollect(this.parentItem, errors);

        Ext.apply(operation, {
            resultSet: Ext.create('Ext.data.ResultSet', {
                records: errors,
                total  : errors.length,
                loaded : true
            })
        });

        operation.setCompleted();
        operation.setSuccessful();
        Ext.callback(callback, scope || this, [operation]);
    },

    _recursiveCollect: function(item, errors) {
        var me = this;
        Ext.each(item.getErrors(), function(i){errors.push(i);});
        item.eachChild(function(c) {me._recursiveCollect(c, errors);});
    }
});
 
