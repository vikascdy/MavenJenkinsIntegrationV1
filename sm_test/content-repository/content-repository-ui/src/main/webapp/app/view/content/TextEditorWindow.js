
// VIEW: Text Editor Window
// A full-featured text editor that allows the user to view, edit, and save
// plaintext files in the UI.
// ----------------------------------------------------------------------------

Ext.define("SM.view.content.TextEditorWindow", {
    extend: 'Ext.window.Window',
    alias : 'widget.texteditorwindow',

    title : 'Text Editor',
    layout: 'fit',
    width : 640,
    height: 560,
    autoShow: true,
    modal:true,
    maximizable: true,
    resizable:false,
    draggable:false,
    filepath: null,
    filedata: null,
    closeAction: 'destroy',
    tbar: [{
        text: 'Save',
        itemId: 'save',
        iconCls: 'mico-save'
    }, {
        text: 'Revert',
        itemId: 'revert',
        iconCls: 'mico-refresh'
    }
//        , '->', {
//        xtype: 'textfield',
//        emptyText: 'Search',
//        itemId: 'search'
//    }
        ],

    items: [{
        xtype : 'textareafield',
        itemId: 'textarea',
        cls   : 'text-editor-field'
    }],

    initComponent: function(options) {
        this.bbar = [{
            xtype: 'displayfield',
            fieldLabel: 'Editing File',
            labelWidth: 64,
            value: this.filepath
        }];
        this.title = 'Text Editor (' + this.filepath.split('/').pop() + ')';
        this.callParent(arguments);
        this.down('#textarea').setValue(this.filedata);
    }
});

