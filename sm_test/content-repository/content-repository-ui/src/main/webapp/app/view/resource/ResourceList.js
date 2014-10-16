
// VIEW: Resource List
// A Grid that lists all resources under a specific parent item.
// ----------------------------------------------------------------------------

Ext.define('SM.view.resource.ResourceList', {
    extend: 'SM.view.abstract.ConfigItemList',
    alias : 'widget.resourcelist',
    itemType : 'Resource',
    storeType: 'SM.store.ResourceStore',
    title    : '<span>Resources</span>',
    iconCls  : 'ico-resource',
    
    extraColumns: [{
        header: 'Type',
        dataIndex: 'restype',
        flex: 2
    }, {
        text: 'Status',
        dataIndex: 'status',
        renderer: Functions.capitalize
    }]
});

