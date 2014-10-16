
// MODEL: Saved Config
// Represents a config file stored on a server, which may or may not be loaded.
// ----------------------------------------------------------------------------

Ext.define('SM.model.SavedConfig', {
    extend: 'Ext.data.Model',
    fields: [
         {name: 'name',         type: 'string'},
         {name: 'filename',     type: 'string'},
         {name: 'description',  type: 'string'},
         {name: 'version',      type: 'string'},
         {name: 'active',       type: 'boolean'},
         {name: 'lastModified', type: Functions.timestampType()}
    ]
});

