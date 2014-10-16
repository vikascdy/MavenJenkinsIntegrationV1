
// VIEW: Template List
// A Grid that lists all config file templates available.
// ----------------------------------------------------------------------------

Ext.define('SM.view.core.TemplateList', {
    extend: 'Ext.grid.Panel',
    alias : 'widget.templatelist',

    store : 'TemplateStore',
    title : 'Templates',
    preventHeader: true,
    hideHeaders: true,

    columns: [{
        text: 'Name',
        dataIndex: 'name',
        flex: 1
    }],

    initComponent: function(config) {
        this.callParent(config);
        this.store.load();
    }
});

