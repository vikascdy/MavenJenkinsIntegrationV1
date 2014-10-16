
// VIEW: Properties Form
// An autogenerated form based on the list of properties for a Service or
// Resource. It creates fields, complete with validations, for each supported
// property.
// ----------------------------------------------------------------------------

Ext.define('SM.view.core.PropertiesForm', {
    extend: 'Ext.form.Panel',
    alias : 'widget.propertiesform',
    title : '<span>Properties</span>',
    iconCls: 'ico-properties',
    bodyPadding: '10',
    layout: 'anchor',
    defaults: {
        anchor: '100%',
        border:false,
        labelSeparator:'',
        labelWidth:200
    },
    object: null,
    autoSave: true,
    overflowY: 'auto',

    propertyToField: function(prop) {
        if (prop.get === undefined)
            prop = Ext.create('SM.model.Property', prop);
        var name  = prop.get('name'),
            value = prop.get('value'),
            helpText = prop.get('description') ? prop.get('description'): '';
        if (!prop.get('editable')/* || !UserManager.admin*/) // TODO: Determine whether user is admin using Security.
            return {
                xtype: 'displayfield',
                name: name,
                labelSeparator:'',
                fieldLabel: name,
                value: value,
                helpText: helpText
            };
        else 
            switch (prop.get('type').toLowerCase())
        {
        case 'string':
            var regex;
            if (prop.get('regex') !== null && prop.get('regex').length > 0)
                regex = new RegExp(prop.get('regex'));
            return {
                xtype: 'textfield',
                name: name,
                labelSeparator:'',
                fieldLabel: name,
                value: value,
                regex: regex,
                helpText: helpText
            };
        case 'boolean':
            return {
                xtype: 'checkboxfield',
                name: name,
                labelSeparator:'',
                fieldLabel: name,
                checked: value=="true",
                helpText: helpText
            };
        case 'date':
            return {
                xtype: 'datefield',
                name: name,
                labelSeparator:'',
                fieldLabel: name,
                value: value,
                format: 'm/d/Y',
                helpText: helpText
            };
        case 'list':
            return {
                xtype: 'combobox',
                name: name,
                fieldLabel: name,
                labelSeparator:'',
                value: value,
                displayField: 'value',
                valueField: 'value',
                editable: false,
                store: prop.createListValuesStore(),
                helpText: helpText
            };
        default:
            return {
                xtype: 'displayfield',
                name: name,
                labelSeparator:'',
                fieldLabel: name,
                value: 'ERROR - Invalid type: ' + prop.get('type'),
                helpText: helpText
            };
        }
    },

    shouldAutoSave: function() {
        return this.autoSave;
    },

    save: function() {
        var me = this;
        me.getForm().getFields().each(function(field) {
            if (field && field.isDirty() && field.isValid()) {
                try {
                    var value = field.getValue();
                    if (value instanceof Date) {
                        value = (value.getMonth()+1) + '/' + value.getDate() + '/' + value.getFullYear();
                    }
                    me.object.setProperty(field.getName(), value === null ? '' : value);
                    SM.reloadAll();
                } catch (err) {
                    Functions.errorMsg(err.message);
                }
            }
        });
    },

    initComponent: function() {
        if (this.object === null || this.object === undefined) {
            Log.warn("Created properties form without an assiociated object!");
            this.callParent(arguments);
            return;
        }

        // Set up the fields.
        if (!this.object) {
            this.items = [{
                xtype: 'displayfield',
                disabled: true,
                value: 'No object available.'
            }];
        } else if (!this.object.get('properties') ||
                   this.object.get('properties').length === 0) {
            this.items = [{
                xtype: 'displayfield',
                disabled: true,
                value: 'This ' + ((this.object.getType&&this.object.getType())||'object') + ' has no properties.'
            }];
        } else {
            var items = Ext.Array.map(this.object.get("properties"), this.propertyToField, this);
            items.sort(Functions.nameSorter);
            this.items = items;
        }

        this.callParent(arguments);
    }
});

