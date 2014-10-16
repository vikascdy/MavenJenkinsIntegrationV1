Ext.define('Security.model.Contact', {
    extend: 'Ext.data.Model',
    requires: [
        'Security.model.Language',
        'Security.model.TimeZone'
    ],
    fields:[
        {name:'id', type:'long'},
        {name:'salutation', type:'string'},
        {name:'firstName', type:'string', sortType:'asUCText'},
        {name:'middleName', type:'string', sortType:'asUCText'},
        {name:'lastName', type:'string', sortType:'asUCText'},
        {name:'emailAddress', type:'string'},
        {name:'preferredLanguage',type: Functions.childArrayType('Security.model.Language')},
        {name:'preferredTimezone',type: Functions.childArrayType('Security.model.TimeZone')},
        {name:'addresses',type:'auto'}
    ],
    idProperty: 'id'
});

