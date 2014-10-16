Ext.define('DD.model.DataSourceTypeListModel', {
    extend:'Ext.data.Model',
    fields:[
        {name:'impClass', type:'string'},
        {name:'properties', type:'auto'},
        {name:'id', type:'integer'},
        {name:'name', type:'string'},
        {name:'description', type:'string'},
        {name:'category', type:'string'}
    ]
});

