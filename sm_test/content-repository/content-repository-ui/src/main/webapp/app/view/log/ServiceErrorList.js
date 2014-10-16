
// VIEW: Service Error List
// A Grid that displays a list of all Error Logs under the current tree level.
// ----------------------------------------------------------------------------

Ext.define('SM.view.log.ServiceErrorList', {
    extend: 'Ext.grid.Panel',
    alias : 'widget.serviceerrorlist',
    title : '<span>Service Errors</span>',
    iconCls: 'ico-error',
    
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
        flex: 1,
        renderer: function(value, metadata, record) {
            var source = record.getSource();
            if (!source) return "Unknown";
            return source.getType();
        }
    },  {
        header: 'Message',
        dataIndex: 'message',
        flex: 3
    }],

    initComponent: function() {
    	var data=this.errors;
        this.store = Ext.create("Ext.data.Store", {
        			 model : 'SM.model.Server',
        			 data:data
        });
        this.callParent(arguments);
    }
});

