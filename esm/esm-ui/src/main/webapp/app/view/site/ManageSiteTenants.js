Ext.define('Security.view.site.ManageSiteTenants', {
    extend: 'Security.view.common.CommonManagePage',
    alias : 'widget.managesitetenants',
    detailPage:{xtype:'tenantdetailpane'},
    configurationUrl:'resources/json/ManageSiteTenantJson.json',
    treeId:'manageTenants',
    config : {
    	loadingParams : null
    },
    update: function(record) {
    	var me=this;
    	this.down('tenantdetailpane').down('tenantdetailpaneheader').loadTenantDetail(record,function(){
    		me.down('tenantdetailpane').down('tenantdetailpanetabpanel').update(record,function(){});
    		 		
    	});    	
    },    
    reset : function(){
    	this.down('tenantdetailpane').down('tenantdetailpaneheader').reset();
		this.down('tenantdetailpane').down('tenantdetailpanetabpanel').reset();
    	
    }
});
