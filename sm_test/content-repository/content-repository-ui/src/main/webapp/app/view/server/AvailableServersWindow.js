// VIEW: Available Servers Window
// Auxillary window for ServerForm. Allows the user to select an available
// server on the network, and populates a ServerForm with this server's
// information.
// ----------------------------------------------------------------------------

Ext.define('SM.view.server.AvailableServersWindow', {
    extend: 'Ext.window.Window',
    alias : 'widget.availableserverswindow',

    title : 'Use Existing Server',
    iconCls: 'ico-newserver',
    layout: {
        type: 'hbox',
        align: 'stretch'
    },
    autoShow: true,
    modal : true,
    resizable:false,
    width : 560,
    height: 400,
    draggable: false,
    form  : null,

    items : [
        {
            xtype : 'gridpanel',
            itemId: 'list',
            title : 'Available Servers',
            store : 'AvailableServerStore',
            columns: [
                {
                    header: 'Hostame',
                    dataIndex: 'hostname',
                    flex: 1
                },
                {
                    header: 'IP Address',
                    dataIndex: 'ipAddress',
                    flex: 1
                }
            ],
            margin: 8,
            flex: 1,
            viewConfig: {
                populateForm : function(infoForm, record) {
                    infoForm.loadRecord(record);
                    infoForm.getForm().setValues({
                        cpu: Ext.String.format("{0} cores ({1}GHz)", record.data.cpuCores, record.data.cpuMHz / 1000.0),
                        mem: Ext.String.format("{0}GB", record.data.memMB / 1024.0)
                    });
                },
                listeners: {
                    'viewready' : function(grid) {
                        grid.getSelectionModel().select(0);
                        var record = grid.getStore().first();
                        var infoForm = this.up('window').down('#info');
                        this.populateForm(infoForm, record);

                    },
                    'itemclick': function(view, record) {
                        var infoForm = view.up('window').down('#info');
                        this.populateForm(infoForm, record);

                    }

                }
            }

        },
        {
            xtype : 'form',
            itemId: 'info',
            border: false,
            defaultType: 'displayfield',
            items: [
                {
                    fieldLabel: 'Hostname',
                    name: 'hostname'
                },
                {
                    fieldLabel: 'IP Address',
                    name: 'ipAddress'
                },
                {
                    fieldLabel: 'Operating System',
                    name: 'os'
                },
                {
                    fieldLabel: 'Architecture',
                    name: 'arch'
                },
                {
                    fieldLabel: 'CPUs',
                    name: 'cpu'
                },
                {
                    fieldLabel: 'Memory',
                    name: 'mem'
                }
            ],
            margin: '32 8 8',
            flex: 1
        }
    ],

    buttons: [
        {
            text: 'Select',
            handler: function(btn) {
                var win = btn.up('window');
                var selection = win.down('#list').getSelectionModel().getSelection();
                if (selection.length > 0) {
                    var server = selection[0];
                    win.form.getForm().setValues({
                        name: server.get('hostname'),
                        ipAddress: server.get('ipAddress'),
                        hostName: server.get('hostname')
                    });
                    win.close();
                } else {
                    Functions.errorMsg("You have not selected a server.");
                }
            }
        },
        {
            text: 'Cancel',
            handler: function(btn) {
                btn.up('window').close();
            }
        }
    ]
});

