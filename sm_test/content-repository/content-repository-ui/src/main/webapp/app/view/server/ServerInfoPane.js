// VIEW: Server Info Pane
// Displays comprehensive information on a single Server. An Info Pane is
// displayed in the center pane of the Service Manager Page.
// ----------------------------------------------------------------------------

Ext.define('SM.view.server.ServerInfoPane', {
    extend: 'Ext.container.Container',
    alias : 'widget.serverinfopane',
    server: null,

    layout: {
        type: 'vbox',
        align: 'stretch'
    },
    padding: '0 20 0 15',

    showEditForm: function() {
        this.down("#subheader").hide();
        this.down("#editform").show();
        this.down("#editform").loadRecord(this.server);
    },

    hideEditForm: function() {
        this.down("#editform").hide();
        this.down("#subheader").show();
    },

    initComponent: function(config) {
        // TODO: Perform actual permission checking here.
        var admin = true; // UserManager.admin;
        this.items = [
            {
                xtype: 'infopaneheader',
                item: this.server,
                editButtonVisible: admin,
                propertiesButtonVisible: false,
                listeners: {
                    editbutton: function(item, btn, header) {
                        header.up('serverinfopane').showEditForm();
                    }
                }
            },
            {
                xtype: 'infopanesubheader',
                itemId: 'subheader',
                margin: '0 0 -15 0',
                item: this.server,
                fields: [
                    Functions.statusIconHeader1,
                    Functions.statusIconHeader2,
                    {
                        name: 'pid',
                        title: 'PID'
                    },
                    {
                        name: 'ipAddress',
                        title: 'IP Address'
                    }, {
                        name: 'hostName',
                        title: 'Hostname'
                    },
                    {
                        fieldLabel: 'Message Port',
                        name: 'messagePort'
                    },
                    {
                        name: 'clusterName',
                        title: 'Cluster',
                        dataFn: function(item, callback) {
                            return item.parentItem.get('name')
                        }
                    }
                    , {
                        name: 'os',
                        title: 'Operating System',
                        async: true,
                        dataFn: function(item, callback) {
                            item.getSystemInfo('os', callback);
                        }
                    }, {
                        name: 'arch',
                        title: 'Architecture',
                        async: true,
                        dataFn: function(item, callback) {
                            item.getSystemInfo('arch', callback);
                        }
                    }]
            },
            {
                xtype: 'serverform',
                itemId: 'editform',
                height: 150,
                server: this.server,
                horizontal: true,
                columnPadding: 16,
                hidden: true,
                buttons: [
                    {
                        text: 'Save',
                        formBind:true,
                        handler: function(btn) {
                            if (!btn.up('serverform').getForm().isValid()) {
                                Functions.errorMsg("One or more of the form values is invalid or missing.");
                                return;
                            }
                            btn.up('serverform').save();
                            btn.up('serverinfopane').hideEditForm();
                        }
                    },
                    {
                        text: 'Cancel',
                        handler: function(btn) {
                            btn.up('serverinfopane').hideEditForm();
                        }
                    }
                ]
            },
            {
                xtype:'component',
                cls:'separator-line',
                layout:{align:'left'},
                margin:'15 0 0 0',
                html:'<div></div>'
            },
            {
                xtype: 'container',
                border: false,
                flex: 4,
                layout: {
                    type: 'hbox',
                    align: 'stretch'
                },
                items: [
                    {
                        xtype: 'nodelist',
                        cls:'sm-item-header header-plain-bkg',
                        parentItem: this.server,
                        flex: 1,
                        margin: '15 15 0 0',
                        tbar: admin ? [
                            {
                                xtype:'CustomButtonGroup',
                                buttonItems: [
                                    {
                                        text: 'Add',
                                        icon: 'resources/images/toolbar-add.png',
                                        itemId: 'newnode'
                                    },
                                    {
                                        text: 'Delete',
                                        icon: 'resources/images/toolbar-delete.png',
                                        itemId: 'deletenode'
                                    }
                                ]
                            }
                        ] : undefined,
                        mergeHeaders: true
                    },
                    {
                        xtype: 'servicelist',
                        cls:'sm-item-header header-plain-bkg',
                        parentItem: this.server,
                        flex: 1,
                        margin: '15 0 0 0',
                        tbar: [
                            {
                                xtype:'CustomButtonGroup',
                                buttonItems:[
                                    {
                                        text: 'Start',
                                        icon: 'resources/images/toolbar-play.png',
                                        itemId: 'start'
                                    },
                                    {
                                        text: 'Stop',
                                        icon: 'resources/images/toolbar-stop.png',
                                        itemId: 'stop'
                                    },
                                    {
                                        text: 'Uninstall',
                                        icon: 'resources/images/toolbar-delete.png',
                                        itemId: 'uninstall',
                                        disabled: !admin
                                    }
                                ]
                            }
                        ],
                        mergeHeaders: true
                    }
                ]
            },
            {
                xtype: 'tabpanel',
                cls:'sm-item-header sm-tab-plain-bkd',
                flex: 3,
                activeTab: 0,
                margin: '15 0 0 0',
                items: [
                    {
                        xtype: 'errorlist',
                        parentItem: this.server
                    },
                    {
                        xtype: 'systeminfo',
                        title: '<span class="sm-item-header">Performance</span>',
                        disabled: !ConfigManager.usingDefaultConfig || this.server.get('status')=='offline',
                        layout: {
                            type: 'hbox',
                            align: 'stretch',
                            padding: 4
                        },
                        server: this.server
                    },
                    {
                        xtype: 'logfilelist',
                        disabled: !ConfigManager.usingDefaultConfig || this.server.get('status')=='offline',
                        service: this.server
                    }
                ]
            }
        ];

        this.callParent(arguments);
    }
});

