Ext.define('HelloExample.controller.HelloController', {
    extend: 'Ext.app.Controller',
    
    views : ['HelloView'],
    
    models: ['HelloModel'],
    
    stores : ['HelloStore'],
    
    init: function() {
    	this.control({
    		'viewport > panel': {
    			render: this.onPanelRendered
    		}
    	});
    },
    
    onPanelRendered: function() {
    	console.log('The panel was rendered');
    }
    
});