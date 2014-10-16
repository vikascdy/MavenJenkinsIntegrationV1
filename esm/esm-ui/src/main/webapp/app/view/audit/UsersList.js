Ext.define('Security.view.audit.UsersList', {
    extend:'Ext.grid.Panel',
    alias:'widget.audituserslist',

    requires:['Ext.selection.CheckboxModel'],

    store: {
        model: 'Security.model.User'
        ,sorters:[
            {
                property:'userName',
                direction:'ASC'
            }
        ]
        ,autoLoad:false
        , proxy: {
            type: 'ajax',
            url: 'security-data/permissions/users'
        }
    },
    columns:[
        {header:'Username', dataIndex: 'userName', xtype:'templatecolumn',
            tpl:true?'<a href="#!/{[users]}/{userId}">{userName}</a>':'{userName}',
            flex:1}
    ],

    constructor:function (config) {
        Ext.apply(this, config || {});

        Ext.apply(this, {
            dockedItems:[
                {
                    dock:'top',
                    xtype:'toolbar',
                    items:[
                        'Users with All Selected Permissions' //todo count: 'Users with All Selected Permissions (124)'
                    ]
                    ,height:27
                }
            ]
        });

        this.callParent();
    }
});
