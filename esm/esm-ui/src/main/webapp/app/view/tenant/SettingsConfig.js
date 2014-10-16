Ext.define('Security.view.tenant.SettingsConfig', {
    extend: 'Ext.container.Container',
    alias : 'widget.settingsconfig',
    layout:'anchor',
    treeId:'settingsConfig',
    bodyPadding:20,
//    minHeight:900,
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
                    passwdRegex:"^[a-zA-Z]\\w{3,14}$",
                    passwdRegexDesc:"It must contain at least 4 characters and no more than 15 characters and no characters other than letters, numbers and the underscore may be used. It should always start with an alphabet."
                },
                {
                    passwdRegex:"^([a-zA-Z0-9@*#]{8,15})$",
                    passwdRegexDesc:"Password matching expression. Match all alphanumeric character and predefined wild characters. Password must consists of at least 8 characters and not more than 15 characters."
                },
                {
                    passwdRegex:"^(?![0-9]{6})[0-9a-zA-Z]{6}$",
                    passwdRegexDesc:"Matches a six character password that has to consist of numbers and letters with at least one letter in it."
                }

            ]
        });

        this.items= [
            {
                xtype: 'form',
                bodyPadding: 10,
                id:"tenantLandingPage",
                border: false,
                layout: 'anchor',
                defaults: {labelSeparator: ''},
                items: [
                    {
                        xtype: 'component',
                        margin: '0 0 20 0',
                        html: '<h2>Tenant Landing Page</h2>'

                    },
//                    {
//                        xtype: 'displayfield',
//                        fieldLabel: 'Description',
//                        name: 'description'
//                    },
                    {
                        xtype:'textfield',
                        width: 600,
                        allowBlank:false,
                        regex: /^[http|\/]/,
                        regexText: "Landing Page must begin with \"http\" for an absolute URL or \"\/\" for a relative URL",
                        fieldLabel: "Landing Page",
                        name: "landingPage",
                        cls:"x-body landingpage"
                    },


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
                                text:'Update LandingPage',
                                ui:'bluebutton',
                                action:'saveTenantLandingPage',
                                id:'tenantLogo-saveTenantLandingPage'
                            }]
                    }


                ]
            },
            {
                xtype: 'form',
                bodyPadding: 10,
                id:"tenantLogo",
                border: false,
                layout: 'anchor',
                defaults: {labelSeparator: ''},
                items: [
                    {
                        xtype: 'component',
                        margin: '0 0 20 0',
                        html: '<h2>Tenant Logo</h2>'

                    },
//                    {
//                        xtype: 'displayfield',
//                        fieldLabel: 'Description',
//                        name: 'description'
//                    },
                    {
                        xtype:'displayfield',
                        fieldLabel: "Current Logo",
                        name: "logo",
                        cls:"x-body logoDisp",
                        fieldCls: "logoFieldDisp",
                        listeners: {
                            afterRender: function (t, eOpts) {
                                t.setValue("<img src=\""+ t.rawValue+"\" />");




                            }
                        }
                    },

                    {
                        xtype: 'filefield',
                        name: 'newLogo',
                        fieldLabel: 'New Logo',
                        labelWidth: 50,
                        msgTarget: 'side',
                        allowBlank: false,
                        width: 600,
                        buttonText: 'Select New Logo',
                        listeners: {
                            change: function (field, value, opts) {

                                var reader = new FileReader(),
                                    inputEl = field.fileInputEl.dom,
                                    fileList = inputEl.files;
                                // read in file
                                reader.readAsDataURL(fileList[ 0 ]);
                                reader.onload = function (e) {
                                    var im=new Image();
                                    im.src= e.target.result;
                                    var closeEnough= (im.height < 70 && im.height > 50 && im.width > 150 && im.width < 220 );
                                    if(closeEnough){
                                        me.fireEvent('logoloaded', this, e.target.result, field);
                                    }
                                    else {
                                        Ext.MessageBox.show({
                                            title: 'Confirm Image Dimensions',
                                            msg: 'The selected image is not the recommended 60x190 pixels.  Do you wish to continue with this selection?',
                                            buttons: Ext.MessageBox.YESNO,
                                            fn: function (btn) {
                                                if (btn == 'yes') {
                                                    me.fireEvent('logoloaded', this, e.target.result, field);


                                                }
                                            }
                                        });
                                    }


                                }
                            }
                        }
                    },
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
                                text:'Upload Logo',
                                ui:'bluebutton',
                                action:'saveTenantLogo',
                                id:'tenantLogo-saveTenantLogo'
                            }]
                    }


                ]
            },
                /*buttons: [{
                    text: 'Upload',
                    ui:'bluebutton',
                    handler: function() {
                        var form = this.up('form').getForm();
                        if(form.isValid()){
                            form.submit({
                                url: 'photo-upload.php',
                                waitMsg: 'Uploading your photo...',
                                success: function(fp, o) {
                                    Ext.Msg.alert('Success', 'Your photo "' + o.result.file + '" has been uploaded.');
                                }
                            });
                        }
                    }
                }]
            },*/
            {
                xtype:'form',
                bodyPadding:10,
                id:"passwordPolicyConfigForm",
                border:false,
                layout:'anchor',
                defaults:{labelSeparator:''},
                items:[

                    {
                        xtype:'component',
                        margin:'0 0 20 0',
                        html:'<h2>Password Policy Configuration</h2>'
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
//                              name:'passwdRegexName',
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
            },
            {
                xtype: 'form',
                bodyPadding: 10,
                id:"tenantAppSettings",
                border: false,
                layout: 'anchor',
                defaults: {labelSeparator: ''},
                items:[]                    
            }
        ];

        this.callParent(arguments);
    }

});
