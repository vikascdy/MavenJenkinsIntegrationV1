Ext.define('SM.view.job.LogOnTriggerForm', {
	extend : 'Ext.form.Panel',
	alias : 'widget.logontriggerform',
	flex : 1,
	bodyPadding : '5',
	layout : 'anchor',
	defaults : {
		anchor : '100%',
		flex : 1
	},


	initComponent : function() {
		
			this.items = [ {
				xtype : 'fieldset',
				title : 'Settings',
				items : [ {
					xtype : 'fieldcontainer',
					defaultType : 'radiofield',
					border : false,
					defaults : {
						flex : 1
					},
					items : [ {
						boxLabel : 'Any User',
						name : 'user',
						inputValue : 'anyUser',
						checked : true
					}, {
						boxLabel : 'Specific User',
						name : 'user',
						inputValue : 'specificUser'
					} ]
				}]
			}];

		this.callParent(arguments);
	}
});
