Ext.define('DD.view.core.Portlet', {
    extend:'Ext.container.Container',
    alias:'widget.portlet',
    layout:{type:'vbox', align:'stretch'},
    config:{
        savedWidgetId:null,
        gridDimensions:null,
        widgetTitle:null,
        movement:null
    },
    initComponent:function () {

        var me = this;

        this.resizable = DashboardManager.isEditMode ? true : false;

        this.draggable = DashboardManager.isEditMode ? {
            moveOnDrag: true,
            constrain:true,
            constrainTo:this.ownerCt
        } : false;


        var hideWidgetOptions = false;
        var hideHeader = false;

        if (me.widgetType == 'shape' || ( (me.widgetType == 'text' || me.widgetType == 'embedded' || me.widgetType == 'image') && !DashboardManager.isEditMode))
            hideHeader = true;
        if (me.widgetType == 'text' || me.widgetType == 'embedded' || me.widgetType == 'image' || me.widgetType == 'shape')
            hideWidgetOptions = true;

        me.cls = me.widgetType == 'text' || me.widgetType == 'image' || me.widgetType == 'shape' || me.widgetType == 'embedded' ? 'transparentPanel' : '';
        var headerPanel =
        {
            xtype:'panel',
            border:false,
            cls:DashboardManager.isEditMode ? 'widgetHeaderPanel' : '',
            hidden:hideHeader ,
            layout:{type:'hbox', align:'stretch'},
            items:[
                {
                    xtype:'component',
                    itemId:'widgetHeading',
                    padding:'7 0 0 15'
//                    html:'<h3>Widget Heading</h3>'
                },
                {
                    xtype:'tbspacer',
                    flex:1
                },
                {
                    xtype:'button',
                    iconCls:'batch-object',
                    margin:2,
//                    style:{
//                        'z-index':19001
//                    },
                    ui:'widget-menu',
                    arrowCls:"",
                    menu:Ext.create('Ext.menu.Menu', {
                        plain:true,
                        shadow:false,
                        width:100,
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
                                text:'Reload Data',
                                hidden:hideWidgetOptions,
                                handler:function () {
                                    me.reloadWidgetData();
                                }
                            },
                            {
                                text:'Show Data',
                                hidden:hideWidgetOptions || me.widgetType == 'grid',
                                handler:function () {
                                    me.showDataWindow(this);
                                }
                            },
//                            {
//                                text:'Configure',
//                                hidden:hideWidgetOptions || !DashboardManager.isEditMode,
//                                handler:function () {
//                                    WidgetManager.activePortlet = me;
//                                    var widgetLibrary = Ext.widget({xtype:'widgetlibrary'});
//                                    widgetLibrary.show(this.getEl());
//                                }
//                            },
                            {
                                text:'Export as Image',
                                hidden:hideWidgetOptions || !DashboardManager.isEditMode || me.widgetType == 'grid',
                                handler:function () {
                                    me.widget.save({
                                        type:'image/png'
                                    });
                                }
                            },
                            {
                                text:'Remove Widget',
                                hidden : !DashboardManager.isEditMode,
                                handler:function () {
                                    me.removePortlet();
                                }
                            }
                        ],
                        listeners:{
                            'click':function (btn) {
                                btn.up("button").blur();
                            },

                            'mouseenter':function (btn, e) {
                                if (!btn.up("button").hasVisibleMenu()) {
                                    btn.up("button").showMenu();
                                }
                            },
                            'mouseleave':function (btn, e) {
                                if (btn.up("button").hasVisibleMenu()) {
                                    btn.up("button").hideMenu();
                                    btn.up("button").blur();
                                }
                            }
                        }
                    }),
                    listeners:{
                        'mouseover':function (btn, e) {
                            if (!btn.hasVisibleMenu()) {
                                btn.showMenu();
                            }
                        },
                        'mouseout':function (btn, e) {
                            if (btn.hasVisibleMenu()) {
                                btn.hideMenu();
                                btn.blur();
                            }
                        }
                    }
                }
            ]
        };

        this.items = [
            headerPanel,
            {
                xtype:'panel',
                bodyPadding:2,
                border:false,
                itemId:'widgetContainer',
                flex:1,
                layout:'fit'
            }
        ];

        this.callParent(arguments);
    },

    updateWidget:function (widget, callback) {

        var me = this;
        var widgetContainer = me.down('#widgetContainer');
        widgetContainer.removeAll(true);

        widgetContainer.add(widget);
        widgetContainer.updateLayout();
        me.widget = widget;

        me.updateWidgetHeading(widget, function(title) {
//            if (widget.configObj) {
//                if (widget.configObj.parameterFields) {
//                    ParameterManager.addParametersToDashboard(widget, widget.configObj.parameterFields, widget.getStore(), title, widget.dataSetId, function() {
//
//                    });
//                }
//            }
            Ext.callback(callback, this, [widget]);
        });

    },
    updateWidgetHeading : function(widget, callback) {
        var me = this;
        var widgetHeading = me.down('#widgetHeading');
        var title = '';

        if (widget.configObj) {
            if (widget.configObj.title) {
                title = widget.configObj.title;
                widgetHeading.update('<span class="widgetHeading">' + title + '</span>');
            }
            else {
                title = 'Widget';
                widgetHeading.update('<span class="widgetHeading"></span>');
            }

        }

        me.setWidgetTitle(title);
        Ext.callback(callback, this, [title]);
    },


    getWidget:function () {
        var me = this;
        var widgetContainer = me.down('#widgetContainer');
        return widgetContainer.getLayout().getLayoutItems()[0];
    },

    removePortlet : function() {
        var me = this;
        var dashboardElementsTreeStore = Ext.StoreManager.lookup('DashboardElementsTreeStore');
        var rootNode = dashboardElementsTreeStore.getRootNode();
        var treeNode = rootNode.findChild('id', this.id + '-node');
        treeNode.destroy();
//        DD.currentPage.down('freeformlayout').remove(me.id,true);
        this.destroy();
    },
    reloadWidgetData:function () {
        var me = this;
        var widget = me.getWidget();
        var nameField = null;
        var dataField = null;
        var store = widget.getStore();
        var configObj = widget.configObj;

        widget.setLoading('Refreshing Data');


        if (widget.useAggregateData) {
            if (configObj.widgetXtype) {

                if (configObj.widgetXtype == 'barchart' || configObj.widgetXtype == 'piechart') {
                    dataField = configObj.Xfields;
                    nameField = configObj.Yfields;
                }
                else {
                    dataField = configObj.Yfields;
                    nameField = configObj.Xfields;
                }
            }
            else {
                if (Ext.isArray(configObj.Yfields)) {
                    dataField = configObj.Yfields[0];
                    nameField = configObj.Xfields;
                }
                else {
                    dataField = Ext.isArray(configObj.Xfields) ? configObj.Xfields[0] : configObj.Xfields;
                    nameField = configObj.Yfields;
                }
            }
        }


        DataSetManager.getDatasetResult(widget.dataSetId, nameField, dataField, widget.useAggregateData, function(records) {
            store.removeAll();
            store.clearFilter();
            store.loadData(records.get('data'));
            widget.setLoading(false);
        });
    },

    showDataWindow:function (btn) {
        var me = this;
        var widgetContainer = me.down('#widgetContainer');
        var widget = widgetContainer.getLayout().getLayoutItems()[0];

        var store = widget.getStore();
        var widgetDataWindow = Ext.widget({
            xtype:'widgetdatawindow',
            widgetConfigObj:widget.configObj,
            store:store
        });
        widgetDataWindow.show(btn.getEl());
    },

    showAlignmentLines : function() {

    }

});