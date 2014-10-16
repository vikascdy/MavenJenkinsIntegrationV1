Ext.define('Security.view.organization.ManageOrganizationRoles', {
    extend: 'Security.view.common.CommonManagePage',
    alias : 'widget.manageorganizationroles',
    detailPage:{xtype:'roledetailpane'},
    configurationUrl:'resources/json/ManageOrganizationRoles.json',
    config : {
        loadingParams : null
    },
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