Ext.define('Security.view.user.UsersUploadWindow', {
    extend: 'Ext.window.Window',
    alias : 'widget.usersuploadwindow',
    id    : 'uploadWindow',
    
    title : 'Import Users',
    layout: 'fit',
    border: false,
    width : 500,
    height: 140,
    modal : true,
    resizable: false,
    autoShow: true,
    closeAction: 'destroy',

    initComponent: function() {
        this.items = [{
            xtype: 'form',
            padding:'10',
            itemId: 'uploadForm',
            border:false,
            defaults: { anchor:'100%'},
            items: [{
                xtype: 'filefield',
                emptyText: 'Select user file',
                name: 'stream',
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
        
        formPanel.getForm().submit({
        	url: JSON_SERVLET_PATH + "upload/Service/esm-service/user.validateUsersCSV",
        	waitMsg: 'Processing file...',
            success: function(form, action) {
            	var respJson = Ext.decode(action.response.responseText);
            	
            	if(respJson.success==true){
                    loadingWindow.destroy();
                    uploadWindow.destroy();
                    
                    var batchUsersList = Ext.widget({
                    	xtype:'batchuserslist',
                    	result : respJson.result
                    });
                    batchUsersList.show();
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

