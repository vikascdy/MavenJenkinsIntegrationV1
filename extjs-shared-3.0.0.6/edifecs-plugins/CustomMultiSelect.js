Ext.Loader.setPath({
    'Edifecs':'../edifecs-plugins'
});
Ext.define('Edifecs.CustomMultiSelect', {
    extend: 'Edifecs.MultiSelect',
    alias: 'widget.customMultiSelect',
    // user defined variables
    dataURL: '',
    itemTitle: '',
    border: false,
    itemValues: null,
    toggleGroup: '',
    initComponent: function () {
        var me = this;

        me.buttonGroup = [{
                text: 'All',
                itemId: 'allId',
                pressed: true,
                toggleGroup: me.toggleGroup,
                ui: 'edifecs-multiselect'
            }, {
                text: 'Custom',
                itemId: 'customId',
                toggleGroup: me.toggleGroup,
                ui: 'edifecs-multiselect'
            }
        ];

        me.callParent(arguments);

    },
    onDestroy: function () {
        this.removeAll(true);

        this.callParent(arguments);
    },
    getStore: function() {
        return this.dataStore;
    }
});