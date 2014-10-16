Ext.define('Security.view.tenant.ManageTenantOrganizations', {
    extend: 'Security.view.common.CommonManagePage',
    alias : 'widget.managetenantorganizations',
    detailPage:{
		    	xtype:'organizationdetailpane'
    			},
    configurationUrl:'resources/json/ManageTenantOrganizationsJson.json',
    config : {
    	loadingParams : null
    },
    treeId:'manageOrganizations',
    update: function(record) {
//    	Security.loadingWindow = Ext.widget('progresswindow', {text: 'Loading Organization Detail...'});
    	var me=this;
    	this.down('organizationdetailpane').down('organizationdetailpaneheader').loadOrganizationDetail(record,function(){
    		
    		var realmConfigHolder=me.down('organizationdetailpane').down('#realmConfigHolder');
            me.currentRecord = record;
            realmConfigHolder.removeAll();
            realmConfigHolder.add({
    	        xtype:'realmconfigform',
    	    	organization:record,
    	    	securityRealms:record.get('securityRealms')
            });
            realmConfigHolder.down('realmconfigform').updatedRealmConfiguration(record.get('securityRealms')[0],false,function(){
//            	Security.removeLoadingWindow(function(){                	
//            	});
            }); 
            
            
    	});    	
    },
    
    reset : function(){
    	this.down('organizationdetailpane').down('organizationdetailpaneheader').reset();
    	this.down('organizationdetailpane').down('#realmConfigHolder').removeAll();
    }
});
