Ext.define('SM.view.job.NewTriggerWindow', {
	extend : 'Ext.window.Window',
	alias : 'widget.newtriggerwindow',
	padding:10,
	title : 'New trigger',
	border : false,
	width : 500,
	height : 480,
	resizable : false,
	draggable : true,
	autoShow : true,
	modal : false,
	closeAction : 'destroy',
	layout : 'anchor',
	defaults : {
		anchor : '100%',
		flex : 1
	},

	initComponent : function() {
		this.items = [ {
				xtype : 'combo',
				itemId:'typeOfJob',
				fieldLabel : 'Begin the Job',
				displayField : 'display',
				valueField : 'value',
				value : 'schedule',
				store : Ext.create('Ext.data.Store', {
					fields : [ 'display', 'value' ],
					proxy : {
						type : 'memory',
						reader : 'json'
					},
					data : [ {
						display : 'On a schedule',
						value : 'schedule'
					}, {
						display : 'At log on',
						value : 'logOn'
					}, {
						display : 'At Startup',
						value : 'startup'
					}, {
						display : 'On connection to user session',
						value : 'userSession'
					}, {
						display : 'On connection to the server',
						value : 'server'
					} ]
				}),
				 listeners: {
		                change: function(cbox, value) {
		                    cbox.up('newtriggerwindow').showTimingForm(value);
		                }
		            }
		}, {
	        xtype : 'container',
	        itemId: 'triggerTimeCtr',
	        layout: 'fit',
	        margin:'10 0 0 0',
	        flex  : 1
	    }];

		this.buttons = [ {
			text : 'OK',
			itemId:'ok'
		}, {
			text : 'Cancel',
			handler : function() {
				this.up('window').destroy();
			}
		} ];

		
		this.callParent(arguments);
		this.showTimingForm('schedule');
	},
	showTimingForm: function(value) {
		var me=this;
        var propFormCtr = this.down('#triggerTimeCtr');
        propFormCtr.removeAll();
        
        propFormCtr.add({
            xtype: me.getTimingForm(value)
        });
    },
    getTimingForm: function(value)
    {
    	switch(value){
	    	case 'schedule' : return 'scheduletriggerform';
	    	case 'logOn' : return 'logontriggerform';
	    	case 'startup' : return Ext.create('Ext.Component',{ html:'No additional settings required.'});
	    	default : return 'component';
    	}
    }
});
