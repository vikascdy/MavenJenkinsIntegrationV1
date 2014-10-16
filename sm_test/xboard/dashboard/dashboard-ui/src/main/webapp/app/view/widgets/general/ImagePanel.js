Ext.define('DD.view.widgets.general.ImagePanel', {
    extend:'Ext.panel.Panel',
    alias :'widget.imagepanel',
    layout:'fit',
    style:{backgroundColor:'transparent'},
    html:'<div><center><b>Double click to add image.</b></center></div>',
    initComponent : function() {
        this.listeners = {
            'afterRender':function() {
                this.getEl().on('dblclick', function() {
                    Ext.widget({
                        xtype:'newimagewindow',
                        widget:this
                    });
                });
            }
        };

        this.callParent(arguments);
    }
});
