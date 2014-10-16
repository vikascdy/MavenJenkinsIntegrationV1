// PROXY: Remote Proxy
// Extends Ext.data.proxy.Rest and add an exception listener to handle server
// exceptions and display to user.
// ----------------------------------------------------------------------------

Ext.define('Security.proxy.RemoteProxy', {
    extend: 'Ext.data.proxy.Rest',
    alias: 'proxy.remoteproxy',

    listeners: {
        exception: function (proxy, request, operation) {
        	
            if (request.responseText != undefined) {
                // responseText was returned, decode it
                responseObj = Ext.decode(request.responseText, true);
	
                if (responseObj != null && responseObj.error != undefined) {
                	
                	if(responseObj.error.class=='com.edifecs.epp.security.exception.AuthenticationFailureException')
            		{
                		Functions.showSessionTimeoutWindow();
            		}
                	else
					if(responseObj.error.class=='com.edifecs.epp.security.exception.AuthorizationFailureException')
            		{
                		var targetId = operation.scope.id;
						var targetComp = Ext.getCmp(targetId);
						if(targetComp)
							{
							Security.removeLoadingWindow(function(){
									targetComp.setErrors(responseObj.error.message);
							});
								
							}
            		}
                	else
                	  Functions.errorMsg(responseObj.error.message,'Remote Error',true);                	
                } else {
                    // responseText was decoded, but no message sent
                    Functions.errorMsg('Unknown error: The server did not send any information about the error. ' + operation + " - " + request.responseText + " - " + proxy,'Remote Error',true);
                }
            }
            else {
                // no responseText sent
                Functions.errorMsg('Unknown error: Unable to understand the response from the server','Remote Error',true);
            }
        }
    }
});
