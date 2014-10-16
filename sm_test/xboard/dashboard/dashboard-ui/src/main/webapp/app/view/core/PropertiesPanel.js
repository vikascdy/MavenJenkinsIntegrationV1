Ext.define('DD.view.core.PropertiesPanel', {
    extend:'Ext.grid.property.Grid',
    alias:'widget.propertiespanel',
    config :{
        widgetType:null,
        widgetHolderId:null,
        widgetInfo:null
    },
    overflowX:'hidden',
    initComponent : function() {
        var me = this;
        if (this.showUpdateButton) {
            this.tbar = [
                '->',
                {
                    text:'Update Properties',
                    iconCls:'apply',
                    itemId:'updateProperties',
                    disabled:true
                }
            ];
            this.listeners = {
                'propertychange' : function(source, recordId, value, oldValue, eOpts) {
                    me.down('#updateProperties').setDisabled(false);
                }
            };
        }
        this.callParent(arguments);
    },
    setComboValues : function(){
        var grid=this;
        var valueColumn=grid.columns[1];

        var cellgrid = grid.getView().getCell(valueColumn, valueColumn);

    }
});
