Ext.define('Portal.view.Samples', {
	extend : 'Ext.container.Container',
	alias : 'widget.samples',
	border : 0,
	margins:{top:0, right:15, bottom:15, left:15},
	title : 'Sample page',
	layout: 'border',
    eventBus:new Ext.util.Observable(),
	
	items : [
		{
		    xtype:'container',
		    margin:'0 0 0 10',
		    region:'center',
		    layout:{
                type:'hbox'
            },
		    items:[
	            {
	               xtype : 'label',
	               text : 'Samples',
	               cls : 'header-page',
	               margins:{top:10, right:10, bottom:10, left:0}
	            },
                {
                    xtype:'formfieldcomponent',
                    id:"myForm",
                    layout:'anchor',
                    defaults:{'anchor':'50%'},
                    margins:{top:10, right:10, bottom:10, left:0},
                    configUrl:'resources/json/TestFormFieldConfiguration.json',
                    entityId:1,
                    flex:1,
                   // enableRootHeader:false,st
                    saveFieldEvent: "saveFieldEvent",
                    saveFormEvent: "saveFormEvent",

                    listeners: {
                        customFormSaveEvent: function(val, old){
                            debugger;
                        },
                        customChangeEvent: function(comp, val, old, id){
                            //handle event
                        }
                    }


                },
                {
                   xtype:'button',
                   text: "Update Context",
                   handler: function(btn) {
                       var comp=this.up().up();
                       comp.eventBus.fireEvent("updatecontext", {val1:"uno", val2:2}, false);
                       console.log("fired ev");

                   }
                }
			]
		}
	],
	
	initComponent : function() {



		/*Functions.setupCommand("version", {}, {
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
		});*/

		this.callParent(arguments);

	},

    afterRender: function() {
        var me = this;
        var myForm=Ext.ComponentQuery.query("#myForm")[0];

        myForm.setEventBus(this.eventBus);


    }
});
