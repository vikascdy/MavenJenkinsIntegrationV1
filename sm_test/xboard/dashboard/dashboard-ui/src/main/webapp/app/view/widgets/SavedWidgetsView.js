Ext.define('DD.view.widgets.SavedWidgetsView', {
    extend:'Ext.view.View',
    alias :'widget.savedwidgetsview',
    enableDragDrop   : true,
    trackOver: true,
    overItemCls: 'saveWidgetOver',
    itemSelector: 'div.savedWidget',
    selectedItemCls: 'selectedWidget',
    autoScroll:true,
    margin:'10px 0px 0px 0px',
    store:'WidgetListStore',
    initComponent : function() {
        var me = this;

        this.tpl = [
            '<div class="widgetScroller">',
            '<tpl for=".">',
            '<div class="savedWidget">',
            '<div class="item">',
            '<div class="leftImage"><img src="{imageUrl}"/></div>',
            '<div class="rightImage"><h4>{name}</h4>',
            '<p>{description}</p></div>',
            '<div id={id} class="close widgetClose"></div>',
            '</div>',
            '</div>',
            '</tpl>',
            '</div>'
        ];

        this.listeners = {
            'render' : function() {
                var store = this.getStore();
                store.getProxy().url=JSON_SERVLET_PATH + 'getWidgetsForWidgetType';
                store.getProxy().actionMethods = {read:'POST'};
                store.getProxy().extraParams= {
                    data : '{"widgetTypeId":' + WidgetManager.activeWidget.id + '}'
                };
                store.removeAll();
                store.load();
            },
            'afterrender':function() {
                this.getEl().on('click', function(e, t) {
                    var store = me.getStore();
                    var index = store.find('id', t.id);
                    if (index != -1) {
                        var record = store.getAt(index);
                    }
                    me.deleteWidget(record, me);
                }, this, {delegate:'.close'});
            }
        };
        this.callParent(arguments);
    },
    deleteWidget : function(record, me) {
        Ext.Msg.confirm(
            'Delete Confirmation',
            'Are you sure you want to delete this widget ?',
            function(btn) {
                if (btn === 'yes') {
                    DD.loadingWindow = Ext.widget('progresswindow', {
                        text: 'Deleting Widget...'
                    });
                    WidgetManager.removeWidget(record.get("id"), function() {
                        me.getStore().load();
                        DD.loadingWindow.destroy();
                    });
                }
            }
        );
    }
});
