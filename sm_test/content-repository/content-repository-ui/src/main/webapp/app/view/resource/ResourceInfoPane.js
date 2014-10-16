// VIEW: Resource Info Pane
// Displays comprehensive information on a single Resource. An Info Pane is
// displayed in the center pane of the Service Manager Page.
// ----------------------------------------------------------------------------

Ext.define('SM.view.resource.ResourceInfoPane', {
    extend: 'Ext.container.Container',
    alias : 'widget.resourceinfopane',
    resource: null,

    layout: {
        type: 'vbox',
        align: 'stretch'
    },
    padding: '0 20 0 15',

    showEditForm: function() {
        this.down("#subheader").hide();
        this.down("#editform").show();
        this.down("#editform").loadRecord(this.resource);
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
            item: this.resource,
            editButtonVisible: admin,
            propertiesButtonVisible: false ,
            listeners: {
                editbutton: function(item, btn, header) {
                    header.up('resourceinfopane').showEditForm();
                }
            }
        }, {
            xtype: 'infopanesubheader',
            itemId: 'subheader',
            item: this.resource,
            fields: [
                Functions.statusIconHeader1,
                Functions.statusIconHeader2,
            {
                name: 'restype',
                title: 'Resource Type'
            }]
        }, {
            xtype: 'resourceform',
            itemId: 'editform',
            height: 140,
            resource: this.resource,
            horizontal: true,
            columnPadding: 16,
            hidden: true,
            buttons: [{
                    text: 'Save',
                    formBind:true,
                    handler: function(btn) {
                        if (!btn.up('resourceform').getForm().isValid()) {
                            Functions.errorMsg("One or more of the form values is invalid or missing.");
                            return;
                        }
                        btn.up('resourceform').save();
                        btn.up('resourceinfopane').hideEditForm();
                    }
                }, {
                    text: 'Cancel',
                    handler: function(btn) {
                        btn.up('resourceinfopane').hideEditForm();
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
                xtype: 'propertiesform',
                cls:'sm-item-header header-plain-bkg',
                bodyStyle:'backgroundColor:#ffffff',
                object: this.resource,
                bodyPadding:'10 25 10 10',
                flex:1,
                margin: '15 15 0 0'
            }, {
                xtype: 'tabpanel',
                activeTab: 0,
                cls:'sm-item-header',
                flex: 1,
                margin: '15 0 0 0',
                items: [{
                    xtype: 'dependentserviceslist',
                    title: '<span>Services Using this Resource</span>',
                    resource: this.resource
                }, {
                    xtype: 'resourcelist',
                    title: '<span>All ' + this.resource.get('restype') + 's in Cluster</span>',
                    parentItem: this.resource.getParent(), // Cluster
                    extraCriteria: {restype: this.resource.get('restype')}
                }]
            }]
        }, {
            xtype: 'errorlist',
            cls:'sm-item-header header-plain-bkg',
            parentItem: this.resource,
            flex: 1,
            margin: '15 0 0 0',
            padding:'0'
        }];

        this.callParent(arguments);
    }
});

