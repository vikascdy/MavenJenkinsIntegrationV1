Ext.define('Security.view.audit.PermissionsFilter', {
    extend:'Ext.tree.Panel',
    alias:'widget.permissionsfilter',

    store:'PermissionsTree',

    rootVisible:false,

    constructor:function (config) {

        Ext.apply(this, config || {});

        Ext.apply(this, {
            dockedItems:[
                {
                    xtype:'toolbar',
                    dock:'top',
                    items:[
                        {
                            xtype:'tbtext',
                            text:'Business Object Permissions',
                            flex:10
                        },
                        {
                            xtype:'button',
                            tooltip:'Clear All',
                            action:'clearall',
                            text:'Clear All'
                        }
                    ]
                }
            ]
        });

        this.callParent();
    }

});

