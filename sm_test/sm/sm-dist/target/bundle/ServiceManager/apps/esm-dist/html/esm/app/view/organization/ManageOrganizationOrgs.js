Ext.define('Security.view.organization.ManageOrganizationOrgs', {
    extend: 'Ext.container.Container',
    alias : 'widget.manageorganizationorgs',
    layout:'border',
    config:{
        minHeight:700
    },
    defaults : {
        style:{
            backgroundColor:'#FFF!important'
        }
    },
    treeId:'manageSubOrganizations',
    items : [
            {
                xtype:'component',
                region:'north',
                padding:'20 0 0 20',
                height:60,
                html:'<h1>Sub-Organizations</h1>'
            },
            {
                xtype:'container',
                layout:'fit',
                itemId:'organizationTreeCont',
                region:'center',
                padding:'0 10 20 20',
                flex:1
            },
//            {
//                xtype:'organizationdetailpane',
//                diabled:true,
//                itemId:'detailHolder',
//                margin:'0 10 20 0',
//                region:'east',
//                width:'60%'
//            }
        ],
        
    update : function(parentOrganizationRecord){        	
    	
        this.currentRecord = parentOrganizationRecord;
		var me=this;
		Ext.Ajax.request({
                    url: '/rest/service/esm-service/organization.getOrganizationDetail',
                    params:{
						data:Ext.encode({
									organizationId:me.orgRecord.get('id'),
									node:me.orgRecord.get('canonicalName')
								})
						},
					success : function(response){
					
						var respJson = Ext.decode(response.responseText);
						
						var treeStore = Ext.create('Ext.data.TreeStore', {
								root: {
									id:respJson.data.id,
									text:respJson.data.text,
									expanded:true,
									children:respJson.data.children
									}
							});
						var organizationTree = Ext.widget({
							xtype:'organizationtree',
							store:treeStore,
							parentOrganizationRecord:parentOrganizationRecord,
							});		    	
							
						var organizationTreeCont = me.down('#organizationTreeCont');
						organizationTreeCont.removeAll();
						organizationTreeCont.add(organizationTree);
					}
		});
		
				    		
    	   	
    },
    
    updateChildOrganizationDetail: function(childOrganizationId) {
    	var me=this;
    	
    	OrganizationManager.getOrganizationById(childOrganizationId, function(orgObj){
    		var record = Ext.create('Security.model.Organizations',orgObj);
    		
    		me.down('organizationdetailpane').enable();
    		me.down('organizationdetailpane').down('organizationdetailpaneheader').loadOrganizationDetail(record,function(){
        		
        		var realmConfigHolder=me.down('organizationdetailpane').down('#realmConfigHolder');

                realmConfigHolder.removeAll();
                realmConfigHolder.add({
        	        xtype:'realmconfigform',
        	    	organization:record,
        	    	securityRealms:record.get('securityRealms')
                });
                realmConfigHolder.down('realmconfigform').updatedRealmConfiguration(record.get('securityRealms')[0],false);    		
        	});    	
    		
    	},this);
    	
    	
    },
    
    resetChildOrganizationDetail : function(){
    	this.down('organizationdetailpane').down('organizationdetailpaneheader').reset();
    	this.down('organizationdetailpane').down('#realmConfigHolder').removeAll();
    },
    
    disableOrganizationDetail : function(){
    	this.down('organizationdetailpane').disable();    	
    }
    
});