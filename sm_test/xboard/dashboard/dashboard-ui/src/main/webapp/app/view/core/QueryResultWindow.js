Ext.define('DD.view.core.QueryResultWindow', {
    extend:'Ext.window.Window',
    alias:'widget.queryresultwindow',
    resizable:true,
    draggable:true,
    modal:true,
    width:630,
    height:500,
    closeAction:'destroy',
    autoShow:true,
    layout:{type:'vbox',align:'stretch'},
    title:'Query Result',
    bodyPadding:5,
    initComponent : function() {
        var me = this;

        this.items = [
            {
                xtype:'panel',
                bodyPadding:10,
                html:'<h3>' + me.query + '</h3>'
            },
            {
                xtype:'panel',
                flex:1,
                bodyPadding:10,
                autoScroll:true,
                tbar : [
                    '->',
                    {
                        text:'Format Result',
                        handler : function() {
                            var editor = ace.edit("editor");
                            this.up('panel').update("<div id='editor'>" + js_beautify(me.queryResult) + "</div>");
                            me.showEditor();
                        }
                    }
                ],
                html: "<div id='editor'>" + js_beautify(me.queryResult) + "</div>"

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
    },
     showEditor : function() {
        var editor = ace.edit("editor");
        editor.setTheme("ace/theme/dreamweaver");
        editor.getSession().setMode("ace/mode/javascript");
    }
});