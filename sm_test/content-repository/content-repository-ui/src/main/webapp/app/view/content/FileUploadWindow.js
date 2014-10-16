
// VIEW: File Upload Window
// A popup window to upload files to a Content Repository.
// ----------------------------------------------------------------------------

Ext.define('SM.view.content.FileUploadWindow', {
    extend: 'Ext.window.Window',
    alias : 'widget.fileuploadwindow',
    layout: 'fit',

    width : 500,
    height: 160,
    autoShow: true,
    modal: true,
    padding: '10',
    resizable: false,
    draggable: false,

    title : 'Upload File',
    closeAction: 'destroy',
    border: false,

    parentDir: null,

    items: [{
        xtype: 'form',
        padding: '0 0 0 10',
        itemId: 'uploadForm',
        border: false,
        defaults: {anchor: '100%'},
        items: [{
            xtype: 'displayfield',
            itemId: 'parentDir',
            fieldLabel: 'Parent Directory',
            value: 'Loading...'
        }, {
            xtype: 'filefield',
            itemId: 'upload',
            emptyText: 'Select a file to upload',
            fieldLabel: 'File',
            name: 'upload',
            allowBlank: false
        }, {
            xtype:  'container',
            layout: 'hbox',
            margin: '10 0 0 0',
            items: [{
                xtype:'tbspacer',
                flex:1
            }, {
                xtype: 'button',
                text: 'Upload',
                itemId: 'uploadButton',
                disabled: true,
                formBind: true
            }, {
                xtype:'button',
                text: 'Cancel',
                margin:'0 0 0 5',
                handler: function(btn) {
                    btn.up('window').close();
                }
            }]
        }]
    }],

    initComponent: function(config) {
        this.callParent(arguments);
        this.down('#parentDir').setValue(this.parentDir);
    }
});

