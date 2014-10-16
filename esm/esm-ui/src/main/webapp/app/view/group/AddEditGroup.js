Ext.define('Security.view.group.AddEditGroup', {
    extend: 'Ext.window.Window',
    alias : 'widget.addeditgroup',
    title:'Add Group',
    width:350,
    height:210,
    layout:'fit',
    modal:true,
    resizable:false,
    items:[
        {
            xtype:'form',
            id:'addgroupform',
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
                            fieldLabel:'Group Name',
                            allowBlank:false,
                            regex: /^[A-Za-z0-9 _]*$/,
						    regexText: '# check that the string contains *only* one or more alphanumeric chars or underscores',
							msgTarget: 'side',
                            maxLength:60
                        },
                        {
                            xtype: 'textarea',
                            name: 'description',
                            fieldLabel: 'Description'
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
                action: 'createGroup',
                hidden:me.operation != 'create'
            },
            {
                text:'Update',
                ui:'greenbutton',
                width:80,
                action: 'updateGroup',
                hidden:me.operation == 'create'
            }
        ];
        this.callParent(arguments);
    }

});

