//Main tab panel where tabs are added
//It uses TabCloseMenu plugin to handle tab context menu

Ext.define('PP.view.core.PluginDetail', {
    extend       :  'Ext.tab.Panel',
    alias        :  'widget.plugindetail',
    bodyPadding  :  '10',
    id           :  'PluginDetail',
    activeTab    :  0,
    animCollapse :  true,
    tabBar       :  {
                        plain   :   true
    },
    initComponent:  function (config) {

        //By default welcome tab is shown
        this.items = [
            {
                title      :   'Welcome',
                autoScroll :   true,
                closable   :   false,
                id         :   'Welcome',
                width      :   300,
                bodyPadding:   '0',
                html       :   'welcome note',
                listeners  :   {
                    'afterrender'   :   function()
                    {
                        setPanelScroller(this);
                    }
                }
            }
        ];

        //Using TabCloseMenu plugin for tab context menu
        this.plugins = Ext.create('PP.view.core.TabCloseMenu', {
            listeners:{
                aftermenu :function () {
                    currentItem = null;
                },
                beforemenu:function (menu, item) {
                    currentItem = item;
                }
            }
        });

        //Ajax request to retrieve welcome notes from welcome.txt file
        Ext.Ajax.request({
            url    :'welcome.txt',
            success:function (response) {
                Ext.getCmp('Welcome').update(response.responseText);
            }
        });

        this.callParent(arguments);
    }
});
