Ext.define('DD.model.ParametersListModel', {
    extend:'Ext.data.Model',
    fields:[
        {name:'properties', type:'auto'},
        {name:'id', type:'string'},
        {name:'text', type:'string'},
        {name:'description', type:'string'},
        {name:'category', type:'string'},
        {name:'type', type:'string'},
        {name:'iconCls', type:'string'},
        {name:'controlType', type:'string'},
        {name:'componentType',type:'string',defaultValue:'parameters'}
    ]
});

