Ext.define('DD.view.dashboard.DashboardCanvasHolder', {
    extend:'Ext.container.Container',
    alias:'widget.dashboardcanvasholder',
    layout:{
        type:'vbox',
        align:'stretch'
    },
    initComponent:function () {
        var me = this;

        this.items = [
            {
                xtype:'container',
                layout:{
                    type:'hbox'
                },
                items:[
                    {
                        xtype:'button',
                        itemId:'dashboardList',
                        scale:'large',
                        text:'No Dashboard Loaded',
                        arrowCls:'move-down',
                        ui:'dashboardHeading',
                        menu:Ext.create('Ext.menu.Menu', {
                            itemId:'xBoardList',
                            plain:true,
                            cls:'headingmenu',
                            width:250,
                            border:false,
                            shadow:false,
                            style:{
                                'box-shadow':'0px 9px 20px #CCCCCC'
                            },
                            defaults:{
                                plain:true
                            },
                            listeners : {
                                'beforeshow' : function() {
                                    var menu = this;
                                    menu.removeAll();

                                    if (!DashboardManager.isEditMode)
                                        DashboardManager.getXBoards(function(xBoards) {

                                            Ext.each(xBoards, function(board, index) {
                                                menu.insert(0, {text:board.get('name'),xBoardId:board.get('id')});
                                            });

                                            menu.add({
                                                xtype:'menuseparator'
                                            }, {
                                                plain:false,
                                                iconCls:'transmission-object',
                                                text:'New Dashboard',
                                                handler:function () {
                                                    var canvasHolder = me.down('#canvasHolder');
                                                    var newCanvasWindow = Ext.widget({
                                                        xtype:'newcanvaswindow'
                                                    });
                                                    newCanvasWindow.show(this.getEl());
                                                }

                                            });

                                        });
                                    else
                                        menu.add(
                                            {
                                                plain:false,
                                                hidden:DashboardManager.currentDashboardId == null,
                                                iconCls:'reload',
                                                text:'Reload Dashboard',
                                                handler:function () {
                                                    DD.loadingWindow = Ext.widget('progressbarwindow', {
                                                        text: 'Reloading Dashboard "' + DashboardManager.dashboardName + '"...'
                                                    });

                                                    DashboardManager.parseXBoardConfiguration(DashboardManager.currentDashboardId, function() {
                                                        DD.removeLoadingWindow(function() {

                                                        });
                                                    });
                                                }
                                            }, {
                                                xtype:'menuseparator',
                                                hidden:DashboardManager.currentDashboardId == null
                                            }, {
                                                plain:false,
                                                iconCls:'transmission-object',
                                                text:'New Dashboard',
                                                handler:function () {
                                                    var canvasHolder = me.down('#canvasHolder');
                                                    var newCanvasWindow = Ext.widget({
                                                        xtype:'newcanvaswindow'
                                                    });
                                                    newCanvasWindow.show(this.getEl());
                                                }
                                            });
                                },
                                click : function(menu, item) {
                                    if (item && item.xBoardId) {
                                        DashboardManager.dashboardName = item.text;
                                        window.location.href = '#/dashboard/' + item.xBoardId;
                                    }
                                }
                            },
                            items:[
                                {}
                            ]
                        })
                    },
                    {
                        xtype:'tbspacer',
                        flex:1
                    },
                    {
                        xtype:'button',
                        text:'Publish',
                        iconCls:'publish',
                        hidden:!DashboardManager.isEditMode,
                        margin:'0 5 0 0',
                        itemId:'publishDashboard',
                        handler : function() {
                            if (DashboardManager.currentDashboardId)
                                window.location.href = '#/dashboard/' + DashboardManager.currentDashboardId;
                            else
                                Ext.Msg.alert('Invalid Operation', 'Dashboard should be saved before publishing');
                        }
                    },
                    {
                        xtype:'button',
                        text:'Clone Dashboard',
                        iconCls:'copy',
                        hidden:!DashboardManager.isEditMode,
                        margin:'0 5 0 0',
                        itemId:'cloneDashboard',
                        mode:'clone'
                    },
                    {
                        xtype:'button',
                        text:'Save',
                        iconCls:'save',
                        hidden:!DashboardManager.isEditMode,
                        margin:'0 5 0 0',
                        itemId:'saveDashboard',
                        mode:'save'
                    },
                    {
                        xtype:'button',
                        text:'Delete Board',
                        iconCls:'delete',
                        hidden:true,
                        margin:'0 5 0 0',
                        itemId:'deleteDashboard'
                    },
                    {
                        xtype:'button',
                        text:'Edit Board',
                        iconCls:'edit',
                        hidden:true,
                        margin:'0 5 0 0',
                        itemId:'editDashboard',
                        handler : function() {
                            window.location.href = '#/editDashboard/' + DashboardManager.currentDashboardId;
                        }
                    },
                    {
                        xtype:'button',
                        text:'Add Parameters',
                        hidden:!DashboardManager.isEditMode,
                        id:'addParameters',
                        iconCls:'add',
                        margin:'0 5 0 0',
                        menu:Ext.create('Ext.menu.Menu', {
                            plain:true,
                            items:[
                                {
                                    xtype:'parametertoolbar'
                                }
                            ]
                        })
                    }
                ]
            },
            {
                xtype:'container',
                flex:1,
                layout:'border',
                margin:'10 0 0 0',
                items:[
                    {
                        xtype:'widgettypeview',
                        hidden:!DashboardManager.isEditMode,
                        autoScroll:false,
                        region:'west',
                        margins:'0 10 0 0',
                        width:60
                    },
                    {
                        xtype:'container',
                        border : DashboardManager.isEditMode,
                        region:'center',
                        itemId:'canvasHolder',
                        flex:1
                    }
                ]
            }

        ];

        this.callParent(arguments);
    },

    setDashboardName : function(name, callback) {
        var dashboardName = this.down('#dashboardList');
        dashboardName.setText(name);
        Ext.callback(callback, this);
    },

    loadDefaultDashboardToCanvas : function(callback) {

        Ext.Function.defer(function () {
            DashboardManager.getXBoards(function(xBoards) {
                if (xBoards && xBoards.length > 0) {

                    DashboardManager.dashboardName = xBoards[0].get('name');
                    window.location.href = '#/dashboard/' + xBoards[0].get('id');
                }
            });
        }, 500);

        Ext.callback(callback, this);
    },

    showDashboardOptions : function(viewMode, callback) {

        var deleteDashboard = this.down('#deleteDashboard');
        var editDashboard = this.down('#editDashboard');
        if (viewMode) {
            deleteDashboard.show();
            editDashboard.show();
        }
        else {
            deleteDashboard.hide();
            editDashboard.hide();
        }
        Ext.callback(callback, this);

    },

    resizeCanvas:function (noOfRows, noOfCols, callback) {

        var canvasHolder = this.down('#canvasHolder');

        this.noOfRows = noOfRows;
        this.noOfCols = noOfCols;

        var actualCanvasWidth = canvasHolder.getEl().dom.clientWidth;
        var actualCanvasHeight = canvasHolder.getEl().dom.clientHeight;

        var requiredCellWidth = parseFloat((actualCanvasWidth / noOfCols).toString().split(".")[0]);
        var requiredCellHeight = parseFloat((actualCanvasHeight / noOfRows).toString().split(".")[0]);


        var requiredCanvasWidth = requiredCellWidth * noOfCols;
        var requiredCanvasHeight = requiredCellHeight * noOfRows;


        var leftRightMargin = Math.abs(actualCanvasWidth - requiredCanvasWidth);
        var topBottomMargin = Math.abs(actualCanvasHeight - requiredCanvasHeight);

        var canvas = Ext.widget({
            xtype:'freeformlayout',
            noOfRows:noOfRows,
            noOfCols:noOfCols,
            height:requiredCanvasHeight,
            width:requiredCanvasWidth
        });

        var marginString = Ext.String.format('0 {0} {1} 0', leftRightMargin, topBottomMargin);

        canvasHolder.removeAll();
        canvasHolder.add(canvas);

        Ext.callback(callback, this, [canvas]);

    }

});
