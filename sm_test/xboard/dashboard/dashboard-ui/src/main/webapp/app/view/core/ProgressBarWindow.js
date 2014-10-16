Ext.define('DD.view.core.ProgressBarWindow', {
    extend: 'Ext.window.Window',
    alias : 'widget.progressbarwindow',
    layout: 'fit',
    width : 360,
    height: 30,
    header:false,
    text  : "Initializing...",
    modal : true,
    autoShow: true,
    closable: false,
    resizable: false,
    movable: false,

    initComponent: function() {
        var me = this;

        this.items = [
            {
                xtype: 'progressbar',
                cls:'progress-status-text',
                id:'progressBarStatus',
                text:me.text
            }
        ];

        this.callParent(arguments);
    }
});

