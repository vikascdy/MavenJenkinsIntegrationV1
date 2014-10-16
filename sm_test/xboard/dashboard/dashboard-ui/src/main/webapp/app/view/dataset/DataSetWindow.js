Ext.define('DD.view.dataset.DataSetWindow', {
    extend:'Ext.window.Window',
    title: 'Data Set',
    draggable:true,
    resizable:false,
    alias:'widget.datasetwindow',
    minHeight:500,
    width:1000,
    bodyPadding:20,
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
                    xtype: 'datasetlist',
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
                    xtype: 'createdatasetform',
                    flex:1
                }
            ]
        }
    ],
    initComponent : function() {
        var me = this;

        this.buttons = [
            {
                text: 'Back',
                iconCls:'previous',
                itemId:'back'
            },
            {
                text: 'Proceed',
                iconCls:'next',
                iconAlign:'right',
                itemId:'proceed'

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
                if (btn === 'yes'){
                    WidgetManager.isWidgetWizardActive=false;
                     WidgetManager.removeWidgetFromCanvas(function(){
                        me.destroy();
                    });
                }
            }
        );
    }
});
