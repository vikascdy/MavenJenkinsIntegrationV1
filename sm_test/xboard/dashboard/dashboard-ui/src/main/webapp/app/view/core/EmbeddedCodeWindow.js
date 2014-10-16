Ext.define('DD.view.core.EmbeddedCodeWindow', {
    extend:'Ext.window.Window',
    alias:'widget.embeddedcodewindow',
    resizable:true,
    draggable:false,
    modal:true,
    width:670,
    height:300,
    closeAction:'destroy',
    autoShow:true,
    layout:{type:'vbox',align:'stretch'},
    title:'Embedded Code',
    initComponent : function() {
        var me = this;
        var portlet = me.portlet;


        this.items = [
            {
                xtype:'textarea',
                flex:1
            }
        ];
        this.buttons = [
            {
                text:'Update',
                handler : function() {
                    var code = me.down('textarea');
                    var combo = me.down('combo');
                    var output = {};

                    WidgetManager.embeddedCode = code.getValue();

                    output = {
                        xtype: "uxiframe",
                        code:code.getValue(),
                        isEmbeddedWidget:true,
                        layout:'fit',
                        src: 'resources/eg-iframe.html'
                    };
                    portlet.updateWidget(output, function() {
                        me.close();
                    });

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