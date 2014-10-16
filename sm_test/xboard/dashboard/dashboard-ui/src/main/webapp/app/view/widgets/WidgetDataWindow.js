Ext.define('DD.view.widgets.WidgetDataWindow', {
    extend:'Ext.window.Window',
    alias:'widget.widgetdatawindow',
    resizable:false,
    draggable:true,
    modal:true,
    width:700,
    minHeight:300,
    maxHeight:500,
    closeAction:'destroy',
    layout:'fit',
    title:'Widget Data',
    initComponent : function() {
        var me = this;

        var fields = [];

        fields.push({
                name:me.widgetConfigObj.Xfields,type:'auto'
            },
            {
                name:me.widgetConfigObj.Yfields,type:'auto'
            });

        Ext.define('Model', {
            extend: 'Ext.data.Model',
            fields:fields
        });


        this.items = [
            {
                xtype:'grid',
                store:me.store,
                scaffold: {
                    target: Model,
                    oneStorePerModel: true,
                    deletable: false,
                    buttons: []
                }
            }
        ];

        this.buttons = [
            {
                text:'Close',
                handler: function() {
                    me.close();
                }
            }
        ];

        this.callParent(arguments);
    }
});