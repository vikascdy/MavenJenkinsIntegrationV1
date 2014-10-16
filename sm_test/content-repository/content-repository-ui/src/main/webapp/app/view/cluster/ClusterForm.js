// VIEW: Cluster Form
// The form used when editing or creating a Cluster.
// ----------------------------------------------------------------------------

Ext.define('SM.view.cluster.ClusterForm', {
    extend : 'SM.view.abstract.FlexibleForm',
    alias  : 'widget.clusterform',
    cluster: null,
    border : false,
    trackResetOnLoad: true,
    padding:'0',
    bodyPadding: '0',
    margin:'0 0 15 0',
    columns: [
        [
            {
                xtype: 'textfield',
                name: 'name',
                fieldLabel: 'Name',
                labelSeparator :"",
                allowBlank: false,
                maskRe: /[^:]/,
                margin:'0 15 0 0'
            },
            {
                xtype: 'textfield',
                name: 'version',
                labelSeparator :"",
                fieldLabel: 'Version',
                allowBlank: false,
                maskRe: /[\d.]/,
                margin:'0 15 0 0'
            },
            {
                xtype: 'combobox',
                name: 'environment',
                labelSeparator :"",
                fieldLabel: 'Environment',
                allowBlank: false,
                editable: false,
                displayField: 'name',
                valueField: 'name',
                margin:'0 15 0 0',
                store: Ext.create('Ext.data.Store', {
                    fields: ['name'],
                    data: [
                        {
                            name: 'Production'
                        },
                        { name: 'Pre-production'
                        },
                        { name: 'Testing'
                        }
                    ]
                })
            }
        ],
        [
            {
                xtype: 'textarea',
                name: 'description',
                labelSeparator :"",
                fieldLabel: 'Description',
                margin:'0 15 0 0'
            }
        ]
    ],

    initComponent: function(config) {
        this.callParent(arguments);
        if (this.cluster) {
            this.loadRecord(this.cluster);
        }
    },

    save: function() {
        this.getForm().updateRecord(this.cluster);
        this.cluster.getConfig().set('version', this.getForm().getValues().version);
        this.cluster.getConfig().set('description', this.getForm().getValues().description);
        this.cluster.normalize();
        SM.reloadAll();
    },

    loadRecord: function(record) {
        this.callParent(arguments);
        if (record && record.getType && record.getType() == 'Cluster')
            this.getForm().setValues({
                version    : record.getConfig().get('version'),
                description: record.getConfig().get('description')});
    }
});


