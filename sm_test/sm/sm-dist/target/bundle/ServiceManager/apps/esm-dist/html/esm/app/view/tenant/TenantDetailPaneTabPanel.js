Ext.define('Security.view.tenant.TenantDetailPaneTabPanel', {
    extend: 'Ext.tab.Panel',
    plain: true,
    alias: 'widget.tenantdetailpanetabpanel',
    initComponent : function() {
        this.items = [
            {
                title:'Details',
                layout:'fit',
                itemId:'tenantDetailCont'
            },
			 {
                title:'Available Apps',
                layout:'fit',
                itemId:'availableApps'
            },
			 {
                title:'Installed Apps',
                layout:'fit',
                itemId:'installedApps'
            }
//            {
//                title:'Notifications',
//                layout:'fit',
//                itemId:'tenantNotificationsCont'
//            }
        ];

        this.listeners = {
            'afterrender' : function() {
                this.fireEvent('tabchange',this,this.getActiveTab());
            }
        };
        this.callParent(arguments);
    },
    update : function(record, callback){
		
	
				Security.loadingWindow = Ext.widget('progresswindow', {text: 'Loading Tenant Details...'});
				
				var availableAppsStore = Ext.create('Security.store.AvailableApps');
				availableAppsStore.getProxy().setExtraParam('tenantId',record.get('id'));
				
				
				var installedAppsStore = Ext.create('Security.store.InstalledApps');
				installedAppsStore.getProxy().setExtraParam('tenantId',record.get('id'));
				
				
				this.down('#tenantDetailCont').removeAll();
				this.down('#availableApps').removeAll();
				this.down('#installedApps').removeAll();
				
				this.down('#tenantDetailCont').add({
					xtype:'tenantdetailtabpanel'
				});			
				
				
				
				this.down('#availableApps').add({
					xtype:'appslist',
					storeInstance:availableAppsStore,
					installApp:true
				});
				
				
				this.down('#installedApps').add({
					xtype:'appslist',
					storeInstance:installedAppsStore,
					installApp:false
				});
				
		//    	this.down('#tenantNotificationsCont').add({
		//            xtype:'tenantnotificationtabpanel'
		//        });
				
				this.down('tenantdetailtabpanel').update(record);

				Security.removeLoadingWindow(function(){
					Ext.callback(callback, this);
				});
				
    },
    reset : function(){
    	this.down('#tenantDetailCont').removeAll();    	
//    	this.down('#tenantNotificationsCont').removeAll();
    }
});