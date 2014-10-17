Ext.define('Edifecs.Notifications', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.notifications',
    width: 300,
    border: false,
    bodyBorder: false,
    padding: '5px 8px 0px 8px',
    notificationJson: "",
    layout: 'hbox',
    config: {
        dockedItem: [
            {
                xtype: 'toolbar',
                dock: 'bottom',
                height: 30,
                ui: 'edifecs-applicationbar-notification',
                padding: 0,
                border: false,
                items: [
                    {
                        xtype: 'container',
                        autoEl: 'div',
                        layout: 'fit',
                        cls: 'showAllNotificationContainer',
                        width: '100%',
                        border: false,
                        height: 30,
                        items: {
                            xtype: 'component',
                            autoEl: 'a',
                            id: 'showAllNotificationId',
                            cls: 'showAllNotification',
                            html: 'Show All Notifications',
                            listeners: {
                                'afterrender': function (c) {
                                    c.getEl().on('click', function (e, t) {
                                        c.up('ApplicationBar').fireEvent('showAllNotificationClicked', t.id);
                                    }, c);
                                }
                            }
                        }
                    }
                ]
            }
        ]
    },

    // overriding superclass template method
    afterRender: function () {
        var me = this;
        if (me.notificationJson.length != 0) {
            var tpl = me.createNotificationTemplate();
            me.notificationJson.sort(me.sortJson);
            tpl.insertFirst(me.body, me.notificationJson);
            if (me.notificationJson.length >= 6)
                me.addDocked(me.getDockedItem());
            me.updateLayout();
        }
        Edifecs.Notifications.superclass.afterRender.apply(this, arguments);
        return;
    },


    sortJson: function (a, b) {
        a = Ext.Date.parse(a.dated, 'm/d/Y');
        b = Ext.Date.parse(b.dated, 'm/d/Y');
        return a > b ? -1 : a < b ? 1 : 0;
    },

    createNotificationTemplate: function () {
        var tpl = new Ext.XTemplate(
            '<div style="float:left;display:inline-block;">',
            '<tpl for=".">',
            '<tpl if="xindex &lt; 6">',
            '<div class="notificationtr">',
            '<table cellpadding="0" cellspacing="0" width="100%">',
            '<tr>',
            '<td valign="top" align="left" class="{iconCls}" width="16" height="16">&nbsp;</td>',
            '<td valign="middle" align="left" class="notificationtext">{content}</td>',
            '</tr>',
            '</table>',
            '</div>',
            '</tpl>',
            '</tpl>',
            '</div>'
        );
        return tpl;
    },

    // private, clean up
    onDestroy: function () {
        this.removeAll();
        Edifecs.Notifications.superclass.onDestroy.apply(this, arguments);
    }
});