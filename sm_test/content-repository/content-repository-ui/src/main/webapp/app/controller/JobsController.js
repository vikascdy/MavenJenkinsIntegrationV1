// CONTROLLER: Jobs Controller
// Used to create,edit and manage jobs.
// ----------------------------------------------------------------------------

Ext.define('SM.controller.JobsController', {
    extend: 'Ext.app.Controller',

    stores: ['JobStatusStore','ActiveJobStore','SavedTriggerStore','JobStore'],
    models: ['JobStatus','ActiveJob','JobTrigger','JobType'],

    views: [
        'job.JobManagerPage',
        'job.JobTypeList',
        'job.CreateJobWindow',
        'job.JobList',
        'job.JobsStatusList',
        'job.ActiveJobList',
        'job.GeneralJobInfo',
        'job.JobTriggers',
        'job.SavedTriggersList',
        'job.NewTriggerWindow',
        'job.JobActions',
        'job.SavedActionsList',
        'job.StartProgramActionForm',
        'job.SendMailActionForm',
        'job.DisplayMessageActionForm',
        'job.NewActionWindow',
        'job.ScheduleTriggerForm',
        'job.LogOnTriggerForm',
        'job.JobProperties',
        'job.JobSettings'
        
    ],

    refs: [{
        ref: 'jobTypeList',
        selector: 'jobtypelist'
    }],
       
    init: function() {
        var me=this;
    	
        this.control({
            'jobtypelist':{
                select: this.showJobTypeInformation
            },
            'jobmanagerpage button': {
                click: this.jobManagerButtonClicked
            },
            'createjobwindow #createJob': {
                click: this.addJobToSelectedNode
            },
            'jobtriggers button': {
                click: this.manageJobTriggersButton
            },            
            'newtriggerwindow #ok':{
                click: this.addJobTrigger
            },
            'jobactions button': {
                click: this.manageJobActionButton
            }
        });
    },
    
    showJobTypeInformation: function(selModel,record,index){
        var selection=record.data.object;
        
        var grid=this.getJobTypeList();
        var window=grid.up('window');
        var jobConfigTabPanel=grid.up('window').down('tabpanel');
        var generalJobInfo=jobConfigTabPanel.down('generaljobinfo');
        jobConfigTabPanel.setDisabled(false);
        var form=generalJobInfo.getForm();
        
        var nameField=form.findField('name');
        var descriptionField=form.findField('description');
        
        nameField.setValue(ConfigManager.getNextAvailableIncrementedName(selection.get('name')));
        descriptionField.setValue(selection.get('description'));
    },

    addJobToSelectedNode: function(btn){
         var win = btn.up('createjobwindow');
         var node=win.node;
         var jobListStore=Ext.getStore('JobStore');
         if (node) {
             var selection = win.down('jobtypelist').getSelectionModel().getSelection();
             if (selection.length > 0) {
                 
                 var job=node.addJobFromType(selection[0].get('object'));
                 jobListStore.loadData(job);
                 //SM.reloadAll();
                 win.close();
             }
         }
    },

    jobManagerButtonClicked: function(btn) {
        switch (btn.getItemId()) {
            case 'create':
                new Ext.widget('createjobwindow');
                break;
        }
    },

    manageJobTriggersButton: function(btn) {
        switch (btn.getItemId()) {
            case 'new':
                new Ext.widget('newtriggerwindow');
                break;
            case 'edit':
               
                break;
            case 'delete':
            {
                var triggersList=btn.up('panel').down('gridpanel');
                var selection=triggersList.getSelectionModel().getSelection()[0];
                if(selection)
                    triggersList.getStore().remove(selection);
                break;
            }
        }
    },

    addJobTrigger: function(btn) {
        var window=btn.up('window');
        var combo=window.down('combo');
        var typeOfJob=combo.getValue();
        var form=window.down('form');
        var store=Ext.getStore('SavedTriggerStore');
        switch(typeOfJob) {
            case 'schedule':
                var settings=form.query('#settings')[0];
                var enabledStatus=form.query('#enabledStatus')[0];
                var date=form.query('datefield')[0];
                var time=form.query('timefield')[0];

                var record=Ext.create('SM.model.JobTrigger',{
                    name: settings.getChecked()[0].boxLabel,
                    description: 'At '+  time.getValue() +', on '+date.getValue(),
                    status: enabledStatus.getValue() ? 'Enabled' : 'Disabled'
                });
                store.add(record);
                break;
        }
        window.destroy();
    },

    manageJobActionButton: function(btn) {
        switch (btn.getItemId()) {
            case 'new':
                new Ext.widget('newactionwindow');
                break;
            case 'edit':
               
                break;
            case 'delete':
            {
                var actionList=btn.up('panel').down('gridpanel');
                var selection=actionList.getSelectionModel().getSelection()[0];
                if(selection)
                    actionList.getStore().remove(selection);
                break;
            }
        }
    }
});

