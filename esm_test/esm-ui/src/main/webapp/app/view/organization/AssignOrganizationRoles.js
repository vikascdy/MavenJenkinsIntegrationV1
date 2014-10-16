Ext.define('Security.view.organization.AssignOrganizationRoles', {
	extend: 'Ext.container.Container',
    alias : 'widget.assignorganizationroles',
    treeId:'assignRoles',
    initComponent : function(){
    	var me=this;
    	
    	 this.items = [
    	               {
    	                   xtype:'component',
    	                   itemId:'pageHeader',
    	                   region:'north',
    	                   padding:'20 0 0 20',
    	                   height:60,
    	                   html:'<h1>Organization Roles</h1>'
    	               },
    	               {
    	            	   xtype:'container',
    	            	   itemId:'detailCont',
    	                   region:'center',
	                	   layout:'fit',
	                	   flex:1,
    	                   padding:'0 20 0 20',
    	                   items:[{
    	                	   	   title:"Roles Assigned",
    	    	                   xtype:'organizationroles',    	    	                   
    	    	                   organization:me.organization,
    	    	                   flex:1
    	                   }]
    	               }
    	           ];
    	
    	this.callParent(arguments);
    }
    

});