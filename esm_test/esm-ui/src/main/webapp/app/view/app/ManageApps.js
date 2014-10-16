Ext.define('Security.view.app.ManageApps', {
    extend: 'Ext.container.Container',
    alias : 'widget.manageapps',
    layout:'border',
    treeId:'apps',
    config:{
        minHeight:1000
    },
    defaults : {
        style:{
            backgroundColor:'#FFF!important'
        }
    },
    items: [
        {
            xtype:'container',
            region:'north',
            padding:'10 30 10 20',
            height:60,
            layout:{type:'hbox',align:'middle'},
            items:[
                {
                    xtype:'component',
                    html:'<h1>Apps</h1>'
                },
                {
                    xtype:'tbspacer',
                    flex:1
                },
                {
                    xtype:'textfield',
                    emptyText:'App Search',
                    enableKeyEvents : true,
                    listeners : {
                    	'keyup' : function(field){
                    		var val = field.getValue();
                    		var appStore = field.up('manageapps').down('appslist').getStore();
                    		appStore.clearFilter();
                    		appStore.filter('name',val);
                    	}
                    }
                }
            ]
        },
        {
            xtype:'tabpanel',
            margin:'0 30 0 0',
            region:'center',
            flex:1,
			items:[
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
			]
           
        }
    ],
	
	update : function(tenantId, callback){
	
	
				var availableAppsStore = Ext.create('Security.store.AvailableApps');
				availableAppsStore.getProxy().setExtraParam('tenantId',tenantId);
				
				
				var installedAppsStore = Ext.create('Security.store.InstalledApps');
				installedAppsStore.getProxy().setExtraParam('tenantId',tenantId);
				
				
				this.down('#availableApps').removeAll();
				this.down('#installedApps').removeAll();
				
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
				
				Ext.callback(callback,this,[]);
	
	}

});
