Ext.define('Security.view.core.UploadWindow', {
    extend: 'Ext.window.Window',
    alias : 'widget.uploadwindow',
    id    : 'uploadWindow',
    layout: 'fit',
    border: false,
    width : 500,
    height: 140,
    modal : true,
    resizable: false,
    autoShow: true,
    closeAction: 'destroy',

    initComponent: function() {
        var me=this;
        this.title = this.heading ? this.heading : 'Import Files';
       
        this.items = [{
            xtype: 'form',
            padding:'10',
            itemId: 'uploadForm',
            border:false,
            defaults: { anchor:'100%'},
            items: [
            {
                xtype: 'textfield',
                hidden:true,
                name:me.paramName,
                value:me.paramValue
            },{
                xtype: 'filefield',
                emptyText: 'Select user file',
                name: 'inputStream',
                allowBlank:false,
                vtype:'fileUpload',
                msgTarget:'under'
            }],
            buttons:[{
                    xtype:'button',
                    text: 'Upload',
                    action: 'upload',
                    formBind: true,
                    listeners: {
                        click: function(btn) {
                            var page = btn.up('window');
                            page.uploadUserFile(page);
                        }
                    }
                }, {
                    xtype:'button',
                    text: 'Cancel',
                    margin:'0 0 0 5',
                    scope: this,
                    handler: this.close
                }]
        }];

        this.callParent(arguments);
    },

    uploadUserFile: function(uploadWindow) {
        var loadingWindow = Ext.widget('progresswindow', {text: 'Uploading file...'});
        var formPanel     = this.down('#uploadForm');
        var me=this;
        formPanel.getForm().submit({
            url: JSON_SERVLET_PATH + "service/esm-service/"+me.targetUrl,
            waitMsg: 'Processing file...',
            success: function(form, action) {
                var respJson = Ext.decode(action.response.responseText);
                if(respJson.success==true){
                    loadingWindow.destroy();
                    uploadWindow.destroy();                   
					var totalRecords = respJson.data.length;
                     me.grid.getStore().load({
                                callback:function () {
                                    console.log( me.grid.getStore());
                                    var record =
                                        me.grid.getStore().getAt(0);
                                    if (record) {
                                        me.grid.getSelectionModel().select(record);
                                        me.grid.updateLayout();
										Functions.errorMsg('"'+totalRecords+'" successful import(s).', 'Success');
                                   }
                                }
                            });
                 }
            },
            failure: function(form, action) {
                loadingWindow.destroy();
                uploadWindow.destroy();

                switch (action.failureType) {
                    case Ext.form.action.Action.CLIENT_INVALID:
                        Functions.errorMsg('You must provide a file to import.', 'File Upload Failed');
                        break;
                    case Ext.form.action.Action.CONNECT_FAILURE:
                        Functions.errorMsg('Could not connect to server.', 'File Upload Failed');
                        break;
                    case Ext.form.action.Action.SERVER_INVALID:
                        Functions.errorMsg(action.result.error, 'Invalid File');
                }
            }
        });
    }
});

