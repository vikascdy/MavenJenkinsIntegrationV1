Ext.define('DD.model.DashboardListModel', {
    extend:'Ext.data.Model',
    fields:[
        {name:'id', type:'string'},
        {name:'xBoardType', type:'auto'},
        {name:'widgetIds', type: 'auto'},
        {name:'parameters', type:'auto'},
        {name:'name', type:'string'},
        {name:'description', type:'string'},
        {name:'category', type:'string'},
        {name:'configuration', type:'string'}

    ]
});

