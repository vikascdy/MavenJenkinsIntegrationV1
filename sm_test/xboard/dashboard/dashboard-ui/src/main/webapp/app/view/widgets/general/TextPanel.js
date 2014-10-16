Ext.define('DD.view.widgets.general.TextPanel', {
    extend:'Ext.panel.Panel',
    alias :'widget.textpanel',
    style:{backgroundColor:'transparent'},
    html:'<div><center><b>Double click to add text.</b></center></div>',
    initComponent : function() {
        this.listeners = {
            'afterRender':function() {
                this.getEl().on('dblclick', function() {
                    Ext.widget({
                        xtype:'texteditorwidnow',
                        component:this
                    });
                });
            }
        };

        this.callParent(arguments);
    }
});
