Ext.define('DD.model.DataSetListModel', {
    extend:'Ext.data.Model',
    fields:[
        {name:'id', type:'string'},
        {name:'datasetType', type:Functions.childArrayType('DD.model.DataSetTypeListModel')},
        {name:'parameters', type:'auto'},
        {name:'name', type:'string'},
        {name:'description', type:'string'},
        {name:'query', type:'string'}
    ]
});

