
// VIEW: Service Type List
// A Grid that lists all available Service Types, provides a text field for
// quick filtering, and allows drag-and-drop installing and uninstalling of
// Services.
// ----------------------------------------------------------------------------

Ext.define('SM.view.service.ServiceTypeList', {
    extend: 'SM.view.abstract.AdHocGrid',
    mixins: ['SM.mixin.GridFilterMixin'],
    alias : 'widget.servicetypelist',

    title : 'Service Types',
    iconCls: 'ico-servicetype',

    fields: [
        {name: 'object',  type: 'auto'},
        {name: 'name',    type: 'string'},
        {name: 'version', type: 'version'}
    ],

    columns: [{
        header: '&nbsp;',
        width: 32,
        renderer: function(value, metadata, record) {
            return Ext.String.format('<div class="icon {0}">&nbsp;</div>', record.get('object').getIconCls());
        }
    }, {
        header: 'Name',
        dataIndex: 'name',
        flex: 2
    }, {
        header: 'Version',
        dataIndex: 'version',
        flex: 1
    }],

    getData: function() {
        return Ext.Array.map(ConfigManager.config.getProduct().get('serviceTypes'), function(type) {
            return {
                object : type,
                name   : type.get('name'),
                version: type.get('version')
            };
        });
    }
});

