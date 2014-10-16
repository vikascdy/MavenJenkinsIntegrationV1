Ext.define('Security.view.organization.AddEditOrganization', {
	extend : 'Ext.window.Window',
	alias : 'widget.addeditorganization',
	requires : [ 'Ext.form.Panel', 'Ext.form.FieldSet' ],

	title : 'Create Organization',
	width : 400,
	height : 240,
	layout : 'fit',
	resizable : false,
	modal : true,
	initComponent : function() {

		var me = this;

		this.items = [ {
			xtype : 'addorganizationform',
			orgInfo : me.orgInfo
		} ];

		this.buttons = [
				{
					text : 'Cancel',
					width : 80,
					handler : function() {
						this.up('window').close();
					}
				},
				{
					text : me.mode == 'create' ? 'Save' : 'Update',
					ui : 'greenbutton',
					width : 80,
					action : me.mode == 'create' ? 'saveNewOrganization'
							: 'updateOrganization'
				} ];
		this.callParent(arguments);
	}

});
