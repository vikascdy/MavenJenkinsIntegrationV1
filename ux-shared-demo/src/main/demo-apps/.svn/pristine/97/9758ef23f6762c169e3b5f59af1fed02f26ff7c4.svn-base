Ext.onReady(function() {
		Ext.create('Ext.Button', {
		    text: 'Show Dialog Window',
		    renderTo: document.getElementById('dialog'),
		    handler: function() {
									Ext.create('Ext.window.Window', {
															    title: 'Create Internal Role',
															    width: 400,
															    bodyPadding: 10,
															    padding:0,
															    items:[ {
															        xtype: 'textfield',
															        name: 'name',
															        fieldLabel: 'Title',
															        allowBlank: false,  // requires a non-empty value
															    	width: 280
															    }, {
															        xtype: 'textarea',
															        name: 'description',
															        fieldLabel: 'Description',
															        width: 280
															    }, {
															        xtype: 'checkbox',
															        name: 'checkbox',
                    												boxLabel  : 'Active',
															        fieldLabel: 'Status'
															    }],
															    buttons: [
																		  { text: 'Cancel' },
																		  { text: 'Save',  }
																		 ]
									}).show();
			    				}
		});	 
});	
		   



