// VIEW: Config Form
// The form used when editing or creating a Config.
// ----------------------------------------------------------------------------

Ext.define('SM.view.core.ConfigForm', {
    extend: 'SM.view.abstract.FlexibleForm',
    alias : 'widget.configform',
    config: null,
    border: false,
    trackResetOnLoad: true,
    bodyPadding: '5',

    columns: [
        [
            {
                xtype:'fieldcontainer',
                layout:'hbox',
                fieldLabel:'Product Name',
                items:[
                    {
                        xtype: 'displayfield',
                        name: 'productName'
                    },
                    {
                        xtype: 'displayfield',
                        name: 'productVersion',
                        margin:'0 0 0 5',
                        renderer : function(value) {
                            return Ext.String.format("ver {0}", value);
                        }
                    }
                ]
            },
            {
                xtype: 'textfield',
                name: 'name',
                fieldLabel: 'Configuration Name',
                allowBlank: false,
                maskRe: /[^\/\\:*?\"<>|]/,
                enforceMaxLength:true,
                maxLength:50,
                maxLengthText:'Configuration name cannot be more than 50 characters'
            },
            {
                xtype: 'textfield',
                name: 'version',
                fieldLabel: 'Configuration Version',
                value: '1.0',
                allowBlank: false,
                maskRe: /[\d.]/
            }
        ],
        [
            {
                xtype: 'textarea',
                name: 'description',
                fieldLabel: 'Description'
            }
        ]
    ],

    initComponent: function(config) {
        this.callParent(arguments);
        if (this.config)
            this.loadRecord(this.config);
    },

    save: function() {
        this.getForm().updateRecord(this.config);

        if (this.config.get('clusters').length > 0) {
            var values = this.getForm().getValues();
            this.config.get('clusters')[0].set('name', values.name);
            this.config.get('clusters')[0].set('description', values.description);
        }
        this.config.normalize();
        SM.reloadAll();
    }
});


