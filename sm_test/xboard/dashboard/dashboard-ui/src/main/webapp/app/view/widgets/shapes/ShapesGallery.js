Ext.define('DD.view.widgets.shapes.ShapesGallery', {
    extend:'Ext.toolbar.Toolbar',
    alias :'widget.shapesgallery',
//    ui:'dashboardDesigner-widgetlibrary',
    initComponent : function() {
        this.store = Ext.create('DD.store.ShapesGalleryStore');
        this.callParent(arguments);
    },
    afterRender:function()
    {
        var me = this;
        this.store.each(function(record){
            me.add(
                    {
                        xtype:'button',
                        padding:'5px 5px',
                        ui:'widgetConfiguration',
                        icon:record.get('url'),
                        id:record.get('id'),
                        scale:'large',
                        text:'<span style="color:#333">'+record.get('name')+'</span>'
                    }
            );
        });
        this.callParent(arguments);
    }

});