Ext.define('Security.view.organization.OrganizationConfiguration', {
    extend: 'Ext.container.Container',
    alias : 'widget.organizationconfiguration',
    border:0,
    layout   : 'border',
    overflowX: 'auto',
    overflowY: 'hidden',
    defaults:{
        margins:{top:0, right:0, bottom:15, left:0}
    },
    config:{
        organizationId:null,
        organizationRecord:null,
        disableLinking:false
    },
    title:'Organization Home',
    items: [
        {
            region      :   'west',
            width       :   220,
            xtype       :   'LeftMenu',
            bodyPadding :	15,
            id      	:   "configOrganizationMenu",
            itemId      :   "configOrganizationMenu",
            url         :   'resources/json/organization-json.json',
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
            margin:'0 0 0 10',
            items: [
//                {
//                    xtype:'organizationoverview'
//                }
            ]
        }
    ]

});
