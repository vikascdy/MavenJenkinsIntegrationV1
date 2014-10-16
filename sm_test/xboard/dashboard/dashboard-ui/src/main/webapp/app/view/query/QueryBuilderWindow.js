Ext.define('DD.view.query.QueryBuilderWindow', {
    extend:'Ext.window.Window',
    alias:'widget.querybuilderwindow',
    resizable:true,
    draggable:true,
    modal:true,
    width:900,
    height:550,
    closeAction:'destroy',
    layout:{type:'vbox',align:'stretch'},
    title:'Query Builder',
    bodyPadding:5,
    initComponent : function() {
        var me = this;

        this.items = [
            {
                xtype:'container',
                flex:1,
                layout:{type:'hbox',align:'stretch'},
                items:[
                    {
                        xtype:'tablelisttree',
                        width:250
                    },
                    {
                        xtype:'panel',
                        flex:1
                    }
                ]
            },
            {
                xtype:'querybuildergrid',
                margin:'5 0 5 0',
                height:150
            },
            {
                xtype:'querybuilderoutput',
                height:100
            }
        ];

        this.tbar = [
            '->',
            {
                text:'Save',
                iconCls:'save'
            },
            {
                text:'Run',
                iconCls:'run'
            }
        ];
        this.callParent(arguments);
    }
});