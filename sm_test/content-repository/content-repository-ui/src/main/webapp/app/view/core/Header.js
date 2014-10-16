// VIEW: Header
// The header for a Service Manager Page. Displays the name of the currently
// open config file, as well as a row of buttons to perform basic tasks such as
// saving/loading config files.
// ----------------------------------------------------------------------------

Ext.define('SM.view.core.Header', {
    extend: 'Ext.container.Container',
    alias : 'widget.configheader',
    layout:'hbox',
    height: 45,
    align:'stretch',
    padding:'0 0 30 20',
    dateFormat: 'F j, Y, g:i A', // http://docs.sencha.com/ext-js/4-0/#!/api/Ext.Date

    initComponent: function(config) {
        this.items = [
            {
                xtype: 'component',
                itemId: 'header_title',
                data: {
                    name: ConfigManager.config.get('name'),
                    productName: ConfigManager.config.get('productName'),
                    productVersion: ConfigManager.config.get('productVersion'),
                    lastModified: Ext.Date.format(new Date(ConfigManager.config.get('timestamp')),
                        this.dateFormat),
                    draft: !ConfigManager.usingDefaultConfig,
                    editVisibility: 'visible'
                    // TODO: Use actual permissions checks in place of all the commented-out UserManager references.
                    //editVisibility: UserManager.admin ? 'visible' : 'hidden'
                },
                flex: 1,
                tpl: '<div id="header-container">' +
                    '<h1 id="header-config-name">{productName}-{productVersion} ({name}) <tpl if="draft"> - Draft</tpl>' +
                    '</a></h1></div>'
            },
            {
                xtype:'tbspacer'
            },
            {
                xtype:'CustomButtonGroup',
                margin:'7 2 0 5',
                buttonItems:[
                    {
                        text: 'Refresh',
                        icon: 'resources/images/toolbar-reload.png',
                        itemId: 'refresh'
                    },
                    {
                        text: 'Export',
                        icon: 'resources/images/toolbar-export.png',
                        itemId: 'export'
                    }
                ]
            },
            {
                xtype:'CustomButtonGroup',
                margin:'7 2 0 5',
                buttonItems:[
                    {
                        
                        text: 'Validate',
                        itemId: 'validate',
                        icon: 'resources/images/toolbar-validate.png',
                        disabled: false //!UserManager.admin
                    },
                    {
                        
                        text: 'Deploy',
                        itemId: 'deploy',
                        icon: 'resources/images/toolbar-deploy.png',
                        disabled:  false //!UserManager.admin
                    },
                    {
                        
                        text: 'Patch',
                        icon: 'resources/images/toolbar-upgrade.png',
                        itemId: 'patch'
                    }
                ]
            }, {
                xtype:'CustomButtonGroup',
                margin:'7 20 0 5',
                buttonItems:[
                    {
                        
                        text: 'Save',
                        itemId: 'save',
                        icon: 'resources/images/toolbar-save.png',
                        disabled: false //!UserManager.admin
                    },
                    {
                        
                        text: 'Save As',
                        itemId: 'saveAs',
                        icon: 'resources/images/toolbar-save.png',
                        disabled: false //!UserManager.admin
                    }
                ]
            }
        ];

        this.callParent(arguments);
    },

    reload: function() {
        this.down("#header_title").update({
            name: ConfigManager.config.get('name'),
            productName: ConfigManager.config.get('productName'),
            productVersion: ConfigManager.config.get('productVersion'),
            lastModified: Ext.Date.format(new Date(ConfigManager.config.get('timestamp')), this.dateFormat),
            draft: !ConfigManager.usingDefaultConfig,
            editVisibility: 'visible' //UserManager.admin ? 'visible' : 'hidden'
        });
    }
});

