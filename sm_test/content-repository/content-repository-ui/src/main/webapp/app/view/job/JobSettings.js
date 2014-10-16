Ext.define('SM.view.job.JobSettings', {
    extend: 'Ext.panel.Panel',
    alias : 'widget.jobsettings',
    border: false,
    bodyPadding: '5',
    height:250,
    initComponent: function(config) {
    	 
    	this.items= [{
    	        xtype:'component',
    	        html:'Specify additional settings that affect the behavior of the job.'
    	    }
    	    ];
    	

        this.callParent(arguments);
       
    }
});


