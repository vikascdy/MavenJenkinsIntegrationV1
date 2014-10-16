Ext.define('HelloExample.view.HelloView', {
    extend:'Ext.grid.Panel',
    alias:'widget.helloview',
    
    layout: 'vbox',
    
    store: 'HelloStore',
    
    initComponent: function() {
    	this.columns = [
	        {header: '', dataIndex: 'message', flex: 1}
    	];
    	this.callParent(arguments);
    }
    
});