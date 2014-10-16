
Ext.define('SM.view.job.JobsStatusList', {
    extend: 'Ext.grid.Panel',
    alias : 'widget.jobstatuslist',

    store : 'JobStatusStore',
    title : 'Job Status',
    columns: [
              {
        text: 'Job Name',
        dataIndex: 'name',
        flex: 2
    }, 
    {
        text: 'Run Result',
        dataIndex: 'result',
        flex: 1
    }, {
        text: 'Run Start',
        dataIndex: 'runStart',
        flex: 1
    }, {
        text: 'Run End',
        dataIndex: 'runEnd',
        flex: 1
    }, {
        text: 'Triggered By',
        dataIndex: 'triggeredBy',
        flex: 1
    }],
    
    initComponent : function(){
    	
    this.features = [{
        ftype: 'groupingsummary',
        groupHeaderTpl:  [
                          '{name} (Last run was {[values.rows[0].data.result]}, at {[values.rows[0].data.runEnd]} )'
                      ],
        startCollapsed:true,
        showSummaryRow:false
 	  }];
     
    var store=Ext.getStore('JobStatusStore');
    var data=store.data;
    var running=0,stopped=0,success=0,failure=0;
    
    Ext.each(data.items,function(job){
    	switch(job.get('result')){
    		case 'success':success++;break;
    		case 'failure':failure++;break;
    		case 'running':running++;break;
    		case 'stopped':stopped++;break;
    	}
    });
    
    this.tbar = [
           {
        	   xtype: 'component',
        	   padding:'5',
        	   html:'<div>Status of Jobs :</div><br/>'
        		   	+'Summary : <b>'+store.data.length+'</b> total - <b>'
        		   	+ running+'</b> running, <b>'
        		   	+ success+'</b> succeeded, <b>'
        		   	+ stopped+'</b> stopped, <b>'
        		   	+ failure+'</b> failed.',
    		   	styleHtmlContent:true,
        	   height:50 
        	}
       ];
    this.callParent(arguments);   

    },
    listeners : {
        'render' : function(){
//            this.getStore().load();
        }
    }
	


});


