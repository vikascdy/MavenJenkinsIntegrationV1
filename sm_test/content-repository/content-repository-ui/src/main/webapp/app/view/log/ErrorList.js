
// VIEW: Error List
// A Grid that displays a list of all Error Logs under the current tree level.
// ----------------------------------------------------------------------------

Ext.define('SM.view.log.ErrorList', {
    extend: 'Ext.grid.Panel',
    alias : 'widget.errorlist',
    parentItem: null,
    title : '<span>Errors</span>',
    iconCls: 'ico-error',

    tools: [{
        type: 'refresh',
        tooltip:'Refresh',
        handler: function(e, target, owner, tool) {
            owner.up('errorlist').reload();
        }
    }],

    columns: [{
        header: 'Severity',
        dataIndex: 'severity',
        renderer: function(value, metadata, record) {
            return Ext.String.format("<div class='icon {0}' style='position: absolute;'>&nbsp;</div> <div style='padding-left: 20px;'>{1}</div>",
                record.getIconCls(), Functions.capitalize(value));
        }
    }, {
        header: 'Type',
        dataIndex: 'type',
        renderer: Functions.capitalize
    }, {
        header: 'Source',
        flex: 1,
        renderer: function(value, metadata, record) {
            var source = record.getSource();
            if (!source) return "Unknown";
            return Ext.String.format("<div class='icon {0}' style='position: absolute;'>&nbsp;</div> <div style='padding-left: 20px;'>{1}</div>",
                source.getIconCls(), source.get('name'));
        }
    }, {
        header: 'Source Type',
        renderer: function(value, metadata, record) {
            var source = record.getSource();
            if (!source) return "Unknown";
            return source.getType();
        }
    }, {
        header: 'Message',
        dataIndex: 'message',
        flex: 3
    }],

    getValidationErrors: function() {
    	var validationErrors=[];
    	if(this.showValidationErrors)
    	{
    			ConfigManager.getRequiredServiceTypes();
		    	validationErrors=ConfigManager.checkRequiredServiceTypes(false);
    	}
    	return validationErrors;
    },
    initComponent: function() {
    	

    	var store=Ext.create("SM.store.FilteredErrorStore", {parentItem: this.parentItem});
    	var validationErrorsList=this.getValidationErrors();

        store.removeAll();
        if(validationErrorsList){        	
        	store.load();
        	store.add(validationErrorsList);
	}
        this.store = store;
        this.callParent(arguments);
//        this.reload();
    },

    reload: function() {
    	var validationErrorsList=this.getValidationErrors();
		
        this.store.removeAll();
    	this.store.load();
    	
        if(validationErrorsList){        
        this.store.add(validationErrorsList);
        }
    }
});

