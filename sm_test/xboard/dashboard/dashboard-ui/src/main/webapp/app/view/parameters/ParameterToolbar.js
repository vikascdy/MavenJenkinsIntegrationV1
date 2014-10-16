Ext.define('DD.view.parameters.ParameterToolbar', {
    extend:'Ext.view.View',
    alias:'widget.parametertoolbar',
    enableDragDrop   : true,
    trackOver: true,
    overItemCls: 'menuItemOver',
    itemSelector: 'div.menuItem',
    selectedItemCls: 'menuItemSelected',
    store:'ParameterToolbarStore',
    initComponent:function() {
        this.tpl = this.createTemplate();
        this.callParent(arguments);
    },
    createTemplate: function() {
        var tpl = new Ext.XTemplate(
            '<tpl for=".">',
            '<div class="menuItem">',
            '<span class="{iconCls}"></span>',
            '<span class="menuText">{text}</span>',
            '</div>',
            '</tpl>'
        );
        return tpl;
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
            var freeformLayout = this.up('viewport').down('freeformlayout');
            var controlInfo = sender.dragData.dragData;

            var x = freeformLayout.getEl().dom.clientWidth / 2;
            var y = freeformLayout.getEl().dom.clientHeight / 2;

            LayoutManager.addParameterControl(freeformLayout, x, y, controlInfo, function(parameterControl) {
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
        }
    }
});