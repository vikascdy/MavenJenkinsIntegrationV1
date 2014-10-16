// VIEW: Create Config Page
// Displays a form that allows the user to create a new, blank config file.
// ----------------------------------------------------------------------------

var radioButtonTpl = new Ext.XTemplate(
    '<table class="radio-desc" border="0">',
      '<tr>',
        '<td rowspan="2" class="radio-icon">',
          '<img src="{icon}" width="45" height="45"/>',
        '</td>',
        '<td class="radio-heading">{heading}</td>',
      '</tr>',
      '<tr>',
        '<td class="radio-detail">{detail}</td>',
      '</tr>',
    '</table>'
);

Ext.define('SM.view.core.CreateConfigPage', {
    extend: 'SM.view.abstract.GenericPage',
    alias : 'widget.createconfigpage',

    header: 'Select Creation Method',
    template: null,
    product : null,
    unsavedChanges: false,
    minHeight: 720,
    items : [{
        xtype: 'container',
        cls: 'formpage',
        margin:'0 0 0 0',
        layout: {
            type: 'hbox',
            align: 'stretch'
        },
        items: [{
            // ---- COLUMN 1 ---- //
            xtype: 'container',
            minWidth:600,
            border: false,
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            flex: 2,
            items: [{
                xtype: 'container',
                layout: 'anchor',
                border: false,
                flex: 1,
                minHeight: 200,
                items: [{
                        xtype: 'component',
                        margin:'0 0 25 0',
                        html: "<h2 class='formpage-bigheader'>How would you like to do the installation?</h2>",
                        border: false
                    }, {
                        xtype: 'radiogroup',
                        columns: 1,
                        vertical: true,
                        padding: '20 0',
                        margin:'0 0 0 -3',
                        id:'radioGroup',
                        cls:'radio-config-choice',

                        items: [
                            {boxLabel: radioButtonTpl.apply({icon: 'resources/images/product-icon.png', heading: "Select a product", detail: "Build a configuration from scratch, based on a product."}), name: 'config', inputValue: 'product'},
                            {boxLabel: radioButtonTpl.apply({icon: 'resources/images/template-icon.png', heading: "Using a template", detail: "Start with a configuration template, and customize it according to your needs."}), name: 'config', inputValue: 'template'},
                            {boxLabel: radioButtonTpl.apply({icon: 'resources/images/import-icon.png', heading: "Importing an existing configuration", detail: "Import a previously exported configuration XML file."}), name: 'config', inputValue: 'import'}
                        ],
                        listeners: {
                            change: function(radioGrp, value) {

                                var page = radioGrp.up('createconfigpage');
                                var bottomPane = page.down('#bottomPane');
                                var sidePane = page.down('#sidePane');
                                var button = value.config;

                                if (button instanceof Array) return;
                                page.clearConfig();
                                bottomPane.getLayout().setActiveItem(button);
                                if (button == 'product') {
                                    var productList = page.down('productlist');
                                    productList.getSelectionModel().select(0);
                                    sidePane.getLayout().setActiveItem('productInfo');
                                    var defaultProduct = productList.getStore().first();
                                    if (defaultProduct) {
                                        page.updateProductInfo(defaultProduct);
                                    }

                                } else if (button == 'template' || button == 'import') {
                                    var templateList = page.down('templatelist');
                                    var defaultTemplate;

                                    sidePane.getLayout().setActiveItem('configPreview');
                                    if (button == 'template') {
//                                                    templateList.getSelectionModel().select(0);
                                        defaultTemplate = templateList.getStore().first();
                                        if (defaultTemplate) {
                                            ConfigManager.loadTemplate(defaultTemplate.get('filename'), function() {
                                                page.updateConfigPreview();
                                            });
                                        }
                                    }
                                } else
                                    sidePane.getLayout().setActiveItem('nosidebar');
                            }
                        }
                    }]
            }, {
                xtype: 'container',
                margin:'160 0 0 0',
                layout: 'card',
                itemId: 'bottomPane',
                border: false,
                flex: 2,
                //style: {overflow: 'auto'},
                minHeight: 300,
                defaults: {
                    xtype: 'container',
                    border: false,
                    layout: 'anchor',
                    defaults: {anchor: '100%'}
                },
                items: [{
                    itemId: 'product',
                    items: [{
                        xtype: 'component',
                        html: "<h2 class='formpage-bigheader'>Select a Product</h2>",
                        margin:'0 0 5 0',
                        border: false
                    }, {
                        xtype: 'productlist',
                        height: 128,
                        margin: '10 10 10 0',
                        listeners: {
                            'viewready' :function(grid) {
                                grid.getSelectionModel().select(0);
                            },
                            'select' : function(rowModel, record, index, e) {
                                var page = SM.viewport.down('createconfigpage');
                                page.updateProductInfo(record);
                            }
                        }
                    }, {
                        xtype: 'container',
                        layout: {
                            type: 'hbox',
                            align: 'stretch'
                        },
                        items: [{
                            xtype: 'component',
                            flex: 1,
                            border: false
                        }, {
                            xtype: 'wizardbutton',
                            margin:'0 0 0 5',
                            listeners: {
                                click: function(btn) {
                                    var page = btn.up('createconfigpage');
                                    page.createConfigFromProduct();
                                    SM.setPage(Ext.create('SM.view.wizard.WizardPage'),true);
                                }
                            }
                        }, {
                            xtype: 'configeditorbutton',
                            margin:'0 10 0 5',
                            listeners: {
                                click: function(btn) {
                                    var page = btn.up('createconfigpage');
                                    page.createConfigFromProduct();
                                    Ext.widget('defineconfigwindow', {config: ConfigManager.config});
                                }
                            }
                        }]
                    }]
                }, {
                    itemId: 'template',
                    items: [{
                        xtype: 'component',
                        margin:'0 0 25 0',
                        html: "<h2 class='formpage-bigheader'>Select a Template</h2>",
                        border: false
                    }, {
                        xtype: 'templatelist',
                        height: 128,
                        margin: '10 10 10 0',
                        listeners: {
                            'select' : function(rowModel, record, index, e) {
                                var page = SM.viewport.down('createconfigpage');
                                ConfigManager.loadTemplate(record.get('filename'), function() {
                                    page.updateConfigPreview();
                                });
                            }
                        }
                    }, {
                        xtype: 'container',
                        layout: {
                            type: 'hbox',
                            align: 'stretch'
                        },
                        items: [{
                            xtype: 'component',
                            flex: 1,
                            border: false
                        }, {
                            xtype: 'configeditorbutton',
                            margin:'0 10 0 5',
                            listeners: {
                                click: function(btn) {
                                    if (!ConfigManager.config) {
                                        Functions.errorMsg("You must select a Template.", "No Template Selected");
                                        return;
                                    }
                                    Ext.widget('defineconfigwindow', {config: ConfigManager.config});
                                }
                            }
                        }]
                    }]
                }, {
                    itemId: 'import',
                    items: [{
                        xtype: 'component',
                        margin:'0 0 25 0',
                        html: "<h2 class='formpage-bigheader'>Import from Configuration File</h2>",
                        border: false
                    }, {
                        xtype: 'form',
                        itemId: 'importForm',
                        border: false,
                        defaults: {anchor: '100%'},
                        items: [{
                            xtype: 'filefield',
                            name: 'import',
                            fieldLabel: 'Select Import File',
                            labelSeparator:'',
                            margin: '10 10 10 0',
                            allowBlank: false
                        }, {
                            xtype: 'container',
                            layout: {
                                type: 'hbox',
                                align: 'stretch'
                            },
                            items: [{
                                xtype: 'component',
                                flex: 1,
                                border: false
                            }, {
                                xtype: 'configeditorbutton',
                                margin:'0 10 0 5',
                                formBind: true,
                                listeners: {
                                    click: function(btn) {
                                        var page = btn.up('createconfigpage');
                                        page.importConfigFromForm();
                                    }
                                }
                            }]
                        }]
                    }]
                }]
            }]
        }, {
            // ---- COLUMN 2 ---- //
            xtype: 'container',
            minWidth:395,
            border: false,
            layout: 'card',
            flex: 2,
            itemId: 'sidePane',
            defaults: {
                xtype: 'container',
                border: false,
//                padding: 40,
                layout: {
                    type:  'vbox',
                    align: 'stretch'
                }
            },
            items: [{
                itemId: 'nosidebar',
                html: '&nbsp'
            }, {
                itemId: 'productInfo',
                border: true,
                items: [{
                    xtype: 'panel',
                    title:'Product Details',
                    border: false
                }, {
                    xtype     : 'component',
                    cls       : 'create-config-sidebar-header',
                    itemId    : 'productInfoHeader',
                    minHeight : 120,
                    border    : false,
                    flex:  1,
                    data:  {
                        name: "Some Product",
                        version: "Some Version",
                        description: "Description goes here..."
                    },
                    tpl: new Ext.XTemplate("<dl>",
                        "<dt>Product</dt><dd class='config-info'>{name}</dd>",
                        "<dt>Version</dt><dd class='config-info'>{version}</dd>",
                        "<dt>Description</dt><dd class='config-info'>{description}</dd>",
                        "</dl>")
                }, {
                    xtype: 'panel',
                    style:'background-color:#ffffff; tex-align:left;',
                    flex: 3,
                    layout: 'fit',
                    items: [{
                        xtype: 'component',
                        padding: '15 15 15 15',
                        flex: 1,
                        itemId: 'productReleaseNotes',
                        data: {
                            releaseNotes: 'N/A'
                        },
                        tpl: new Ext.XTemplate("<h3>Release Notes</h3>",
                            "<br /><p>{releaseNotes}</p>")
                    }]
                }]
            }, {
                itemId: 'configPreview',
                items: [{
                    xtype: 'panel',
                    height: 24,
                    border: false,
                    title:'Configuration Preview'
                }, {
                    xtype:  'component',
                    cls:    'create-config-sidebar-header',
                    itemId: 'configPreviewHeader',
                    minHeight: 120,
                    border:true,
                    flex:   1,
                    data:  {
                        name: "Some Configuration File",
                        description: "Description goes here...",
                        numServers: 2,
                        installType: "Distributed"
                    },
                    tpl: new Ext.XTemplate("<dl>",
                        "<dt>Configuration</dt><dd>{name}</dd>",
                        "<dt>Description</dt><dd>{description}</dd>",
                        "<dt>Servers</dt><dd>{numServers}</dd>",
                        "<dt>Installation Type</dt><dd>{installType}</dd>",
                        "</dl>")
                }, {
                    xtype: 'configtree',
                    flex: 3,
                    preventHeader: true,
                    enableDragDrop: false
                }]
            }]
        }]
    }],

    createConfigFromProduct: function() {
        if (!this.product) {
            Functions.errorMsg("You must select a Product.", "No Product Selected");
            return;
        }

        var resourceTypeStore = Ext.getStore('ResourceTypeStore');
        if (resourceTypeStore) resourceTypeStore.load();

        var name = "New " + this.product.data.name;

        var config = Ext.create('SM.model.Config', {
            name: name,
            version: '1.0',
            productName: this.product.data.name,
            productVersion: this.product.data.version,
            id: name + "-" + this.product.data.version,
            timestamp: Date.now()
        });
        config.appendChild(Ext.create('SM.model.Cluster', {
            name       : name,
            environment: 'Production'
        }));
        ConfigManager.usingDefaultConfig = false;
        ConfigManager.config = config;
        config.normalize();
    },

    importConfigFromForm: function() {
        var loadingWindow = Ext.widget('progresswindow', {text: 'Importing configuration...'});
        var formPanel = this.down('#importForm');
        formPanel.getForm().submit({
            url: JSON_UPLOAD_URL + '/config.importFile',
            params: {
                data: "{import: null}"
            },
            success: function(form, action) {
                Log.debug(action);
                ConfigManager.loadSavedConfig(action.result.configName, action.result.configVersion,
                    function() {
                        loadingWindow.destroy();
                        SM.setPage(Ext.create('SM.view.core.ServiceManagerPage'));
                    }
                );
            },
            failure: function(form, action) {
                loadingWindow.destroy();
                switch (action.failureType) {
                case Ext.form.action.Action.CLIENT_INVALID:
                    Functions.errorMsg('You must provide a file to import.', 'Import Failed');
                    break;
                case Ext.form.action.Action.CONNECT_FAILURE:
                    Functions.errorMsg('Could not connect to server.', 'Import Failed');
                    break;
                case Ext.form.action.Action.SERVER_INVALID:
                    Functions.errorMsg(action.result.error, 'Import Failed');
                }
            }
        });
    },

    clearConfig: function() {
        ConfigManager.clearConfig();
        this.product = null;
        this.template = null;
        this.down('#configPreviewHeader').update({
            name:        "N/A",
            description: "N/A",
            numServers:  "N/A",
            installType: "N/A"
        });
        this.down('#productInfoHeader').update({
            name:        "N/A",
            version:     "N/A",
            description: "N/A"
        });
        this.down('#productReleaseNotes').update({
            releaseNotes: "N/A"
        });
        this.down('configtree').reload();
    },

    updateConfigPreview: function() {
        var config = ConfigManager.config;
        this.down('#configPreviewHeader').update({
            name:        config.get('name'),
            description: config.get('description'),
            numServers:  config.getChildrenWith({type: 'Server'}).length,
            installType: config.getChildrenWith({type: 'Server'}).length > 1 ? 'Distributed' : 'Local'
        });
        var tree = this.down('configtree');
        tree.reload(function() {
            tree.getRootNode().collapse(true);
            tree.getRootNode().expand(true);
        });
    },

    updateProductInfo: function(product) {
        this.product = product;
        this.down('#productInfoHeader').update({
            name:        product.get('name'),
            version:     product.get('version'),
            description: product.get('description')
        });
        this.down('#productReleaseNotes').update({
            releaseNotes: product.get('releaseNotes')
        });
    },

    initComponent: function(config) {
        this.callParent(config);
        this.down('allconfigslink').show();
        this.down('#radioGroup').setValue({config: 'product'});
    }
});

