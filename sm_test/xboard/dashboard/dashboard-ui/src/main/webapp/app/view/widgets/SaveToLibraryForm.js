Ext.define('DD.view.widgets.SaveToLibraryForm', {
    extend:'Ext.form.Panel',
    alias:'widget.savetolibraryform',
    bodyPadding: 5,
    layout: 'anchor',
    border:false,
    defaults: {
        anchor: '100%'
    },
    defaultType: 'textfield',
    initComponent : function() {
        var fields = [];
        Ext.each(WidgetManager.activeWidget.properties, function(prop) {
            if (prop.name != 'Configuration') {
                fields.push({
                    xtype:prop.name == 'Description' ? 'textarea' : 'textfield',
                    fieldLabel:prop.name,
                    name:prop.name,
                    allowBlank:!prop.isRequired
                });
            }
        });
        this.items = fields;
        this.callParent(arguments);
    }
});