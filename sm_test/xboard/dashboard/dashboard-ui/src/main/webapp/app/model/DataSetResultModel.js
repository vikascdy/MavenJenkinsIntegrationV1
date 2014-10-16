Ext.define('DD.model.DataSetResultModel', {
    extend:'Ext.data.Model',
    fields:[
        {name:'data', type:'auto'},
        {name:'meta', type:'auto'},
        {name:'datasetId', type:'string'},
        {name:'index', type:'string'},
        {name:'type', type:'string'}
    ]
});

