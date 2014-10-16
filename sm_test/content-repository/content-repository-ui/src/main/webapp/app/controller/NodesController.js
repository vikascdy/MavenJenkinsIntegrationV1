// CONTROLLER: Nodes Controller
// Manages loading and displaying data related to Nodes.
// ----------------------------------------------------------------------------

Ext.define('SM.controller.NodesController', {
    extend: 'Ext.app.Controller',
    requires: ['Ext.window.MessageBox', 'SM.view.service.InstallServicesWindow'],

    stores: ['NodeStore'],
    models: ['Node', 'Role'],

    views: [
        'node.NodeList',
        'node.RoleList',
        'node.NodeForm',
        'node.NodeContextMenu',
        'node.NodePropertiesWindow',
        'node.NodeOverview',
        'node.NodeInfoPane',
        'node.JvmSettingsGrid',
        'node.JvmSystemInformation',
        'node.NewNodeWindow',
        'node.NewRoleWindow'
        
    ],

    init: function() {
        var controller = this;
        var contextMenu = function(handler) {
            return {click: function(mitem) {
                var node = mitem.up('nodecontextmenu').node;
                return handler(node, mitem);
            }};
        };
        this.control({
            'nodelist': {
                itemdblclick: Functions.showPropertiesWindow,
                itemcontextmenu: Functions.showContextMenu
            },
            'nodelist #newnode': {
                click: function(button) {
                    Ext.widget('newnodewindow', {
                        server: button.up('nodelist').parentItem
                    });
                }
            },
            'nodelist #deletenode': {
                click: function(button) {
                    var selection = button.up('nodelist').getSelectionModel().getSelection();
                    if (selection.length > 0) {
                        var node = selection[0];
                        node.askToDelete();
                    }
                }
            },
            /*'nodelist #startorstop': {
             click: function(button) {
             var selection = button.up('nodelist').getSelectionModel().getSelection();
             if (selection.length > 0) {
             var node = selection[0];
             controller.startOrStopNode(node);
             }
             }
             },*/
            'jvmsettingsgrid #add': {
                click: function(btn) {
                    var grid = btn.up('jvmsettingsgrid');
                    grid.getStore().add({arg: grid.defaultText});
                }
            },
            'jvmsettingsgrid #remove': {
                click: function(btn) {
                    var grid = btn.up('jvmsettingsgrid');
                    var selected = grid.getSelectionModel().getSelection();
                    Ext.each(selected, function(arg) {
                        grid.getStore().remove(arg);
                        grid.save();
                    });
                }
            },
            'nodecontextmenu > #tab0': contextMenu(function(node) {
                node.showPropertiesWindow(0);
            }),
            'nodecontextmenu > #tab1': contextMenu(function(node) {
                node.showPropertiesWindow(1);
            }),
            'nodecontextmenu > #tab2': contextMenu(function(node) {
                node.showPropertiesWindow(2);
            }),
            'nodecontextmenu > #tab3': contextMenu(function(node) {
                node.showPropertiesWindow(3);
            }),
            //'nodecontextmenu > #startorstop': contextMenu(this.startOrStopNode),
            'nodecontextmenu > #clone': contextMenu(function(node) {
                var server = node.parentItem;
                var jvmProperties=node.get('jvmProperties');
                var newNodeName = ConfigManager.getNextAvailableIncrementedName(node.get('name'));
                server.spawnNode(newNodeName, node.get('port'),node.get('logLevel'), node.get('description'),jvmProperties);
                var newNode = ConfigManager.searchConfigById(server.getId() + ':' + newNodeName);
                if (node.getChildren()) {
                    var service = node.getChildren();
                    Ext.each(service, function(service) {
                        newNode.addServiceFromType(service.getServiceType(), ConfigManager.getNextAvailableIncrementedName(service.get('name')));
                    });
                }
                SM.reloadAll();
            }),
            'nodecontextmenu > #delete': contextMenu(function(node) {
                node.askToDelete();
            }),
            'nodecontextmenu > #install': contextMenu(function(node) {
                Ext.widget('installserviceswindow', {node: node});
            }),
            'nodecontextmenu > #jobs': contextMenu(function(node) {
                Ext.widget('createjobwindow', {node: node});
            })
        });
    }/*,

     startOrStopNode: function(node) {
     var active = node.get('status') == 'active';
     Ext.Msg.confirm(
     'Confirm',
     'Are you sure you want to ' + (active?'stop':'start') + ' this Node?',
     function(btn) {
     if (btn == 'yes')
     (active ? node.stop : node.start).apply(node, [SM.reloadAll]);
     }
     );
     }*/
});

