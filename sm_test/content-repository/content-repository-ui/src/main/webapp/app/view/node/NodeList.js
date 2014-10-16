
// VIEW: Node List
// A Grid that lists all Nodes under a specific parent item.
// ----------------------------------------------------------------------------

Ext.define('SM.view.node.NodeList', {
    extend: 'SM.view.abstract.ConfigItemList',
    alias : 'widget.nodelist',
    itemType : 'Node',
    storeType: 'SM.store.NodeStore',
    title    : '<span>Nodes</span>',
    iconCls  : 'ico-node',
    
    extraColumns: [{
        header: 'Port',
        dataIndex: 'port'
    }, {
        text: 'Status',
        dataIndex: 'status',
        renderer: Functions.capitalize
    }]
});


