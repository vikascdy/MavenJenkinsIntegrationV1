Ext.onReady(function() {
	Ext.create('Ext.slider.Single', {
	    width: 214,
	    minValue: 0,
	    maxValue: 100,
	    margin: '40 0 0 0',
	    useTips: true,
	    renderTo: document.getElementById('slider1')
	});

	  	Ext.create('Ext.slider.Single', {
	    height: 107,
	    minValue: 0,
	    maxValue: 100,
	    margin: '0 0 0 30',
	    useTips: true,
	    vertical: true,
	    renderTo: document.getElementById('slider2')
	});

});


