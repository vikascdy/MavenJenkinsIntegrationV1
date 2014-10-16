Ext.define('SM.view.job.SavedTriggersList', {
    extend: 'Ext.grid.Panel',
    alias : 'widget.savedtriggerslist',
    store : 'SavedTriggerStore',
    title : null,
    height:185,
    initComponent: function(){
    	
    
    this.columns= [
              {
        text: 'Trigger',
        dataIndex: 'name',
        flex: 1
    }, 
    {
        text: 'Details',
        dataIndex: 'description',
        flex: 2
    }, {
        text: 'Status',
        dataIndex: 'status',
        flex: 1
    }];    
   
    this.listeners = {
        'render' : function(){
//            this.getStore().load();
        }
    };
    
    this.callParent(arguments);
    this.getSelectionModel().on('selectionchange', this.onSelectChange, this);
    },
	    
    onSelectChange: function(selModel, selections){
	    	var record=selModel.getSelection()[0]; 
	    	var editButton= this.up('panel').down('#edit');
	    	var deleteButton= this.up('panel').down('#delete');
	    		editButton.setDisabled(selections.length === 0);
	    		deleteButton.setDisabled(selections.length === 0);			    	
	    }

});


