Ext.define('DD.view.core.NewImageWindow', {
    extend:'Ext.window.Window',
    alias:'widget.newimagewindow',
    resizable:false,
    draggable:true,
    modal:true,
    minHeight:500,
    maxHeight:700,
    width:1000,
    closeAction:'destroy',
    layout:{
        type:'vbox',
        align:'stretch'
    },
    title:'Image Library',

    initComponent : function() {
        var me = this;
        var widget = me.portlet;
        var widgetId = widget.id;
        var widgetRef = Ext.getCmp(widgetId);

        this.dockedItems = [
            {
                xtype:'toolbar',
                dock:'top',
                ui:'dashboardDesigner-widgetlibrary',
                padding:'10 7 10 10',
                items:[
                    {
                        text:'Add Image',
                        ui:'widgetlibrary',
                        scale:'small',
                        itemId:'customWidget',
                        padding:'0px 10px',
                        pressed:true,
                        handler : function(btn) {
                            Ext.getCmp("uploadForm").show(btn.getEl());
                        }
                    },
                    '->',
                    {
                        xtype:'textfield',
                        emptyText:'Search Image'
                    }
                ]
            }
        ];

        this.items = [
            {
                xtype:'form',
                hidden:true,
                id:'uploadForm',
                height:200,
                bodyPadding:5,
                defaults:{anchor:'100%'},
                layout:'anchor',
                items:[
                    {
                        xtype:'textfield',
                        allowBlank:false,
                        flex:1,
                        fieldLabel:'Name',
                        name:'Name'
                    },
                    {
                        xtype:'textarea',
                        flex:1,
                        fieldLabel:'Description',
                        name:'Description'
                    },
                    {
                        xtype:'filefield',
                        id:'imageField',
                        allowBlank:false,
                        name:'Image',
                        fieldLabel:'Image',
                        accept        : ['jpg','jpeg','png','gif','bmp'],
                        listeners     : {
                            validitychange : function(me) {
                                var indexofPeriod = me.getValue().lastIndexOf("."),
                                    uploadedExtension = me.getValue().substr(indexofPeriod + 1, me.getValue().length - indexofPeriod);
                                if (!Ext.Array.contains(this.accept, uploadedExtension)) {
                                    me.setActiveError('Please upload files with an extension of :  ' + this.accept.join() + ' only!');
                                    Ext.MessageBox.show({
                                        title   : 'File Type Error',
                                        msg   : 'Please upload files with an extension of :  ' + this.accept.join() + ' only!',
                                        buttons : Ext.Msg.OK,
                                        icon  : Ext.Msg.ERROR
                                    });
                                    me.setRawValue(null);
                                }
                            }
                        }
                    }
                ],
                buttons:[
                    {
                        xtype:'button',
                        formBind:true,
                        text:'Upload',
                        margin:'0 5 0 5',
                        handler: function(btn) {
                            var form = btn.up('form').getForm();
                            if (form.isValid()) {
                                var values=form.getValues();
                                var filename = Ext.getCmp('imageField').getValue().split('/').pop().split('\\').pop();
                                var widgetProperties={
                                    Name:values.Name,
                                    Description:values.Description,
                                    Image:null
                                };
                                form.submit({
                                    url:JSON_SERVLET_PATH + 'createWidget',
                                    method:'POST',
                                    params:{
                                        data : Ext.encode({
                                            dataSetId:null,
                                            widgetTypeId:WidgetManager.activeWidget.id,
                                            widgetProperties : Ext.encode(widgetProperties)
                                        })
                                    },
//                                    waitMsg: 'Uploading Image...',
                                    success: function(fp, o) {
                                        if (o.result) {
                                            var imageGallery = me.down('imagegallery');
                                            imageGallery.getStore().load();
                                        }

                                    }
                                });
                            }
                            else
                                Ext.Msg.alert('Invalid Image', 'Please provide an image to upload.');
                        }
                    },
                    {
                        xtype:'button',
                        text:'Cancel',
                        margin:'0 5 0 5',
                        handler: function(btn) {
                            Ext.getCmp("uploadForm").hide(btn.getEl());
                        }
                    }
                ]
            },
//            {
//                xtype:'container',
//                flex:1,
//                padding:5,
//                layout:{type:'hbox',align:'stretch',pack:'center'},
//                items:[
//                    {
//                        xtype:'panel',
//                        itemId:'imagePreview',
//                        bodyPadding:5,
//                        flex:1,
//                        html:'<center><h2>Image Preview</h2></center>'
//                    },
            {
                xtype:'imagegallery',
                autoScroll:true,
                flex:1
            }
//                ]
//            }
        ];
        this.buttons = [
            {
                text:'Delete',
                handler : function() {
                    var imageGallery = me.down('imagegallery');
                    var selection = imageGallery.getSelectionModel().getSelection();
                    if (selection.length > 0) {
                        var selected = selection[0];
                        MediaManager.deleteImage(selected.get('name'), function() {
                            imageGallery.getStore().load();
                        });
                    }
                    else
                        Ext.Msg.alert('Image gallery', 'Please first select an image to delete.');
                }
            },
            '->',
            {
                text:'Add to Dashboard',
                handler : function() {
                    var imageGallery = me.down('imagegallery');
                    var selection = imageGallery.getSelectionModel().getSelection();
                    if (selection.length > 0) {
                        var selected = selection[0];
                        widgetRef.updateWidget({
                            xtype:'container',
                            isImageWidget:true,
                            layout:'fit',
                            configObj:{parameterFields:[],info:selected.data},
                            html:'<img width=100% height=100% src="' + selected.get('url') + '" title="'+selected.get('name') +'"/>'
                        });

                        me.close();
                    }
                    else
                        Ext.Msg.alert('Image gallery', 'Please select an image from the gallery first.');

                }
            },
            {
                text:'Cancel',
                handler: function() {
                    me.close();
                }
            }
        ];

        this.callParent(arguments);
    }
});