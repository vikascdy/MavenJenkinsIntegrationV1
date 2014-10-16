Ext.define('DD.view.widgets.WidgetTypeView', {
    extend:'Ext.view.View',
    alias :'widget.widgettypeview',
    enableDragDrop   : true,
    trackOver: true,
    overItemCls: 'x-item-over',
    itemSelector: 'div.widget',
    autoScroll:true,
    initComponent : function() {
        this.store = Ext.create('DD.store.WidgetTypeListStore');
        this.tpl = [
            '<tpl for=".">',
            '<div class="widget-wrap">',
            '<div class="widget"><img src="{imageUrl}" title={label} /></div>',
            '</div>',
            '</tpl>'];

        this.callParent(arguments);
    },
    listeners: {
        render: function(v) {
            v.dragZone = new Ext.dd.DragZone(v.getEl(), {
                ddGroup: 'WidgetHolder',
                getDragData: function(e) {
                    var sourceEl = e.getTarget(v.itemSelector, 10);
                    if (sourceEl) {
                        d = sourceEl.cloneNode(true);
                        d.id = Ext.id();
                        v.dragData = {
                            sourceEl: sourceEl,
                            repairXY: Ext.fly(sourceEl).getXY(),
                            ddel: d,
                            dragData: v.getRecord(sourceEl).data,
                            store: v.store
                        };
                        return v.dragData;
                    }
                },
                getRepairXY: function() {
                    return this.dragData.repairXY;
                }
            });
        },
        itemdblclick:function(sender, record, item, index, e) {
            var freeformLayout = this.up('container').down("freeformlayout");
            var controlInfo = sender.dragData.dragData;
            var widgetType = controlInfo.type;
            var x = freeformLayout.getEl().dom.clientWidth;
            var y = freeformLayout.getEl().dom.clientHeight;

            if (controlInfo.componentType == 'widgets') {
                LayoutManager.addPortlet(freeformLayout, x, y, controlInfo, widgetType, function(portlet) {
                    x = (x - portlet.getEl().dom.clientWidth) / 2;
                    y = (y - portlet.getEl().dom.clientHeight) / 2;
                    WidgetManager.findWidgetType(widgetType, portlet, e, controlInfo, function(widget) {
                        LayoutManager.updatePortletProperties(sender, portlet, widget, widgetType, x, y);
                        LayoutManager.clearNeighbourCells(function() {
                            return true;
                        });
                    });
                });
            }
        }
    }
});
