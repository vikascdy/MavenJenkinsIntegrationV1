Ext.define('DD.view.widgets.WidgetPreviewPanel', {
    extend:'Ext.panel.Panel',
    alias:'widget.widgetpreviewpanel',
    layout:'fit',
    bodyPadding:10,
    flex:1,
    isWidgetSelected:false,
    items:[
        {
            xtype:'component',
            html:'<center><h2>Preview</h2></center>'
        }
    ],
    style:{backgroundColor: '#ced5d9'},

    updateWidgetPreview : function(widget, callback) {
        this.removeAll();
        this.add(widget);
        this.isWidgetSelected = true;
        Ext.callback(callback, this, []);
    },

    getWidget : function() {
        if (this.isWidgetSelected)
            return this.getLayout().getLayoutItems()[0];
        else
            return null;
    }
});