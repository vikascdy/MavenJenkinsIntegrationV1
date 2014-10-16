Ext.define('Security.view.app.AppOverviewPage', {
    extend:'Ext.container.Container',
    alias:'widget.appoverviewpage',
	title:'Overview',
	flex:1,
	layout:{type:'vbox',align:'stretch'},
	autoScroll:true,
	initComponent : function(){
	
	var me = this;
	
	
	this.items = [
			{
				xtype:'container',
				padding:10,
				itemId:'appInfoCont',
				tpl: new Ext.XTemplate(
					'<div class="appContainer">' +
					'<div class="appImage"><img width="80" height="80" src="resources/images/app_icon.png"/></div>' +
					'<div class="appPrimaryDetails">' +
					'<div class="appSubHeader" style="margin-top:5px;"><b>Name : </b>{name}</div>' +
					'<div class="appSubHeader" style="margin-top:5px;"><b>Version : </b>{version}</div>' +
					'<div class="appSubHeader" style="margin-top:5px;"><b>Display Version : </b>{displayVersion}</div>' +
					'</div>' +
					'<div class="appSecondaryDetails">' +
			        '<div id="installAppButton"></div>' +
					'</div>'+
					'</div>',
					{
						
					}
				),
				listeners : {
					'afterrender' : function(cont){

					//AppManager.getAppStatus(me.appRecord.get('id'),me.storeParams.tenantId,function(response){
										
						Ext.widget({
								xtype:'button',
								itemId:'installButton',
								ui:'greenbutton',
								hidden:!me.installApp,
								renderTo:'installAppButton',
								text:'Install',
								handler : function(){
								
								var flexFieldConfig = {
										"flexGroupUrl":"/rest/service/esm-service/AppStore.getAppConfiguration",
										"saveValueUrl":"/rest/service/flexfields-service/FlexField.setFlexFieldValue",
										"enableRootHeader":false,
										"tenantName":"*",
										"appName":me.appRecord.get('id'),
										"componentName":"*",
										"entityName":"TenantEntity",
										"extraParams": {
											"tenantId":me.storeParams.tenantId
										}
									};
									var flexFieldComponent = Ext.widget({
											   xtype:'flexfieldcomponent',
											   layout:'anchor',
											   defaults:{'anchor':'50%'},
											   margin:'10 0 10 0',
											   useContextMapParam : false,
											   useConfigurationUrl : false,
											   fieldConfigurationData : flexFieldConfig
										   });
										   
								flexFieldComponent.setEntityId(me.storeParams.tenantId);		   
								me.down('#appConfiguration').removeAll();
								me.down('#appConfiguration').add(flexFieldComponent);
								me.down('#appConfiguration').show();
										
									
								}
							});
					//});
						
					}
				}
			},
			{
				xtype:'container',
				padding:10,
				itemId:'appDescription',
				tpl: new Ext.XTemplate(
					'<div class="appContainer">' +
					'<div class="appSubHeader" style="margin-top:5px;"><b>{description}</b></div>' +
					'</div>',
					{
						
					}
				)
			},
			{
				xtype:'form',
				hidden:true,
				padding:10,
				title:'App Configuration',
				border:true,
				itemId:'appConfiguration',
				buttons:[
					{
						xtype:'button',
						ui:'bluebutton',
						formBind:true,
						text:'Send Request',
						handler : function(){
						var appInfoWindow = me.up('appinfowindow');
						var availableApps = Security.viewport.down('#availableApps').down('appslist');
						var installedApps = Security.viewport.down('#installedApps').down('appslist');
						var availableAppsStore = null;
						var installedAppsStore = null;
						
						
						if(availableApps)
							availableAppsStore = availableApps.getStore();
						if(installedApps)
							installedAppsStore = installedApps.getStore();
						
						AppManager.sendInstallAppRequest(me.appRecord.get('id'),me.storeParams.tenantId,function(response){
									if(response){
										
										if(availableAppsStore){
											availableAppsStore.getProxy().setExtraParam('tenantId',me.storeParams.tenantId);
											availableAppsStore.load({
											callback:function(){
											if(installedAppsStore){
												installedAppsStore.getProxy().setExtraParam('tenantId',me.storeParams.tenantId);
												installedAppsStore.load({
														callback:function(){
														Functions.errorMsg('Application request is sent.','Request Sent',null,'INFO');
														appInfoWindow.close();
														}
													});
											}
											}
										});			
										}	
										}
										else
											Functions.errorMsg("Failed To Send App Request.",'Request Failed');
							});
						}
					}
				]
			}
	];
	
	this.callParent(arguments);
	},
	
	update : function(record, callback){
	
		this.down('#appInfoCont').update(record.data);
		this.down('#appDescription').update(record.data);
	
		Ext.callback(callback,this,[]);
	}
	
});
	