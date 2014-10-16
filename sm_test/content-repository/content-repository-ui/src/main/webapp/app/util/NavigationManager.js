Ext.define('Util.NavigationManager', {});

window.NavigationManager = {

    initiateRoutes : function(callback) {

        Path.map("#!/config").to(function() {
                NavigationManager.showOpenConfigPage();
        });
        
        Path.map("#!/content").to(function() {
                NavigationManager.showContentRepositoryPage();
        });
        
        Path.map("#!/createconfig").to(function() {
                NavigationManager.showCreateConfigurationPage();
        });   
        
        Path.map("#!/createconfig/import").to(function() {
            NavigationManager.showImportConfigurationPage();
        }); 
        
        Path.map("#!/service/:name/:version/:status").to(function() {
            var me = this;
            NavigationManager.showSavedConfigurationPage(
                		me,
                		me.params["name"],
                		me.params["version"],
                		me.params["status"],
                		function(){}
            );
        });
        
        
        Path.root("#!/config");
        
        Path.listen();

        Path.rescue(function() {
            NavigationManager.showErrorPage();
        });        

        Ext.callback(callback, this);

    },
    
    checkIfLoggedIn : function(callback){
        Functions.jsonCommand("esm-service", "isSubjectAuthenticated", {}, {
            success: function(response) {
            	if (response === true) {
            		 var loadingEl = Ext.get('site-loading');
                   if (loadingEl) loadingEl.hide();
            		Ext.callback(callback,this,[]);
            	}
            	else
            		Functions.redirectToLogin();
            },
            failure: function(response) {
                // If not logged in, redirect to the login page.
                Functions.redirectToLogin();
            }
        });
    	
    },

    showErrorPage : function() {

        SM.removeLoadingWindow(function(){
        	Ext.Msg.alert("404: Route Not Found", "Redirecting to home page.");
            location.href='#!/config';       	
        });
        
    },

    showOpenConfigPage : function(callback) {    	
    	NavigationManager.checkIfLoggedIn(function() {
    		ConfigManager.checkConfigList(function(){
    			SM.loadingWindow = Ext.widget('progresswindow', {
                    text: 'Loading Configuration Page...'
                });
                SM.setPage(Ext.create('SM.view.core.OpenConfigPage'));
                SM.removeLoadingWindow(function(){
                	Ext.callback(callback, this);
                });
    		});            
        });    
    },
    
    showContentRepositoryPage : function(callback) {    
    	 NavigationManager.checkIfLoggedIn(function() {
             SM.loadingWindow = Ext.widget('progresswindow', {
                 text: 'Loading Content Repository Page...'
             });
             SM.setPage(Ext.create('SM.view.content.ContentRepositoryPage'),true);
             SM.removeLoadingWindow(function(){
             	Ext.callback(callback, this);
             });
         });
    },
    
    showCreateConfigurationPage : function(callback){    	
    	NavigationManager.checkIfLoggedIn(function() {
            SM.loadingWindow = Ext.widget('progresswindow', {
                text: 'Loading Configuration Creation Page...'
            });
        	SM.setPage(Ext.create('SM.view.core.CreateConfigPage'));
        	SM.removeLoadingWindow(function(){
            	Ext.callback(callback, this);
            });
        });
    },
    
    showImportConfigurationPage : function(callback){    	
    	NavigationManager.checkIfLoggedIn(function() {
            SM.loadingWindow = Ext.widget('progresswindow', {
                text: 'Loading Import Configuration Page...'
            });
        	SM.setPage(Ext.create('SM.view.core.CreateConfigPage'));
	        	Functions.waitFor(function() {
	                return (!!SM.page && !!SM.page.down("#radioGroup"));
	            }, function() {
	                SM.page.down("#radioGroup").setValue({config: 'import'});
	            });
        	SM.removeLoadingWindow(function(){
            	Ext.callback(callback, this);
            });
        });
    },
    
    showSavedConfigurationPage : function(me,name,version,status,callback) {    	
    	var path=[];
        NavigationManager.checkIfLoggedIn(function() {
            SM.loadingWindow = Ext.widget('progresswindow', {
                text: 'Loading Configuration "'+me.params["name"]+'"...'
            });            
        	path[0]=name;
        	path[1]=version;
        	path[2]=status;    	
        	ConfigManager.loadConfigFromUrl(path);
        	SM.removeLoadingWindow(function(){
            	Ext.callback(callback, this);
            });
        });
    }   
};