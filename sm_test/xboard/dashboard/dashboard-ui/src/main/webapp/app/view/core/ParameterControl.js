Ext.define('DD.view.core.ParameterControl', {
    extend:'Ext.container.Container',
    alias:'widget.parametercontrol',
    layout:{type:'hbox',align:'middle'},
    bodyPadding:5,
    config:{
        savedWidgetId:null,
        isParameterConfigured:null,
        configObj:null,
        parameterConfig:null,
        gridDimensions:null
    },
    initComponent:function () {
        var me = this;

        this.resizable = DashboardManager.isEditMode ? true : false;

        this.draggable = DashboardManager.isEditMode ? {
            moveOnDrag: true,
            constrain:true,
            constrainTo:this.ownerCt
        } : false;


        this.items = [
            {
                xtype:'container',
                padding:10,
                border:false,
                itemId:'parameterControlContainer',
                flex:1,
                layout:'fit'
            },
            {
                xtype:'button',
                hidden: !DashboardManager.isEditMode,
                margin:'0 10 0 5',
                iconCls:'configure',
                menu:Ext.create('Ext.menu.Menu', {
                    plain:true,
                    shadow:false,
                    style:{
                        'border':'1px solid #E0E8ED',
                        'borderTop':'0px',
                        'box-shadow':'8px 1px 4px #888888'
                    },
                    defaults:{
                        plain:true,
                        padding:'5px 10px 5px 10px'
                    },
                    items:[
                        {
                            text:'Configure',
                            handler : function() {
                                ParameterManager.showConfigurationWindow(me, this);
                            }
                        },
                        {
                            text:'Remove',
                            handler:function() {
                                var dashboardElementsTreeStore = Ext.StoreManager.lookup('DashboardElementsTreeStore');
                                var rootNode = dashboardElementsTreeStore.getRootNode();
                                var treeNode = rootNode.findChild('id', me.id + '-node');
                                treeNode.destroy();
                                me.destroy();
                            }
                        }
                    ]
                })


            }
        ];


        this.callParent(arguments);

    },


    updateParameterControl:function (widget, callback) {
        var me = this;
        var parameterControlContainer = me.down('#parameterControlContainer');
        parameterControlContainer.removeAll(true);
        if (widget.extraConfig)
            Ext.apply(widget, widget.extraConfig);

        parameterControlContainer.add(widget);
        parameterControlContainer.updateLayout();

        me.widget = widget;
        Ext.callback(callback, this, [widget]);
    },

    getParameterControl:function () {
        var me = this;
        var parameterControlContainer = me.down('#parameterControlContainer');
        return parameterControlContainer.getLayout().getLayoutItems()[0];
    }

});