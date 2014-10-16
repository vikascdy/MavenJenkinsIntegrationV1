Ext.define('Security.view.user.UsersList', {
    extend: 'Security.view.common.BaseGridList',
    alias : 'widget.userslist',
    title : 'Users',
	storeName:'UsersList',
	enableSearch : true,
	enableCheckboxSel : false,
	hideHeaders:true,
    columns : [
        {
            header: 'Name',
            dataIndex: 'name',
            flex: 1,
            renderer : function(v,m,r){
            	return Ext.String.format('<div id="{0}">{1}</div>', r.get('username'), v);
            }
        }
    ]
});