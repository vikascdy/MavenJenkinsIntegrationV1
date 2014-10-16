// PROXY: Remote Proxy
// Extends Ext.data.proxy.Ajax and add an exception listener to handle server
// exceptions and display to user.
// ----------------------------------------------------------------------------

Ext.define('SM.proxy.RemoteProxy', {
    extend: 'Ext.data.proxy.Ajax',
    alias: 'proxy.remoteproxy',

    buildRequest: function(operation) {
        // Group parameters under "data", for the JsonServlet.
        var request = this.callParent(arguments);
        request.params = {data: Ext.encode(request.params)};
        return request;
    },

    listeners: {
        exception: function (proxy, request, operation) {
            if (request.responseText !== undefined) {
                // responseText was returned, decode it
                responseObj = Ext.decode(request.responseText, true);
                if (responseObj !== null && responseObj.error !== undefined) {
                    Functions.errorMsg("<b>Error occurred while retrieving data from server:</b><br /><br />" +
                                       "<code>" + responseObj.errorClass + "</code><br /><br />" +
                                       responseObj.error,
                                       'Remote Error');
                } else {
                    // responseText was decoded, but no message sent
                    Functions.errorMsg('Unknown error: The server did not send any information about the error.','Remote Error');
                }
            }
            else {
                // no responseText sent
                Functions.errorMsg('Unknown error: Unable to understand the response from the server','Remote Error');
            }
        }
    }
});
