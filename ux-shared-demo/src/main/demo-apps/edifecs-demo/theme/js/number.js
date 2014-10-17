Ext.onReady(function() {
	    Ext.createWidget('form', {
        renderTo: document.getElementById('number'),
        border:0,
        width:250,
        fieldDefaults: {
            labelAlign: 'left'
        },
        items: [{
                xtype: 'numberfield',
                fieldLabel: 'Label',
                name: 'basic',
		        labelAlign: 'top',
				fieldBodyCls: 'noRightCorner',
                value: 1,
                minValue: 1,
                maxValue: 125
            }]

    });
		   
});


