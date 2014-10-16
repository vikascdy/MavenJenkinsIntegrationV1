
// VIEW: Saved Config List
// A Grid that lists all saved config files that can be loaded from the backend
// server.
// ----------------------------------------------------------------------------

Ext.define('SM.view.core.SavedConfigList', {
    extend: 'Ext.grid.Panel',
    alias : 'widget.savedconfiglist',

    store : 'SavedConfigStore',
    title : 'Install Configurations',

    columns: [{
        text: 'Name',
        dataIndex: 'name',
        flex: 2,
        renderer: function(value) {
            return "<a href='#' class='config-link'>" + value + "</a>";
        }
    }, {
        text: 'Version',
        dataIndex: 'version',
        flex: 1
    }, {
        xtype: 'booleancolumn',
        text: 'Status',
        trueText: 'Active',
        falseText: 'Draft',
        dataIndex: 'active',
        flex: 1
    }, {
        text: 'Description',
        dataIndex: 'description',
        flex: 4
    }, {
        xtype: 'datecolumn',
        text: 'Last Modified',
        dataIndex: 'lastModified'
    }],

    initComponent: function(config) {
        this.callParent(config);
        this.store.load();
    }
});


