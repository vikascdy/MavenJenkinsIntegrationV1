Ext.define('SM.model.ActiveJob', {
    extend: 'Ext.data.Model',
    fields: [
         {name: 'name',         type: 'string'},
         {name: 'runTime',      type: 'string'},
         {name: 'triggers',  	type: 'string'},
         {name: 'location',      type: 'string'}
    ]
});

