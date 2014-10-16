Ext.define('Security.view.role.RolesList', {
	extend: 'Security.view.common.BaseGridList',
    alias : 'widget.roleslist',    
    title : 'Roles',
	storeName:'RolesList',
	enableSearch : true,
	enableCheckboxSel : false,
	hideHeaders:true,
    columns : [
        {
            header: 'canonicalName', dataIndex: 'canonicalName', flex: 1,
            renderer:function(v,m,r) {
            	return Ext.String.format('<div id="{0}" class="new-item-icon" style="padding-left: 25px;">{1}</div>', r.get('canonicalName'), v);
            }
        }
    ]
});
