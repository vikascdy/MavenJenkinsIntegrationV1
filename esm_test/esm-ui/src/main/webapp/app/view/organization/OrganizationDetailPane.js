Ext.define('Security.view.organization.OrganizationDetailPane', {
	extend:'Ext.panel.Panel',
    alias:'widget.organizationdetailpane',
    padding:0,
    layout:'border',
    defaults : {
        style:{
            backgroundColor:'#FFF!important'
        }
    },
    initComponent : function() {
        this.items = [
            {
                xtype:'organizationdetailpaneheader',
                minHeight:120,
                region:'north'
            },
            {
            	xtype:'tabpanel',
            	region:'center',
            	items:[{
            			xtype:'container',
               	    	title:'Authentication Provider Settings',
               	    	itemId:'realmConfigHolder'
            	       }]
		    	
            }
        ];
        this.callParent(arguments);
    }
});