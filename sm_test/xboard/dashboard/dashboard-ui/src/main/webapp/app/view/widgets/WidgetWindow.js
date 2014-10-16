Ext.define('DD.view.widgets.WidgetWindow', {
    extend:'Ext.window.Window',
    title: 'Widget Configuration',
    draggable:true,
    resizable:false,
    alias:'widget.widgetwindow',
    height:600,
    width:1000,
    bodyPadding:20,
    animateTarget:'',
    modal:true,
    layout:'fit',
    config:{
        isWidgetSaved:false,
        isWidgetConfigured:false,
        savedWidgetId:null
    },
    initComponent : function() {
        var me = this;

        this.buttons = [
            {
                text: 'Back',
                iconCls:'previous',
                itemId:'back'
            },
            {
                text: 'Save in Library',
                itemId:'saveInLibrary'
            },
            {
                text: 'Add to Dashboard',
                itemId:'addToDashboard'
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
                if (btn === 'yes') {
                    WidgetManager.isWidgetWizardActive = false;
                    WidgetManager.removeWidgetFromCanvas(function() {
                        me.destroy();
                    });
                }
            }
        );
    },
    addConfigurationToWidgetType : function(type, callback) {

        var me = this;
        me.removeAll();

        switch (type) {
            case 'chart' :
                me.add(WidgetManager.getChartConfiguration());
                me.addDocked({xtype:'chartsgallery'});
                break;
            case 'grid' :
                me.add(WidgetManager.getGridConfiguration());
                break;
        }

        Ext.callback(callback, this, []);
    }

});
