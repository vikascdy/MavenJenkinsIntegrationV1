Ext.define('Security.view.role.IncludeRoles', {
    extend:'Ext.grid.Panel',
    alias:'widget.includeroles',
    selModel:{mode:'MULTI'},
    store:'IncludeRoles',
    columns:[
        {
            header: 'canonicalName', dataIndex: 'canonicalName', flex: 1
        }
    ],
    viewConfig:{
        emptyText: 'There are no included roles',
		getRowClass: function(record, rowIndex, rowParams, store){
						return "select-grid-row-includeRoles";
					}
    },
    tools:[
        {
            type:'plus',
            tooltip:'Include New Role',
            id:'addRoleToRole',
            action:'includenewrole'
        },
        {
            type:'minus',
            tooltip:'Remove Role',
            id:'removeRoleFromRole',
            action:'removememberrole'
        }
    ]
});