Ext.define('Security.view.site.SiteOverview', {
    extend: 'Ext.container.Container',
    alias : 'widget.siteoverview',
    layout:'fit',
    treeId:'overview',
    items: [
        {
            xtype:'form',
            bodyPadding:20,
            border:false,
            layout:'anchor',
            defaults:{labelAlign:'top',labelSeparator:'',anchor:'50%'},
            items:[
                {
                    xtype:'component',
                    html:'<h1>Site Overview</h1>'
                },
                {
                    xtype:'textfield',
                    allowBlank:false,
                    fieldLabel:'NAME',
                    name:'canonicalName',
                    id:'siteOverview-canonicalName',
                    fieldCls:'x-form-field site-name-dummy',
                    emptyText:'edifecscloud.com'
                },
                {
                    xtype:'textarea',
                    fieldLabel:'DESCRIPTION',
                    name:'description',
                    id:'siteOverview-description',
                    fieldCls:'x-form-field site-description-dummy',
                    emptyText:'Add Description'
                },
//                {
//                    xtype:'displayfield',
//                    fieldLabel:'ENVIRONMENT'
//                },
//                {
//                    xtype:'CustomButtonGroup',
//                    buttonItems:[
//                        {
//                            text:'Dev'
//                        },
//                        {
//                            text:'Test'
//                        },
//                        {
//                            text:'Production'
//                        }
//                    ]
//                },
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
                            action:'updateSite',
                            id:'siteOverview-updateSite',
                            formBind:true,
                            text:'Save',
                            cls:'update-site-dummy',
                            ui:'bluebutton',
//                            handler : function(btn){
//                            	var form = btn.up('form').getForm();
//                            	if (form.isValid()) {
//                            		var record= form.getRecord();
//                            		if(record){
//	                                    record.set(form.getValues());
//	                                    record.save();
//	                                    Functions.errorMsg("Site updated successfully",'Success',null,'INFO');
//                            		}
//                                }
//                            }
                        }]
                }
            ]
        }
    ],
    listeners : {
        'boxready' : function(){
            this.fireEvent('showDefaultSite',this.down('form'));
        }
    }

});
