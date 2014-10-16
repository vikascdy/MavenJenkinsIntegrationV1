Ext.define('DD.model.DataSourceListModel', {
    extend:'Ext.data.Model',
    fields:[
        {name:'id', type:'integer'},
        {name:'datastoreType', type:'auto'},
        {name:'datasourceTypeId', type:'string'},
        {name:'parameters', type:'auto'},
        {name:'name', type:'string'},
        {name:'category', type:'string'},
        {name:'description', type:'string'}
    ]
});

