Ext.define('Security.view.site.SiteConfiguration', {
    extend: 'Ext.container.Container',
    alias : 'widget.siteconfiguration',
    border:0,
    layout   : 'border',
    overflowX: 'auto',
    overflowY: 'hidden',
    defaults:{
        margins:{top:0, right:0, bottom:15, left:0}
    },
    config:{
    	siteId:null,
        siteRecord:null,
        disableLinking:false
    },
    title:'Site Home',
    items: [
        {
            region      :   'west',
            width       :   220,
            xtype       :   'LeftMenu',
            bodyPadding :	15,
            id      	:   "manageSiteMenu",
            itemId      :   "manageSiteMenu",
            url         :   'resources/json/site-json.json',
            menuType    :   "type2"
        },
        {
            id    : 'Home-page-container',
            xtype : 'container',
            layout: 'fit',
            region: 'center',
            flex  : 1,
            minWidth : 960,
            overflowY: 'auto',
            overflowX: 'hidden',
            bodyPadding:20,
            margin:'0 0 0 10'
        }
    ]

});

