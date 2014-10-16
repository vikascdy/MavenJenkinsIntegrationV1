Ext.define('Security.view.user.UserRoleAssignments', {
    extend:'Ext.grid.Panel',
    alias:'widget.userroleassignments',
    title:null,
    hideHeaders:true,
    flex:1,
    selModel:{mode:'MULTI'},
    store:'UserAssignedRolesList',
    columns : [
        {
            header:'role',
            renderer : function(v,m,r){
                return  r.get('canonicalName');
            },
            flex:1
        }
    ],
    features: [{
    	groupHeaderTpl: 'Type : {name}',
        ftype: 'groupingsummary'
    }],
    viewConfig : {
        emptyText: 'There are no assigned roles.',
		getRowClass: function(record, rowIndex, rowParams, store){
						return "select-grid-row-userRolesAssignment";
						}
    },
    initComponent : function(){
        
    	var me=this;	
    
	    this.tools = [
	        {
            type:'plus',
            hidden:me.readOnly ? me.readOnly :false,
            tooltip:'Assign New Role',
            id:'assignRoleToUser',
            action:'newuserassignment'
        },
        {
            type:'minus',
            hidden:me.readOnly ? me.readOnly :false,
            tooltip:'Remove Role',
            id:'removeRoleFromUser',
            action:'removeuserassignment'
        }
	    ];
	    
	    this.callParent(arguments);
    }
});