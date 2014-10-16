Ext.define('Security.view.group.GroupDetailPane', {
	extend:'Ext.panel.Panel',
    alias:'widget.groupdetailpane',
    padding:0,
    layout:'border',
    defaults : {
        style:{
            backgroundColor:'#FFF!important'
        }
    },
    initComponent : function() {
        this.items = [
            {
                xtype:'groupdetailpaneheader',
                minHeight:120,
                region:'north'
            },
            {
                xtype:'groupdetailpanetabpanel',
                region:'center'
            }
        ];
        this.callParent(arguments);
    }
});