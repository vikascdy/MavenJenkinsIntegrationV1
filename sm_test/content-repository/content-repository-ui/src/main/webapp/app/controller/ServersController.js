
// CONTROLLER: Servers Controller
// Manages loading and displaying data related to Servers.
// ----------------------------------------------------------------------------

Ext.define('SM.controller.ServersController', {
    extend: 'Ext.app.Controller',
    
    stores: ['ServerStore', 'AvailableServerStore'],
    models: ['Server'],
    
    views: [
        'server.ServerList',
        'server.ServerForm',
        'server.ServerContextMenu',
        'server.ServerInfoPane',
        'server.ServerOverview',
        'server.SystemInformation',
        'server.ServerPropertiesWindow',
        'server.NewServerWindow',
        'server.AvailableServersWindow'
    ],

    init: function() {
        var contextMenu = function(handler) {
            return {click: function(mitem) {
                var server = mitem.up('servercontextmenu').server;
                return handler(server, mitem);
            }};
        };
        this.control({
            'serverlist': {
                itemdblclick: Functions.showPropertiesWindow,
                itemcontextmenu: Functions.showContextMenu

            },
            'serverlist #newserver': {
                click: function(button) {
                    Ext.widget('newserverwindow', {
                        cluster: button.up('serverlist').parentItem
                    });
                }
            },
            'serverlist #deleteserver': {
                click: function(button) {
                    var selection = button.up('serverlist').getSelectionModel().getSelection();
                    if (selection.length > 0) {
                        var server = selection[0];
                        server.askToDelete();
                    }
                }
            },
            'servercontextmenu > #tab0': contextMenu(function(server) {
                server.showPropertiesWindow(0);
            }),
            'servercontextmenu > #tab1': contextMenu(function(server) {
                server.showPropertiesWindow(1);
            }),
            'servercontextmenu > #tab2': contextMenu(function(server) {
                server.showPropertiesWindow(2);
            }),
            'servercontextmenu > #tab3': contextMenu(function(server) {
                server.showPropertiesWindow(3);
            }),
            'servercontextmenu > #clone': contextMenu(function(server){
                var cluster=server.parentItem;
                var newServerName=ConfigManager.getNextAvailableIncrementedName(server.get('name'));
                cluster.spawnServer(newServerName, server.get('hostName'), server.get('ipAddress'), server.get('messagePort'), server.get('description') );

                var newServer=ConfigManager.searchConfigById(cluster.getId() + ':' + newServerName);

                if(server.getChildren())
                {
                    var node=server.getChildren();

                    Ext.each(node,function(node){
                       var newNodeName=ConfigManager.getNextAvailableIncrementedName(node.get('name'));
                       var jvmProperties=node.get('jvmProperties');
                       newServer.spawnNode(newNodeName, node.get('port'), node.get('description'),jvmProperties);
                       var newNode=ConfigManager.searchConfigById(newServer.getId() + ':' + newNodeName);
                       if(node.getChildren())
                       {
                           var service=node.getChildren();
                           Ext.each(service,function(service){
                              newNode.addServiceFromType(service.getServiceType(),ConfigManager.getNextAvailableIncrementedName(service.get('name')));
                           })
                       }
                    });

                }
                 SM.reloadAll();
            }),
            'servercontextmenu > #delete': contextMenu(function(server){server.askToDelete();}),
            'servercontextmenu > #newrole': contextMenu(function(server) {
                Ext.widget('newrolewindow', {server: server});
            }),
            'servercontextmenu > #newnode': contextMenu(function(server) {
                Ext.widget('newnodewindow', {server: server});
            })
        });


    }
});

