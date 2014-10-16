Ext.define('Security.view.realm.RealmConfigForm', {
    extend: 'Ext.panel.Panel',
    alias : 'widget.realmconfigform',
    autoScroll:true,
    border:false,
    bodyPadding:10,
    layout:{type:'vbox',align:'stretch'},
    config:{
    	realmType:null,
    	organizationRecord:null,
    	securityRealms:null,
    	realmId:null,
    	propertiesArray:[]
    },
    initComponent : function(){
    var me=this;
    	
   	
    this.items=[
                {
                	xtype:'form',
                	height:50,
                	border:false,
                	items:[
                	       {
            	    	    xtype:'container',
                           	layout:{type:'hbox'},
                           	flex:1,
                           	items:[
									 {
		                	    	   xtype:'combobox',
		                	    	   editable:false,
		                	    	   fieldLabel:'Authentication Type',
		                	    	   id:'realm-authenticationType',
		                	    	   labelWidth:120,
		                	    	   width:300,
		                	    	   submitValue:false,
		                	    	   // FIXME: Make this Dynamic and not Hardcoded
		                	    	   store: [['DATABASE','Built-in User Database'],['LDAP','LDAP Server'],['ACTIVEDIRECTORY','Active Directory Server']],
		                	    	   queryMode: 'local',
		                	    	   value:'DATABASE',
		                	    	   itemId:'authenticationType',
		                	    	   listeners :{
		                	    		   select : function(combo,records){
		                	    			   var value=records[0].get('field1');
		                	    			   		var realConfigCont=me.down('#realConfigCont');
		                	    			   		var authPropGrid=me.down('#authPropGrid');
		                	    			   		var saveRealmChanges=me.down('#saveRealmChanges');
		                	    			   		var testConnection=me.down('#testConnection');
		                	    			   		
		                	    			   		if(value=="DATABASE"){
		                	    			   			realConfigCont.hide();
		                	    			   			
		                	    			   			saveRealmChanges.hide();
		                	    			   			testConnection.hide();
		                	    			   			
		                	    			   			if(me.getRealmId()){
		                	    			   				saveRealmChanges.show();
		                	    			   				SecurityRealmManager.generatePropertiesArray(authPropGrid.getStore(),function(propertiesArray){
		                	    			   					me.setPropertiesArray(propertiesArray);
		                	    			   				});
		                	    			   			}
		                	    			   			authPropGrid.getStore().removeAll();
		                	    			   		}
		                	    			   		else{
		                	    			   			if(value == me.getRealmType()){
		                	    			   				console.log(me.getSecurityRealms());
		                	    			   				me.updatedRealmConfiguration(me.getSecurityRealms(),true);
		                	    			   				saveRealmChanges.hide();
			                	    			   			testConnection.show();	
		                	    			   			}
		                	    			   			else
		                	    			   			{
		                	    			   				SecurityRealmManager.getDefaultPropertiesForRealmType(value,false,function(){
				                	    			   			realConfigCont.show();
				                	    			   			saveRealmChanges.hide();
				                	    			   			testConnection.show();				                	    			   			
				                	    			   		});
		                	    			   			}
		                	    			   		}
		                	    			   }
		                	    	   }
		                	       },
		                	       {
		                	    	   xtype:'tbspacer',
		                	    	   flex:1
		                	       },
		                	       {
		                	    	    xtype:'button',
		                	    	    margin:'0 10 0 0',
		                	    	    hidden:true,
		                	            text:'Save Configuration',
		                	            ui:'greenbutton',
		                	            itemId: 'saveRealmChanges',
		                	            id: 'saveRealmChanges',
		                	            handler : function(){

		                	           		var authPropGrid=me.down('#authPropGrid');
		                	           		var realmConfigObj={};
		                	           		
		                	           		SecurityRealmManager.generatePropertiesArray(authPropGrid.getStore(),function(propertiesArray){
		                	           			
			                	           		if(me.getRealmId()){

				                	           		realmConfigObj=me.getSecurityRealms().data;
				                	           		realmConfigObj['properties']=propertiesArray;
				                	           		
				                	           		
				                	           		if(me.down('#authenticationType').getValue()=='DATABASE')
				                	           			{
				                	           			realmConfigObj['properties']=me.getPropertiesArray();
				                	           			realmConfigObj['enabled']=false;
				                	           			}
				                	           		else
				                	           			realmConfigObj['enabled']=true;
				                	           		
				                	           		
				                	           		Ext.each(me.getSecurityRealms().data.properties,function(prop){
				                	           			if(prop.hasOwnProperty('id'))
				                	           				delete prop['id'];
				                	           		});
				                	           		
				                	           		
				                	            	SecurityRealmManager.updateRealm(realmConfigObj,me.organization.get('id'),function(realmObj){
				                	            		var realm=Ext.create('Security.model.SecurityRealm',realmObj);
				                	            		me.setSecurityRealms(realm);
				                	            		me.setRealmType(realmObj.realmType);
				                	        	   		me.setRealmId(realmObj.id);
				                	        	   		me.setPropertiesArray(realm.get('properties'));
				                	            		Ext.Msg.alert("Realm Updated", "Realm configuration updated successfully");
				                	            	},this);
			                	           		}
		                	           		});
		                	            }
		                	        },
		                	        {
		                	    	    xtype:'button',
		                	    	    hidden:true,
		                	            text:'Test & Save',
		                				ui:'greenbutton',
		                	            itemId:'testConnection',
		                	            id:'testConnection',
		                	            handler:function(btn){
		                	            	var testConnectionWindow = Ext.widget({
		                	            		xtype:'testconnectionwindow',
		                	            		realmConfigForm:me,
		                	            		authPropGrid:me.down('#authPropGrid')
		                	            	});
		                	            	testConnectionWindow.show(btn.getEl());
		                	            }
		                	        }
	                	       ]
                	       }                	      
            	       ]
                },
                {
                	xtype:'container',
                	hidden:true,
                	itemId:'realConfigCont',
                	flex:1,
                	margin:'10 0 0 0',	
                	layout:{type:'hbox',align:'stretch'},
                	flex:1,
                	items:[
			                {
			                	xtype:'realmpropertiesgrid',
			                	autoScroll:true,
			                	itemId:'authPropGrid',
			                	id:'authPropGrid',
			                	flex:1,
			                	listeners : {
			                		selectionchange : function(grid, selected){
			                			var realConfigCont=this.up('#realConfigCont');
			                			if(selected.length>0){
			                				realConfigCont.down('#deleteProp').enable();
			                			}
			                			else
			                				realConfigCont.down('#deleteProp').disable();
			                			
			                		}
			                	}
			                },
			                {
			                	xtype:'container',
			                	margin:'0 0 0 10',
			                	width:80,
			                	layout:{
			                		type:'vbox',
			                		align:'stretch'
			                	},
			                	defaults:{margin:'0 0 5 0'},
			                	 items: [
			                 	        { xtype: 'button', text: 'New', itemId:'addNewProp', id:'addNewProp',
			                 	        	handler : function(btn){
			                 	        		var authenticationType=me.down('#authenticationType');
			                 	        		if(authenticationType.getValue()!="DATABASE"){	                	    			   		
	                	    			   				SecurityRealmManager.getDefaultPropertiesForRealmType(authenticationType.getValue(),true,function(){
		                	    			   			
			                	    			   		});
	                	    			   		  }
			                 	        		

			                 	        		var realmConfigProperties=Ext.widget({
					                 	        			xtype:'realmconfigproperties',
					                 	        			title:authenticationType.getRawValue()+' Property'
			                 	        				});
			                 	        		realmConfigProperties.show(btn.getEl());
			                 	        	}
			                 	        },
			                 	        { xtype: 'button', text: 'Delete',itemId:'deleteProp', id:'deleteProp', action:'deleteProperty',disabled:true}
			                 	    ]
			                }
		                ]
                }
			                
    ];

    
    this.callParent(arguments);
    },
    
    
    updatedRealmConfiguration : function(securityRealms, originalRealm, callback){
    	var me=this;
    	me.setSecurityRealms(securityRealms);

		var realConfigCont=me.down('#realConfigCont');
   		var authPropGrid=realConfigCont.down('#authPropGrid');
   		var authenticationType=me.down('#authenticationType');   
//   		var saveRealmChanges=me.down('#saveRealmChanges');
   		var testConnection=me.down('#testConnection');
   		
    	if(securityRealms!=undefined){   
    		
    		var securityRealmConfig=securityRealms;
    		var realmType=securityRealmConfig.get('realmType');    		
    		authenticationType.setValue(realmType);
    		me.setRealmType(realmType);
	   		me.setRealmId(securityRealmConfig.get('id'));
    		
    		if(securityRealmConfig.get('enabled') || originalRealm){
    			authPropGrid.getStore().clearFilter();
		   		authPropGrid.getStore().loadData(securityRealmConfig.get('properties'));
		   		me.setPropertiesArray(securityRealmConfig.get('properties'));
		   		realConfigCont.show();		   		
		   		testConnection.show();
    		}
    		else
    			authenticationType.setValue('DATABASE');
    		
//    		saveRealmChanges.show();
	   		
    	}
    	else
		{    		
			authenticationType.setValue('DATABASE');
	   		realConfigCont.hide();
//	   		saveRealmChanges.hide();
	   		testConnection.hide();
		}

    	Ext.callback(callback,this,[]);
    }
});

