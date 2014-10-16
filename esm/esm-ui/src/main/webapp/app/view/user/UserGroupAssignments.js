Ext.define('Security.view.user.UserGroupAssignments', {
    extend:'Ext.grid.Panel',
    alias:'widget.usergroupassignments',
    title:null,
    hideHeaders:true,
    flex:1,
    selModel:{mode:'MULTI'},
    store:'UserGroupsList',
    columns : [
        {
            header:'group',
            renderer : function(v,m,r){
                return r.get('canonicalName');
            },
            flex:1
        }
    ],
    viewConfig : {
        emptyText: 'There are no assigned roles.',
		getRowClass: function(record, rowIndex, rowParams, store){
						return "select-grid-row-userGroupsAssignment";
						}
    },
    initComponent : function(){
    
    	var me=this;	
    
	    this.tools = [
	        {
	            type:'plus',
	            hidden:me.readOnly ? me.readOnly :false,
	            tooltip:'Assign New Group',
	            id:'addGroupToUser',
	            action:'newgroupassignment'
	        },
	        {
	            type:'minus',
	            hidden:me.readOnly ? me.readOnly :false,
	            tooltip:'Remove Group',
	            id:'removeGroupFromUserr',
	            action:'removegroupassignment'
	        }
	    ];
	    
	    this.callParent(arguments);
    }

});