Ext.define('DD.view.widgets.WidgetLibrary', {
    extend:'Ext.window.Window',
    alias:'widget.widgetlibrary',
    title:'Widget Library',
    layout:'fit',
    height:500,
    width:700,
    resizable:false,
    draggable:true,
    border:false,
    bodyBorder:false,
    modal:true,
    previousButton:null,
    dockedItems:[
        {
            xtype:'toolbar',
            dock:'top',
            ui:'dashboardDesigner-widgetlibrary',
            padding:'10 7 0 10',
            items:[
                {
                    text:'New Widget',
                    ui:'widgetlibrary',
                    scale:'small',
                    itemId:'customWidget',
                    enableToggle:true,
                    padding:'0px 10px',
                    pressed:true
                },
                '->',
                {
                    xtype:'textfield',
                    enableKeyEvents:true,
                    emptyText:'Search Widget',
                    listeners :{
                        keyup:function(field) {
                            field.up('window').filterWidgetStore(field.getValue());
                        }
                    }
                }
            ]
        }
    ],
    buttons:[
        {
            text:'Cancel',
            ui:'redbutton',
            margin:'0 10 10 0',
            handler:function () {
                this.up('window').onEsc();
            }
        },
        {
            text:'Add to Dashboard',
            iconCls:'next',
            iconAlign:'right',
            itemId:'addToDashboard',
            ui:'greenbutton',
            margin:'0 10 10 0'
        }
    ],
    items:[
        {
            xtype:'savedwidgets',
            presetsWidgetUrl:'resources/json/SavedWidgets.json'
        }
    ],
    onEsc: function() {
        var me = this;
        Ext.Msg.confirm(
            'Closing Confirmation',
            'You really want to close widget configuration ?',
            function(btn) {
                if (btn === 'yes') {
                    WidgetManager.removeWidgetFromCanvas(function() {
                        me.destroy();
                    });
                }
            }
        );
    },
    filterWidgetStore : function(value) {
        var widgetListStore = Ext.StoreManager.lookup('WidgetListStore');
        if (value == "")
            widgetListStore.clearFilter();
        else {
            widgetListStore.clearFilter();
            widgetListStore.filter([
                Ext.create('Ext.util.Filter', {filterFn: function(item) {
                    if (item.get('name').toLowerCase().indexOf(value.toLowerCase()) != -1)
                        return item.get("name");
                }})
            ]);
        }

    }
});
