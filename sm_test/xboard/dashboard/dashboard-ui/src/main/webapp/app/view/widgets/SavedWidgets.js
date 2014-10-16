Ext.define('DD.view.widgets.SavedWidgets', {
    extend:'Ext.container.Container',
    alias:'widget.savedwidgets',
    flex:1,
    width:500,
    height:300,
    border:false,
    padding:'20px 10px 10px 10px',
    style:{
        backgroundColor:'#F5F7F9'
    },
    layout:{
        type:'hbox',
        align:'left'
    },
    previousButton:null,
    presetsWidgetUrl:"",
    afterRender:function () {

        var me, dockedToolbar, presetsPanel, columnPanel, presetWidgetListPanel, listTemplate
        me = this;

        dockedToolbar = me.createDockedToolbar();
        presetsPanel = me.createPresetsPanel('card', 'presetsId');

        Ext.each({ "id": "allId",
            "active":true,
            "name": "ALL"}, function (menuItem, index) {

            dockedToolbar.add(me.addDockedToolbarIcon(menuItem.id, menuItem.name));

            columnPanel = me.addColumnPanel(listTemplate);

            presetWidgetListPanel = me.createPresetsPanel('hbox', menuItem.id);
            presetWidgetListPanel.add(columnPanel);

            presetsPanel.addDocked(dockedToolbar);
            presetsPanel.add(presetWidgetListPanel);

            me.add(presetsPanel);
        });

        DD.view.widgets.SavedWidgets.superclass.afterRender.apply(this, arguments);
        return;
    },

    addColumnPanel:function (tpl) {
        var colPanelItem = Ext.create('Ext.container.Container', {
            flex:1,
            style:{
                borderWidth:'1px',
                borderColor:'#E6EAEE #E6EAEE #E6EAEE transparent',
                borderStyle:'solid'
            },
            height:'100%',
            items:[
                {xtype:'savedwidgetsview'}
            ]
        });
        return colPanelItem;
    },

    createDockedToolbar:function () {
        var me = this;
        var dockedtoolbar = Ext.create('Ext.toolbar.Toolbar', {
            padding:0,
            style:{
                backgroundColor:'#F5F7F9',
                backgroundImage:'none'
            },
            border:false,
            height:'100%',
            width:150,
            flex:1,
            dock:'left',
            listeners:{
                'afterrender':function () {
//                    console.log(this.items.items[0]);
                    this.items.items[0].toggle(true);
                    me.previousButton = this.items.items[0];
                }
            }
        });
        return dockedtoolbar;
    },

    addDockedToolbarIcon:function (tabId, tabName) {
        var me = this;
        var dockedToolbarIcon = Ext.create('Ext.Button', {
            scale:'medium',
            width:150,
            ui:'preset-widget',
            textAlign:'left',
            text:tabName.toUpperCase(),
            handler:function () {
                this.up("panel").getLayout().setActiveItem(tabId);
                if (me.previousButton != null) {
                    me.previousButton.toggle(false);
                }
                this.toggle(true);
                me.previousButton = this;
            }
        });
        return dockedToolbarIcon;
    },

    createPresetsPanel:function (layoutType, panelId) {
        var presetsPanel = Ext.create('Ext.panel.Panel', {
            id:panelId,
            flex:1,
            height:'100%',
            border:false,
            bodyBorder:false,
            bodyStyle:{backgroundColor:'#FDFEFE'},
            layout:layoutType
        });
        return presetsPanel;
    },


    // private, clean up
    onDestroy:function () {
        this.removeAll();
//        DD.view.widgets.SavedWidgets.superclass.afterRender.apply(this, arguments);
    }
});