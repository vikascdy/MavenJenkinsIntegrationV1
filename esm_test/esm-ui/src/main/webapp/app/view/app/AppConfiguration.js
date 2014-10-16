Ext.define('Security.view.app.AppConfiguration', {
	extend : 'Ext.form.Panel',
	alias : 'widget.appconfiguration',
	bodyPadding : 10,
	autoScroll : true,
	border : false,
	layout : 'anchor',
	defaults : {
		labelSeparator : '',
		anchor : '100%'
	},
	initComponent : function() {
		var me = this;
		this.items = [ {
			xtype : 'checkbox',
			fieldLabel : 'HIE Portal'
		}, {
			xtype : 'checkbox',
			fieldLabel : 'Duplicate Check'
		}, {
			xtype : 'container',
			layout : {
				type : 'hbox',
				align : 'stretch'
			},
			defaults : {
				margin : '0 10 0 0'
			},
			items : [ {
				xtype : 'fieldset',
				title : 'Learning Engine',
				defaults : {
					labelWidth : 150,
					labelSeparator : '',
					anchor : '100%'
				},
				items : [ {
					xtype : 'checkbox',
					fieldLabel : 'Enabled'
				}, {
					xtype : 'fieldset',
					title : 'Notifications',
					defaults : {
						labelWidth : 150,
						labelSeparator : '',
						anchor : '100%'
					},
					items : [ {
						xtype : 'checkbox',
						fieldLabel : 'Enabled'
					}, {
						xtype : 'combobox',
						margin : '0 0 10 20',
						fieldLabel : 'Clinical'
					}, {
						xtype : 'combobox',
						margin : '0 0 0 20',
						fieldLabel : 'Financial'
					} ]
				} ]
			}, {
				xtype : 'fieldset',
				title : 'Data Feed',
				defaults : {
					labelWidth : 150,
					labelSeparator : '',
					anchor : '100%'
				},
				items : [ {
					xtype : 'checkbox',
					fieldLabel : 'Clinical Data Validation',
					labelWidth : 150
				}, {
					xtype : 'fieldset',
					title : 'EMPI',
					defaults : {
						labelWidth : 150,
						labelSeparator : '',
						anchor : '100%'
					},
					items : [ {
						xtype : 'checkbox',
						fieldLabel : 'Enabled'
					}, {
						xtype : 'checkbox',
						fieldLabel : 'Custom EMPI Engine',
						labelWidth : 150
					}, {
						xtype : 'fieldset',
						title : 'Interface Detail',
						items : [ {
							xtype : 'textfield',
							fieldLabel : 'URL'
						}, {
							xtype : 'textfield',
							fieldLabel : 'Credentials'
						}, {
							xtype : 'textfield',
							fieldLabel : 'SLA Expectations'
						}, ]
					},

					]
				}, {
					xtype : 'checkbox',
					fieldLabel : 'Edifecs EMPI Engine',
					labelWidth : 150
				} ]
			}, {
				xtype : 'fieldset',
				defaults : {
					labelWidth : 150,
					labelSeparator : '',
					anchor : '100%'
				},
				title : 'Terminology',
				items : [ {
					xtype : 'checkbox',
					fieldLabel : 'Custom Terminology Engine',
					labelWidth : 150
				}, {
					xtype : 'fieldset',
					title : 'Interface Detail',
					items : [ {
						xtype : 'textfield',
						fieldLabel : 'URL'
					}, {
						xtype : 'textfield',
						fieldLabel : 'Credentials'
					}, {
						xtype : 'textfield',
						fieldLabel : 'SLA Expectations'
					}, ]
				}, {
					xtype : 'checkbox',
					fieldLabel : 'Edifecs Terminology Engine',
					labelWidth : 150
				} ]
			} ]
		} ];

		this.buttons = [ {
			text : 'Cancel',
			handler : function(btn) {
				this.up('window').close();
			}
		}, {
			text : 'Proceed',
			ui:'greenbutton',
			handler : function(btn) {
				
				var p = Ext.create('Ext.ProgressBar', {
					   width: 300
					});

				var SearchAppsListStore = Ext.StoreManager.lookup('SearchAppsListStore');
				var index = SearchAppsListStore.find('id',btn.up('form').appId);
				
				
				
				if(index!=-1){
					
					
					
					var appRecord = SearchAppsListStore.getAt(index);
					var TenantAppsListStore = Ext.StoreManager.lookup('TenantAppsListStore');
					TenantAppsListStore.add(appRecord);
			
					btn.up('window').close();
					var progressWindow = Ext.widget({
								xtype:'window',
								draggable:false,
								resizable:false,
								modal:true,
								autoShow:true,
								closable:false,
								height:25,
								title:null,
								layout:'fit',
								items:p						
							});
					
					p.wait({
					    interval: 500, //bar will move fast!
					    duration: 5000,
					    increment: 15,
					    text: '<b>Installing "'+appRecord.get('name')+'" Application...</b>',
					    scope: this,
					    fn: function(){
					        p.updateText('Done!');
					        progressWindow.close();
					    }
					});
										
				}
			}
		} ];
		this.callParent(arguments);
	}
});
