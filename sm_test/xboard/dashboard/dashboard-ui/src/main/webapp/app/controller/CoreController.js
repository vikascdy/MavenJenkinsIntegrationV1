Ext.define('DD.controller.CoreController', {
    extend: 'Ext.app.Controller',
    views:[
        'core.Portlet',
        'core.ParameterControl',
        'core.ProgressWindow',
        'core.ProgressBarWindow',
        'core.PropertiesPanel',
        'core.NewCanvasWindow',
        'core.QueryResultWindow',
        'core.ImageGallery',
        'core.NewImageWindow',
        'core.TextEditorWindow',
        'core.EmbeddedCodeWindow'
    ],
    models:[],
    stores:['ImageGalleryStore','DashboardElementsTreeStore'],
    refs: [
        {
            selector: 'propertiespanel',
            ref: 'propertiesPanel'
        }
    ],
    init: function() {
        this.control({
            'dashboardelementstree > treeview':{
                nodedragover : function(targetNode, position, dragData, e, eOpts) {
                    return (position != 'append' && !targetNode.isRoot());

                }
            },
            'dashboardelementstree':{
                checkchange :  function(node, checked) {
                    var holderId = node.raw.holderId;
                    var holder = Ext.getCmp(holderId);
                    node.get('checked') ? holder.show() : holder.hide();
                },
                selectionchange:function(selModel, selected, eOpts) {
                    var getter = this;
                    var propertiesPanel = getter.getPropertiesPanel();
                    if (selected.length != 0) {

                        var portlet = Ext.getCmp(selected[0].raw.holderId);
//                        if (portlet && portlet.getSavedWidgetId() != null) {
                        if (portlet) {
                            propertiesPanel.setSource({}, {});
                            propertiesPanel.down('#updateProperties').setDisabled(true);
                            WidgetPropertiesManager.getWidgetProperties(selected[0].raw.widgetType, portlet, function(widgetProperties) {
                                propertiesPanel.setSource(widgetProperties[0], widgetProperties[1]);
                                propertiesPanel.setComboValues(function() {
                                    propertiesPanel.setWidgetInfo(selected[0].raw);
                                    propertiesPanel.setWidgetType(selected[0].raw.widgetType);
                                    propertiesPanel.setWidgetHolderId(selected[0].raw.holderId);
                                });

                            });
                        }
                        else {
                            propertiesPanel.setSource({}, {});
                            propertiesPanel.down('#updateProperties').setDisabled(true);
                        }
                    }

                }
            },
            'dashboardelementstree > node':{
                destroy :  function(node, checked) {
                    alert("destroy");
                }
            }
        });
    }
});