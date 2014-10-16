
// VIEW: Service Form
// The form used when editing a Service; it can only be used to change the name
// and description.
// ----------------------------------------------------------------------------

Ext.define('SM.view.service.ServiceForm', {
    extend : 'SM.view.abstract.FlexibleForm',
    alias  : 'widget.serviceform',
    service: null,
    border : false,
    trackResetOnLoad: true,
    padding:'0',
    bodyPadding: '0',
    margin:'0 0 15 0',
    columns: [[{
        xtype: 'textfield',
        name: 'name',
        fieldLabel: 'Name',
        allowBlank: false,
        maskRe: /[^:]/,
        margin:'0 15 0 0'
    }],
    [{
        xtype: 'textarea',
        name: 'description',
        fieldLabel: 'Description',
        padding:'0 15 0 0'
    }]],
    
    initComponent: function(config) {
        this.callParent(arguments);
        if (this.service)
            this.loadRecord(this.service);
    },

    save: function() {
        this.getForm().updateRecord(this.service);
        this.service.normalize();
        SM.reloadAllWithStatuses();
    }
});

