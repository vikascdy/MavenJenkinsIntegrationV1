
// MODEL: Saved Users
// Represents a Users file stored on a server, which may or may not be loaded.
// ----------------------------------------------------------------------------

Ext.define('SM.model.SavedUsers', {
    extend: 'Ext.data.Model',
    fields: [
         {name: 'username',      type:'string'},
         {name: 'fname',         type: 'string'},
         {name: 'lname',         type: 'string'},
         {name: 'email',         type: 'email'},
         {name: 'role',          type: 'string'}
    ]
});

