Ext.define('DD.view.datasource.DataSourceWindow', {
    extend:'Ext.window.Window',
    title: 'Data Source',
    draggable:true,
    resizable:false,
    alias:'widget.datasourcewindow',
    minHeight:500,
    width:1000,
    bodyPadding:20,
    animateTarget:'',
    modal:true,
    layout:'fit',
    items:[

        {
            xtype:'container',
            layout:{
                type:'hbox',
                align:'stretch'
            },
            items:[
                {
                    xtype: 'datasourcelist',
                    flex:1
                },
                {
                    xtype:'container',
                    style:{
                        'color':'#CCC'
                    },
                    margin:'0 10 0 10',
                    border:false,
                    layout:{
                        type:'vbox',
                        align:'stretch'
                    },
                    items:[
                        {
                            xtype:'container',
                            flex:1,
                            width:1,
                            border:false,
                            layout:{
                                type:'hbox',
                                align:'stretch'
                            },
                            items:[
                                {
                                    xtype:'tbspacer',
                                    flex:1
                                },
                                {
                                    xtype:'container',
                                    width:1,
                                    style:{
                                        'borderRight':'1px dotted #CCC'
                                    }
                                },
                                {
                                    xtype:'tbspacer',
                                    flex:1
                                }
                            ]


                        },
                        {
                            html:'<span style="color:#CCC;">OR</span>',
                            border:false
                        },
                        {
                            xtype:'container',
                            flex:1,
                            width:1,
                            border:false,
                            layout:{
                                type:'hbox',
                                align:'stretch'
                            },
                            items:[
                                {
                                    xtype:'tbspacer',
                                    flex:1
                                },
                                {
                                    xtype:'container',
                                    width:1,
                                    style:{
                                        'borderRight':'1px dotted #CCC'
                                    }
                                },
                                {
                                    xtype:'tbspacer',
                                    flex:1
                                }
                            ]


                        }
                    ]
                },
                {
                    xtype: 'createdatasourceform',
                    flex:1
                }
            ]
        }
    ],

    initComponent : function() {
        var me = this;

        this.buttons = [
            {
                text: 'Proceed',
                iconAlign:'right',
                iconCls:'next',
                handler: function() {
                    DataSetManager.showDataSetWindow(function() {
                        me.close();
                    });
                }
            }
        ];


        this.callParent(arguments);

    },
    onEsc: function() {
        var me = this;
        Ext.Msg.confirm(
            'Closing Confirmation',
            'You really want to close widget configuration ?',
            function(btn) {
                if (btn === 'yes') {
                    WidgetManager.isWidgetWizardActive = false;
                     WidgetManager.removeWidgetFromCanvas(function(){
                        me.destroy();
                    });
                }
            }
        );
    }

});
