Ext.Loader.setConfig({enabled:true});

Ext.application({
	requires: ['Ext.container.Viewport'],
	name : 'HelloExample',
	
	appFolder: 'app',
	
	controllers: ['HelloController'],
	
	launch : function() {
		var me = this;

	    Ext.create('Ext.container.Viewport', {
	        id : 'helloexample-root-viewport',
	        layout : 'fit',
	        border : false,
	        items : {
	        	xtype : 'helloview'
	        } 
	    });
	}
	
});