Ext.define('Security.view.app.ManageSiteApps', {
    extend: 'Ext.container.Container',
    alias : 'widget.managesiteapps',
    layout:'border',
    treeId:'site-apps',
    config:{
        minHeight:1000
    },
    defaults : {
        style:{
            backgroundColor:'#FFF!important'
        }
    },
	
	initComponent : function(){
	
	var me=this;
	
	var appsListStore = Ext.create('Security.store.AppsListStore');

	
    this.items= [
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
                    		var appStore = field.up('managesiteapps').down('appslist').getStore();
                    		appStore.clearFilter();
                    		appStore.filter('name',val);
                    	}
                    }
                }
            ]
        },
        {
            region      :   'west',
            width       :   200,
            xtype       :   'LeftMenu',
            bodyPadding :   '0 15 0 0',
			id          :	"westLeftMenuId",
            itemId      :   "westLeftMenuId",
            url         :   'resources/json/siteapp-category-json.json',
            menuType    :   "type2"
        },
        {
            xtype:'container',
			id : 'siteapps',
            margin:'0 30 0 0',
            region:'center',
            flex:1,
			items:
			[
			{xtype : 'appslist',storeInstance:appsListStore}
			]
           
        }
    ];
	
	this.callParent(arguments);
	
	}

});
