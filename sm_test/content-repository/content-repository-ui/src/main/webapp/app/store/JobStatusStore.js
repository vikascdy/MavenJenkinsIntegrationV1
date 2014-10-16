
Ext.require('SM.proxy.RemoteProxy');

Ext.define('SM.store.JobStatusStore', {
    extend: 'Ext.data.Store',
    model: 'SM.model.JobStatus',
    groupField: 'jobType',
    autoLoad:false,
    sorters: [{
        property : 'runEnd',
        direction: 'DESC'
    }],
    data : [
            {name: 'Proxy',jobType: 'Proxy', result: 'success',runStart:'08-01-2013 13:49:00',runEnd:'08-01-2013 13:49:00',triggeredBy:'Server request'},
            {name: 'Proxy',jobType: 'Proxy', result: 'success',runStart:'10-01-2013 13:49:00',runEnd:'10-01-2013 13:49:00',triggeredBy:'Server request'},
            {name: 'Queue Reporting',jobType: 'Queue Reporting', result: 'failure',runStart:'12-01-2013 13:49:00',runEnd:'12-01-2013 13:49:00',triggeredBy:'User log on'},
            {name: 'Queue Reporting',jobType: 'Queue Reporting', result: 'success',runStart:'09-01-2013 13:49:00',runEnd:'09-01-2013 13:49:00',triggeredBy:'User log on'},
            {name: 'Queue Reporting',jobType: 'Queue Reporting', result: 'success',runStart:'10-01-2013 13:49:00',runEnd:'10-01-2013 13:49:00',triggeredBy:'User log on'},
            {name: 'User Task',jobType: 'User Task', result: 'success',runStart:'08-01-2013 13:49:00',runEnd:'08-01-2013 13:49:00',triggeredBy:'User log on'},
            {name: 'Server Task',jobType: 'Server Task', result: 'success',runStart:'02-01-2013 13:49:00',runEnd:'02-01-2013 13:49:00',triggeredBy:'Server startup'},
            {name: 'Server Task',jobType: 'Server Task', result: 'running',runStart:'04-01-2013 13:49:00',runEnd:'04-01-2013 13:49:00',triggeredBy:'Server startup'}
        ]
});


