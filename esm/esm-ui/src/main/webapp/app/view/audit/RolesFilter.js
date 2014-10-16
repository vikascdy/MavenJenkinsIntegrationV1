Ext.define('Security.view.audit.RolesFilter', {
    extend:'Ext.grid.Panel',
    alias:'widget.rolesfilter',

    store : 'RolesList',
    columns:[
        {
            header:'All Roles',
            dataIndex: 'canonicalName',
            renderer : function(v,m,r){
                return Ext.String.format('<a href="#!/ManageRoles/{0}" class="role-link">{1}</a>', r.get('id'), r.get('canonicalName'));
            },
            flex:1
        }
    ],


    selType:'checkboxmodel',
    selModel:{
        allowDeselect:true,
        mode:'MULTI'
    },

    constructor:function (config) {

        Ext.apply(this, config || {});

        Ext.apply(this, {
            dockedItems:[
                {
                    dock:'top',
                    xtype:'toolbar',
                    items:[
                        'Filter By Roles'
                    ],height:28
                }
            ],
            listeners : {
                'render' : function() {
                    this.getStore().load();
                }
            }
        });

        this.callParent();
    }

});
