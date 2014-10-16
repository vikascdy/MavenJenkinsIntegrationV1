Ext.define('Security.view.user.EditUserCredentials', {
    extend: 'Ext.window.Window',
    alias : 'widget.editusercredentials',
    requires:[
        'Ext.form.Panel',
        'Ext.form.FieldSet'
    ],
    title:'Update Password',
    width:450,
    height:190,
    layout:'fit',
    resizable:false,
    modal:true,
    initComponent : function() {
        var me = this;
        this.items = [
            {
                xtype:'usercredentialsform',
                userInfo:me.userInfo
            }
        ];
        this.callParent(arguments);
    }
});

