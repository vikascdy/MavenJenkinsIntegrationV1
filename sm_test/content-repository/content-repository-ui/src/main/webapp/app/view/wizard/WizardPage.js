
// VIEW: Wizard Page
// The main container view for the New Configuration Wizard. Displays a
// navigation menu, a contextual help box, a tree showing the current Config,
// and a WizardPane in the center.
// ----------------------------------------------------------------------------

Ext.define('SM.view.wizard.WizardPage', {
    extend: 'Ext.container.Container',
    alias : 'widget.wizardpage',

    minHeight : 600,
    autoScroll: false,

    layout: 'border',
    currentPane: null,
    paneName: null,
    defaultHelpText: "Mouse over an object to display helpful information here.",
    serverIndex: 0,
    serviceIndex: 0,
    validationErrors: -1,
    unsavedChanges: true,

    items : [{
        // ---- HEADER ---- //
        xtype : 'container',
        region: 'north',
        items:[{
            xtype : 'allconfigslink',
            itemId: 'back'
        }, {
            xtype  : 'container',
            layout : 'hbox',
            height : 45,
            align  : 'stretch',
            padding: '0 0 25 20',
            border : false,
            items  : [{
                    xtype: 'component',
                    height:45,
                    html : '<h1 id="header-config-name">Configuration Setup</h1>'
                }, {
                    flex: 1,
                    xtype:'tbspacer'
                }, {
                    xtype : 'button',
                    iconCls:'back-button-icon',
                    text  : 'Back',
                    itemId: 'wizardPrev',
                    margin: '0 5 0 0'
                }, {
                    xtype : 'button',
                    iconCls:'next-button-icon',
                    iconAlign: 'right',
                    text  : 'Next',
                    itemId: 'wizardNext',
                    cls   : 'default-button',
                    margin: '0 20 0 0',
                    reload: function() {
                        try {
                            this.setText("Next: " +
                                this.up('wizardpage').currentPane.getNextPaneName());
                        } catch (err) {
                            this.setText('Next');
                        }
                    }
                }, {
                    xtype:'button',
                    text: 'Cancel',
                    itemId: 'cancel',
                    margin:'0 5 0 0'
                }, {
                    xtype: 'splitbutton',
                    text: 'Save',
                    itemId: 'save',
                    margin:'0 20 0 0',
                    menu: [{
                        text: 'Save and Close',
                        itemId: 'saveAndClose',
                        iconCls: 'mico-no'
                    }]
                }]
            }
        ]
    }, {
        // ---- LEFT SIDEBAR ---- //
        xtype : 'container',
        region: 'west',
        itemId: 'wizardLeftSidebar',
        flex  : 1,
        width: 280,
        layout: {
            type : 'vbox',
            align: 'stretch',
            padding: '4 15 8 10'
        },
        items : [{
            xtype: 'wizardnavbutton',
            title: 'Define Configuration Properties',
            paneCls: 'SM.view.wizard.ConfigPropertiesPane',
            createPane: function() {
                return Ext.create('SM.view.wizard.ConfigPropertiesPane', {flex: 1});
            }
        }, {
            xtype: 'wizardnavbutton',
            title: 'Define Servers',
            paneCls: 'SM.view.wizard.DefineServersPane',
            createPane: function() {
                var wizardPage = this.up('wizardpage');
                if (wizardPage.serverIndex === 0) {
                    if (ConfigManager.config.getChildrenWith({type: 'Server'}).length > 0)
                        wizardPage.serverIndex = 1;
                    else {
                        Functions.errorMsg("No servers are defined yet.");
                        return null;
                    }
                }
                return Ext.create('SM.view.wizard.DefineServersPane', {flex: 1, index: wizardPage.serverIndex});
            },
            reload: function() {
                var wizardPage = this.up('wizardpage');
                if (wizardPage) {
                    var s1 = wizardPage.serverIndex;
                    var s2 = ConfigManager.config.getChildrenWith({type: 'Server'}).length;
                    this.title = "Define Servers (" + s1 + '/' + s2 + ')';
                }
                this.updateHtml();
            }
        }, {
            xtype: 'wizardnavbutton',
            title: 'Define Services',
            paneCls: 'SM.view.wizard.DefineServicesPane',
            createPane: function() {
                var wizardPage = this.up('wizardpage');
                if (wizardPage.serviceIndex === 0) {
                    if (ConfigManager.config.getChildrenWith({type: 'Service'}).length > 0)
                        wizardPage.serviceIndex = 1;
                    else {
                        Functions.errorMsg("No services are defined yet.");
                        return null;
                    }
                }
                return Ext.create('SM.view.wizard.DefineServicesPane', {flex: 1, index: wizardPage.serviceIndex});
            },
            reload: function() {
                var wizardPage = this.up('wizardpage');
                if (wizardPage) {
                    var s1 = wizardPage.serviceIndex;
                    var s2 = ConfigManager.config.getChildrenWith({type: 'Service'}).length;
                    this.title = "Define Services (" + s1 + '/' + s2 + ')';
                }
                this.updateHtml();
            }
        }, {
            xtype: 'wizardnavbutton',
            title: 'Validate Configuration',
            paneCls: 'SM.view.wizard.ValidationPane',
            createPane: function() {
                return Ext.create('SM.view.wizard.ValidationPane', {flex: 1});
            }
        }, {
            xtype: 'wizardnavbutton',
            title: 'Deploy to Server(s)',
            paneCls: 'SM.view.wizard.DeploymentPane',
            createPane: function() {
                var wizardPage = this.up('wizardpage');
                if (wizardPage.validationErrors < 0) {
                    Functions.errorMsg("The configuration must be validated before it can be deployed.");
                    return null;
                } else if (wizardPage.validationErrors > 0) {
                    Functions.errorMsg("Cannot proceed to deployment: there are still unresolved" +
                        " validation errors. Resolve these errors before continuing.");
                    return null;
                }
                return Ext.create('SM.view.wizard.DeploymentPane', {flex: 1});
            }
        }, {
            // Spacer
            xtype : 'component',
            flex  : 2,
            border: false
        }, {
            xtype    : 'component',
            cls      : 'wizard-contextual-help',
            itemId   : 'wizardContextualHelp',
            flex     : 1,
            maxHeight: 200,
            margin:'0 0 -5 10',
            data     : {
                text: "Mouse over an object to display helpful information here."
            },
            tpl      : new Ext.XTemplate(
                "<h3 class='wizard-contextual-help-header' style='padding-left:20px;'>Contextual Help</h3>",
                "<p>{text}</p>")
        }]
    }, {
        // ---- RIGHT SIDEBAR ---- //
        xtype : 'configtree',
        region: 'east',
        itemId: 'wizardRightSidebar',
        flex  : 1,
        layout:'fit',
        split : true,
        margin:'0 20 0 0',
        collapsible: true,
        collapsed  : false,
        title : 'Configuration Preview',
        iconCls: 'ico-properties',
        iconAlign:'right'
    }, {
        // ---- CENTER PANE ---- //
        xtype : 'container',
        region: 'center',
        itemId: 'wizardCenterPane',
        overflowX:'hidden',
        style:'background-color:#ffffff;',
        layout: 'fit',
        bodyPadding:'20',
        minWidth:440,
//        maxWidth:680,
        flex  : 3,
        items : [{
                xtype: 'panel'
        }] // WizardPane goes here
    }, {
        // ---- FOOTER ---- //

        xtype: 'component',
        border: true,
        id:'wizardFooterDetail',
        region: 'south',
        padding:'14 20 0 0',
        cls: 'generic-page-footer',
        html: '<p>Copyright &copy; 2013, Edifecs Inc</p>',
        height:40

    }],

    initComponent: function() {
        if (Ext.getCmp('wizardAllConfigLink')) {
            Ext.getCmp('wizardAllConfigLink').destroy();
        }
        this.callParent(arguments);
        if(SM.testMode=='true') {
            Ext.getCmp('wizardFooterDetail').html=
                '<p><span style="color:red; font-weight:bold;">' +
                'TEST MODE ENABLED&nbsp;&nbsp;&nbsp;&nbsp;' +
                '</span>Copyright &copy; 2013, Edifecs Inc</p>';
        }
        this.setPane(Ext.create('SM.view.wizard.ConfigPropertiesPane', {flex: 1}));
    },

    setPane: function(pane) {
        this.currentPane = pane;
        this.paneName = Ext.getClassName(pane);
        this.down('#wizardCenterPane').removeAll();
        this.down('#wizardCenterPane').add(pane);
        this.setHelpText(this.defaultHelpText);
        this.recursiveAddContextualHelp(pane);
        if(this.paneName=='SM.view.wizard.DefineServicesPane')
        	SM.reloadAll('noRefresh');
        else
        	SM.reloadAll();
    },

    recursiveAddContextualHelp: function(cmp) {
        if (!cmp) return;
        var page = this;
        if (cmp.helpText) {
            cmp.mon(cmp.getEl(), {
                scope: page,
                mouseover: function() {
                    page.setHelpText(cmp.helpText);
                },
                mouseout: function() {
                    page.setHelpText('');
                }
            });
        }
        if (cmp.child && cmp.child())
            page.recursiveAddContextualHelp(cmp.child());
        if (cmp.nextSibling && cmp.nextSibling())
            page.recursiveAddContextualHelp(cmp.nextSibling());
    },

    getCurrentPane: function() {
        return this.currentPane;
    },

    getPaneName: function() {
        return this.paneName;
    },

    setHelpText: function(text) {
        this.down('#wizardContextualHelp').update({text: text});
    },

    promptToExit: function() {
        // The 'prompt' part of promptToExit is now handled by SM.setPage().
        SM.setPage(Ext.create('SM.view.core.CreateConfigPage'));
    },

    promptToSwitch: function() {
        Ext.Msg.confirm(
            "Switch to Config Editor?",
            "Are you sure you want exit Configuration Setup to switch to the advanced Configuration" +
                " Editor? You will not be able to return to Configuration Setup for this configuration.",
            function(btn) {
                if (btn == 'yes'){
                ConfigManager.saveConfig(function(){
                    var status=ConfigManager.usingDefaultConfig;
                    SM.isConfigLoaded=false;
                    SM.disableUnsavedChanges=true;
                    location.href='#!/service/'+ConfigManager.config.get('name')+ '/'+ConfigManager.config.get('version') + '/' + status;
                });
               }
            }
        );
    }
});

