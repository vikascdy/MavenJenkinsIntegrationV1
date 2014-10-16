Ext.define('Security.model.Permission', {
    extend      : 'Ext.data.Model',
    fields      : [
        {name:'id', type:'int'},
        {name:'canonicalName', type:'string'},
        {name:'permissions', type:'string'},
        {name:'productCanonicalName', type:'string'},
        {name:'categoryCanonicalName', type:'string'},
        {name:'typeCanonicalName', type:'string'},
        {name:'subTypeCanonicalName', type:'string'},
        {name:'sortOrder', type:'int'}
    ]
});