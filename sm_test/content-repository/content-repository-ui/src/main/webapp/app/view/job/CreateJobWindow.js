Ext.define('SM.view.job.CreateJobWindow', {
	extend : 'Ext.window.Window',
	alias : 'widget.createjobwindow',

	title : 'Create Job',
	border : true,
	width : 600,
	height : 550,
	modal : true,
	padding : 10,
	resizable : false,
	draggable : false,
	autoShow : true,
	closeAction : 'destroy',
	initComponent : function() {
		var me = this;

		this.items = [ {
			xtype : 'jobtypelist',
			title : 'Available Jobs',
			hideHeaders : true,
			node : me.node,
			height : 150
		}, {
			xtype : 'tabpanel',
			disabled : true,
			margin : '10 0 0 0',
			items : [ {
				xtype : 'generaljobinfo',
				title : 'General',
				node : me.node
			}, {
				xtype : 'jobtriggers',
				title : 'Triggers',
				node : me.node
			}, {
				xtype : 'jobactions',
				title : 'Actions',
				node : me.node
			}, {
				xtype : 'jobproperties',
				title : 'Properties',
				node : me.node
			}, {
				xtype : 'jobsettings',
				title : 'Settings',
				node : me.node
			} ]
		} ];

		this.buttons = [ {
			text : 'Create',
			itemId : 'createJob'
		}, {
			text : 'Cancel',
			handler : function() {
//				SM.reloadAll();
				this.up('window').destroy();
			}
		} ];

		this.callParent(arguments);
	}
});
