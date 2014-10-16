Ext.define('SM.view.job.JobProperties', {
    extend: 'Ext.panel.Panel',
    alias : 'widget.jobproperties',
    border: false,
    bodyPadding: '5',
    height:250,
    initComponent: function(config) {
    	 
    	this.items= [{
    	        xtype:'component',
    	        html:'Specify the value for properties of various job types.'
    	    }
    	    ];
    	

        this.callParent(arguments);
       
    }
});


