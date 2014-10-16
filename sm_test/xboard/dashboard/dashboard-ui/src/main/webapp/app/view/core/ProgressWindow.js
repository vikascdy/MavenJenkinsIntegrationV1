Ext.define('DD.view.core.ProgressWindow', {
    extend: 'Ext.window.Window',
    alias : 'widget.progresswindow',
    
    layout: 'fit',
    width : 360,
    height: 130,
    header:false,
    text  : "Loading...",
    modal : true,
    autoShow: true,
    closable: false,
    resizable: false,
    movable: false,
    
    initComponent: function() {
        this.items = [{
            xtype: 'component',
            data : {text: this.text},
            cls: 'progress-window',
            tpl: new Ext.XTemplate(
                    "<img src='resources/images/site-loading.gif' alt='Loading...' />",
                    "<p>{text}</p>")
        }];
        this.callParent(arguments);
    }
});

