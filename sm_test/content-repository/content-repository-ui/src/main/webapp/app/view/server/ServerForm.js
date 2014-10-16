// VIEW: Server Form
// The form used when editing or creating a Server. This is used in several
// different places throughout the UI; if you want to modify the appearance of
// only one ServerForm, do not modify this file!
// ----------------------------------------------------------------------------

Ext.define('SM.view.server.ServerForm', {
    extend: 'SM.view.abstract.FlexibleForm',
    alias : 'widget.serverform',
    server: null,
    border: false,
    trackResetOnLoad: true,
    padding:'0',
    bodyPadding: '0',
    margin:'0 0 15 0',

    columns: [[
        {
            xtype: 'container',
            layout: {
                type: 'hbox',
                align: 'stretch'
            },
            border: false,
            margin: '0 0 6 0',
            items: [{
                xtype: 'textfield',
                name: 'name',
                labelSeparator :"",
                fieldLabel: 'Name',
                allowBlank: false,
                maskRe: /[^:]/,
                flex: 1
            }, {
                // An extra vbox container pushes the button to the bottom of its
                // container. Used to guarantee that the button lines up with the
                // field when the field's label is on top.
                xtype: 'container',
                border: false,
                width: 25,
                layout: 'vbox',
                margin:'0 15 0 0',
                items: [{
                    xtype: 'component',
                    flex: 1
                }, {
                    xtype:  'button',
                    itemId: 'add-existing',
                    padding: '4 2 4 2',
                    iconCls: 'ico-newserver',
                    tooltip: 'Use Existing Server',
                    width: 25,
                    handler: function(btn) {
                            Ext.getStore('AvailableServerStore').load();
                        Ext.widget('availableserverswindow', {form: btn.up('serverform')});
                    }
                }]
            }]
        }, {
            xtype: 'textfield',
            name: 'hostName',
            labelSeparator :"",
            fieldLabel: 'Host Name',
            maskRe: /[^:]/,
            margin:'-5 15 0 0'
        }
    ], [
        {
            xtype: 'textfield',
            name: 'ipAddress',
            labelSeparator :"",
            fieldLabel: 'IP Address',
            regex: /^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/,
            regexText: 'Must be a valid IP address.',
            maskRe: /[\d.]/,
            margin:'0 15 0 0'
        }, {
            xtype: 'textfield',
            name: 'messagePort',
            labelSeparator :"",
            fieldLabel: 'Message Port',
            allowBlank: true,
            maskRe: /\d/,
            margin:'0 15 0 0'
        }], [{
            xtype: 'textarea',
            name: 'description',
            labelSeparator :"",
            fieldLabel: 'Description',
            margin:'0 15 0 0'
        }
    ]],

    initComponent: function(config) {
        this.callParent(arguments);
        if (this.server)
            this.loadRecord(this.server);
    },

    save: function() {
        this.getForm().updateRecord(this.server);
        this.server.normalize();
        SM.reloadAll();
    }
});

