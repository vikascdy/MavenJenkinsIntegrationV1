Ext.define('DD.model.WidgetTypeListModel', {
    extend:'Ext.data.Model',
    fields:[
        {name:'properties', type:'auto'},
        {name:'id', type:'integer'},
        {name:'name', type:'string'},
        {name:'label', type:'string',convert : function(v, record) {
            var val=record.get('name');
            return val.charAt(0).toUpperCase() + val.substr(1, val.length);
        }},
        {name:'description', type:'string'},
        {name:'category', type:'string'},
        {name:'type', type:'string'},
        {name:'imageUrl', type:'string'},
        {name:'componentType',type:'string',defaultValue:'widgets'}
    ]
});

