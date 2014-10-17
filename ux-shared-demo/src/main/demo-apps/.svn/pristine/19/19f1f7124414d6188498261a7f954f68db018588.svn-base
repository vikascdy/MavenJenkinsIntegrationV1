Ext.onReady(function() {


        Ext.form.field.VTypes.emailText = 'This is an Edifecs error message.'
		Ext.create('Ext.form.Panel', {
			bodyStyle: {
			    border: 'none'
			},
        	width:400,
        fieldDefaults: {
            msgTarget: 'side'
        },
		    renderTo: document.getElementById('forms'),
		    items: [{
		        xtype: 'textfield',
		        name: 'name',
		        labelAlign: 'top',
		    	width:250,
		        fieldLabel: 'Label',
		        labelSeparator: '',
		        labelWidth: '100%'
		    },
		    {
		        xtype: 'textfield',
		        name: 'name',
		        labelAlign: 'top',
		        inputType: 'password',
		    	width:250,
		        fieldLabel: 'Passwords',
		        labelSeparator: '',
		        labelWidth: '100%'
		    },
		    {
		        xtype: 'textfield',
		        name: 'name',
		        labelAlign: 'top',
		    	width:250,
		        fieldLabel: 'Email Validation',
		        labelSeparator: '',
		        labelWidth: '100%',
            	vtype:'email'

		    }
		    
		    ]

		   }); 
		   

});