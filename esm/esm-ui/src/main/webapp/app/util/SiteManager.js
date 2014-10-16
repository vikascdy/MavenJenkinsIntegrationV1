// SITEMANAGER.JS
// ----------------------------------------------------------------------------

Ext.define('Util.SiteManager', {});

window.SiteManager = {

    loadSitePages : function(sitePage){

        Functions.jsonCommand("esm-service", "isSubjectAuthenticated", {}, {
                success: function(response) {

                    if (response === true) {
                        Security.loadingWindow = Ext.widget('progresswindow', {text: 'Loading Page...'});
                        
	                        Security.selectTreeNode(sitePage,function(){
	                    		Security.removeLoadingWindow(function(){
	                        	});
	                    		var ctr = Ext.getCmp('Home-page-container');
	                            ctr.removeAll();
	                            ctr.add(sitePage);
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
    

    getDefaultSiteRecord : function(callback){
        var siteStore = Ext.create('Ext.data.Store',{model:'Security.model.Sites'});
        siteStore.load({
            scope: this,
            callback: function(records, operation, success) {

                if(success && records && records.length>0){
                    Ext.callback(callback,this,[records[0]]);
                }
                else
                	Functions.showSessionTimeoutWindow();
            }
        });
    },

    updateSite : function(siteObj, callback, scope){
        Functions.jsonCommand("esm-service", "site.updateSite", {
            Site   : siteObj
        }, {
            success : function(response) {
                Ext.callback(callback, scope, [response]);
            },
            failure : function(response) {
                Ext.callback(callback, scope, [null]);
            }
        });
    }
};
