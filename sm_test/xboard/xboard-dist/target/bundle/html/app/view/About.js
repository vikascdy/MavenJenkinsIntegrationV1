Ext.define('Portal.view.About', {
	extend : 'Ext.container.Container',
	alias : 'widget.about',
	border : 0,
	margins:{top:0, right:15, bottom:15, left:15},
	title : 'User Profile',
	layout: 'border',
	
	items : [
		{
		    xtype:'container',
		    margin:'0 0 0 10',
		    region:'center',
		    layout:{
                type:'vbox'
            },
		    items:[
	            {
	               xtype : 'label',
	               text : 'About',
	               cls : 'header-page',
	               margins:{top:10, right:10, bottom:10, left:0}
	            },
				{
					id : 'version',
					xtype : 'displayfield',
					readOnly : true,
					name : 'version',
					fieldLabel : 'Version',
					value : null
				}, {
					id : 'buildNumber',
					xtype : 'displayfield',
					readOnly : true,
					name : 'buildNumber',
					fieldLabel : 'BuildNumber',
					value : null
				}
			]
		}
	],
	
	initComponent : function() {
		var me = this;

		Functions.setupCommand("version", {}, {
            success: function(response) {
            	var container = Ext.getCmp('version');
            	container.setValue(response);
            }
		});
		
		Functions.setupCommand("buildNumber", {}, {
            success: function(response) {
            	var container = Ext.getCmp('buildNumber');
            	container.setValue(response);
            }
		});
		
		this.callParent(arguments);
	}
});
