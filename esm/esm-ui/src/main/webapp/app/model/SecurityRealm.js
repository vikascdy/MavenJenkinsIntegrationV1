Ext.define('Security.model.SecurityRealm', {
    extend: 'Ext.data.Model',
    idProperty: 'id',
    fields:[
            {name:'id', type:'long'},
            {name:'name', type:'string'},
            {name:'enabled', type:'boolean'},
            {name:'realmType', type:'string'},            
            {name:'properties', type : 'auto'}
    ]
   
});

