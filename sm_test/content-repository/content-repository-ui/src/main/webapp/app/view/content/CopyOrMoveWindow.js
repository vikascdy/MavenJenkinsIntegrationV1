
// VIEW: Copy or Move Window
// Provides an interface for the user to select a folder and filename to copy
// or move an existing file or folder to.
// ----------------------------------------------------------------------------

Ext.define("SM.view.content.CopyOrMoveWindow", {
    extend: 'Ext.window.Window',
    alias : 'widget.copyormovewindow',

    title : 'Copy or Move',
    width : 400,
    height: 560,
    autoShow: true,
    layout: 'fit',
    resizable:false,
    draggable:false,
    node: null,
    modal:true,
    items: [{
        xtype: 'form',
        bodyPadding: 4,
        layout: {
            type : 'vbox',
            align: 'stretch'
        },
        items: [{
            xtype: 'displayfield',
            height: 24,
            name: 'oldPath',
            itemId: 'oldPath',
            fieldLabel: 'Original File'
        }, {
            xtype: 'textfield',
            height: 24,
            name: 'filename',
            itemId: 'filename',
            fieldLabel: 'New Filename'
        }, {
            xtype: 'repositorytree',
            itemId: 'folders',
            title: 'Select Target Directory',
            flex: 1,
            foldersOnly: true
        }]
    }],

    buttons: [{
        text: 'Copy',
        itemId: 'copy',
        iconCls: 'mico-copy'
    }, {
        text: 'Move',
        itemId: 'move',
        iconCls: 'mico-export'
    }, {
        text: 'Cancel',
        itemId: 'cancel',
        iconCls: 'mico-cancel',
        handler: function(btn) {btn.up('window').close();}
    }],

    initComponent: function(config) {
        this.callParent(arguments);
        this.down('form').getForm().setValues({
            oldPath: this.node.get('id'),
            filename: this.node.get('id').split('/').pop()
        });
    }
});

