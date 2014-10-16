// VIEW: Service List
// A Grid that lists all Services under a specific parent item.
// ----------------------------------------------------------------------------

Ext.define('SM.view.service.ServiceList', {
    extend: 'SM.view.abstract.ConfigItemList',
    alias : 'widget.servicelist',
    itemType : 'Service',
    storeType: 'SM.store.ServiceStore',
    title    : '<span>Services</span>',
    iconCls  : 'ico-service',

    extraColumns: [
        {
            header: 'Type',
            dataIndex: 'serviceName',
            flex: 2
        },
        {
            text: 'Status',
            dataIndex: 'status',
            renderer: Functions.capitalize
        }
    ],
    initComponent: function() {
        this.callParent();
        this.getSelectionModel().on('selectionchange', this.onSelectChange, this);
    },

    onSelectChange: function(selModel, selections, record) {
        var records = this.getSelectionModel().getSelection();
        var selected = null;
        if (records.length > 0)
            selected = records[0];
        if (this.down('#start') && this.down('#stop')) {
            if (!selected || selected.data.status == 'new') {
                this.down('#start').setDisabled(true);
                this.down('#stop').setDisabled(true);
            }
            else {
                this.down('#start').setDisabled(false);
                this.down('#stop').setDisabled(false);
            }
        }
    }
});

