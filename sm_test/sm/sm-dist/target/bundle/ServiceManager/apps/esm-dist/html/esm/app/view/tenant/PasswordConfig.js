Ext.define('Security.view.tenant.PasswordConfig', {
    extend: 'Ext.container.Container',
    alias : 'widget.passwordconfig',
    layout:'fit',
    treeId:'passwordConfig',
    minHeight:1700,
    autoScroll:true,
    initComponent : function(){

        var me=this;

        Ext.define('RegXModel', {
            extend : 'Ext.data.Model',
            fields : [
                {name:'passwdRegex',type:'string'},
                {name:'passwdRegexDesc',type:'string'}
            ]
        });

        var store = Ext.create('Ext.data.Store',{
            model:'RegXModel',
            data: [
                {
                    passwdRegex:"^[a-zA-Z]\w{3,14}$",
                    passwdRegexDesc:"The password's first character must be a letter, it must contain at least 4 characters and no more than 15 characters and no characters other than letters, numbers and the underscore may be used"
                },
                {
                    passwdRegex:"^([a-zA-Z0-9@*#]{8,15})$",
                    passwdRegexDesc:"Password matching expression. Match all alphanumeric character and predefined wild characters. Password must consists of at least 8 characters and not more than 15 characters."
                },
                {
                    passwdRegex:"^(?![0-9]{6})[0-9a-zA-Z]{6}$",
                    passwdRegexDesc:"Matches a six character &quot;password&quot; that has to consist of numbers and letters with at least one letter in it."
                }

            ]
        });

        this.items= [
            {
                xtype:'form',
                bodyPadding:20,
                border:false,
                layout:'anchor',
                defaults:{labelSeparator:''},
                items:[

                    {
                        xtype:'component',
                        margin:'0 0 20 0',
                        html:'<h1>Password Policy Configuration</h1>'
                    },
                    {
                        xtype:'checkboxfield',
                        width :240,
                        labelWidth :160,
                        inputValue:true,
                        fieldLabel: 'Change Password At First Login',
                        name      : 'changePasswdAtFirstLogin',
                        id:'managePasswordPolicy-changePasswdAtFirstLogin'
                    },
                    {
                        xtype:'container',
                        margin:'0 0 5 0',
                        layout :{type :'hbox',align :'stretch'},
                        items : [
                            {
                                xtype: 'numberfield',
                                minValue:1,
                                name:'passwdAge',
                                id:'managePasswordPolicy-passwdAge',
                                fieldLabel :'Maximum Password Age',
                                allowBlank :false,
                                width :240,
                                labelWidth :160

                            },
                            {
                                html : 'days',
                                padding:'5 0 0 10',
                                border: false

                            }
                        ]
                    },
                    {
                        xtype:'container',
                        margin:'0 0 5 0',
                        layout :{type :'hbox'},
                        items : [
                            {
                                xtype: 'numberfield',
                                minValue:1,
                                name:'passwdHistory',
                                id:'managePasswordPolicy-passwdHistory',
                                fieldLabel :'Enforce Password History',
                                allowBlank :false,
                                width :240,
                                labelWidth :160

                            },
                            {
                                html : 'last passwords remembered',
                                padding:'5 0 0 10',
                                border: false

                            }
                        ]
                    },
                    {
                        xtype:'container',
                        margin:'0 0 5 0',
                        layout :{type :'hbox'},
                        items : [
                             {
                                 xtype: 'numberfield',
                                 minValue:1,
                                 name:'passwdLockoutDuration',
                                 id:'managePasswordPolicy-passwdLockoutDuration',
                                 fieldLabel :'Password Lockout Duration',
                                 allowBlank :false,
                                 width :240,
                                 labelWidth :160

                             },
                            {
                                html : 'minutes',
                                padding:'5 0 0 10',
                                border: false

                            }
                        ]
                    },
                    {
                        xtype:'container',
                        margin:'0 0 5 0',
                        layout :{type :'hbox'},
                        items : [                             
                             {
                                 xtype: 'numberfield',
                                 minValue:1,
                                 name:'passwdMaxFailure',
                                 id:'managePasswordPolicy-passwdMaxFailure',
                                 fieldLabel :'Password Max Failure',
                                 allowBlank :false,
                                 width :240,
                                 labelWidth :160

                             },
                            {
                                html : 'attempts',
                                padding:'5 0 0 10',
                                border: false

                            }
                        ]
                    },
                    {
                        xtype:'container',
                        margin:'0 0 5 0',
                        layout :{type :'hbox'},
                        items : [  
                             {
                                     xtype: 'numberfield',
                                     minValue:1,
                                     name:'passwdResetFailureLockout',
                                     id:'managePasswordPolicy-passwdResetFailureLockout',
                                     fieldLabel :'Password Reset Failure Lockout',
                                     allowBlank :false,
                                     width :240,
                                     labelWidth :160

                             },
                            {
                                html : 'minutes',
                                padding:'5 0 0 10',
                                border: false

                            }
                        ]
                    },
                    {
                        xtype:'fieldset',
                        margin:'30 0 0 0',
                        padding:10,
                        title: 'Password Regular Expression',
                        width:600,
                        defaults:{anchor:'100%'},
                        layout: 'anchor',
                        items :[
//                            {
//                                xtype:'textfield',
//                                hidden:true,
//                                value:'false',
//                                name:'isValid',
//                                id:'managePasswordPolicy-isValid',
//                                itemId:'isValid'
//                            },
                            {
                                xtype:'textfield',
                                allowBlank:false,
                                name:'passwdRegexName',
                                fieldLabel:'Name'
                            },
                            {
                                xtype:'combobox',
                                name:'passwdRegex',
                                id:'managePasswordPolicy-passwdRegex',
                                itemId:'passwdRegex',
                                valueField:'passwdRegex',
                                queryMode:'local',
                                displayField:'passwdRegex',
                                store:store,
                                fieldLabel:'Regular Expression',
                                allowBlank:false,
                                listeners : {
                                    select : function(combo,records){

                                        var description=me.down('#passwdRegexDesc');
                                        description.setValue(records[0].get('passwdRegexDesc'));

                                    },
                                    change : function(combo,newValue){

                                        var index = combo.getStore().find('passwdRegex',newValue);
                                        var description=me.down('#passwdRegexDesc');
                                        if(index!=-1)
                                        {
                                            var rec=combo.getStore().getAt(index);
                                            description.setValue(rec.get('passwdRegexDesc'));
                                        }
                                        description.setValue('');

                                    }
                                }
                            },
                            {
                                xtype:'container',
                                flex:1,
                                layout:{type:'hbox',align:'stretch'},
                                items:[{
                                    xtype:'textfield',
                                    flex:1,
                                    fieldLabel: 'Test Password',
                                    name: 'password',
                                    id:'managePasswordPolicy-password',
                                    itemId:'password'
                                },{
                                    xtype:'button',
                                    margin:'0 0 0 10',
                                    text:'Validate',
                                    id:'managePasswordPolicy-validate',
                                    iconCls:'validate',
                                    tooltip : 'Validate password against given regular expression',
                                    handler : function(btn) {
                                        var form=btn.up('form');
                                        var password = form.down('#password');
                                        var passwdRegex = form.down('#passwdRegex');
//                                        var isValidStatus = form.down('#isValid');

                                        if(password.getValue().length<=0 || passwdRegex.getValue().length<=0)
                                            Functions.errorMsg("Expression OR Password Field Empty", 'Warning', null, 'WARN');
                                        else
                                        {
                                            UserManager.matchPattern(passwdRegex.getValue(),password.getValue(),function(isValid){
                                                if(isValid){
                                                    Functions.errorMsg("Password matched sucessfully to given expression.", 'Success', null, 'INFO');
//                                                    isValidStatus.setValue('true');
                                                }
                                                else{
                                                    Functions.errorMsg("Password matching failed.", 'Warning', null, 'WARN');
//                                                    isValidStatus.setValue('false');
                                                }
                                            },this);

                                        }
                                    }
                                }]
                            },{
                                xtype:'textarea',
                                margin:'5 0 0 0',
                                allowBlank:false,
                                fieldLabel: 'Description',
                                name: 'passwdRegexDesc',
                                id:'managePasswordPolicy-passwdRegexDesc',
                                itemId: 'passwdRegexDesc'
                            }]
                    },
//                   {
//                	   xtype:'regxgrid',
//                	   margin:'20 0 0 0',
//                	   width:500
//                   },
                    {
                        xtype:'component',
                        width : 600,
                        margin:'30 0 0 0',
                        html:'<div style="border-style:dashed none  none none; border-width: 1px; border-color: gray;"></div>'
                    },
                    {
                        xtype:'container',
                        margin:'10 0 0 0',
                        layout:'hbox',
                        width : 600,
                        items:[
                            {
                                xtype:'tbspacer',
                                flex:1
                            },
                            {
                                xtype:'button',
                                formBind:true,
                                text:'Save',
                                ui:'bluebutton',
                                action:'savePasswordPolicy',
                                id:'managePasswordPolicy-savePasswordPolicy'
                            }]
                    }
                ]
            }
        ];

        this.callParent(arguments);

    }

});
