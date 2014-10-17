Ext.onReady(function () {
    Ext.create('Ext.container.Viewport',
        {
            layout  :'border',
            renderTo:Ext.getBody(),
            defaults:{border:false},
            items   :[
                {
                    region      :   'west',
                    width       :   220,
                    xtype       :   'LeftMenu',
                    itemId      :   "westLeftMenuId",
                    url         :   'json/LeftMenu.json',
                    menuType    :   "type1" // add attribute for menuType
                },
                {
                    region      :   'east',
                    width       :   220,
                    xtype       :   'LeftMenu',
                    itemId      :   "eastLeftMenuId",
                    url         :   'json/LeftMenu.json',
                    menuType    :   "type2" // add attribute for menuType
                }
            ]
        });
});