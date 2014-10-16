// VIEW: Patch Upload Window
// A popup window to upload patch files.
// ----------------------------------------------------------------------------


Ext.define('SM.view.core.PatchUploadWindow', {
    extend: 'Ext.window.Window',
    alias : 'widget.patchuploadwindow',
    id    : 'uploadWindow',
    
    title : 'Upload Patch',
    layout: 'fit',
    border: false,
    width : 500,
    height: 120,
    modal : true,
    padding: 10,
    resizable: false,
    draggable: false,
    autoShow: true,
    closeAction: 'destroy',

    initComponent: function() {
        this.items = [{
            xtype: 'form',
            padding:'0 0 0 10',
            itemId: 'uploadForm',
            border:false,
            defaults: { anchor:'100%'},
            items: [{
                xtype: 'filefield',
                itemId: 'patch-file',
                emptyText: 'Select a patch file',
                fieldLabel: 'Patch File',
                name: 'patch-file',
                allowBlank:false
            }, {
                xtype:'container',
                layout:'hbox',
                margin:'10 0 0 0',
                items: [{
                    xtype:'tbspacer',
                    flex:1
                }, {
                    xtype:'button',
                    text: 'Upload',
                    action: 'upload',
                    disabled: true,
                    formBind: true,
                    listeners: {
                        click: function(btn) {
                            var page = btn.up('patchuploadwindow');
                            page.uploadPatchFile();
                        }
                    }
                }, {
                    xtype:'button',
                    text: 'Cancel',
                    margin:'0 0 0 5',
                    scope: this,
                    handler: this.close
                }]
            }]
        }];

        this.callParent(arguments);
    },

    uploadPatchFile: function() {
        var loadingWindow = Ext.widget('progresswindow', {text: 'Evaluating patch file...'});
        var formPanel     = this.down('#uploadForm');
        var uploadWindow  = Ext.getCmp('uploadWindow');
        
        formPanel.getForm().submit({
            url: JSON_URL + '/config.evaluatePatch',
            success: function(form, action) {
                loadingWindow.destroy();
                uploadWindow.destroy();
                Ext.widget('patchoverviewwindow', {
                    patchData: action.result.patchData,
                    patchId  : action.result.patchId
                });
            },
            failure: function(form, action) {
                loadingWindow.destroy();
                uploadWindow.destroy();

                switch (action.failureType) {
                    case Ext.form.action.Action.CLIENT_INVALID:
                        Functions.errorMsg('You must provide a file to import.', 'Patch Failed');
                        break;
                    case Ext.form.action.Action.CONNECT_FAILURE:
                        Functions.errorMsg('Could not connect to server.', 'Patch Failed');
                        break;
                    case Ext.form.action.Action.SERVER_INVALID:
                        Functions.errorMsg(action.result.error, 'Invalid Patch');
                }
            }
        });
    }
});

