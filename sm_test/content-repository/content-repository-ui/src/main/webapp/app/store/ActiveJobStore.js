
Ext.require('SM.proxy.RemoteProxy');

Ext.define('SM.store.ActiveJobStore', {
    extend: 'Ext.data.Store',
    model: 'SM.model.ActiveJob',
    data : [
            {name: 'Data Archival', runTime: '08-01-2013 13:49:00',triggers:'At 13:49:00 every Tuesday of every week, starting 18-05-2012',location:'ServiceManager'},
            {name: 'Report Generation', runTime: '09-01-2013 02:49:00',triggers:'At 01:00:00 every Wedesday of every week, starting 18-05-2012',location:'ServiceManager'},
            {name: 'System Clean', runTime: '10-01-2013 13:49:00',triggers:'At 03:30:00 every Thursday of every week, starting 18-05-2012',location:'ServiceManager'}            
        ]
});


