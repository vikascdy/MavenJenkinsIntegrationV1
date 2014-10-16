// VIEW: Cluster Info Pane
// Displays comprehensive information on a single Node. An Info Pane is
// displayed in the center pane of the Service Manager Page.
// ----------------------------------------------------------------------------

Ext.define('SM.view.cluster.ClusterInfoPane', {
    extend : 'Ext.container.Container',
    alias  : 'widget.clusterinfopane',
    cluster: null,
    cls:'sm-item-header',
    layout : {
        type: 'vbox',
        align: 'stretch'
    },
    padding: '0 20 0 15',

    showEditForm: function() {
//        this.down("#subheader").hide();
        this.down("#editform").show();
        this.down("#editform").loadRecord(this.cluster);
    },

    hideEditForm: function() {
        this.down("#editform").hide();
//        this.down("#subheader").show();
    },

    initComponent: function(config) {
        // TODO: Perform actual permission checking here.
        var admin = true; //UserManager.admin;
        this.items = [{
            xtype: 'infopaneheader',
            item: this.cluster,
            editButtonVisible: false,
            listeners: {
                editbutton: function(item, btn, header) {
                    header.up('clusterinfopane').showEditForm();
                }
            }
        }, {
            xtype: 'clusterform',
            itemId: 'editform',
            height: 175,
            cluster: this.cluster,
            horizontal: true,
            columnPadding: 16,
            hidden: true,
            buttons: [{
                text: 'Save',
                formBind:true,
                handler: function(btn) {
                    if (!btn.up('clusterform').getForm().isValid()) {
                        Functions.errorMsg("One or more of the form values is invalid or missing.");
                        return;
                    }
                    btn.up('clusterform').save();
                    btn.up('clusterinfopane').hideEditForm();
                }
            }, {
                text: 'Cancel',
                handler: function(btn) {
                    btn.up('clusterinfopane').hideEditForm();
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
            flex: 1,
            layout: {
                type: 'hbox',
                align: 'stretch'
            },
            items: [{
                xtype: 'serverlist',
                cls:'sm-item-header header-plain-bkg',
                parentItem: this.cluster,
                flex: 1,
                margin: '15 15 0 0',
                tbar: admin ? [{
                    xtype : 'CustomButtonGroup',
                    buttonItems : [{
                        text: 'Add',
                        icon: 'resources/images/toolbar-add.png',
                        itemId: 'newserver'
                    }, {
                        text: 'Delete',
                        icon: 'resources/images/toolbar-delete.png',
                        itemId: 'deleteserver'

                    }]
                }] : undefined,
                mergeHeaders: true
            }, {
                xtype: 'resourcelist',
                cls:'sm-item-header header-plain-bkg',
                parentItem: this.cluster,
                flex: 1,
                margin: '15 0 0 0',
                tbar: admin ? [{
                    xtype : 'CustomButtonGroup',
                    buttonItems : [{
                        text: 'Add',
                        icon: 'resources/images/toolbar-add.png',
                        itemId: 'newresource'
                    }, {
                        text: 'Delete',
                        icon: 'resources/images/toolbar-delete.png',
                        itemId: 'deleteresource'
                    }]
                }] : undefined,
                mergeHeaders: true
            }]
        }, {
            xtype: 'errorlist',
            cls:'sm-item-header header-plain-bkg',
            parentItem: this.cluster,
            showValidationErrors: true,
            flex: 1,
            margin: '15 0 0 0',
            padding:'0'
        }];

        this.callParent(arguments);
    }
});

