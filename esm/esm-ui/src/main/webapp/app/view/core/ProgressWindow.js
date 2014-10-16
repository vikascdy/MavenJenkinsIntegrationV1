
// VIEW: Progress Window
// Modal dialog that shows a loading spinner while a long-running process is in
// progress. This dialog is not closable once opened; make sure to have some
// kind of timer that will destroy the window when the process is finished!
// ----------------------------------------------------------------------------

Ext.define('Security.view.core.ProgressWindow', {
    extend: 'Ext.window.Window',
    alias : 'widget.progresswindow',
    
    layout: 'fit',
    width : 360,
    height: 130,
//    title : false,
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
                    "<img src='../resources/images/site-loading.gif' alt='Loading...' />",
                    "<p>{text}</p>")
        }];
        this.callParent(arguments);
    }
});

