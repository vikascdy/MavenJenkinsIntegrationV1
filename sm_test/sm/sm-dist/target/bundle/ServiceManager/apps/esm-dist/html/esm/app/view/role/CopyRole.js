Ext.define('Security.view.role.CopyRole', {
    extend: 'Ext.window.Window',
    alias : 'widget.copyrole',
    title: 'Copy Role',
    width:350,
    height:370,
    layout:'fit',
    modal:true,
    resizable:false,
    items:[
        {
            xtype:'form',
            id:'copyroleform',
            fieldDefaults: {
                labelAlign: 'left',
                labelWidth: 90,
                anchor: '100%'
            },
            bodyPadding:5,
            items:[
                {
                    xtype:'fieldset',
                    title:'Role Info',
                    items: [
                        {
                            xtype:'textfield',
                            name:'canonicalName',
                            fieldLabel:'canonicalName',
							regex: /^[A-Za-z0-9 _]*$/,
							regexText: '# check that the string contains *only* one or more alphanumeric chars or underscores',
							msgTarget: 'side',
                            allowBlank:false,
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
                },
                {
                    xtype:'fieldset',
                    title:'Copy',
                    items: [
                        {
                            xtype:'checkbox',
                            name:'copyRoles',
                            boxLabel: 'Child Roles',
                            checked:true,
                            inputValue:true,
                            uncheckedValue:false
                        },
                        {
                            xtype:'checkbox',
                            name:'copyPermissions',
                            boxLabel: 'Permissions',
                            checked:true,
                            inputValue:true,
                            uncheckedValue:false
                        }
                    ]
                }
            ]
        }
    ],
    buttons:[
        {
            text:'Cancel',
            width:80,
            handler:function() {
                this.up('window').close();
            }
        },
        {
            text:'Save',
            width:80,
            action: 'copyAndSaveRole'
        }
    ]

});

