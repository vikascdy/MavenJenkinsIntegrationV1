Ext.define('Security.view.group.GroupRoleAssignments', {
    extend:'Ext.grid.Panel',
    alias:'widget.grouproleassignments',
    title:"Roles Assigned",
    margins:{top:0, right:0, bottom:15, left:0},
    hideHeaders:true,
    selModel:{mode:'MULTI'},
    store:'GroupRolesList',
    columns : [
        {
            header:'role',
            renderer : function(v, m, r) {
                return r.get('canonicalName');
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
						return "select-grid-row-groupsRolesAssigned";
						}
    },
    tools : [
        {
            type:'plus',
            tooltip:'Assign New Role',
            id:'addRoleToGroup',
            action:'newgroupassignment'
        },
        {
            type:'minus',
            tooltip:'Remove Role',
            id:'removeRoleFromGroup',
            action:'removegroupassignment'
        }
    ],
    dockedItems:[
        {
            xtype: 'pagingtoolbar',
            itemId: 'pagingToolbar',
            store: 'GroupRolesList',
            dock: 'bottom',
            displayInfo: false,
            listeners: {
                beforechange: function(paging, page) {
                    var me=this.up('grid');
                    me.getStore().getProxy().setExtraParam('groupId', me.group.get('id'));
                }
            }
        }]
});