
// MODEL: Error Log
// Represents any kind of error that has occurred either in the UI's loaded
// config file or in a running Service or Node.
// ----------------------------------------------------------------------------

Ext.define('SM.model.ErrorLog', {
    extend: 'Ext.data.Model',

    fields: [
         {name: 'sourceId', type: 'string'},
         {name: 'type',     type: 'string'},
         {name: 'severity', type: 'string'},
         {name: 'message',  type: 'string'}
    ],
    
    getSource: function() {
        if (!this.source || this.source.getId() != this.get('sourceId'))
        	{
            this.source = ConfigManager.searchConfigById(this.get('sourceId'));
        	}
        
        if(this.get('type')=='Required'){
		        	var clusterInstance = Ext.create('SM.model.Cluster', {
		            name: ConfigManager.config.getChildren()[0].get('name')
		        	});
		        	return clusterInstance;
        }
        return this.source;
    },

    shouldShowFor: function(item) {
        var source = this.getSource();
        return source && source.hasAncestor(item);
    },

    getIconCls: function() {
        var iconTable = {
            warning: 'ico-warning',
            error:   'ico-error',
            fatal:   'ico-fatal'
        };
        return iconTable[this.get('severity')] || 'ico-unknown';
    }
});

