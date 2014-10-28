Ext.onReady(function() {


Ext.create('Ext.form.Panel', {
    renderTo: document.getElementById('datePicker'),
    width: 100,
	bodyCls: 'noRightCorner',
    border:0,
    items: [ {
        xtype: 'datefield',
        anchor: '100%',
        name: 'to_date',
        value: new Date()  // defaults to today
    }]
});
		   
});


