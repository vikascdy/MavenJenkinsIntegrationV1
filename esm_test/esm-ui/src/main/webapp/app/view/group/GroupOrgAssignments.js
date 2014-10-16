Ext.define('Security.view.group.GroupOrgAssignments', {
    extend:'Ext.grid.Panel',
    alias:'widget.grouporgassignments',
    title:"Organizations Included",
    hideHeaders:true,
    selModel:{mode:'MULTI'},
    store:'GroupOrgsList',
    columns : [
        {
            header:'role',
            renderer : function(v, m, r) {
                return r.get('canonicalName');
            },
            flex:1
        }
    ],
    viewConfig : {
        emptyText: 'There are no organizations in this group.',
		getRowClass: function(record, rowIndex, rowParams, store){
						return "select-grid-row-groupOrgAssignments";
						}
    },
    tools : [
        {
            type:'plus',
            tooltip:'Add Organization To Group',
            id:'addOrganizationToGroup',
            action:'neworgassignment'
        },
        {
            type:'minus',
            tooltip:'Remove Organization From Group',
            id:'removeOrganizationFromGroup',
            action:'removeorgassignment'
        }
    ],
    dockedItems:[
        {
            xtype: 'pagingtoolbar',
            itemId: 'pagingToolbar',
            store: 'GroupOrgsList',
            dock: 'bottom',
            displayInfo: false,
            listeners: {
                beforechange: function(paging, page) {
                    var me=this.up('grid');
                    me.getStore().getProxy().setExtraParam('id', me.group.get('id'));
                }
            },
        }]

});