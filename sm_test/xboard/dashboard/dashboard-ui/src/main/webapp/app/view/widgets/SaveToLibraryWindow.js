Ext.define('DD.view.widgets.SaveToLibraryWindow', {
    extend:'Ext.window.Window',
    alias:'widget.savetolibrarywindow',
    draggable:true,
    resizable:false,
    minHeight:200,
    width:600,
    bodyPadding:20,
    modal:true,
    layout:'fit',
    title:'Save Widget',
    initComponent : function() {
        var me = this;

        this.items = [
            {
                xtype:'savetolibraryform',
                widget:WidgetManager.activeWidget
            }
        ];
        this.buttons = [
            {
                text: 'Cancel',
                handler : function() {
                    me.close();
                }
            },
            {
                text: 'Save',
                iconCls:'save',
                itemId:'saveWidget'
            }
        ];
        this.callParent(arguments);
    }
});
