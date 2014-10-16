
// CONTROLLER: Resources Controller
// Manages loading and displaying data related to Resources.
// ----------------------------------------------------------------------------

Ext.define('SM.controller.ResourcesController', {
    extend: 'Ext.app.Controller',
    
    stores: ['ResourceStore', 'ResourceTypeStore'],
    models: ['Resource', 'ResourceType'],
    
    views: [
        'resource.ResourceList',
        'resource.DependentServicesList',
        'resource.ResourceForm',
        'resource.ResourceContextMenu',
        'resource.ResourceInfoPane',
        'resource.ResourcePropertiesWindow',
        'resource.NewResourceWindow'
    ],

    init: function() {
        var contextMenu = function(handler) {
            return {click: function(mitem) {
                var resource = mitem.up('resourcecontextmenu').resource;
                return handler(resource, mitem);
            }};
        };
        this.control({
            'resourcelist': {
                itemdblclick: Functions.showPropertiesWindow,
                itemcontextmenu: Functions.showContextMenu
            },
            'resourcelist #newresource': {
                click: function(button) {

                    Ext.widget('newresourcewindow', {
                        cluster: button.up('resourcelist').parentItem
                    });
                }
            },
            'resourcelist #deleteresource': {
                click: function(button) {
                    var selection = button.up('resourcelist').getSelectionModel().getSelection();
                    if (selection.length > 0) {
                        var resource = selection[0];
                        resource.askToDelete();
                    }
                }
            },
            'resourcecontextmenu > #tab0': contextMenu(function(resource) {
                resource.showPropertiesWindow(0);
            }),
            'resourcecontextmenu > #tab1': contextMenu(function(resource) {
                resource.showPropertiesWindow(1);
            }),
            'resourcecontextmenu > #delete': contextMenu(function(resource){resource.askToDelete();})
        });
    }
});

