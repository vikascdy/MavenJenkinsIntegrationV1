Ext.define('SM.view.job.DisplayMessageActionForm', {
	extend : 'Ext.form.Panel',
	alias : 'widget.displaymessageactionform',
	flex : 1,

	bodyPadding : '5',
	layout : 'anchor',
	defaults : {
		anchor : '100%',
		flex : 1
	},
	fieldDefaults : {
		labelWidth : 100
	},

	initComponent : function() {

		this.items = [ {
			xtype : 'fieldset',
			title : 'Settings',
			height : 290,
			items : [ {
				xtype : 'component',
				html : 'This action displays a message box on the desktop.',
				margin:'0 0 10 0'
			}, {
				xtype : 'textfield',
				anchor : '100%',
				fieldLabel : 'Title'
			}, {
				xtype : 'textarea',
				anchor : '100%',
				fieldLabel : 'Message',
				height:190
			} ]
		} ];

		this.callParent(arguments);
	}
});
