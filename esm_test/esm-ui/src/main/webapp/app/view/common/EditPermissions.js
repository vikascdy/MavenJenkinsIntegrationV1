Ext.define('Security.view.common.EditPermissions', {
    extend:'Ext.container.Container',
    requires: [
        'Security.view.common.EditPermissionsTabPanel'
    ],
    alias: 'widget.editpermissions',
    layout:{
        type:'vbox',
        align:'stretch'
    },
    showHeader:true,

    initComponent: function() {
        var me = this;
        me.items = [
           {
                xtype: 'editpermissionstabpanel',
                productGroups:me.productGroups,
                record:me.record,
                readOnly:me.readOnly,
                grayed:me.grayed,
                tbar : [
                    {xtype: "tbfill"},
//                    {
//                        xtype:'combo',
//                        fieldLabel:'Show',
//                        labelWidth:40,
//                        labelSeparator:'',
//                        editable:false,
//                        width:120,
//                        value:'All'
//                    },
                    {
                        xtype: "button",
                        text:  "Refresh",
                        itemId:'refreshPermissionForRole',
                        margin:'0 0 0 5'
                    },
                    {
                        xtype: "button",
                        text:  "Save",
                        itemId:'savePermissionsForRole',
                        margin:'0 0 0 5',
                        hidden : me.readOnly
                    }
                ]
                
            },{
                xtype:'panel',
                bodyPadding:10,
                flex:1,
                padding:'0 0 10 0',
                html:'No Permission found',
                hidden:me.productGroups.length > 0
            }
        ];
        this.callParent(arguments);
    }
});