Ext.define('Security.view.role.RoleDetailPane', {
	extend:'Ext.panel.Panel',
    alias:'widget.roledetailpane',
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
                xtype:'roledetailpaneheader',
                minHeight:120,
                region:'north'
            },
            {
                xtype:'roledetailpanetabpanel',
                region:'center'
            }
        ];
        this.callParent(arguments);
    }
});