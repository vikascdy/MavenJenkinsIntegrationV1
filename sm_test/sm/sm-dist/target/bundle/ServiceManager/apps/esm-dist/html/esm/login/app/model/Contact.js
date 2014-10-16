Ext.define('Security.model.Contact', {
    extend: 'Ext.data.Model',
    fields:[
        {name:'id', type:'long'},
        {name:'salutation', type:'string'},
        {name:'firstName', type:'string', sortType:'asUCText'},
        {name:'middleName', type:'string', sortType:'asUCText'},
        {name:'lastName', type:'string', sortType:'asUCText'},
        {name:'emailAddress', type:'string'},
        {name:'preferredLanguage',type: 'auto'},
        {name:'preferredTimezone',type: 'auto'},
        {name:'addresses',type:'auto'}
    ],
    idProperty: 'id'
});

