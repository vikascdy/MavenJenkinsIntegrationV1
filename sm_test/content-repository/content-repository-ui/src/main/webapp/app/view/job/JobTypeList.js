// VIEW: Job Type List
// A Grid that lists all available Job Types, provides a text field for
// quick filtering, and allows drag-and-drop addition and deletion  of
// Jobs.
// ----------------------------------------------------------------------------

Ext.define('SM.view.job.JobTypeList', {
	extend : 'SM.view.abstract.AdHocGrid',
	mixins : [ 'SM.mixin.GridFilterMixin' ],
	alias : 'widget.jobtypelist',

	title : 'Job Types',
	iconCls : 'ico-servicetype',

	fields : [ {
		name : 'object',
		type : 'auto'
	}, {
		name : 'name',
		type : 'string'
	}, {
		name : 'version',
		type : 'version'
	} ],

	columns : [
			{
				header : '&nbsp;',
				width : 32,
				renderer : function(value, metadata, record) {
					return Ext.String.format(
							'<div class="icon {0}">&nbsp;</div>', record.get(
									'object').getIconCls());
				}
			}, {
				header : 'Name',
				dataIndex : 'name',
				flex : 2
			}, {
				header : 'Version',
				dataIndex : 'version',
				flex : 1
			} ],

	getData : function() {
		
		var record1=Ext.create('SM.model.JobType',{
 			     'name':'Data Reporting',
		         'version':'1.0',
		         'className':'DataReporting',
		         'description':'User to generate reports.',
		         'maxInstances':10,
		         'required':false,
		         'unmovable':false,
		         'properties':[],
		         'serviceDependencies':[],
		         'resourceDependencies':[]
		});
		
		var record2=Ext.create('SM.model.JobType',{
		     'name':'Data Archival',
	         'version':'2.0',
	         'className':'DataArchival',
	         'description':'User to archive data.',
	         'maxInstances':10,
	         'required':false,
	         'unmovable':false,
	         'properties':[],
	         'serviceDependencies':[],
	         'resourceDependencies':[]
		});
		
		var record3=Ext.create('SM.model.JobType',{
		     'name':'System Cleanup',
	         'version':'1.0',
	         'className':'systemCleanup',
	         'description':'User to clean system.',
	         'maxInstances':10,
	         'required':false,
	         'unmovable':false,
	         'properties':[],
	         'serviceDependencies':[],
	         'resourceDependencies':[]
		});
		
		var record4=Ext.create('SM.model.JobType',{
		     'name':'System Backup',
	         'version':'1.0',
	         'className':'systemBackup',
	         'description':'User to back up system.',
	         'maxInstances':10,
	         'required':false,
	         'unmovable':false,
	         'properties':[],
	         'serviceDependencies':[],
	         'resourceDependencies':[]
		});
		
		return Ext.Array.map([record1,record2,record3,record4], function(type) {
			return {
				object : type,
				name : type.get('name'),
				version : type.get('version')
			};
		});
	}
});
