Ext.define('Security.view.group.GroupInfo', {
    extend: 'Ext.container.Container',
    alias : 'widget.groupinfo',

    requires: [
        'Security.store.PermissionTabs',
        'Security.view.group.GroupProps'
    ],

    currentRecord:null

    ,layout : {
        type:'vbox',
        align:'stretch',
        defaultMargins: {top: 0, right: 15, bottom: 0, left: 15}
    }

    ,items: [
        {
            xtype: 'groupprops',
            itemId: 'groupprops',
            border:0,
            height:90,
            margins: {top: 0, right: 15, bottom: 15, left: 16}
        },
        {
            xtype: 'grouproles',
            itemId: 'grouproles',
            border:0,
            margin:'0 0 15 0'
        },
        {
            xtype: 'groupusers',
            itemId: 'groupusers',
            border:0
        }
//        {
//            xtype:'container',
//            itemId:'grouppermissionspanel',
//            layout:'fit',
//            flex:1,
//            border :0
//        }
    ],

    updatePermissions: function(permissions) {
        var permissionsContainer = this.down('#grouppermissionspanel');
        permissionsContainer.removeAll();
        var store = Ext.create('Ext.data.Store', {
            model: 'Security.model.Permission',
            autoLoad:true,
            data:permissions,
            groupField:'productCanonicalName'
        });

        permissionsContainer.add({
            xtype:'editpermissions',
            readOnly:true,
            productGroups:store.getGroups(),
            headerCaption: 'Permissions'
        });

    },

    update: function(record) {
        var me = this;
        this.currentRecord = record;
        var data = record ? record.data : {};

//         var loadingWindow = Ext.widget('progresswindow', {
//            text: 'Loading ' + record.get('name') + '...'
//        });
        me.down('#groupprops').update(data);
        me.down('#grouproles').update(record, function() {
//            UserManager.getPermissionsForUser(record, function(permissions) {
//                me.updatePermissions(permissions);
//                loadingWindow.destroy();
//            });
            me.down('#groupusers').update(record, function() {
            });
        });
    }
});