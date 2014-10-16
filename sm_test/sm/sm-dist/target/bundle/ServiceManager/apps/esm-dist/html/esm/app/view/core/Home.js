Ext.define('Security.view.core.Home', {
    extend: 'Ext.container.Container',
    alias : 'widget.home',
    border:0,
    layout   : 'border',
    overflowX: 'auto',
    overflowY: 'hidden',
    items: [
        {
            xtype: 'DoorMateApplicationBar',
            id: 'Home-header-container',
            url:'home.json',
            logoIcon:'edifecs-logo',
            border:0,
            region: 'north',
            minWidth: 960
        },
        {
            id    : 'Home-page-container',
            border:0,
            xtype : 'container',
            layout: 'fit',
            region: 'center',
            flex  : 1,
            minWidth : 960,
            overflowY: 'auto',
            overflowX: 'hidden',
            items: [
                {
                    xtype:'panel',
                    title:'About Security Manager',
                    bodyPadding:15,
                    minHeight:700,
                    padding:20,
                    styleHtmlContent:true,
                    html:'<p> Information on Security</p>'
                }
            ]
        }
    ],
    initComponent: function() {

        if (Ext.getCmp('menuToolbar'))
            Ext.getCmp('menuToolbar').destroy();

        this.callParent(arguments);
    }

});
