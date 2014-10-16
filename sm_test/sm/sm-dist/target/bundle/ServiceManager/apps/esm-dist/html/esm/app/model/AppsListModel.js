Ext.define('Security.model.AppsListModel', {
    extend:'Ext.data.Model',
    fields:[
        {name:'id', type:'long'},
        {name:'name', type:'string'},
        {name:'description', type:'string'},
        {name:'version', type:'string'},
        {name:'displayVersion', type:'string'},
        {name:'appComponentList', type:'auto'},
        {name:'releaseDate', type:'string'},
        {name:'publishedBy', type:'string'},
        {name:'rating', type:'integer',defaultValue:5},
        {name:'pendingRequest' , type:'integer'},
        {name:'acceptedRequest' , type:'integer'}
    ]
});

