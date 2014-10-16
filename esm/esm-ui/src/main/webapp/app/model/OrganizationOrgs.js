Ext.define('Security.model.OrganizationOrgs', {
    extend:'Ext.data.Model',
    idProperty:'id',
    fields:[
        {
            name:'id',
            type:'long'
        },
        {
            name:'text',
            type:'string'
        },
        {
            name:'leaf',
            type:'boolean'
        },
		{
            name:'children',
            type:'auto'
        },

    ]

});