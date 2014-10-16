Ext.define('Security.view.organization.OrganizationOverview', {
    extend: 'Ext.container.Container',
    alias : 'widget.organizationoverview',
    layout:'fit',
    treeId:'overview',
	minHeight : 1000,
    config : {
        realmConfigObj :null
    },
    initComponent : function(){

        var me=this;
        this.items= [
            {
                xtype:'form',
				itemId:'organizationForm',
                bodyPadding:20,
                border:false,
                layout:'anchor',
                defaults:{labelAlign:'top',labelSeparator:'',anchor:'50%'},
                items:[
                    {
                        xtype:'component',
                        itemId:'redirectLink',
                        html:'<a href="#" id="tenantOverview-link" class="redirectURL quickLinks">BACK TO TENANT PAGE</a>',
                        listeners : {
                            'afterrender':function () {
                                this.getEl().on('click', function(e, t, opts) {
                                    e.stopEvent();
                                    var redirectPage = Functions.getUrlParameters('tenant','',true);
                                    if(redirectPage)
                                        location.href=redirectPage;
                                }, null, {delegate: '.redirectURL'});

                            }
                        }
                    },
                    {
                        xtype:'component',
                        html:'<h1>Organization Overview</h1>'
                    },
                    {
                        xtype:'textfield',
                        allowBlank:false,
                        fieldLabel:'NAME',
                        name:'canonicalName',
						regex: /^[A-Za-z0-9 _]*$/,
						regexText: '# check that the string contains *only* one or more alphanumeric chars or underscores',
						msgTarget: 'side',
                        id:'organizationOverview-canonicalName',
						enableKeyEvents : true,
                        emptyText:'edifecscloud.com',
						listeners: {
						keyup : function(field,e,eOpts)
						{
						    me.down('#organizationName').setValue(field.getValue());
						}
						}
                    },
                    {
                        xtype:'textfield',
                        hidden:true,
                        id:'organizationName',
						regex: /^[A-Za-z0-9 _]*$/,
						regexText: '# check that the string contains *only* one or more alphanumeric chars or underscores',
						msgTarget: 'side',
						name : 'organizationName'
                    },
                    {
                        xtype:'textarea',
                        fieldLabel:'DESCRIPTION',
                        name:'description',
                        id:'organizationOverview-description',
                        emptyText:'Add Description'
                    },
                    {
                        xtype:'container',
                        margins: '10 0 0 0',
                        flex:1,
                        itemId:'realmConfigHolder'
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
                                id:'organizationOverview-saveOrganization',
                                formBind:true,
                                text:'Save',
                                ui:'bluebutton',
                                handler : function(btn){
                                  /*  var parentCont = btn.up('organizationoverview');
                                    var form = btn.up('form').getForm();
                                    if (form.isValid()) {
                                        var record= form.getRecord();
                                        var orgObj = form.getValues();
                                        orgObj['id']=record.get('id');
                                        orgObj['tenant']=record.get('tenant');
                                        orgObj['securityRealms']=[];
                                        orgObj['securityRealms'].push(parentCont.getRealmConfigObj());


                                        OrganizationManager.updateOrganization(orgObj,function(updatedOrgObj){
										console.log(updatedOrgObj);
                                            var updatedOrganizationRecord = Ext.create('Security.model.Organizations',updatedOrgObj);
											console.log(updatedOrganizationRecord);                                           
										    form.loadRecord(updatedOrganizationRecord);
											console.log(updatedOrganizationRecord);
                                            btn.up('organizationconfiguration').setOrganizationRecord(updatedOrganizationRecord);
											console.log(updatedOrganizationRecord);
                                            Functions.errorMsg("Organization updated successfully",'Success',null,'INFO');
                                        },this);
                                    }*/
									var form = btn.up('#organizationForm').getForm();
                            	    if (form.isValid()) {
                            		var record = form.getRecord();
									var values = form.getValues();
									
									record.set('canonicalName',values.organizationName);
									record.set('description',values.description);

									OrganizationManager.updateOrganization(record.data,function(response){
									
											if(response==null)
											Functions.errorMsg('Failed to update organization.','Failure',null,'ERROR');
										else{
											if(response && response.error)
												Functions.errorMsg(response.error,'Failure',null,'ERROR');
											else
												Functions.errorMsg("Organization updated successfully",'Success',null,'INFO');
												}
									});
                                    
                                }
                                }
                            }]
                    }
                ]
            }
        ];

        this.callParent(arguments);

    },

    showOrganizationInfo : function(organizationRecord){
    	Security.loadingWindow = Ext.widget('progresswindow', {text: 'Loading Organization Overview...'});
		var me=this;
        this.down('form').loadRecord(organizationRecord);
        this.down('#organizationName').setValue(organizationRecord.get('canonicalName'));
		
        var realmConfigHolder=this.down('#realmConfigHolder');

        realmConfigHolder.removeAll();
        realmConfigHolder.add({
            xtype:'realmconfigform',
            title:'Authentication Provider Settings',
            organization:organizationRecord,
            securityRealms:organizationRecord.get('securityRealms')
        });
        realmConfigHolder.down('realmconfigform').updatedRealmConfiguration(organizationRecord.get('securityRealms')[0],false, function(){
        	
	        	if(organizationRecord.get('securityRealms')[0])
	                me.setRealmConfigObj(organizationRecord.get('securityRealms')[0].data);   			
	    			
	    			
	    		if( organizationRecord.data.canonicalName =='edfx')
	    		{
		    		Ext.each(me.down('form').getLayout().getLayoutItems(),function(item){
		    			if(item.name =='canonicalName')
		    				item.setDisabled(true);
		    		});
	    		}
	        	
	        	Security.removeLoadingWindow(function(){                	
	        	});
        	
        });
		
    },

    listeners : {
        'boxready' : function(){

            if(this.up('organizationconfiguration').getDisableLinking())
                this.down('#redirectLink').hide();


            var organizationRecord = this.up('organizationconfiguration').getOrganizationRecord();
            if(organizationRecord)
                this.showOrganizationInfo(organizationRecord);

        }
    }

});
