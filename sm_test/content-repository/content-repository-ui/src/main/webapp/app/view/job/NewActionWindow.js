Ext
		.define(
				'SM.view.job.NewActionWindow',
				{
					extend : 'Ext.window.Window',
					alias : 'widget.newactionwindow',
					padding : 10,
					title : 'New Action',
					border : false,
					width : 500,
					height : 480,
					resizable : false,
					draggable : true,
					autoShow : true,
					modal : false,
					closeAction : 'destroy',
					layout : 'anchor',
					defaults : {
						anchor : '100%',
						flex : 1
					},

					initComponent : function() {
						this.items = [
								{
									xtype : 'component',
									html : 'You must specify what action this job will perform.<br/><hr/>'
								},
								{
									xtype : 'combo',
									itemId : 'typeOfAction',
									fieldLabel : 'Action',
									displayField : 'display',
									valueField : 'value',
									value : 'startProgram',
									store : Ext.create('Ext.data.Store', {
										fields : [ 'display', 'value' ],
										proxy : {
											type : 'memory',
											reader : 'json'
										},
										data : [ {
											display : 'Start a program',
											value : 'startProgram'
										}, {
											display : 'Send an e-mail',
											value : 'sendEmail'
										}, {
											display : 'Display a message',
											value : 'displayMessage'
										} ]
									}),
									listeners : {
										change : function(cbox, value) {
											cbox.up('newactionwindow')
													.showActionSettings(value);
										}
									}
								}, {
									xtype : 'container',
									itemId : 'actionSettingCtr',
									layout : 'fit',
									margin : '10 0 0 0',
									flex : 1
								} ];

						this.buttons = [ {
							text : 'OK',
							itemId : 'ok',
							disabled : true
						}, {
							text : 'Cancel',
							handler : function() {
								this.up('window').destroy();
							}
						} ];

						this.callParent(arguments);
						this.showActionSettings('startProgram');
					},
					
					showActionSettings : function(value) {
						var me = this;
						var propFormCtr = this.down('#actionSettingCtr');
						propFormCtr.removeAll();

						propFormCtr.add({
							xtype : me.getActionSettings(value)
						});
					},
					getActionSettings : function(value) {
						
						switch (value) {
						case 'startProgram':
							return 'startprogramactionform';
						case 'sendEmail':
							return 'sendmailactionform';
						case 'displayMessage':
							return 'displaymessageactionform';
						default:
							return 'component';
						}
					}
				});
