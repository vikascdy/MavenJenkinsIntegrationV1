
// MODEL: Content Node
// Represents a single file or folder in a Content Repository.
// ----------------------------------------------------------------------------

Ext.define("SM.model.ContentNode", {
    extend: 'Ext.data.Model',

    fields: [
        {name: 'id',        type: 'string'},
        {name: 'name',      type: 'string'},
        {name: 'version',   type: 'string'},
        {name: 'mimeType',  type: 'string'},
        {name: 'typeName',  type: 'string'},
        {name: 'directory', type: 'boolean'},
        {name: 'leaf',      type: 'boolean'} // Tree compatibility
    ],

    getContextMenu: function() {
        var menuCls = this.get('leaf') ? 'SM.view.content.FileContextMenu' : 'SM.view.content.FolderContextMenu';
        return Ext.create(menuCls, {node: this});
    }
});

