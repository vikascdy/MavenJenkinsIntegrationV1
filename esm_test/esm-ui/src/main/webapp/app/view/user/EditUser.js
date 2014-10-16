Ext.define('Security.view.user.EditUser', {
    extend: 'Ext.window.Window',
    alias : 'widget.edituser',
    requires:[
        'Security.view.common.DateDisplayField',
        'Ext.form.Panel',
        'Ext.form.FieldSet'
    ],
    title:'Edit User',
    width:370,
    height:470,
    layout:'fit',
    resizable:false,
    modal:true,
    initComponent : function() {
        var me = this;
        this.items = [
            {
                xtype:'form',
                id:'adduserform',
                fieldDefaults: {
                    labelAlign: 'left',
                    labelWidth: 110,
                    anchor: '100%'
                },
                bodyPadding:10,
                items:[
                    {
                        xtype:'fieldset',
                        id: 'addUserContactInfo',
                        title:'Contact Info',
                        items:[{
                               xtype:'textfield',
                               name:'username',
							   id : 'edit-user-username',
                               value:me.userInfo.get('username'),
                               hidden:true
                            },
                            {
                                xtype:'textfield',
                                name:'firstName',
								id : 'edit-user-firstname',
                                maxLength:20,
                                regex: /^[A-Za-z0-9 _]*$/,	
                                regexText: 'Invalid chars',
                                fieldLabel:'First Name',
                                allowBlank:false,
                                value:me.contactInfo.get('firstName') ? me.contactInfo.get('firstName') : ''
                            },
                            {
                                xtype:'textfield',
                                name:'middleName',
								id : 'edit-user-middlename',
                                maxLength:20,
                                regex: /^[A-Za-z0-9 _]*$/,	
                                regexText: 'Invalid chars',
                                fieldLabel:'Middle Name',
                                defaultValue:'',
                                value:me.contactInfo.get('middleName') ? me.contactInfo.get('middleName') : ''
                            },
                            {
                                xtype:'textfield',
                                name:'lastName',
								id : 'edit-user-lastname',
                                maxLength:19,
                                regex: /^[A-Za-z0-9 _]*$/,	
                                regexText: 'Invalid chars',
                                fieldLabel:'Last Name',
                                allowBlank:false,
                                value:me.contactInfo.get('lastName') ? me.contactInfo.get('lastName') : ''
                            },
                            {
                                xtype:'textfield',
                                name:'salutation',
								id : 'edit-user-salutation',
                                maxLength:20,
                                fieldLabel:'Title',
								regex: /^[A-Za-z0-9 _]*$/,	
                                allowBlank:true,
                                value:me.contactInfo.get('salutation') ? me.contactInfo.get('salutation') : ''
                            },
                            {
                                xtype:'textfield',
                                name:'emailAddress',
								id : 'edit-user-emailAddress',
                                fieldLabel:'Email',
                                maxLength:100,
                                vtype: 'email',
                                allowBlank:false,
                                value:me.contactInfo.get('emailAddress') ? me.contactInfo.get('emailAddress') : ''
                            }
                        ]
                    },
                    {
                        xtype:'fieldset',
                        title:'Status',
                        items: [
                            {
                                xtype: 'checkbox',
                                hidden:me.mode=='editProfile',
                                inputValue:true,
                                uncheckedValue:false,
                                name: 'active',
								id : 'edit-user-active',
                                flex:1,
                                boxLabel: 'Active',
                                checked:me.userInfo.get('active')

                            },
                            {
                                xtype: 'checkbox',
                                hidden:me.mode=='editProfile',
                                inputValue:true,
                                uncheckedValue:false,
                                name: 'suspended',
								id : 'edit-user-suspended',
                                flex:1,
                                boxLabel: 'Suspended',
                                checked:me.userInfo.get('suspended')

                            },
                            {
                                xtype:'displayfield',
                                readOnly:true,
                                fieldLabel:'Created On',
                                value:me.userInfo.get('formattedCreatedDateTime')
                            },
                            {
                                xtype:'displayfield',
                                readOnly:true,
                                name:'modifiedDateTime',
								id : 'edit-user-modifiedDateTime',
                                fieldLabel:'Modified On',
                                value:me.userInfo.get('formattedModifiedDateTime')
                            },
                            {
                                xtype:'textfield',
                                name:'createdDateTime',
								id : 'edit-user-createdDateTime',
                                hidden:true,
                                value:me.userInfo.get('createdDateTime')
                            },
                            {
                                xtype:'textfield',
                                name:'modifiedDateTime',
								id : 'edit-user-textfield-modifiedDateTime',
                                hidden:true,
                                value:me.userInfo.get('modifiedDateTime')
                            }

                        ]
                    }

                ]
            }
        ];
        this.callParent(arguments);
    },
    buttons:[
        {
            text:'Cancel',
            width:80,
            handler:function() {
                this.up('window').close();
            }
        },
        {
            text:'Update',
            ui:'greenbutton',
            width:80,
            action: 'updateUser'
        }
    ],
    bindCustomFields: function(records) {
    	var theFieldSet = this.queryById('addUserContactInfo');
    	Ext.each(records, function(record, index, recordArray) {
    		theFieldSet.add(
	    		new Ext.form.field.Text({
		            xtype: 'textfield',
		            name: record.name,
		            fieldLabel: record.label,
		            value: record.value,
		            disabled: true
	            })
	    	);
    	});
    }
});

