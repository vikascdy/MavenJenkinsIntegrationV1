Ext.define('SM.model.JobStatus', {
    extend: 'Ext.data.Model',
    fields: [
         {name: 'name',         type: 'string'},
         {name: 'jobType',     type: 'string'},
         {name: 'result',       type: 'string'},
         {name: 'runStart',  	type: 'string'},
         {name: 'runEnd',  		type: 'string'},
         {name: 'triggeredBy',  type: 'string'}
    ]
});

