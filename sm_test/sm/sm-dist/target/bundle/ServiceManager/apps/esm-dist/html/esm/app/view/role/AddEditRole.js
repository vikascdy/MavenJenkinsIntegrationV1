Ext.define('Security.view.role.AddEditRole', {
    extend: 'Ext.window.Window',
    alias : 'widget.addeditrole',
    title:'Add Role',
    width:350,
    height:210,
    layout:'fit',
    modal:true,
    resizable:false,
    items:[
        {
            xtype:'form',
            id:'addroleform',
            fieldDefaults: {
                labelAlign: 'left',
                labelWidth: 90,
                anchor: '100%'
            },
            bodyPadding:5,
            items:[
                        {
                            xtype:'textfield',
                            name:'canonicalName',
                            fieldLabel:'Role Name',
                            allowBlank:false,
                            regex:/^[A-Za-z0-9 _]*$/,
						    regexText: '# check that the string contains *only* one or more alphanumeric chars or underscores',
							msgTarget: 'side',
                            maxLength:60
                        },
                        {
                            xtype: 'textarea',
                            name: 'description',
                            fieldLabel: 'Description',
							regex: /^[A-Za-z0-9 _]*$/,
							regexText: '# check that the string contains *only* one or more alphanumeric chars or underscores',
							msgTarget: 'side',
                            maxLength:250
                        }
                    ]
        }
    ],
    initComponent : function() {
        var me = this;
        this.buttons = [
            {
                text:'Cancel',
                width:80,
                handler:function() {
                    this.up('window').close();
                }
            },
            {
                text:'Create',
                ui:'greenbutton',
                width:80,
                action: 'createRole',
                hidden:me.operation != 'create'
            },
            {
                text:'Update',
                ui:'greenbutton',
                width:80,
                action: 'updateRole',
                hidden:me.operation == 'create'
            }
        ];
        this.callParent(arguments);
    }

});

