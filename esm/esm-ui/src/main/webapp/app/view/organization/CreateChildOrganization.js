Ext.define('Security.view.organization.CreateChildOrganization', {
    extend: 'Ext.window.Window',
    alias : 'widget.createchildorganization',
    layout:'fit',
    height:300,
    width:400,
    modal:true,
    draggable:false,
    resizable:false,
    title:'Create Organization',
    initComponent : function(){
	var me=this;
    this.items= [
        {
            xtype:'form',
            bodyPadding:20,
            border:false,
            layout:'anchor',
            defaults:{labelAlign:'top',labelSeparator:'',anchor:'100%'},
            items:[
                {
                    xtype:'textfield',
                    allowBlank:false,
                    fieldLabel:'NAME',
                    name:'canonicalName',
					regex: /^[A-Za-z0-9 _]*$/,
					regexText: '# check that the string contains *only* one or more alphanumeric chars or underscores',
					msgTarget: 'side',
                    id:"createChildOrganization-canonicalName",
                    emptyText:'edifecscloud.com'
                },
                {
                    xtype:'textarea',
                    fieldLabel:'DESCRIPTION',
                    name:'description',
					regex: /^[A-Za-z0-9 _]*$/,
					regexText: '# check that the string contains *only* one or more alphanumeric chars or underscores',
					msgTarget: 'side',
                    id:"createChildOrganization-description",
                    emptyText:'Add Description'
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
                            action:'createOrganization',
                            id:"createChildOrganization-createOrganization",
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
