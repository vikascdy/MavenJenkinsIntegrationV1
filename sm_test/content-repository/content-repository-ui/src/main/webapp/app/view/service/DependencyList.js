
// VIEW: Dependency List
// A Grid that lists all dependencies for a Service, and shows whether those
// dependencies are installed or missing.
// ----------------------------------------------------------------------------

Ext.define('SM.view.service.DependencyList', {
    extend: 'SM.view.abstract.AdHocGrid',
    alias : 'widget.dependencylist',
    service: null,
    fields: [
        {name: 'name',       type: 'string'},
        {name: 'typeName',   type: 'string'},
        {name: 'typeVersion',type: 'string'},
        {name: 'service',    type: 'auto'},
        {name: 'invalid',    type: 'boolean'}
    ],
    sorters: ['name', 'typeName'],
    title: '<span>Dependencies</span>',
    iconCls: 'ico-service',
    overflowX: 'hidden',
    overflowY: 'auto',
    columns: [{
        text: '&nbsp;',
        width: 32,
        sortable: false,
        hideable: false,
        dataIndex: 'service',
        renderer: function(value) {
            return Ext.String.format("<div class='icon {0}'>&nbsp;</div>",
                    value?value.getIconCls():'ico-error');
        }
    }, {
        text: 'Name',
        dataIndex: 'name',
        flex: 2
    }, {
        text: 'Type',
        dataIndex: 'typeName',
        flex: 2
    }, {
        text: 'Version',
        dataIndex: 'typeVersion',
        flex: 1
    }, {
        text: 'Service',
        dataIndex: 'service',
        flex: 4,
        renderer: function(value, metadata, record, rowIndex, colIndex, store, view) {
            var id = Ext.id();
            var service = view.up('dependencylist').service;

            Functions.waitFor(
                function() {return Ext.get(id);},
                function() {
                    var btn = Ext.create('SM.view.core.ConfigItemPicker', {
                        renderTo: id,
                        includeNull: true,
                        disabled: !UserManager.admin,
                        parentItem: service.getParent().getParent().getParent(), // Cluster
                        searchCriteria: {
                            type: 'Service',
                            serviceName: record.get('typeName'),
                            serviceVersion: record.get('typeVersion')
                        },
                        listeners: {
                            itemselect: function(btn, item) {
                                try         {service.setServiceForDependency(record.get('name'), item);}
                                catch (err) {Functions.errorMsg(err.message);}
                                SM.reloadAll();
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
        return this.service.getServiceDependencies();
    }
});

