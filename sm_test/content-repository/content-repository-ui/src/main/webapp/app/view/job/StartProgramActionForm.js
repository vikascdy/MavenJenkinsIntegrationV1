Ext.define('SM.view.job.StartProgramActionForm', {
	extend : 'Ext.form.Panel',
	alias : 'widget.startprogramactionform',
	flex : 1,
	bodyPadding : '5',
	layout : 'anchor',
	defaults : {
		anchor : '100%',
		flex : 1
	},
	fieldDefaults : {
		labelWidth : 140
	},

	initComponent : function() {

		this.items = [ {
			xtype : 'fieldset',
			title : 'Settings',
			height:290,
			items : [ {
				xtype : 'filefield',
				name : 'program',
				fieldLabel : 'Program/Script',
				labelAlign : 'top',
				msgTarget : 'side',
				allowBlank : false,
				anchor : '100%',
				buttonText : 'Browse'
			}, {
				xtype : 'textfield',
				anchor : '100%',
				fieldLabel : 'Add arguments (optional)'
			}, {
				xtype : 'textfield',
				anchor : '100%',
				fieldLabel : 'Start in (optional)'
			} ]
		} ];

		this.callParent(arguments);
	},
});
