Ext.define('Security.view.user.UserDetailPane', {
	extend:'Ext.panel.Panel',
    alias:'widget.userdetailpane',
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
                xtype:'accountsettings',
                minHeight:150,
                region:'north'
            },
            {
                xtype:'userdetailpanetabpanel',
                region:'center'
            }
        ];
        this.callParent(arguments);
    }
});