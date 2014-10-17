//Main tab panel where tabs are added
//It uses TabCloseMenu plugin to handle tab context menu

Ext.define('PP.view.core.UXDemoPortal', {
    extend:'Ext.container.Container',
    alias :'widget.uxdemoportal',
    style :{backgroundColor:'#FFFFFF;'},
    layout:{
        type:'border'
    },
    items :[
        //For left component list tree
        {
            id    :'pp-tree-container',
            xtype :'LeftMenu',
            url   :'json/ComponentListTree.json',
            title :'Component List',
            menuType:'type1',
            width :240,
            layout:'fit',
            region:'west',
            split :true

        },
        //For tab panel
        {
            id    :'plugin-container',
            xtype :'plugindetail',
			padding:'25 0 0 25',
            layout:'fit',
            region:'center'
        }
    ]
});
