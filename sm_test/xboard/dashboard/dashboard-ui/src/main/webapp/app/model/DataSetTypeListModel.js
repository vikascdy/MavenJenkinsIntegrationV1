Ext.define('DD.model.DataSetTypeListModel', {
    extend:'Ext.data.Model',
    fields:[
        {name:'properties', type:'auto'},
        {name:'datastoreType', type:Functions.childArrayType('DD.model.DataSourceTypeListModel')},
        {name:'id', type:'integer'},
        {name:'name', type:'string'},
        {name:'description', type:'string'},
        {name:'category', type:'string'}
    ]
});

