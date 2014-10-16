Ext.define('SM.view.job.SavedActionsList', {
	extend : 'Ext.grid.Panel',
	alias : 'widget.savedactionslist',
	height:185,
	// store : 'jobStatusStore',
	title : null,
	initComponent : function() {

		this.columns = [ {
			text : 'Actions',
			dataIndex : 'action',
			flex : 1
		}, {
			text : 'Details',
			dataIndex : 'details',
			flex : 2
		} ];

		this.listeners = {
			'render' : function() {
				// this.getStore().load();
			}
		};

		this.callParent(arguments);
		this.getSelectionModel().on('selectionchange', this.onSelectChange,
				this);
	},

	onSelectChange : function(selModel, selections) {
		var record = selModel.getSelection()[0];
		var editButton = this.up('panel').down('#edit');
		var deleteButton = this.up('panel').down('#delete');
		editButton.setDisabled(selections.length === 0);
		deleteButton.setDisabled(selections.length === 0);
	}

});
