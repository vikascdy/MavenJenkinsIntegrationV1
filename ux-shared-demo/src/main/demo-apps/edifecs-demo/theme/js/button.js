Ext.onReady(function() {

	Ext.create('Ext.Button', {
	    text: 'Click me',
	    renderTo: document.getElementById('button'),
	    handler: function() {
	        alert('You clicked the button!')
	    }
	});
	Ext.create('Ext.Button', {
	    text: 'Disabled Button',
	    disabled: true,
	    margin:10,
	    renderTo: document.getElementById('button'),
	    handler: function() {
	        alert('You clicked the button!')
	    }
	});
});