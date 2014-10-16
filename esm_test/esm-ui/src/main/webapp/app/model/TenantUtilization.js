Ext.define('Security.model.TenantUtilization', {
    extend: 'Ext.data.Model',
    idProperty: 'id',
    fields:[
	        {name:'id', type:'long'},
	        {name:'name', type:'string'},
	        {name:'currentValue', type:'long'},
	        {name:'totalValue', type:'long'},
	        {name:'valueUnit', type:'string'}
        ]

});

