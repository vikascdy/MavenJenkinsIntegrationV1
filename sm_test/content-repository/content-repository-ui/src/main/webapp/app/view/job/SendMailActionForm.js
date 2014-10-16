Ext
		.define(
				'SM.view.job.SendMailActionForm',
				{
					extend : 'Ext.form.Panel',
					alias : 'widget.sendmailactionform',
					flex : 1,
					
					bodyPadding : '5',
					layout : 'anchor',
					defaults : {
						anchor : '100%',
						flex : 1
					},
					fieldDefaults:{
						labelWidth : 100
					},

					initComponent : function() {

						this.items = [ {
							xtype : 'fieldset',
							title : 'Settings',
							height:290,
							items : [
							{
								xtype:'textfield',
								anchor:'100%',
								fieldLabel:'From'
							},
							{
								xtype:'textfield',
								anchor:'100%',
								fieldLabel:'To'
							},
							{
								xtype:'textfield',
								anchor:'100%',
								fieldLabel:'Subject'
							},
							{
								xtype:'textarea',
								anchor:'100%',
								fieldLabel:'Text'
							},
							{
								xtype : 'filefield',
								name : 'attachement',
								fieldLabel : 'Attachment',								
								msgTarget : 'side',
								allowBlank : false,
								anchor:'100%',
								buttonText : 'Browse'
							},
							{
								xtype:'textarea',
								anchor:'100%',
								fieldLabel:'SMTP server'
							}
							]
						} ];

						this.callParent(arguments);
					}				});
