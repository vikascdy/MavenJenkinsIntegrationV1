Ext.define('SM.view.job.JobTriggers', {
    extend: 'Ext.panel.Panel',
    alias : 'widget.jobtriggers',
    border: false,
    bodyPadding: '5',
    layout:'anchor',
    defaults:{
    	anchor:'100% 100%'
    },
    initComponent: function(config) {
    	 
    	this.items= [{
    	        xtype:'component',
    	        html:'When you create a job, you can specify the conditions that will trigger the job.'
    	    },{
    	       xtype:'savedtriggerslist',
    	       margin:'10 0 0 0'
    	       
    	    },
    	    {
    	        xtype:'container',
    	        margin:'10 0 0 0',
    	        layout:{
    	        		type:'hbox',
    	        		pack:'start',
    	        		align:'middle'
    	        },
    	        defaults:{
    	        	margin:'0 5 0 0'
    	        },
    	        items:[
    	        	    {xtype:'button',text:'New...',itemId:'new' },
    		            {xtype:'button',text:'Edit...' ,itemId:'edit',disabled:true},
    		            {xtype:'button',text:'Delete',itemId:'delete',disabled:true}
    		           ]
    	        }
    	    ];
    	

        this.callParent(arguments);
       
    }
});


