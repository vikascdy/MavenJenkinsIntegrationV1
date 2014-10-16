Ext.define('Security.view.organization.ManageOrganizationUsers', {
    extend: 'Security.view.common.CommonManagePage',
    alias : 'widget.manageorganizationusers',
    detailPage:{xtype:'accountsettings'},
    configurationUrl:'resources/json/ManageOrganizationUsers.json',
    config : {
        loadingParams : null
    },
    treeId:'manageUsers',
    update: function(record) {
    	var me=this;
		Security.loadingWindow = Ext.widget('progresswindow', {text: 'Loading User Details...'});
		
		if(record.data.username=='system' || record.data.username=='admin')
		me.down('accountsettings').update(record,true,function(){
		});
		else
		me.down('accountsettings').update(record,false,function(){
		});
		
    },
    
    reset : function(){
    	this.down('accountsettings').reset();
     }


});