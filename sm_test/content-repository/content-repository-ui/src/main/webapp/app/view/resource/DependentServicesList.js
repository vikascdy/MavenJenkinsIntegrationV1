
// VIEW: Dependent Services List
// A Grid that lists all of the Services that depend on a Resource.
// ----------------------------------------------------------------------------

Ext.define('SM.view.resource.DependentServicesList', {
    extend: 'SM.view.abstract.AdHocGrid',
    alias : 'widget.dependentserviceslist',
    resource: null,

    fields: [
        {name: 'name',    type: 'string'},
        {name: 'depName', type: 'string'},
        {name: 'service', type: 'auto'}
    ],
    sorters: ['name', 'depName'],
    title: 'Dependendent Services',

    columns: [{
        text: '&nbsp;',
        width: 32,
        sortable: false,
        hideable: false,
        dataIndex: 'service',
        renderer: function(value) {
            return Ext.String.format('<div class="icon {0}">&nbsp;</div>', value.getIconCls());
        }
    }, {
        text: 'Name',
        dataIndex: 'name',
        flex: 1
    }, {
        text: 'Dependency',
        dataIndex: 'depName',
        flex: 1
    }],

    getData: function() {
        var cluster = this.resource.getParent();
        var services = cluster.getChildrenWith({type: 'Service'});
        var data = [];
        Ext.each(services, function(service) {
            var deps = service.getResourceDependencies();
            if (!deps) return true;
            Ext.each(deps, function(dep) {
                if (dep.resource == this.resource) {
                    data.push({
                        name: service.get('name'),
                        depName: dep.name,
                        service: service
                    });
                }
            }, this);
        }, this);
        return data;
    }
});

