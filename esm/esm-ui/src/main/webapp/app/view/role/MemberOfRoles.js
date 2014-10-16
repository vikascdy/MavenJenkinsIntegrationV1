Ext.define('Security.view.role.MemberOfRoles', {
    extend:'Ext.grid.Panel',
    alias:'widget.memberofroles',
    selModel:{mode:'MULTI'},
    store:'MemberOfRoles',
    columns:[
        {
            header: 'Name',
            dataIndex: 'name',
            flex: 1
        }
    ],
    viewConfig:{
        emptyText: 'There are no users with this role.'
    }
});