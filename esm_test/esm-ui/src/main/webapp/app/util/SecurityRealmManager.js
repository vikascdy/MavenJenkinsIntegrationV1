// SECURITYREALMMANAGER.JS
// ----------------------------------------------------------------------------

Ext.define('Util.SecurityRealmManager', {});

window.SecurityRealmManager = {

	getDefaultPropertiesForRealmType : function(realmType,loadOnlyOptional, callback) {
			SecurityRealmManager.getRealmPropertiesMeta(realmType,function(properties){
				SecurityRealmManager.loadRealmProperties(properties,loadOnlyOptional,callback);
			});
		
	},

	loadRealmProperties : function(properties,loadOnlyOptional,callback){
		var realmPropertiesStore=Ext.StoreManager.lookup('RealmProperties');
		var optionalRealmProperties=Ext.StoreManager.lookup('OptionalRealmProperties');

		if(!loadOnlyOptional)
		{			
            realmPropertiesStore.removeAll();
            realmPropertiesStore.loadData(properties);
            realmPropertiesStore.filter('required',true);
		}
		
		optionalRealmProperties.removeAll();
		optionalRealmProperties.loadData(properties);
		optionalRealmProperties.filter('required',false);
		
		Ext.callback(callback,this,[]);
	},
	
	generatePropertiesArray : function(store,callback){
		
		var propertiesArray=[];
		
		store.each(function(prop){
			propertiesArray.push(prop.data);
		});
		
		Ext.callback(callback,this,[propertiesArray]);
	},
	
	generatePropertiesObject : function(properties,callback){
		var propObj={};
		var optionalPropObj=[];
		
		Ext.each(properties,function(prop){
			if(prop.required)
				propObj[prop.name]=prop.defaultVal;
			else
				optionalPropObj.push(prop);			
		});
		
		Ext.callback(callback,this,[propObj,optionalPropObj]);
	},
	
	
	getRealmPropertiesMeta : function(realmType,callback){		

		Functions.jsonCommand("esm-service", "getRealmPropertiesMeta", {
			"realmType":realmType
		}, {
			success : function(response) {
				Ext.callback(callback, this, [response]);
			},
			failure : function(response) {
				Functions.errorMsg(response.error, "Failed to Update Realm");
			}
		});
		
	},

	generatePropertiesForRealm : function(callback) {

		var realConfigForm = Ext.getCmp('addEditOrganization').down(
				'realconfigform');

		var authPropGrid = realConfigForm.down('#authPropGrid');
		var authenticationType = realConfigForm.down('#authenticationType');

		var securityRealm = {
			"name" : "default",
			"realmType" : "default",
			"properties" : {}
		};

		if (authPropGrid && authenticationType) {
			var properties=[];
			
			SecurityRealmManager.generatePropertiesArray(authPropGrid.getStore().getRange(),function(properties){
				
			});
			
			securityRealm = {
				"name" : authenticationType.getValue(),
				"realmType" : authenticationType.getValue(),
				"properties" : properties
			};
		}

		Ext.callback(callback, this, [ securityRealm,realConfigForm ]);

	},

	updateRealm : function(realmConfigObj, organizationId, callback, scope) {

		Functions.jsonCommand("esm-service", "organization.updateRealm", {
			"realm" : realmConfigObj,
			"organizationId" : organizationId
		}, {
			success : function(response) {
				Ext.callback(callback, scope, [response]);
			},
			failure : function(response) {
				Functions.errorMsg(response.error, "Failed to Update Realm",null,'ERROR');
			}
		});
	},
	
	testLdapConnection : function(realmConfigObj, callback){
		
		Functions.jsonCommand("esm-service", "testLdapConnection", {
			"realm" : realmConfigObj
		}, {
			success : function(response) {
				Ext.callback(callback, this, [true, null]);
			},
			failure : function(response) {
				Ext.callback(callback, this, [false, response.error]);
			}
		});
	}
};
