Ext.define('Security.view.tenant.ManageTenantUserGroups', {
    extend: 'Security.view.common.CommonManagePage',
    alias : 'widget.managetenantusergroups',
    detailPage:{xtype:'groupdetailpane'},
    configurationUrl:'resources/json/ManageTenantUserGroups.json',
    config : {
        loadingParams : null
    },
    treeId:'manageGroups',
    update: function(record) {
    	var me=this;
    	this.down('groupdetailpane').down('groupdetailpaneheader').loadGroupDetail(record,function(){
    		me.down('groupdetailpane').down('groupdetailpanetabpanel').update(record);
	
    	});    	
    },
    
    reset : function(){
    	this.down('groupdetailpane').down('groupdetailpaneheader').reset();
    	this.down('groupdetailpane').down('groupdetailpanetabpanel').reset();
    }

});