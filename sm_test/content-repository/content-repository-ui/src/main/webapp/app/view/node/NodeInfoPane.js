
// VIEW: Node Info Pane
// Displays comprehensive information on a single Node. An Info Pane is
// displayed in the center pane of the Service Manager Page.
// ----------------------------------------------------------------------------

Ext.define('SM.view.node.NodeInfoPane', {
    extend: 'Ext.container.Container',
    alias : 'widget.nodeinfopane',
    node  : null,

    layout: {
        type: 'vbox',
        align: 'stretch'
    },
    padding: '0 20 0 15',

    showEditForm: function() {
        this.down("#subheader").hide();
        this.down("#editform").show();
        this.down("#editform").loadRecord(this.node);
    },

    hideEditForm: function() {
        this.down("#editform").hide();
        this.down("#subheader").show();
    },

    initComponent: function(config) {
        // TODO: Perform actual permission checking here.
        var admin = true; //UserManager.admin;
        this.items = [{
            xtype: 'infopaneheader',
            item: this.node,
            editButtonVisible: admin,
            propertiesButtonVisible: false,
            listeners: {
                editbutton: function(item, btn, header) {
                    header.up('nodeinfopane').showEditForm();
                }
            }
        }, {
            xtype: 'infopanesubheader',
            itemId: 'subheader',
            item: this.node,
            fields: [
                Functions.statusIconHeader1,
                Functions.statusIconHeader2,
                {
                    name: 'pid',
                    title: 'PID'
                },
            {
                name: 'sshPort',
                title: 'SSH Port'
            }, {
                name: 'messagePort',
                title: 'Message Port'
            }, {
                name: 'roleName',
                title: 'Role'
            }, {
                name: 'logLevel',
                title: 'Log Level'
            }
//            {
//                name: 'framework',
//                async: true,
//                dataFn: function(item, callback) {
//                    item.getSystemInfo('framework', callback);
//                }
//            }
            ]
        }, {
            xtype: 'nodeform',
            itemId: 'editform',
            height: 175,
            node: this.node,
            horizontal: true,
            columnPadding: 16,
            hidden: true,
            buttons: [{
                text: 'Save',
                formBind:true,
                handler: function(btn) {
                    if (!btn.up('nodeform').getForm().isValid()) {
                        Functions.errorMsg("One or more of the form values is invalid or missing.");
                        return;
                    }
                    btn.up('nodeform').save();
                    btn.up('nodeinfopane').hideEditForm();
                }
            }, {
                text: 'Cancel',
                handler: function(btn) {
                    btn.up('nodeinfopane').hideEditForm();
                }
            }]
        }, {
            xtype:'component',
            cls:'separator-line',
            layout:{align:'left'},
            html:'<div></div>'
        }, {
            xtype: 'container',
            border: false,
            flex: 4,
            layout: {
                type: 'hbox',
                align: 'stretch'
            },
            items: [{
                xtype: 'servicelist',
                cls:'sm-item-header header-plain-bkg',
                parentItem: this.node,
                flex: 1,
                margin: '15 15 0 0',
                defaults:{
                    flat:true
                },
                tbar: [{
                    xtype:'CustomButtonGroup',
                    buttonItems:[{
                        text: 'Start',
                        icon: 'resources/images/toolbar-play.png',
                        itemId: 'start'
                    }, {
                        text: 'Stop',
                        icon: 'resources/images/toolbar-stop.png',
                        itemId: 'stop'
                    }, {
                        text: 'Install',
                        icon: 'resources/images/toolbar-add.png',
                        itemId: 'install',
                        disabled: !admin || !this.node.isEditable()
                    }, {
                        text: 'Uninstall',
                        icon: 'resources/images/toolbar-delete.png',
                        itemId: 'uninstall',
                        disabled: !admin || !this.node.isEditable()
                    }]
                }],
                mergeHeaders: true
            }
            /*, {
                xtype: 'joblist',
                cls:'sm-item-header header-plain-bkg',
                parentItem: this.node,
                flex: 1,
                margin: '15 15 0 0',
                defaults:{
                    flat:true
                },
                tbar: [{
                    xtype:'CustomButtonGroup',
                    buttonItems:[ {
                        text: 'Add',
                        icon: 'resources/images/toolbar-add.png',
                        itemId: 'Add',
                        disabled: !admin || !this.node.isEditable()
                    }, {
                        text: 'Remove',
                        icon: 'resources/images/toolbar-delete.png',
                        itemId: 'remove',
                        disabled: !admin || !this.node.isEditable()
                    }]
                }],
                mergeHeaders: true
            }*/
            ]
        }, {
            xtype: 'tabpanel',
            cls:'sm-item-header sm-tab-plain-bkd',
            flex: 3,
            activeTab: 0,
            margin: '15 0 0 0',
            items: [{
                xtype: 'errorlist',
                parentItem: this.node
            }, {
                xtype: 'jvmsysteminfo',
                title: '<span>Performance</span>',
                disabled: !ConfigManager.usingDefaultConfig || this.node.get('status')=='offline',
                layout: {
                    type: 'hbox',
                    align: 'stretch',
                    padding: 4
                },
                node: this.node
            }, {
                xtype: 'logfilelist',
                disabled: !ConfigManager.usingDefaultConfig || this.node.get('status')=='offline',
                service: this.node
            },
            {
                xtype: 'jvmsettingsgrid',
                cls:'sm-item-header header-plain-bkg',
                node: this.node
            }]
        }];

        this.callParent(arguments);
    }
});

