Ext.define('Security.view.realm.TestConnectionWindow', {
	extend : 'Ext.window.Window',
	alias : 'widget.testconnectionwindow',
	bodyPadding : 10,
	width : 400,
	height : 160,
	resizable : false,
	modal : true,
	closeAction : 'destroy',
	title : 'Test Connection',
	autoShow : false,
	config:{
		testStatus:false
	},
	initComponent : function() {

		var me = this;
		
		var realmConfigForm=me.realmConfigForm;

		this.items = [ {
			xtype : 'form',
			border : false,
			layout : 'anchor',
			defaults : {
				anchor : '100%'
			},
			defaultType : 'textfield',
			items : [ {
				fieldLabel : 'Username',
				enableKeyEvents:true,
				allowBlank : false,
				name : 'username',
				id : 'testConnection-username',
				listeners : {
					'keyup' : function(){
						if(me.getTestStatus()){
							me.down('#saveRealmChanges').disable();
							me.setTestStatus(false);
						}
					}
				}
			}, {
				fieldLabel : 'Password',
				enableKeyEvents:true,
				allowBlank : false,
				name : 'password',
				id : 'testConnection-password',
				inputType : 'password',
				listeners : {
					'keyup' : function(){
						if(me.getTestStatus()){
							me.down('#saveRealmChanges').disable();
							me.setTestStatus(false);
						}
					}
				}
			} ],
			buttons : [ {
				text : 'Close',
				id : 'testConnection-closeButton',
				width : 80,
				handler : function() {
					this.up('window').close();
				}
			}, {
				text : 'Test Connection',
				id : 'testConnection-testConnectionButton',
				formBind : true,
				action : 'testConnection'
			},
			{
	    	    xtype:'button',
	            text:'Save Configuration',
	            id : 'testConnection-saveRealmChanges',
	            disabled:true,
	            ui:'greenbutton',
	            itemId: 'saveRealmChanges',
	            handler : function(btn){

	           		var authPropGrid=me.authPropGrid;
	           		var realmConfigObj={};
	           		
	           		SecurityRealmManager.generatePropertiesArray(authPropGrid.getStore(),function(propertiesArray){
	           			
    	           		if(realmConfigForm.getRealmId()){

        	           		realmConfigObj=realmConfigForm.getSecurityRealms().data;
        	           		realmConfigObj['properties']=propertiesArray;
        	           		
        	           		
        	           		if(realmConfigForm.down('#authenticationType').getValue()=='DATABASE')
        	           			{
        	           			realmConfigObj['properties']=me.getPropertiesArray();
        	           			realmConfigObj['enabled']=false;
        	           			}
        	           		else
        	           			realmConfigObj['enabled']=true;
        	           		
        	           		
        	           		Ext.each(realmConfigForm.getSecurityRealms().data.properties,function(prop){
        	           			if(prop.hasOwnProperty('id'))
        	           				delete prop['id'];
        	           		});
        	           		
        	           		
        	            	SecurityRealmManager.updateRealm(realmConfigObj,realmConfigForm.organization.get('id'),function(realmObj){
        	            		var realm=Ext.create('Security.model.SecurityRealm',realmObj);
        	            		realmConfigForm.setSecurityRealms(realm);
        	            		realmConfigForm.setRealmType(realmObj.realmType);
        	            		realmConfigForm.setRealmId(realmObj.id);
        	            		realmConfigForm.setPropertiesArray(realm.get('properties'));
        	            		me.fireEvent('updateOrganizationRecord',realmConfigForm.organization.get('id'));
        	            		btn.up('window').close();
        	            		Functions.errorMsg("Realm configuration updated successfully","Realm Updated",null,'INFO');
       	            		
        	            	},this);
    	           		}
    	           		else
	           			if(realmConfigForm.organization && realmConfigForm. down('#authenticationType').getValue()!='DATABASE')
    	           			{
	           				
	           				
        	           			realmConfigObj={		                	           				
            	           				"name":realmConfigForm.down('#authenticationType').getValue(),
            	           				"realmType":realmConfigForm.down('#authenticationType').getValue(),
            	           				"enabled":true,
            	           				"properties":propertiesArray 
            	           		};		   
        	           			
        	           			Ext.each(realmConfigObj.properties,function(prop){
            	           			if(prop.hasOwnProperty('propertyId'))
            	           				delete prop['propertyId'];
            	           		});
    	           				
        	           			OrganizationManager.addRealmToOrganization(realmConfigObj,realmConfigForm.organization.get('id'),function(realmObj){
        	           				OrganizationManager.updateOrganizationRecord(realmConfigForm.organization.get('id'),function(){
        	           					var realm=Ext.create('Security.model.SecurityRealm',realmObj);
        	           					realmConfigForm.setSecurityRealms(realm);
        	           					realmConfigForm.setRealmType(realmObj.realmType);
        	           					realmConfigForm.setRealmId(realmObj.id);
        	           					realmConfigForm.setPropertiesArray(realm.get('properties'));
        	           					realmConfigForm.down('realmpropertiesgrid').getStore().removeAll();
        	           					realmConfigForm.down('realmpropertiesgrid').getStore().add(realm.get('properties'));
        	           					me.fireEvent('updateOrganizationRecord',realmConfigForm.organization.get('id'));
        	           					btn.up('window').close();
        	           					Functions.errorMsg("Realm configuration added successfully","Realm Added",null,'INFO');
        	           				});
        	           			});
    	           			}
	           		});
	            }
	        },
			
			
			
			]
		} ];

		this.callParent(arguments);
	}

});
