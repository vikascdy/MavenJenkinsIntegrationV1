Ext.onReady(function() {
	Ext.create('Ext.form.FormPanel', {
		border: 0,
	    renderTo   : document.getElementById('textArea'),
	    items: [{
	        xtype     : 'textareafield',
			baseCls: 'x-box',
	        grow      : true,
		    labelSeparator: '',
	        name      : 'message',
		    labelAlign: 'top',
	        fieldLabel: 'Text Area',
	        width:250
	    }]
	});		   
});


