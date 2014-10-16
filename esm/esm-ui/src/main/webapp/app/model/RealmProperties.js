Ext.define('Security.model.RealmProperties', {
    extend: 'Ext.data.Model',
    idProperty: 'id',
    fields:[
            {name:'id', type:'long'},
            {name:'propertyId', type:'long',defaultValue:0},
            {name:'name', type:'string'},
            {name:'value', type:'string'},
            {name:'description', type:'string'},            
            {name:'required', type:'boolean'}
    ] 
});

