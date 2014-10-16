Ext.define('Security.view.role.NestedRoles', {
    extend:'Ext.container.Container',
    alias:'widget.nestedroles',
    requires:[
        'Security.view.role.IncludeRoles',
        'Security.view.role.MemberOfRoles'
    ],
    layout:{
        type:'hbox'
    },
    defaults:{
        margins:{top:0, right:0, bottom:35, left:0}
    },
    update:function (record) {

        var memberRoles = this.down('#includeroles');
        var memberofroles = this.down('#memberofroles');
        
        memberRoles.getStore().load({
        	params : {
        		data : '{"role":'+Ext.encode(record.data)+',"startRecord":0,"recordCount":-1}',   
        	}
        });
        
        memberofroles.getStore().load({
        	params : {
        		data : '{"roleId":'+Ext.encode(record.get('id'))+',"startRecord":0,"recordCount":-1}'
        	}
        });

    },

    items:[
        {
            xtype:'includeroles',
            title:"Include Roles",
            itemId:'includeroles',
            height:150,
            hideHeaders:true,
            flex:1,
            margins:{top:0, right:15, bottom:35, left:0}
        },
        {
            xtype:'memberofroles',
            title:'Users with Role',
            itemId:'memberofroles',
            height:150,
            hideHeaders:true,
            flex:1
            }
        ]
    }
);