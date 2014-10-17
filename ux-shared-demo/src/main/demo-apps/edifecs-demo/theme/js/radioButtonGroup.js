Ext.onReady(function() {
	
Ext.create('Ext.form.Panel', {
    renderTo: document.getElementById('radioButtonGroup'),
        width:400,
        border: 0,
    items:[{
        xtype: 'radiogroup',
        fieldLabel: 'Label',
		        labelSeparator: '',
        labelWidth: 50,
        width:350,
        border: 0,
        items: [
            { boxLabel: 'Item 1', name: 'rb', inputValue: '1' },
            { boxLabel: 'Item 2', name: 'rb', inputValue: '2', checked: true },
            { boxLabel: 'Item 3', name: 'rb', inputValue: '3', disabled: true},
            { boxLabel: 'Item 4', name: 'rb', inputValue: '4', checked: true, disabled: true}
        ]
    }]
});
		   
});


