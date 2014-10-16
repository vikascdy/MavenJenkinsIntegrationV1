Ext.define('Security.controller.SecurityRealmController', {
	extend : 'Ext.app.Controller',

	 stores: [
	          'RealmProperties',
	          'OptionalRealmProperties'
	 ],
	models : [
	          'SecurityRealm',
	          'RealmProperties'
	],
	views : [ 
	          'realm.RealmConfigForm',
	          'realm.RealmConfigProperties',
	          'realm.RealmPropertiesGrid',
	          'realm.TestConnectionWindow'
	],

	init : function() {
		this.control({
			'realmconfigform button[action=deleteProperty]':{
				click : this.deletePropertyForRealm
			},
			'testconnectionwindow button[action=testConnection]':{
				click : this.testConnectionForRealm
			},
			'realmconfigproperties button[action=addProperty]':{
				click : this.addPropertyForRealm
			}
		});
	},
	
	deletePropertyForRealm : function(btn){
		var realmconfigform=btn.up('realmconfigform');
		var authPropGrid=realmconfigform.down('#authPropGrid');
		var selectedProperty=authPropGrid.getSelectionModel().getSelection();
		if(selectedProperty.length>0){
			if(selectedProperty[0].get('required'))
				Functions.errorMsg("This property is mandatory and cannot be deleted",'Failure',null,'ERROR');
			else
			authPropGrid.getStore().remove(selectedProperty[0]);
		}
	},
	
	testConnectionForRealm  : function(btn){
		var testWindow=btn.up('testconnectionwindow');
		var realmconfigform=testWindow.realmConfigForm;
		var form = btn.up('form').getForm();
		var realmConfigObj={};
		var saveRealmBtn = testWindow.down('#saveRealmChanges');
		var realm=realmconfigform.getSecurityRealms().data;
		var authPropGrid=realmconfigform.down('#authPropGrid');
		var values=form.getValues();
		
		SecurityRealmManager.generatePropertiesArray(authPropGrid.getStore(),function(properties){
			try{
				Security.loadingWindow = Ext.widget('progresswindow', {
	                text: 'Testing Connection...'
	            });
				
				
				if( (realm!=undefined || realm!=null) && realm.realType==realmconfigform.down('#authenticationType').getValue())
					realmConfigObj=realm;
				else
				{
					realmConfigObj={		                	           				
		       				"name":realmconfigform.down('#authenticationType').getValue(),
		       				"realmType":realmconfigform.down('#authenticationType').getValue(),
		       				"enabled":true,
		       				"properties":properties 
		       		};	
				}	

				
				realmConfigObj['properties'].push({name:'username',value:values.username});
				realmConfigObj['properties'].push({name:'password',value:values.password});

				
                SecurityRealmManager.testLdapConnection(realmConfigObj, function(success, message){

                    Security.removeLoadingWindow(function() {
                        if(success)
                        {
                        	Functions.errorMsg("Connection Success. You can save your configuration now.",'Success',null,'INFO');
                            testWindow.setTestStatus(true);
                            saveRealmBtn.enable();
                        }
                        else
                        {
                        	Functions.errorMsg(message,'Failure',null,'ERROR');
                            testWindow.setTestStatus(false);
                            saveRealmBtn.disable();
                        }
                    });

                });
			}
			catch(e){
				Security.removeLoadingWindow(function() {
					Functions.errorMsg("Failed to test connection",'Error',null,'ERROR');
					testWindow.setTestStatus(false);
	            });
				
			}
		});		
		
		
	},
	
	addPropertyForRealm : function(btn){
		var realmConfigProperties=btn.up('window');
		var realmPropertiesForm=realmConfigProperties.down('form').getForm();
		
		if(realmPropertiesForm.isValid()){
			var values=realmPropertiesForm.getValues();
			var realmPropertiesStore = this.getRealmPropertiesStore();
			var index=realmPropertiesStore.find('name',values.name);
			if(index!=-1){
				Functions.errorMsg('Property "'+values.name+'" is  already added.','Error',null,'ERROR');
			}
			else{
				realmPropertiesStore.add(values);
				realmConfigProperties.close();
			}
		}
		else
			Ext.Msg.alert("Error", "The form contains validation errors and cannot be saved.");
	}

});
