
// VIEW: Resource Dependency List
// A Grid that lists all resource dependencies for a Service, both fulfilled
// and missing, and allows the user to select a Resource to fulfill each
// dependency.
// ----------------------------------------------------------------------------

Ext.define('SM.view.service.ResourceDependencyList', {
    extend: 'SM.view.abstract.AdHocGrid',
    alias : 'widget.resourcedependencylist',
    service: null,

    fields: [
        {name: 'name',     type: 'string'},
        {name: 'restype',  type: 'string'},
        {name: 'resource', type: 'auto'},
        {name: 'invalid',  type: 'boolean'}
    ],
    sorters: ['name', 'restype'],
    title: '<span>Resource Dependencies</span>',
    iconCls: 'ico-resource',
    cls:'sm-item-header',
    overflowX: 'hidden',
    overflowY: 'auto',
    columns: [{
        text: '&nbsp;',
        width: 32,
        sortable: false,
        hideable: false,
        dataIndex: 'resource',
        renderer: function(value) {
            return Ext.String.format("<div class='icon {0}'>&nbsp;</div>",
                    value?value.getIconCls():'ico-error');
        }
    }, {
        text: 'Name',
        dataIndex: 'name',
        flex: 3
    }, {
        text: 'Type',
        dataIndex: 'restype',
        flex: 2
    }, {
        text: 'Resource',
        dataIndex: 'resource',
        flex: 3,
        renderer: function(value, metadata, record, rowIndex, colIndex, store, view) {
            var id = Ext.id();
            var service = view.up('resourcedependencylist').service;

            Functions.waitFor(
                function() {
                	return Ext.get(id);
            	},
                function() {
                    var btn = Ext.create('SM.view.core.ConfigItemPicker', {
                        renderTo: id,
                        includeNull: true,
//                        disabled: !UserManager.admin,
                        parentItem: service.getParent().getParent().getParent(), // Cluster
                        searchCriteria: {
                            type: 'Resource',
                            restype: record.get('restype')
                        },
                        listeners: {
                            itemselect: function(btn, item) {
                                try {
                                	service.setResourceTypeForDependency(record.get('name'), item);
                                } catch (err) {
                                	Functions.errorMsg(err.message);
                            	}
                                SM.reloadAll();
                            },
                            click: function(button) {
                                button.showMenu();
                            }
                        }
                    });
                    try         {btn.setItem(value);}
                    catch (err) {btn.setItem(null);}
                }
            );

            return Ext.String.format('<div id="{0}"></div>', id);
        }
    }],

    getData: function() {
        this.service.isServiceRequired();
        return this.service.getResourceDependencies();
    }
});

