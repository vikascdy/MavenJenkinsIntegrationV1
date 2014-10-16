Ext.define('Security.model.SubSitesListModel', {
    extend:'Ext.data.Model',
    fields:[
        {name:'name', type:'string'},
        {name:'formattedDueDate', type:'string'},
        {name:'assignee', type:'string'},
        {name:'description', type:'string'},
        {name:'owner', type:'string'}
    ]
});

