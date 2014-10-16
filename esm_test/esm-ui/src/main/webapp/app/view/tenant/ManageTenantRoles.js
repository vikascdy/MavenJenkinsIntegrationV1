Ext.define('Security.view.tenant.ManageTenantRoles', {
    extend: 'Security.view.common.CommonManagePage',
    alias : 'widget.managetenantroles',
    detailPage:{xtype:'roledetailpane'},
    configurationUrl:'resources/json/ManageTenantRoles.json',
    config : {
        loadingParams : null
    },
    treeId:'manageRoles',
    update: function(record) {
    	var me=this;
    	this.down('roledetailpane').down('roledetailpaneheader').loadRoleDetail(record,function(){
    		me.down('roledetailpane').down('roledetailpanetabpanel').update(record);
	
    	});    	
    },
    
    reset : function(){
    	this.down('roledetailpane').down('roledetailpaneheader').reset();
    	this.down('roledetailpane').down('roledetailpanetabpanel').reset();
    }

});