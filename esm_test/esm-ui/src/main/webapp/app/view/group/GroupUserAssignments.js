Ext.define('Security.view.group.GroupUserAssignments', {
    extend:'Ext.grid.Panel',
    alias:'widget.groupuserassignments',
    title:"Users Included",
    hideHeaders:true,
    selModel:{mode:'MULTI'},
    store:'GroupUsersList',
    columns : [
        {
            header:'User',
            renderer : function(v, m, r) {
                return r.get('name');
            },
            flex:1
        }
    ],
    viewConfig : {
        emptyText: 'There are no users in this group.',
		getRowClass: function(record, rowIndex, rowParams, store){
						return "select-grid-row-groupUserAssignments";
						}
    },
    tools : [
        {
            type:'plus',
            tooltip:'Add User To Group',
            id:'addUserToGroup',
            action:'newuserassignment'
        },
        {
            type:'minus',
            tooltip:'Remove User From Group',
            id:'removeUserFromGroup',
            action:'removeuserassignment'
        }
    ],
    dockedItems:[
        {
            xtype: 'pagingtoolbar',
            itemId: 'pagingToolbar',
            store: 'GroupUsersList',
            dock: 'bottom',
            displayInfo: false,
            listeners: {
                beforechange: function(paging, page) {
                    var me=this.up('grid');
                    me.getStore().getProxy().setExtraParam('groupId', me.group.get('id'));
                }
            },
        }]

});