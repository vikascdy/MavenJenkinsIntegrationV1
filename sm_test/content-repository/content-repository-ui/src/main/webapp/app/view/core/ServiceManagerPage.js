// VIEW: Service Manager Page
// The default view of the Service Manager UI. Allows the user to view and
// manipulate a config file, and view the status of all Servers, Nodes, and
// Services in the config.
// ----------------------------------------------------------------------------

Ext .define( 'SM.view.core.ServiceManagerPage', {
    extend: 'Ext.container.Container',
    alias: 'widget.servicemanagerpage',

    layout: 'border',

    minHeight: 600,
    autoScroll: false,
    currentRecord: null,
    unsavedChanges: false, // Currently, the ServiceManagerPage is always considered
                           // to have unsaved changes. This may change in the future.

    initComponent: function(config) {
        var me = this;
        this.items = [{
            xtype: 'container',
            border: false,
            region: 'north',
            items: [{
                xtype: 'allconfigslink',
                listeners: {
                    'afterrender': function() {
                        this.getEl().on('click', function(e, t) {
                            e.stopEvent();
                            location.hash = '!/config';
                        }, null, {
                            delegate : '.all-config-link'
                        });

                    }
                }
            }, {
                xtype: 'button',
                itemId: 'back',
                hidden: true
            }, {
                xtype: 'configheader',
                cls: 'generic-page-header'
            }]
        }, {
            xtype: 'configtree',
            columnView: true,
            region: 'west',
            split: false,
            margin: '0 0 0 20',
            flex: 1,
            tbar: [{
                xtype: 'splitbutton',
                text: 'New...',
                iconCls: 'ico-newitem',
                itemId: 'newmenu',
                menu: {
                    items: [{
                        // text: 'Cluster',
                        // itemId: 'newcluster',
                        // iconCls: 'ico-cluster'
                    // }, {
                        text: 'Server',
                        itemId: 'newserver',
                        iconCls: 'ico-newserver'
                    }, {
                        text: 'Resource',
                        itemId: 'newresource',
                        iconCls: 'ico-newresource'
                    }, {
                        text : 'Role',
                        itemId : 'newrole',
                        iconCls : 'ico-newrole'
                    }, {
                        text : 'Node',
                        itemId : 'newnode',
                        iconCls : 'ico-newnode'
                    }, {
                        text : 'Service',
                        itemId : 'newservice',
                        iconCls : 'ico-newservice'
                    }]
                },
                listeners: {
                    click: function(btn) {
                        btn.showMenu();
                    }
                }
            }, {
                xtype: 'button',
                text: 'Delete',
                iconCls: 'ico-delete',
                itemId: 'delete'
            }]
        }, {
            xtype: 'panel',
            bodyStyle: 'background-color:transparent',
            border: false,
            layout: {
                type: 'fit',
                padding: 0
            },
            region: 'center',
            itemId: 'infopane',
            flex: 3
        }, {
            xtype: 'container',
            border: false,
            region: 'south',
            height: 40,
            padding: '14 20 0 20',
            cls: 'generic-page-footer',
            layout: {
                type: 'hbox'
            },
            items: [{
                xtype: 'component',
                html: '<p>Environment : ' + SM.environmentName + '</p>'
            }, {
                xtype: 'tbspacer',
                flex: 1
            }, {
                xtype: 'component',
                id: 'smFooterDetail',
                html: '<p>Copyright &copy; 2013, Edifecs Inc</p>'
            }]
        }];

        SM.changesSavedStatus = true;
        this.callParent(arguments);
        if (ConfigManager.config && ConfigManager.config.data.clusters)
            this.showInfoPaneFor(ConfigManager.config.data.clusters[0]);

        if (SM.testMode) {
            Ext.getCmp('smFooterDetail').html = '<p><span style="color:red; font-weight:bold;">TEST MODE ENABLED&nbsp;&nbsp;&nbsp;&nbsp;</span>Copyright &copy; 2013, Edifecs Inc</p>';
        }
        ConfigManager.getRequiredServiceTypes();
    },

    showPropertiesFor: function(item) {
        item.shouldBeA(['Server', 'Node', 'Service', 'Resource']);
        var propCtr = this.down("#properties");
        propCtr.removeAll();
        switch (item.getType()) {
        case 'Server':
            propCtr.add({
                xtype: 'serveroverview',
                title: 'Properties for ' + item.get('name'),
                server: item
            });
            break;
        case 'Node':
            propCtr.add({
                xtype: 'jvmsettingsgrid',
                title: 'JVM Settings for ' + item.get('name'),
                node: item
                });
            break;
        case 'Service':
        case 'Resource':
            propCtr.add({
                xtype: 'propertiesform',
                title: 'Properties for ' + item.get('name'),
                object: item
            });
            break;
        }
    },

    showInfoPaneFor: function(item) {
        var me = this;
        if (item instanceof SM.model.TreeNode)
            item = item.get('object');
        if (item.getType() == 'Node') {
            item.getNodePID(function() {
                me.down('#delete').enable();
                me.changeInfoPane(me, item);
            });
        } else if (item.getType() == 'Server') {
            item.getServerPID(function() {
                me.down('#delete').enable();
                me.changeInfoPane(me, item);
            });
        } else if (item.getType() == 'Cluster') {
            me.down('#delete').disable();
            me.changeInfoPane(me, item);
        } else {
            me.down('#delete').enable();
            me.changeInfoPane(me, item);
        }
    },

    changeInfoPane: function(me, item) {
         var infoPane = (item.getInfoPane !== undefined) ? item.getInfoPane(item) : undefined;
         infoPane = infoPane || {
             xtype: 'component',
             html: 'No info pane configured for this item.'
         };
         var infoPaneCtr = me.down('#infopane');
         if (infoPaneCtr) {
             infoPaneCtr.removeAll();
             infoPaneCtr.add(infoPane);
         }
         me.currentRecord = item;
     }
});

