Ext.onReady(function() {
// The data store containing the list of states
var states = Ext.create('Ext.data.Store', {
    fields: ['abbr', 'name'],
    data : [
        {"abbr":"AL", "name":"Alabama"},
        {"abbr":"AK", "name":"Alaska"},
        {"abbr":"AZ", "name":"Arizona"}
        //...
    ]
});

// Create the combo box, attached to the states data store
Ext.create('Ext.form.ComboBox', {
    fieldLabel: 'Label',
    width:250,
    store: states,
	labelAlign: 'top',
    labelSeparator: '',
	fieldBodyCls: 'noRightCorner',
    queryMode: 'local',
    displayField: 'name',
    valueField: 'abbr',
    renderTo: document.getElementById('comboBox')
});
		   
});


