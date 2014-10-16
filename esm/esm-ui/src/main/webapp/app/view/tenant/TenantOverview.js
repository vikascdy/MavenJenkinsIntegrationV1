Ext.define('Security.view.tenant.TenantOverview', {
    extend: 'Ext.container.Container',
    alias : 'widget.tenantoverview',
    layout:'fit',
    treeId:'overview',
    minHeight:750,
    autoScroll:true,
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
                    itemId:'redirectLink',
                    html:'<a href="#" id="siteOverview-link" class="redirectURL quickLinks">BACK TO SITE PAGE</a>',
                    listeners : {
                        'afterrender':function () {
                            this.getEl().on('click', function(e, t, opts) {
                                e.stopEvent();
	                                var redirectPage = Functions.getUrlParameters('site','',true);
	                                if(redirectPage)
	                                	location.href=redirectPage;
                            }, null, {delegate: '.redirectURL'});

                        }
                    }
                },
                {
                    xtype:'component',
                    html:'<h1>Tenant Overview</h1>'
                },
                {
                    xtype:'textfield',
                    allowBlank:false,
                    fieldLabel:'NAME',
                    name:'canonicalName',
					regex: /^[A-Za-z0-9 _]*$/,				
					regexText: '# check that the string contains *only* one or more alphanumeric chars or underscores',
					msgTarget: 'side',
                    id:'tenantOverview-canonicalName',
                    emptyText:'edifecscloud.com'
                },
                {
                    xtype:'textarea',
                    fieldLabel:'DESCRIPTION',
                    name:'description',
                    id:'tenantOverview-description',
					emptyText:'Add Description'
                },
                {
                    xtype:'textfield',
                    fieldLabel:'DOMAIN',
                    name:'domain',
                    id:'tenantOverview-domain',
                    emptyText:'edifecscloud.com'
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
                    xtype:'combobox',
                    margin:'5 0 0 0',
                    readOnly:true,
                    editable:false,
                    hidden:true,
                    forceSelection:true,
                    name:'Site',
                    id:'tenantOverview-Site',
                    itemId:'siteList',
                    fieldLabel: 'SITE',
                    store: 'SitesListStore',
                    queryMode: 'local',
                    displayField: 'canonicalName',
                    valueField: 'id',
                    listeners : {
                    	'render' : function(){
                    		this.getStore().load();
                    	}
                    }
                },
//                {
//                    xtype:'component',
//                    margin:'30 0 0 0',
//                    html:'<h3>Organization</h3>'
//                },
//                {
//                    xtype:'textfield',
//                    fieldLabel:'NAME'
//                },
//                {
//                    xtype:'combobox',
//                    itemId:'addressCombo',
//                    fieldLabel:'ADDRESS',
//                    emptyText:'Select Address Type',
//                    editable:false,
//                    store:['Branch','Home','Office'],
//                    listeners : {
//                        'change' : function(combo, value) {
//                            var homeAddress = Ext.get('homeAddress');
//                            homeAddress.slideIn('t', {
//                                easing: 'easeOut',
//                                duration: 1000
//                            });
//                        }
//                    }
//                },
//                {
//                    xtype:'form',
//                    cls:'customPanel',
//                    id:'homeAddress',
//                    layout:'hbox',
//                    bodyPadding: '15',
//                    defaults: {
//                        border:false,
//                        xtype: 'panel',
//                        flex: 1,
//                        layout: 'anchor'
//                    },
//                    listeners : {
//                        'boxready' : function() {
//                            this.hide();
//                        }
//                    },
//                    items: [
//                        {
//                            items: [
//                                {
//                                    xtype:'textfield',
//                                    emptyText: 'Street Name',
//                                    anchor: '-5'
//                                },
//                                {
//                                    xtype:'textfield',
//                                    emptyText: 'City',
//                                    anchor: '-5'
//                                },
//                                {
//                                    xtype:'textfield',
//                                    emptyText: 'State',
//                                    anchor: '-5'
//                                }
//                            ]
//                        },
//                        {
//                            items: [
//                                {
//                                    xtype:'textfield',
//                                    emptyText: 'Zip',
//                                    anchor: '100%'
//                                },
//                                {
//                                    xtype:'textfield',
//                                    emptyText: 'Country',
//                                    anchor: '100%'
//                                },
//                                {
//                                    xtype:'container',
//                                    layout:'hbox',
//                                    items:[
//                                        {
//                                            xtype:'tbspacer',
//                                            flex:1
//                                        },
//                                        {
//                                            xtype:'button',
//                                            text:'Cancel',
//                                            margin:'0 10 0 0',
//                                            ui:'graybutton',
//                                            handler : function() {
//                                                Ext.get('homeAddress').slideOut('t', {
//                                                    easing: 'easeOut',
//                                                    duration: 1000,
//                                                    remove: false,
//                                                    useDisplay: false
//                                                });
//                                                this.up('createsubsite').down('#addressCombo').setRawValue(null);
//                                            }
//                                        },
//                                        {
//                                            xtype:'button',
//                                            text: 'Add',
//                                            ui:'greenbutton',
//                                            handler : function() {
//                                                Ext.get('homeAddress').slideOut('t', {
//                                                    easing: 'easeOut',
//                                                    duration: 1000,
//                                                    remove: false,
//                                                    useDisplay: false
//                                                });
//                                                this.up('createsubsite').down('#addressCombo').setRawValue(null);
//                                            }
//                                        }
//                                    ]
//                                }
//                            ]
//                        }
//                    ]
//                },
//                {
//                    xtype:'addressdataview',
//                    margin:'10 0 0 0'
//                },
//                {
//                    xtype:'container',
//                    margin:'40 0 5 0',
//                    layout:'hbox',
//                    items:[
//                        {
//                            xtype:'component',
//                            html:'<h3>Admin</h3>'
//                        },
//                        {
//                            xtype:'tbspacer',
//                            flex:1
//                        },
//                        {
//                            xtype:'component',
//                            html:'<b><a href="#" class="addAdminDetails">+ Secondary Admin</a></b>',
//                            listeners : {
//                                'afterrender':function () {
//                                    this.getEl().on('click', function(e, t, opts) {
//                                        e.stopEvent();
//                                        var adminDetails = Ext.get('adminDetails');
//                                        adminDetails.slideIn('t', {
//                                            easing: 'easeOut',
//                                            duration: 1000
//                                        });
//                                    }, null, {delegate: '.addAdminDetails'});
//
//                                }
//                            }
//                        }
//                    ]
//                },
//                {
//                    xtype:'form',
//                    cls:'customPanel',
//                    id:'adminDetails',
//                    bodyPadding: '15',
//                    defaults: {
//                        border:false
//                    },
//                    listeners : {
//                        'boxready' : function() {
//                            this.hide();
//                        }
//                    },
//                    items: [
//                        {
//                            xtype:'textfield',
//                            width:200,
//                            emptyText: 'Name',
//                            anchor: '100%'
//                        },
//                        {
//                            xtype:'textfield',
//                            emptyText: 'Email',
//                            anchor: '100%'
//                        },
//                        {
//                            xtype:'textfield',
//                            emptyText: 'Username',
//                            anchor: '100%'
//                        },
//                        {
//                            xtype:'container',
//                            anchor: '100%',
//                            layout:'hbox',
//                            items:[
//                                {
//                                    xtype:'textfield',
//                                    emptyText: 'Password',
//                                    inputType:'password',
//                                    flex:1
//                                },
//                                {
//                                    xtype:'component',
//                                    margin:'5 0 0 10',
//                                    html:'<a href="#">Generate New</a>'
//                                }
//                            ]
//                        },
//                        {
//                            xtype:'container',
//                            margin:'10 0 0 0',
//                            layout:'hbox',
//                            items:[
//                                {
//                                    xtype:'tbspacer',
//                                    flex:1
//                                },
//                                {
//                                    xtype:'button',
//                                    text:'Cancel',
//                                    margin:'0 10 0 0',
//                                    ui:'graybutton',
//                                    handler : function() {
//                                        Ext.get('adminDetails').slideOut('t', {
//                                            easing: 'easeOut',
//                                            duration: 1000,
//                                            remove: false,
//                                            useDisplay: false
//                                        });
//                                    }
//                                },
//                                {
//                                    xtype:'button',
//                                    text:'Save',
//                                    ui:'greenbutton',
//                                    handler : function() {
//                                        Ext.get('adminDetails').slideOut('t', {
//                                            easing: 'easeOut',
//                                            duration: 1000,
//                                            remove: false,
//                                            useDisplay: false
//                                        });
//                                    }
//                                }
//                            ]
//                        }
//                    ]
//                },
//                {
//                    xtype:'admindetaildataview',
//                    margin:'20 0 0 0'
//                },
//                {
//                    xtype:'container',
//                    margin:'30 0 5 0',
//                    layout:'hbox',
//                    items:[
//                        {
//                            xtype:'component',
//                            html:'<h3>Apps</h3>'
//                        },
//                        {
//                            xtype:'tbspacer',
//                            flex:1
//                        }
////                        {
////                            xtype:'component',
////                            html:'<b><a href="#" class="browseAppCatalog">Browse App Catalog</a></b>',
////                            listeners : {
////                                'afterrender':function () {
////                                    this.getEl().on('click', function(e, t, opts) {
////                                        e.stopEvent();
////                                        var homePageContainer = Ext.getCmp('Home-page-container');
////                                        homePageContainer.removeAll();
////                                        homePageContainer.add(Ext.widget('manageapps'));
////                                    }, null, {delegate: '.browseAppCatalog'});
////
////                                }
////                            }
////                        }
//                    ]
//                },
//                {
//                    xtype:'component',
//                    margin:'5 0 10 0',
//                    html:'<div style="border-style:dashed none  none none; border-width: 1px; border-color: gray;"></div>'
//                },
//                {
//                    xtype:'tenantappslist',
//                    disableAppAdd:false
//                },
//                {
//                    xtype:'form',
//                    cls:'customPanel',
//                    frame:true,
//                    height:140,
//                    margin:'20 0 20 0',
//                    layout:{type:'vbox',align:'stretch'},
//                    bodyPadding:20,
//                    items:[
//                        {
//                            xtype:'component',
//                            html:"<div class='appFinder'>Browse the Catalog or search and quickly add that app to this tenant.</div>"
//                        },
//                        {
//                            xtype:'tenantappbrowse',
//                            margin:'20 0 0 0'                            
//                        },
//                        {
//                        	xtype:'container',
//                        	margin:'10 0 0 0', 
//                        	layout:{type:'hbox'},
//                        	items:[
//                        	       {
//                        	    	   xtype:'tbspacer',
//                        	    	   flex:1
//                    	    	   },
//                        	       {
//                        	    	   xtype:'button',
//                        	    	   ui:'greenbutton',
//                        	    	   text:'Install',
//                        	    	   listeners : {
//               							click : function(btn) {
//               								var browseTenantApps = btn.up('tenantoverview').down('tenantappbrowse');
//               								if(browseTenantApps.getValue()==null)
//               									Functions.errorMsg("Please search and select App first.", 'Warning', null, 'WARNING');
//               								else
//            								Ext.widget({
//            									xtype:'window',
//            									autoScroll:true,									
//            									autoShow:true,
//            									modal:true,
//            									resizable:false,
//            									draggable:false,									
//            									title:'Application Configuration',
//            									items:[{xtype:'appconfiguration',appId:browseTenantApps.getValue()}]
//            								});
//            							}
//            						}
//                	    		   }
//                    	       ]
//                        	}
//                       
//
//                    ]
//
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
                            formBind:true,
                            text:'Save',
                            id:'tenantOverview-saveTenant',
                            ui:'bluebutton',
                            handler : function(btn){
                            	var form = btn.up('form').getForm();
                            	if (form.isValid()) {
                            		var record= form.getRecord();
                                    record.set(form.getValues());
									TenantManager.updateTenant(record.data,function(response){
									
									if(response==null)
										Functions.errorMsg('Failed to update tenant.','Failure',null,'ERROR');
									else{
										if(response && response.error)
											Functions.errorMsg(response.error,'Failure',null,'ERROR');
										else
											Functions.errorMsg("Tenant updated successfully",'Success',null,'INFO');
											}
									});
                                }
                            }
                        }]
                }
            ]
        }
    ],    
    showTenantInfo : function(tenantRecord){
		var me=this;
		this.down('form').getForm().loadRecord(tenantRecord);
    	this.down('form').down('#siteList').setValue(tenantRecord.get('siteId'));
		
		if( tenantRecord.data.canonicalName =='_System')
		{
		Ext.each(me.down('form').getLayout().getLayoutItems(),function(item){
			if(item.name=='canonicalName')
				item.setDisabled(true);
		});
		}
    	
    },
    listeners : {
    	'boxready' : function(){
//    		this.fireEvent('showDefaultTenant',this.down('form'));
            if(this.up('tenantconfiguration').getDisableLinking())
                this.down('#redirectLink').hide();
    		
    		var tenantRecord = this.up('tenantconfiguration').getTenantRecord();
    		if(tenantRecord)
	    			this.showTenantInfo(tenantRecord);
    	}
    }

});
