// APPMANAGER.JS
// ----------------------------------------------------------------------------

Ext.define('Util.AppManager', {});

window.AppManager = {

    loadAppPages : function(managesiteapps){

        Functions.jsonCommand("esm-service", "isSubjectAuthenticated", {}, {
                success: function(response) {

                    if (response === true) {
                        Security.loadingWindow = Ext.widget('progresswindow', {text: 'Loading Page...'});
                        
	                        Security.selectTreeNode(managesiteapps,function(){
	                    		Security.removeLoadingWindow(function(){
	                        	});
	                    		var ctr = Ext.getCmp('Home-page-container');
	                            ctr.removeAll();
	                            ctr.add(managesiteapps);
	                        });
                                                    
                    }
                    else
                    	Functions.showSessionTimeoutWindow();
                },
                
                failure : function(response){
                	Functions.showSessionTimeoutWindow();
                }
        });
    },
	
	getAppStatus : function(appName, tenantId, callback){

		 Functions.jsonCommand("esm-service", "AppStore.getAppStatus",{
			"appName": appName,
			"tenantId":tenantId
		 }, {
			success : function(response) {
				Ext.callback(callback, this, [response]);
			},
			failure : function(response) {			
				Functions.errorMsg(response.error,"Failed To Retrieve App Status");
			}
		});

	},
	
	getAppConfiguration : function(appName, tenantId, callback){

		 Functions.jsonCommand("esm-service", "AppStore.getAppConfiguration",{
			"appName": appName,
			"tenantId":tenantId
		 }, {
			success : function(response) {
				Ext.callback(callback, this, [response]);
			},
			failure : function(response) {			
				Functions.errorMsg(response.error,"Failed To Retrieve App Configuration");
			}
		});

	},
	
	sendInstallAppRequest : function(appName, tenantId, callback){

		 Functions.jsonCommand("esm-service", "AppStore.sendInstallAppRequest",{
			"appName": appName,
			"tenantId":tenantId
		 }, {
			success : function(response) {
				Ext.callback(callback, this, [response]);
			},
			failure : function(response) {			
				Functions.errorMsg(response.error,"Failed To Send App Request.");
			}
		});

	}
};
