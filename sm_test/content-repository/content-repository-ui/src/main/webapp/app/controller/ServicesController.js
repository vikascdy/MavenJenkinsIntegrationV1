// CONTROLLER: Services Controller
// Manages loading and displaying data related to Services.
// ----------------------------------------------------------------------------

Ext.define('SM.controller.ServicesController', {
    extend: 'Ext.app.Controller',

    stores: ['ServiceStore'],
    models: ['Service', 'ServiceType', 'ResourceDependency'],

    views: [
        'service.ServiceList',
        'service.ServiceTypeList',
        'service.DependencyList',
        'service.ResourceDependencyList',
        'service.ServiceForm',
        'service.ServiceContextMenu',
        'service.ServiceInfoPane',
        'service.ServicePropertiesWindow',
        'service.InstallServicesWindow',
        'service.ServiceTypeErrorWindow'
    ],

    init: function() {
        var controller = this;
        var contextMenu = function(handler) {
            return {click: function(mitem) {
                var service = mitem.up('servicecontextmenu').service;
                return handler(service, mitem);
            }};
        };
        this.control({
            'servicelist': {
                itemdblclick: Functions.showPropertiesWindow,
                itemcontextmenu: Functions.showContextMenu

            },
            'servicelist configitempicker': {
                itemselect: function(button, item) {
                    button.up('servicelist').parentItem = item;
                    button.up('servicelist').reload();

                }
            },
            'servicelist #install': {
                click: function(button) {
                    Ext.widget('installserviceswindow', {
                        node: button.up('servicelist').parentItem
                    });
                }
            },
            'servicelist #uninstall': {
                click: function(button) {
                    var selection = button.up('servicelist').getSelectionModel().getSelection();
                    if (selection.length > 0) {
                        var service = selection[0];
                        service.askToDelete();
                    }
                }
            },
            'servicelist #startorstop': {
                click: function(button) {
                    var selection = button.up('servicelist').getSelectionModel().getSelection();
                    if (selection.length > 0) {
                        var service = selection[0];
                        controller.startOrStopService(service);
                    }
                }
            },
            'servicelist #start': {
                click: function(button) {
                    var selection = button.up('servicelist').getSelectionModel().getSelection();
                    if (selection.length > 0) {
                        var service = selection[0];
                        controller.startService(service);
                    }
                }
            },
            'servicelist #stop': {
                click: function(button) {
                    var selection = button.up('servicelist').getSelectionModel().getSelection();
                    if (selection.length > 0) {
                        var service = selection[0];
                        controller.stopService(service);
                    }
                }
            },
            'serviceinfopane #startService': {
                click: function(button) {
                        var service = button.up('infopaneheader').item;
                        controller.startService(service);
                        button.up('infopaneheader').updateStatus(service);
                }
            },                      
            'serviceinfopane #stopService': {
                click: function(button) {
                        var service = button.up('infopaneheader').item;
                        controller.stopService(service);
                        button.up('infopaneheader').updateStatus(service);
                }
            },          
            'servicetypelist': {
                render: this.initServiceTypeListDropTarget,
                itemcontextmenu: Functions.showContextMenu
            },
            //'servicetypelist #filter': {
            //    keyup: this.filterGrid
            //},
            'installserviceswindow servicelist': {
                render: this.initServiceListDropTarget
            },
            'installserviceswindow servicetypelist': {
                selectionchange: this.showServiceDescription
            },
         
            
            'installserviceswindow #add': {
                click: function(btn) {
                    var win = btn.up('installserviceswindow');
                    var serviceList=win.down('servicelist');
                    var node = serviceList.parentItem;
                    if (node) {
                        var selection = win.down('servicetypelist').getSelectionModel().getSelection();
                        if (selection.length > 0) {
                            // Drag-and-drop gets messed up if a selected row is removed.
                            // Deselect everything just to be safe.
                            serviceList.getSelectionModel().deselectAll();
                            node.addServiceFromType(selection[0].get('object'));
                            serviceList.getStore().load();
//                            SM.reloadAll();
                        }
                    }
                }
            },
            'installserviceswindow #remove': {
                click: function(btn) {
                    var win = btn.up('installserviceswindow');
                    var serviceList=win.down('servicelist');
                    var selection = serviceList.getSelectionModel().getSelection();
                    if (selection.length > 0) {
                        var service = selection[0];
                        // Drag-and-drop gets messed up if a selected row is removed.
                        // Deselect everything just to be safe.
                        serviceList.getSelectionModel().deselectAll();
                        service.getParent().removeChild(service);
                        serviceList.getStore().load();
//                        SM.reloadAll();
                    }
                }
            },
            'servicecontextmenu > #tab0': contextMenu(function(service) {
                service.showPropertiesWindow(0);
            }),
            'servicecontextmenu > #tab1': contextMenu(function(service) {
                service.showPropertiesWindow(1);
            }),
            'servicecontextmenu > #tab2': contextMenu(function(service) {
                service.showPropertiesWindow(2);
            }),
            'servicecontextmenu > #clone': contextMenu(function(service) {
                var node=service.parentItem;
                var newServiceName=ConfigManager.getNextAvailableIncrementedName(service.get('name'));
                node.addServiceFromType(service.getServiceType(),newServiceName);
                SM.reloadAll();
            }),
            'servicecontextmenu > #startorstop': contextMenu(this.startOrStopService),
            'servicecontextmenu > #delete': contextMenu(function(service) {
                service.askToDelete();
            })
        });
    },

   
    initServiceListDropTarget: function(list) {
        if (list.parentItem instanceof SM.model.Node) {
            Ext.create('Ext.dd.DropTarget', list.el, {
                notifyDrop: this.dropOnServiceList(list),
                ddGroup: 'install'
            });
        }
    },
    
    showServiceDescription: function(selected){
         var selection = selected.getSelection();
         if (selection.length > 0) {
             var descriptionPanel=Ext.getCmp('serviceTypeDescription');
             var description=selection[0].get('object').get('description');
             if (Ext.String.trim(description) === '') {
                 descriptionPanel.hide();
             } else {
                 descriptionPanel.show();
                 descriptionPanel.update(new Ext.XTemplate(
                     "<h3 class='service-type-description-heading'>Service Description</h3><br/>",
                     "<p>"+description+"</p>"));
             }
         }
    },

    initServiceTypeListDropTarget: function(list) {
        Ext.create('Ext.dd.DropTarget', list.el, {
            notifyDrop: function(dragsource, e, data) {
                if (data.alreadyDropped) return false;
                try {
                    e.stopEvent();
                    var service = data.records[0];
                    if (service.shouldBeA) {
                        service.shouldBeA('Service');
                        service.getParent().removeChild(service);
                        SM.reloadAll();
                    } else return false;
                } catch (err) {
                    Functions.errorMsg(err);
                }
                data.alreadyDropped = true;
            },
            ddGroup: 'install'
        });
    },

    dropOnServiceList: function(serviceList) {
        return function(dragsource, e, data) {
            if (data.alreadyDropped) return false;
            try {
                var node = serviceList.parentItem;
                node.shouldBeA('Node');
                var service = data.records[0];
                if (service.get && service.get('object')) // Catchall for TreeNodes,
                    service = service.get('object');      // ServiceTypeList entries, etc.
                if (service instanceof SM.model.Service) {
                    if (service.moveToNode(node)){
                        SM.reloadAll();
                    }
                } else if (service instanceof SM.model.ServiceType) {
                    if (node.addServiceFromType(service)){
                        serviceList.getStore().load();
                    }
                } else
                    Ext.Error.raise('Only a Service can be dropped on a Service List.');
            } catch (err) {
                Functions.errorMsg(err);
            }
            // e.stopEvent() doesn't seem to work for drops. Setting a flag is
            // the only way to keep drop events from "falling through" windows.
            data.alreadyDropped = true;
        };
    },

    startOrStopService: function(service) {
        var active = service.get('status') == 'active';
        Ext.Msg.confirm(
            (active ? 'Stop' : 'Start') + ' Service?',
            'Are you sure you want to ' + (active ? 'stop' : 'start') + " the Service '" + service.get('name') + "'?",
            function(btn) {
                if (btn == 'yes')
                    (active ? service.stop : service.start).apply(service, [SM.reloadAll()]);
            }
        );
    },

    startService: function(service) {
        Ext.Msg.confirm(
            'Start Service?',
            "Are you sure you want to start the Service '" + service.get('name') + "'?",
            function(btn) {
                if (btn == 'yes') service.start(SM.reloadAll());
            }
        );
    },

    stopService: function(service) {
        Ext.Msg.confirm(
            'Stop Service?',
            "Are you sure you want to stop the Service '" + service.get('name') + "'?",
            function(btn) {
                if (btn == 'yes') service.stop(SM.reloadAll());
            }
        );
    },

    filterGrid: function(field, e) {
        var grid = field.up('gridpanel');
        grid.getStore().clearFilter();
        if (field.getValue()) {
            grid.getStore().filterBy(function(record) {
                return record.get('name').toLowerCase().indexOf(field.getValue().toLowerCase()) != -1;
            });
        }
    }
});

