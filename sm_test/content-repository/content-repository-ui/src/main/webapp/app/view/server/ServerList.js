
// VIEW: Server List
// A Grid that lists all Servers under a specific parent item.
// ----------------------------------------------------------------------------

Ext.define('SM.view.server.ServerList', {
    extend: 'SM.view.abstract.ConfigItemList',
    alias : 'widget.serverlist',
    itemType : 'Server',
    storeType: 'SM.store.ServerStore',
    title    : '<span>Servers</span>',
    iconCls  : 'ico-server',
    
    extraColumns: [{
        text: 'IP Address',
        dataIndex: 'ipAddress'
    }, {
        text: 'Host Name',
        dataIndex: 'hostName'
    }, {
        text: 'Status',
        dataIndex: 'status',
        renderer: Functions.capitalize
    }]
});

