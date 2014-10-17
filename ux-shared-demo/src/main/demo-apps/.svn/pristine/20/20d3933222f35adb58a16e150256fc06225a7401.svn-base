Ext.onReady(function() {
Ext.define('Ext.ux.CustomTrigger', {
    extend: 'Ext.form.field.Trigger',
    alias: 'widget.customtrigger',
	fieldBodyCls: 'noRightCorner',

    // override onTriggerClick
    onTriggerClick: function() {
        Ext.Msg.alert('Action Performed', '');
    }
});

Ext.create('Ext.form.FormPanel', {
	border:0,
    renderTo: document.getElementById('searchFilter'),
    items:[{
        xtype: 'customtrigger',
        fieldLabel: '',
        cls: 'searchIcon',
        emptyText: 'Search',
    }]
});
		   
});


