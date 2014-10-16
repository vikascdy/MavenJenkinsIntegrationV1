Ext.define('SM.view.job.JobManagerPage', {
	extend: 'Ext.container.Container',
    alias : 'widget.jobmanagerpage',
    layout: 'border',
    
    items : [
				{
					   xtype: 'container',
	                   border: false,
	                   region: 'north',
	                   layout: {
	                       type: 'vbox',
	                       align: 'stretch'
	                   },
					    items: [
					        {
					            xtype : 'container',
					            height: 75,
					            layout: {
					                type : 'hbox',
					                align: 'stretch'
					            },
					            items: [
					                {
					             	   xtype:'component',
					                    padding:'30 0 0 20',
					                    html  : "<span id='header-config-name'>Job Scheduler</span>",
					                    border: false
					                }
					            ]
					        },
					        {
					            xtype: 'toolbar',
					            margin:'0 20 0 20',
					            border:true,
					            items:[{
					                xtype : 'button',
					                text  : 'Create Job',
					                disabled:true,
					                iconCls: 'mico-new',
					                itemId: 'create'
					            }]
					        }
					    ]
				},
				{
            	 xtype:'panel',
            	 region:'center',
            	 border:true,
            	 margin:'0 20 0 20',
            	 layout: {
            	        type: 'accordion',
            	        titleCollapse: false,
            	        animate: true,
            	        activeOnTop: true,
            	        multi:true
            	    },
            	 items:[
    		             {
					        xtype: 'jobstatuslist',
					        flex:1
					    },{
					        xtype: 'activejoblist',
					        flex:1

					    }
					    ]
				},
	              {
	                   xtype: 'component',
	                   border: false,
	                   region: 'south',
	                   id:'jobManagerFooter',
	                   height:40,
	                   padding:'14 20 0 0',
	                   cls: 'generic-page-footer',
	                   html: '<p>Copyright &copy; 2013, Edifecs Inc</p>'
	               }
          ],

    initComponent: function() {
        this.callParent();       
    }
});

