Ext.define('Security.view.tenant.CreateTenantUserGroup', {
    extend: 'Ext.container.Container',
    alias : 'widget.createtenantusergroup',
    layout:'fit',
    autoScroll:true,
	treeId:'manageGroups',
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
                    html:'<a href="#" id="tenantUserGroups-link" class="redirectURL quickLinks">BACK TO GROUPS LIST</a>',
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
                    html:'<h1>Create User Group</h1>'
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
                    id:'createTenantUserGroup-canonicalName',
                    emptyText:'Enter Name'
                },
                {
                    xtype:'textarea',
                    fieldLabel:'DESCRIPTION',
                    name:'description',
					regex:/^[A-Za-z0-9 _]*$/,
					regexText: '# check that the string contains *only* one or more alphanumeric chars or underscores',
					msgTarget: 'side',
                    id:'createTenantUserGroup-description',
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
                            action:'createUserGroup',
                            id:'createTenantUserGroup-createUserGroup',
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
