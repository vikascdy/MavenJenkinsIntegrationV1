
// PROXY: Config Proxy
// Loads a set of records from the currently loaded config file. Takes a parent
// item and an object representing search criteria.
// ----------------------------------------------------------------------------

Ext.define('SM.proxy.ConfigProxy', {
    extend: 'Ext.data.proxy.Proxy',
    alias: 'proxy.config',
 
    constructor: function(config) {
        config = config || {};
        this.callParent(arguments);
        this.criteria = config.criteria;
        this.parentItem = config.parentItem;
    
        if (!this.criteria)
            Ext.Error.raise("You must provide search criteria for a ConfigProxy.");
    },
    
    create: function(operation, callback, scope) {
        Ext.Error.raise('Create not yet supported for ConfigProxy.');
    },
    
    read: function(operation, callback, scope) {
        if (!this.parentItem)
            this.parentItem = ConfigManager.config;
        if (!this.parentItem)
            Ext.Error.raise("No config file available to load from!");

        var records = this.parentItem.getChildrenWith(this.criteria);
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
    },

    update: function(operation, callback, scope) {
        Ext.Error.raise('Update not yet supported for ConfigProxy.');
    },
   
    destroy: function(operation, callback, scope) {
        Ext.Error.raise('Destroy not yet supported for ConfigProxy.');
    },

    listeners: {
        exception: function (proxy, request, operation) {
            if (request.responseText !== undefined) {
                // responseText was returned, decode it
                responseObj = Ext.decode(request.responseText, true);
                if (responseObj !== null && responseObj.error !== undefined) {
                    Functions.errorMsg("<b>Error occurred while retrieving config data from server:</b><br /><br />" +
                                       "<code>" + responseObj.errorClass + "</code><br /><br />" +
                                       responseObj.error,
                                       'Remote Error');
                } else {
                    // responseText was decoded, but no message sent
                    Ext.Msg.alert('Unknown error: The server did not send any information about the error.','Remote Error');
                }
            }
            else {
                // no responseText sent
                Ext.Msg.alert('Unknown error: Unable to understand the response from the server','Remote Error');
            }
        }
    }
});

