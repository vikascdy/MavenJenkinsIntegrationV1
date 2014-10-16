Ext.define('DD.view.dashboard.SaveDashboardWindow', {
    extend:'Ext.window.Window',
    alias:'widget.savedashboardwindow',
    draggable:true,
    resizable:false,
    minHeight:200,
    width:600,
    bodyPadding:20,
    modal:true,
    layout:'fit',
    title:'Save Dashboard',
    initComponent : function() {
        var me = this;

        me.dashboardTypeId=me.dashboardType.id;

        this.items = [
            {
                xtype:'savedashboardform',
                mode:me.mode,
                dashboardProperties:me.dashboardType.properties
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
                text: DashboardManager.currentDashboardId && me.mode=='save' ? 'Update' : 'Save',
                iconCls:'save',
                itemId:'saveDashboard'
            }
        ];
        this.callParent(arguments);
    }
});
