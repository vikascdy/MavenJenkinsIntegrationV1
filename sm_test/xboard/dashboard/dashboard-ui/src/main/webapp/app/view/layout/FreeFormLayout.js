Ext.define('DD.view.layout.FreeFormLayout', {
    extend:'Ext.panel.Panel',
    alias :'widget.freeformlayout',
    layout:'absolute',
    initComponent : function() {
        var me = this;
        LayoutManager.initializeGridSize(me.noOfRows, me.noOfCols, function() {
            me.html = LayoutManager.generateCanvasGrid();
        });

        this.callParent(arguments);
    },
    afterRender:function() {
        var me = this;

        Ext.Panel.prototype.afterRender.apply(this, arguments);

        if (DashboardManager.isEditMode) {
            this.dropTarget = me.body;
            var dd = new Ext.dd.DropTarget(this.dropTarget, {
                ddGroup:'WidgetHolder',
                notifyDrop:function(source, e, data) {
                    var x = e.browserEvent.layerX;
                    var y = e.browserEvent.layerY;

                    var controlInfo = data.dragData;
                    var widgetType = controlInfo.type;

                    if (controlInfo.componentType == 'widgets') {
                        LayoutManager.addPortlet(me, x, y, controlInfo, widgetType, null, null, function(portlet) {
                            WidgetManager.findWidgetType(widgetType, portlet, e, controlInfo, function(widget) {
                                LayoutManager.updatePortletProperties(me, portlet, widget, widgetType, x, y, function() {
                                    LayoutManager.clearNeighbourCells(function() {
                                        return true;
                                    });
                                });

                            });
                        });
                    }
                    else
                    if (controlInfo.componentType == 'parameters') {
                        LayoutManager.addParameterControl(me, x, y, controlInfo, null, null, function(parameterControl, parameterName) {
                            LayoutManager.updateParameterControlProperties(me, parameterControl, parameterName, x, y, function() {
                                var control = controlInfo.controlType;
                                var extraConfig = null;
                                if (control == 'singleverticalslidercontrol') {
                                    control = 'singleslidercontrol';
                                    extraConfig = {vertical:true};
                                }
                                else
                                if (control == 'multiverticalslidercontrol') {
                                    control = 'multislidercontrol';
                                    extraConfig = {vertical:true};
                                }

                                parameterControl.updateParameterControl({
                                    xtype:control,
                                    extraConfig:extraConfig
                                }, function() {
                                    LayoutManager.clearNeighbourCells(function() {
                                        return true;
                                    });
                                });
                            });


                        });

                    }
                },
                notifyOver:function(source, e, data) {
                    var x = e.browserEvent.x;
                    var y = e.browserEvent.y;

                    LayoutManager.showTargetPane(me, x, y);
                    return true;
                },
                notifyOut:function(source, e, data) {
                    LayoutManager.clearNeighbourCells(function() {
                    });
                }
            });

        }
//
//
//        var dashboardElementsTreeStore = Ext.StoreManager.lookup('DashboardElementsTreeStore');
//        var rootNode = dashboardElementsTreeStore.getRootNode();
//        rootNode.set('text', 'Dashboard Name');
//        rootNode.commit();


    }
});
