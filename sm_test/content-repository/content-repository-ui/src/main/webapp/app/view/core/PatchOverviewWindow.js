
// VIEW: Patch Overview Window
// Displays a list of bundles that will be affected by a patch file, allowing
// the user to evaluate a patch file before applying it.
// ----------------------------------------------------------------------------

Ext.define("SM.view.core.PatchOverviewWindow", {
    extend: "Ext.window.Window",
    alias : "widget.patchoverviewwindow",

    title : "Patch Overview",
    layout: {
        type : 'vbox',
        align: 'stretch'
    },
    defaults: {
        margin: 16
    },

    width : 500,
    height: 350,
    modal : true,
    autoShow: true,

    patchData: null,
    patchId: null,

    items: [{
        xtype: 'component',
        html : '<p>' +
                   "These are the bundles that will be affected by this patch" +
                   " file. If you want to upgrade these bundles, click 'Apply" +
                   " Patch'." +
               '</p>',
        height: 32
    }, {
        xtype: 'adhocgrid',
        preventHeader: true,
        title: "Bundles to Upgrade",
        flex : 1,
        fields: [
            {name: 'name',       type: 'string'},
            {name: 'oldVersion', type: 'string'},
            {name: 'newVersion', type: 'string'}
        ],
        columns: [
            {dataIndex: 'name',       header: 'Name',        flex: 3},
            {dataIndex: 'oldVersion', header: 'Old Version', flex: 1},
            {dataIndex: 'newVersion', header: 'New Version', flex: 1}
        ],
        getData: function() {
            var win = this.up('window');
            if (!win) return [];
            if (win.patchData === null || win.patchData === undefined)
                Ext.Error.raise("A PatchOverviewWindow must have patch data to display.");
            return Ext.Array.map(win.patchData, function(str) {
                var parts = str.split(":");
                return {name: parts[0], oldVersion: parts[1], newVersion: parts[2]};
            });
        }
    }],

    buttons: [{
        text   : 'Apply Patch',
        itemId : 'apply',
        iconCls: 'mico-yes',
        handler: function(btn) {
            btn.up('window').applyPatch();
        }
    }, {
        text   : 'Cancel',
        itemId : 'cancel',
        iconCls: 'mico-cancel',
        handler: function(btn) {
            btn.up('window').close();
        }
    }],

    initComponent: function(config) {
        this.callParent(arguments);
        this.down('adhocgrid').reload();
    },

    applyPatch: function() {
        var win = this;
        var loadingWindow = Ext.widget('progresswindow', {text: 'Applying patch...'});
        Functions.jsonCommand("UI Service", "executePatch", {
            configName   : ConfigManager.config.get('name'),
            configVersion: ConfigManager.config.get('version'),
            defaultConfig: ConfigManager.usingDefaultConfig ? 'true' : undefined
        }, {
            success: function(response) {
                loadingWindow.destroy();
                Ext.Msg.alert("Patch Complete", "Successfully applied patch file.");
                ConfigManager.loadSavedConfig(response.name, response.version,
                    function() {SM.setPage(Ext.create('SM.view.core.ServiceManagerPage'));});
                win.destroy();
            },
            failure: function(response) {
                loadingWindow.destroy();
                Functions.errorMsg("Failed to apply patch file.");
                win.destroy();
            }
        });
    }
});

