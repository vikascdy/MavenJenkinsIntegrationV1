//Used to mask current view while component is loading files

Ext.define('PP.view.core.ProgressWindow', {
    extend:'Ext.window.Window',
    alias :'widget.progresswindow',
    layout       :'fit',
    width        :360,
    height       :160,
    //title : "Loading...",
    text         :"Loading...",
    modal        :true,
    autoShow     :true,
    closable     :false,
    resizable    :false,
    draggable    :false,
    style        :'zIndex:99999',
    initComponent:function () {
        this.items = [
            {
                xtype:'component',
                data :{text:this.text},
                cls  :'progress-window',
                tpl  :new Ext.XTemplate(
                    "<img src='images/site-loading.gif' alt='Loading...' />",
                    "<p>{text}</p>")
            }
        ];
        this.callParent(arguments);
    }
});

