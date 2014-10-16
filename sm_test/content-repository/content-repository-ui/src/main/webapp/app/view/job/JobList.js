// VIEW: Job List
// A Grid that lists all Jobs under a specific parent item.
// ----------------------------------------------------------------------------

Ext.define('SM.view.job.JobList', {
    extend: 'SM.view.abstract.ConfigItemList',
    alias : 'widget.joblist',
    itemType : 'Job',
    storeType: 'SM.store.JobStore',
    title    : '<span>Jobs</span>',
    iconCls  : 'ico-service',

    extraColumns: [
        {
            header: 'Job Type',
            dataIndex: 'jobName',
            flex: 2
        },
        {
            text: 'Status',
            dataIndex: 'status',
            renderer: Functions.capitalize
        }
    ],
    initComponent: function() {
    	
    	var record1=Ext.create('SM.model.Job',{
    		   'id':'1',
	           'name':'Data Archival1',
	           'description':'Archives data',
	           'jobName':'Data Archival',
	           'jobVersion':'2.0',
	           'status':'Enabled',
	           'reqServices':[],
	           'reqResources':[],
	           'properties':[]           
    	});
    	
    	var record2=Ext.create('SM.model.Job',{
 		   	   'id':'2',
	           'name':'Data Reporting1',
	           'description':'Generates report',
	           'jobName':'Data Reporting',
	           'jobVersion':'1.0',
	           'status':'Enabled',
	           'reqServices':[],
	           'reqResources':[],
	           'properties':[]           
 	});

         
        this.callParent();
        var store=this.getStore();
        store.add([record1,record2]);
        this.getSelectionModel().on('selectionchange', this.onSelectChange, this);
    },

    onSelectChange: function(selModel, selections, record) {
        var records = this.getSelectionModel().getSelection();
        var selected = null;
        if (records.length > 0)
            selected = records[0];
      
    }
});

