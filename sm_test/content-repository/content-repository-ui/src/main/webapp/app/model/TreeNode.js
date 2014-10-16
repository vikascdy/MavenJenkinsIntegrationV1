
// MODEL: Tree Node
// A generic "wrapper" model for any ConfigItem displayed in a TreePanel.
// ----------------------------------------------------------------------------

//Ext.require('SM.proxy.ConfigTreeProxy');

Ext.define('SM.model.TreeNode', {
    extend: 'Ext.data.Model',

    fields: [
         {name: 'type',   type: 'string', persist: false},
         {name: 'text',   type: 'string', persist: false},
         {name: 'status', type: 'string', persist: false},
         {name: 'object', type: 'auto',   persist: false}
    ]/*,

    proxy: {
        type: 'configtree',
        reader: {
            type: 'json'
        }
    }*/
});

