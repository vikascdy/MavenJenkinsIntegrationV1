Ext.define('Security.view.role.RoleInfo', {
    extend: 'Ext.container.Container',
    alias : 'widget.roleinfo',

    requires: [
        'Security.view.role.RolesProps',
        'Security.view.role.NestedRoles'
    ],

    currentRecord:null

    ,layout : {
        type:'vbox'
        ,align:'stretch'
        ,defaultMargins: {top: 0, right: 15, bottom: 0, left: 15}
    }
    ,items: [
        {
            xtype: 'roleprops'
            ,itemId: 'roleprops'
            ,border:0
            ,height:60
            ,margins: {top: 0, right: 15, bottom: 15, left: 16}
        },
        {
            xtype: 'nestedroles'
            ,itemId: 'nestedroles'
            ,height:150
            ,border:0
        },
        {
            xtype:'container'
            ,itemId:'rolepermissions'
            ,flex:1
            ,layout:'fit'
            ,border :0
        }
    ],

    updatePermissions: function(permissions,record) {
        var permissionsContainer = this.down('#rolepermissions');
        permissionsContainer.removeAll();

        var store = Ext.create('Ext.data.Store', {
            model: 'Security.model.Permission',
            autoLoad:true,
            data:permissions,
            groupField:'productCanonicalName'
        });

        permissionsContainer.add({
            xtype:'editpermissions',
            grayed:record.get('readOnly'),
            productGroups:store.getGroups(),
            headerCaption: 'Permissions',
            record:record
        });
    },

    update: function(record) {
        var me = this;
        var data = record ? record.data : {};
        this.currentRecord = record;
        this.down('#roleprops').update(data);
        this.down('#nestedroles').update(record);
        this.down('#nestedroles').update(record);
        RoleManager.getAllPermissions(function(permissions) {
            me.updatePermissions(permissions,record);
        });
    }

});