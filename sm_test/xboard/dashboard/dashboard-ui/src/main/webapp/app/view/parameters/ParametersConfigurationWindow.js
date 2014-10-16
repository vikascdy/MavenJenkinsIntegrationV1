Ext.define('DD.view.parameters.ParametersConfigurationWindow', {
    extend:'Ext.window.Window',
    alias:'widget.parametersconfigurationwindow',
    draggable:true,
    resizable:false,
    minHeight:200,
    width:600,
    bodyPadding:20,
    modal:true,
    layout:'fit',
    title:'Parameter Configuration',
    initComponent : function() {
        var me = this;


        this.items = [
            {
                xtype:'parametersconfigurationform',
                parameterConfig:me.parameterConfig
            }
        ];
        this.buttons = [
            {
                text: 'Cancel',
                handler : function() {
                    me.close();
                }
            },
            {
                text: 'Apply',
                iconCls:'apply',
                itemId:'applyChanges'
            }
        ];
        this.callParent(arguments);
    }
});
