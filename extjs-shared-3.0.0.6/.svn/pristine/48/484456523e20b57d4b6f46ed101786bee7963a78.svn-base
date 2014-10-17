Ext.define('Edifecs.Favorites', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.Favorites',
    width: 430,
    border: false,
    bodyBorder: false,
    favoritesjson: "",
    layout: 'hbox',
    listeners: {
        'afterrender': function (c) {
            c.getEl().on('click', function (e, t) {
                c.up('ApplicationBar').fireEvent('favouriteItemClicked', t.id);
            }, null, {delegate: '.favouriteItem'});
        }
    },
    // overriding superclass template method
    afterRender: function () {
        var me = this;
        Ext.each(me.favoritesjson, function (menuItem, index) {
            var tpl = me.createNotificationTemplate(menuItem.heading);
            if (menuItem.items != undefined)
                me.add(me.createItemPanel(tpl, menuItem.items));
            else
                me.add(me.createItemPanel(tpl, menuItem.views));
        });

        Edifecs.Notifications.superclass.afterRender.apply(this, arguments);
        return;
    },

    createNotificationTemplate: function (title) {
        var tpl = new Ext.XTemplate(
                '<span class="favouriteHeading">' + title.toUpperCase() + '</span>',
            '<tpl for=".">',
                '<div class="items">' +
                '<span class="floatLeft {iconCls}" style="width:16px;height:16px;">&nbsp;</span>' +
                '<a href="{hrefTarget}" id="{id}" class="favouriteItem">{text}</a>' +
                '<span class="floatRight pin-object" style="width:16px;height:16px;">&nbsp;</span>' +
                '</div>',
            '</tpl>',
            {
                compiled: true
            }
        );
        return tpl;
    },

    createItemPanel: function (tpl, items) {
        var favouritePanel = Ext.create('Ext.container.Container', {
            width: 215,
            html: tpl.apply(items),
            padding: '20 15'
        });
        return favouritePanel;
    },

    // private, clean up
    onDestroy: function () {
        this.removeAll();
        Edifecs.Notifications.superclass.onDestroy.apply(this, arguments);
    }
});