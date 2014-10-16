// VIEW: Node Form
// The form used when editing or creating a Node.
// ----------------------------------------------------------------------------

Ext.define('SM.view.node.NodeForm', {
    extend: 'SM.view.abstract.FlexibleForm',
    alias : 'widget.nodeform',
    node  : null,
    border: false,
    trackResetOnLoad: true,

    padding:'0',
    bodyPadding: '0',
    margin:'0 0 15 0',

    columns: [
        [
            {
                xtype: 'textfield',
                name: 'name',
                labelSeparator :"",
                fieldLabel: 'Name',
                allowBlank: false,
                maskRe: /[^:]/,
                margin:'0 15 0 0'
            },/*
            {
                xtype: 'textfield',
                name: 'port',
                fieldLabel: 'Port',
                allowBlank: true,
                maskRe: /\d/,
                margin:'0 15 0 0'
            }
        ],
        [*/
            {
                xtype: 'textfield',
                name: 'sshPort',
                labelSeparator :"",
                fieldLabel: 'SSH Port',
                allowBlank: true,
                maskRe: /\d/,
                margin:'0 15 0 0'
            },
            {
                xtype: 'textfield',
                name: 'messagePort',
                labelSeparator :"",
                fieldLabel: 'Message Port',
                allowBlank: true,
                maskRe: /\d/,
                margin:'0 15 0 0'
            }
        ],
        [
         {
             xtype: 'combobox',
             name:'logLevel',
        	 labelSeparator :"",
        	 fieldLabel:'Log Level',
        	 margin:'0 15 0 0',
             displayField: 'display',
             valueField: 'value',
             value: 'WARNING',
             editable: false,
             allowBlank: false,
             store: Ext.create('Ext.data.Store', {
                 fields: ['display', 'value'],
                 proxy: {type: 'memory', reader: 'json'},
                 data: [
                     {display: 'FATAL', value: 'FATAL'},
                     {display: 'DEBUG', value: 'DEBUG'},
                     {display: 'ERROR', value: 'ERROR'},
                     {display: 'WARNING', value: 'WARNING'},
                     {display: 'INFO', value: 'INFO'}
                 ]
             })
         },
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
        if (this.node)
            this.loadRecord(this.node);
    },

    save: function() {
        this.getForm().updateRecord(this.node);
        this.node.normalize();
        SM.reloadAllWithStatuses();
    }
});

