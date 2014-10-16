
// VIEW: Open Config Page
// The default start page once a user account and default config file have been
// created. Lists all of the saved config files stored on the backend server,
// and allows the user to pick one to edit.
// ----------------------------------------------------------------------------

Ext.define('SM.view.core.OpenConfigPage', {
    extend: 'SM.view.abstract.GenericPage',
    alias : 'widget.openconfigpage',
    
    header: 'All Configurations',

    padding: '30 0 0 0',

    items : [{
        xtype: 'savedconfiglist',
        preventHeader: true,
        tbar: [{
            xtype : 'button',
            text  : 'Create',
            iconCls: 'mico-new',
            itemId: 'create'
        }, {
            xtype : 'button',
            text  : 'Open',
            iconCls: 'mico-file',
            itemId: 'open'
        }, {
            xtype : 'button',
            text  : 'Rename',
            iconCls: 'mico-rename',
            itemId: 'rename'
        }, {
            xtype : 'button',
            text  : 'Delete',
            iconCls: 'mico-delete',
            itemId: 'delete'
        },
        '-',
        {
            xtype : 'button',
            text  : 'Import',
            iconCls: 'mico-import',
            itemId: 'import'
        }, {
            xtype : 'button',
            text  : 'Export',
            iconCls: 'mico-export',
            itemId: 'export'
        }]
    }],

    initComponent: function() {
        this.callParent();
        // TODO: Figure out new way to determine whether user has admin privileges.
        /*if (!UserManager.admin) {
            this.down('#create').disable();
            this.down('#delete').disable();
            this.down('#rename').disable();
        }*/
        
        // Preload the ProductStore to prevent race conditions when loading configs.
        Ext.getStore('ProductStore').load();
    }
});

