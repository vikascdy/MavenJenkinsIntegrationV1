Ext.define('Security.model.Role', {
    extend: 'Ext.data.Model',
    fields:[
        {name:'id', type:'long'},
        {name:'canonicalName', type:'string', sortType:'asUCText'},
        {name:'description', type:'string'},
        {name:'readOnly', type:'boolean'},
        {name:'roleType', type:'string'}
    ],
    idProperty: 'id'
});

