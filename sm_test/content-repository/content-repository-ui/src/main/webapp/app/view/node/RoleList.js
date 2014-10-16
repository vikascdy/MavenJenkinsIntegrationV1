
// VIEW: Role List
// A Grid that lists all available Roles, and provides a text field for quick
// filtering.
// ----------------------------------------------------------------------------

Ext.define('SM.view.node.RoleList', {
    extend: 'SM.view.abstract.AdHocGrid',
    mixins: ['SM.mixin.GridFilterMixin'],
    alias : 'widget.rolelist',

    title : 'Roles',
    iconCls: 'ico-role',
    fields: [
        {name: 'name', type: 'string'},
        {name: 'role', type: 'auto'}
    ],

    columns: [{
        header: '&nbsp;',
        width: 32,
        dataIndex: 'role',
        renderer: function(role) {
            return Ext.String.format('<div class="icon {0}">&nbsp;</div>', role.getIconCls());
        }
    }, {
        header: 'Name',
        dataIndex: 'name',
        flex: 1
    }],

    getData: function() {
        return Ext.Array.map(ConfigManager.config.getRoles(), function(role) {
            return {name: role.get('name'), role: role};
        });
    },

    initComponent: function() {
        this.callParent(arguments);
    }
});

