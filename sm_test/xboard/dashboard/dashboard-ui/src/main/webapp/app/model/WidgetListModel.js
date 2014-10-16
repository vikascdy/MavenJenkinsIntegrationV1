Ext.define('DD.model.WidgetListModel', {
    extend:'Ext.data.Model',
    fields:[
        {name:'id', type:'integer'},
        {name:'datasetId', type:'integer'},
        {name:'widgetTypeID', type:'integer'},
        {name:'parameters', type:'auto'},

        {name:'widgetType', type: Functions.childArrayType('DD.model.WidgetTypeListModel')},
        {name:'widgetCategory' , type:'string',convert : function(v,record){
            var widgetType=record.get('widgetType')[0];
            return widgetType.get('category');
        }},
        {name:'dataset',   type:'auto'},


        {name:'name', type:'string'},
        {name:'description', type:'string'},
        {name:'widgetXtype', type:'string'},
        {name:'imageUrl', type:'string'}

    ]
});

