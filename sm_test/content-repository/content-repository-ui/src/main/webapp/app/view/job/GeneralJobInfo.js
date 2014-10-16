Ext.define('SM.view.job.GeneralJobInfo', {
    extend: 'Ext.form.Panel',
    alias : 'widget.generaljobinfo',
    border: false,
    bodyPadding: '5',
    layout:'anchor',
    defaults:{
    	anchor:'100% 100%'
    },

    defaultType: 'textfield',
    
    initComponent: function(){

    
    this.items= [{
        fieldLabel: 'Name',
        name: 'name',
        allowBlank: false
    },{
        fieldLabel: 'Location',
        name: 'location',
        value:'/'+this.node.get('name'),
        disabled:true,
        readOnly:true
    },{
    	xtype:'textarea',
        fieldLabel: 'Description',
        name: 'description',
        readOnly:true,
        height:85
    },
    {
    	xtype:'fieldset',
    	title:'Security Options',
    	items:[{
    			xtype      : 'fieldcontainer',
    			defaultType: 'radiofield',
    			defaults: {
    	                flex: 1
    	            },
    			items:[
    			       {
    	                    boxLabel  : 'Run only when user is logged on',
    	                    name      : 'runWhen',
    	                    inputValue: 'radio1',
    	                    id        : 'radio1',
    	                    checked	  : true
    	                }, {
    	                    boxLabel  : 'Run whether user is logger on or not',
    	                    name      : 'runWhen',
    	                    inputValue: 'radio2',
    	                    id        : 'radio2'
    	                }]
    		}]
    		
    }];

        this.callParent(arguments);
      }
});


