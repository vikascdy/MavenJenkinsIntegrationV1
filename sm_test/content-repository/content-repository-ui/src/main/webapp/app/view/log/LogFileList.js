// VIEW: Log File List
// A Grid that displays a list of all Log Files available for a given Service.
// ----------------------------------------------------------------------------

Ext.define('SM.view.log.LogFileList', {
    extend : 'Ext.grid.Panel',
    alias  : 'widget.logfilelist',
    service : null,
    title  : '<span>Log Files</span>',
    iconCls: 'ico-log',
    tools: [
        {
            type: 'refresh',
            handler: function(e, target, owner, tool) {
                owner.up('logfilelist').reload();
            }
        }
    ],

    columns: [
        {
            header: 'Name',
            dataIndex: 'name',
            flex: 1,
            renderer: function(value) {
                return "<a href='#' class='config-link'>" + value + "</a>";
            }
        },
        {
            xtype: 'datecolumn',
            header: 'Last Entry',
            dataIndex: 'lastEntry'
        },
        {
            header: 'Size (kB)',
            dataIndex: 'sizeInKb'
        }
    ],

    initComponent: function() {
        if (ConfigManager.usingDefaultConfig || this.service.get('status')!='offline') {
            this.store = Ext.create("SM.store.LogFileStore", {service: this.service});
          }
        if(this.service.get('status')!='offline')
            this.reload();
        	
        this.callParent(arguments);
        
    },

    reload: function() {
        this.store.load();
    },
    listeners : {
    	
        render : function(grid) {
            Log.debug("Loading logs...");
            var myMask = new Ext.LoadMask(grid, {msg:"Please wait..."});
            myMask.show();
//            grid.store = Ext.create("SM.store.LogFileStore", {service: this.service});
//            var store = grid.getStore();
//            store.load();
//            console.log(grid.store.getTotalCount());
//            grid.updateLayout();

//            Using below code if it need to pass service parameters
//            store.load.defer(100, store, [
//                {params:{service: this.service}}
//            ]);
            myMask.hide();
        },
        delay: 400
    }
});

