Ext.define('DD.view.widgets.grid.ChartsConfigForm', {
    extend:'Ext.form.Panel',
    title: 'Grid Configuration',
    bodyPadding: 5,
    alias:'widget.gridconfigform',
    layout: 'anchor',
    border:false,
    defaults: {
        anchor: '100%'
    },

    defaultType: 'textfield',
    items: [
        {
            fieldLabel: 'Name',
            name: 'name',
            allowBlank:false
        },
        {
            fieldLabel: 'Description',
            name: 'description',
            xtype:'textarea',
            allowBlank:false
        },
        {
            fieldLabel: 'Columns',
            xtype:'combo',
            allowBlank:false
        }
    ]
});