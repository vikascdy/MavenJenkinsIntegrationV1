
// VIEW: Resource Form
// The form used when editing a Resource; it can only be used to change the
// name and description.
// ----------------------------------------------------------------------------

Ext.define('SM.view.resource.ResourceForm', {
    extend  : 'SM.view.abstract.FlexibleForm',
    alias   : 'widget.resourceform',
    resource: null,
    border  : false,
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
        fieldLabel: 'Description'
    }]],
    
    initComponent: function(config) {
        this.callParent(arguments);
        if (this.resource)
            this.loadRecord(this.resource);
    },

    save: function() {
        this.getForm().updateRecord(this.resource);
        this.resource.normalize();
        SM.reloadAllWithStatuses();
    }
});


