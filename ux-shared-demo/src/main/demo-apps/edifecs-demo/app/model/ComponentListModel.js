//Model for component tree list

Ext.define('PP.model.ComponentListModel', {
    extend:'Ext.data.Model',
    fields:[
        {name:'name', type:'string'},
        {name:'id', type:'string'},
        {name:'text', type:'string'},
        {name:'expanded', type:'boolean'},
        {name:'leaf', type:'boolean'}
    ]
});

