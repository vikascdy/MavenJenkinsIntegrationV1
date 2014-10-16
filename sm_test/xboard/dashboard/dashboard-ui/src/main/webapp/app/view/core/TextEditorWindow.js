Ext.define('DD.view.core.TextEditorWindow', {
    extend:'Ext.window.Window',
    alias:'widget.texteditorwidnow',
    resizable:false,
    draggable:false,
    modal:true,
    width:670,
    height:250,
    closeAction:'destroy',
    layout:'fit',
    title:'Text Editor',
    initComponent : function() {
        var me = this;
        var widget = me.portlet;
        var widgetId = widget.id;
        var widgetRef = Ext.getCmp(widgetId);


//        var initialTextDiv = widgetRef.dom.getElementsByTagName('div')[0];
//        var initialText = initialTextDiv ? initialTextDiv.firstChild : '';
        this.items = [
            {
                xtype:'htmleditor',
//                value:initialText.outerHTML,
                enableSourceEdit:true,
                enableLists:false
            }
        ];
        this.buttons = [
            {
                text:'Update',
                handler : function() {
                    var editor = me.down('htmleditor');
                    var text = '<div class="' + widgetId + '_text editor-text" >' + editor.getValue() + '</div>';
                    widgetRef.updateWidget({
                        xtype:'component',
                        isTextWidget:true,
                        html:text
                    }, function() {
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