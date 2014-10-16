Ext.define('Security.view.group.GroupsList', {
	extend: 'Security.view.common.BaseGridList',
    alias : 'widget.groupslist',    
    title : 'Groups',
	storeName:'GroupsList',
	enableSearch : true,
	enableCheckboxSel : false,
	hideHeaders:true,
    columns : [
        {
            header: 'Name', dataIndex: 'canonicalName', flex: 1,
            renderer:function(v,m,r) {
            	return Ext.String.format('<div id="{0}">{1}</div>', r.get('canonicalName'), v);
            }
        }
    ]
});