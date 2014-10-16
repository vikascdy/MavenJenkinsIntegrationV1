Ext.define('DD.view.dashboard.DashboardDesignerPane', {
    extend:'Ext.container.Container',
    alias:'widget.dashboarddesignerpane',
//    minHeight:800,
    padding:'20 20 5 20',
    layout:{
        type:'border'
    },
    initComponent : function() {
        var me = this;
        me.listeners = {
            render : function() {
                me.removeAll();
                me.add(
                    {
                        xtype:'dashboardcanvasholder',
                        region:'center',
                        flex:1,
                        minWidth:600
                    },
                    {
                        xtype:'panel',
                        hidden:!DashboardManager.isEditMode,
                        collapsible:false,
                        margin:'0 0 0 10',
                        region:'east',
                        layout:{
                            type:'vbox',
                            align:'stretch'
                        },
                        width:250,
                        items:[
                            {
                                xtype:'dashboardelementstree',
                                title:'Dashboard Elements',
                                flex:1
                            },
                            {
                                xtype:'propertiespanel',
                                showUpdateButton:true,
                                title:'Properties',
                                flex:1
                            }
                        ]
                    });
            }
        };
        me.items = [];

        this.callParent(arguments);
    }
});