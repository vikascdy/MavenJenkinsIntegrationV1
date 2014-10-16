Ext.define('DD.view.widgets.charts.ChartsGallery', {
    extend:'Ext.toolbar.Toolbar',
    alias :'widget.chartsgallery',
    ui:'dashboardDesigner-widgetlibrary',
    previousButton:null,
    initComponent : function() {
        this.store = Ext.create('DD.store.ChartsGalleryStore');
        this.callParent(arguments);
    },
    afterRender:function()
    {
        var me = this;
        this.store.each(function(record){
            me.add(
                    {
                        xtype:'button',
                        enableToggle:true,
                        padding:'10px 15px',
                        ui:'widgetConfiguration',
                        icon:record.get('url'),
                        id:record.get('id'),
                        scale:'large',
						selectedWidget:record,
                        listeners:{
                            'click':function()
                            {
                                this.toggle(true);
                                if (me.previousButton != null)
                                {
                                    me.previousButton.toggle(false);
                                }
                                me.previousButton = this;
                            }
                        }
                    }
            );

        });
        this.callParent(arguments);
    }
});