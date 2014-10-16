
Ext.define('SM.view.job.ActiveJobList', {
    extend: 'Ext.grid.Panel',
    alias : 'widget.activejoblist',

    store : 'ActiveJobStore',
    title : 'Active Jobs',

    columns: [{
        text: 'Job Name',
        dataIndex: 'name',
        flex: 2,
        renderer: function(value) {
            return "<a class='config-link'>" + value + "</a>";
        }
    }, {
        text: 'Next Run Time',
        dataIndex: 'runTime',
        flex: 1
    }, {
        text: 'Triggers',
        dataIndex: 'triggers',
        flex: 1
    }, {
        text: 'Location',
        dataIndex: 'location',
        flex: 1
    }], 
    
    initComponent : function(){

        var store=Ext.getStore('ActiveJobStore');      
        
        this.tbar = [
               {
            	   xtype: 'component',
            	   padding:'5',
            	   html:'<div>Active Job are Jobs that are currently enabled and have not expired</div><br/>'
            		   	+'Summary : <b>'+store.data.length+'</b> total.',            		   
        		   styleHtmlContent:true,
            	   height:50 
            	}
           ];
        this.callParent(arguments);   

        },
});


