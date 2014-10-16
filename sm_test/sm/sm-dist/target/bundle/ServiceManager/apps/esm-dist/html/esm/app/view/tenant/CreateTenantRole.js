Ext.define('Security.view.tenant.CreateTenantRole', {
    extend: 'Ext.container.Container',
    alias : 'widget.createtenantrole',
    layout:'fit',
    autoScroll:true,
	treeId:'manageRoles',
    initComponent : function(){
    	
    var me=this;
    this.items = [
        {
            xtype:'form',
            bodyPadding:20,
            border:false,
            layout:'anchor',
            defaults:{labelAlign:'top',labelSeparator:'',anchor:'50%'},
            items:[
                {
                    xtype:'component',
                    html:'<a href="#" id="tenantRoles-link" class="redirectURL quickLinks">BACK TO ROLES LIST</a>',
                    listeners : {
                    	'afterrender':function () {
                            this.getEl().on('click', function(e, t, opts) {
                                e.stopEvent();
                                if(me.redirectPage){
	                                window.location = me.redirectPage;
                                }
                            }, null, {delegate: '.redirectURL'});

                        }
                    }
                },
                {
                    xtype:'component',
                    html:'<h1>Create Role</h1>'
                },
                {
                    xtype:'component',
                    margin:'30 0 0 0',
                    html:'<h3>Basic Information</h3>'
                },
                {
                    xtype:'textfield',
                    allowBlank:false,
                    fieldLabel:'NAME',
                    name:'canonicalName',
					regex: /^[A-Za-z0-9 _]*$/,
					regexText: '# check that the string contains *only* one or more alphanumeric chars or underscores',
					msgTarget: 'side',
                    id:'createTenantRole-canonicalName',
                    emptyText:'Enter Name'
                },
                {
                    xtype:'textarea',
                    fieldLabel:'DESCRIPTION',
                    name:'description',
					regex: /^[A-Za-z0-9 _]*$/,
					regexText: '# check that the string contains *only* one or more alphanumeric chars or underscores',
					msgTarget: 'side',
                    id:'createTenantRole-description',
                    emptyText:'Add Description'
                },
                {
                    xtype:'component',
                    margin:'30 0 0 0',
                    html:'<div style="border-style:dashed none  none none; border-width: 1px; border-color: gray;"></div>'
                },
                {
                    xtype:'container',
                    margin:'10 0 0 0',
                    layout:'hbox',
                    items:[
                        {
                            xtype:'tbspacer',
                            flex:1
                        },
                        {
                            xtype:'button',
                            formBind:true,
                            action:'createRole',
                            id:'createTenantRole-createRole',
                            text:'Save',
                            ui:'bluebutton'
                        }
                    ]
                }
            ]
        }
    ];
    
    this.callParent(arguments);
    }

});
