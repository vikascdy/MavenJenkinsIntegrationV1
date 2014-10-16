
// VIEW: New Role Window
// Displays a list of available Roles, and a list of ServiceTypes which updates
// when a Role is selected to show which ServiceTypes the Role contains. Allows
// the user to add a new Node to a Server prepopulated with the Services
// specified by a Role.
// ----------------------------------------------------------------------------

Ext.define('SM.view.node.NewRoleWindow', {
    extend: 'Ext.window.Window',
    alias : 'widget.newrolewindow',

    title: 'New Role',
    iconCls: 'ico-newrole',
    width: 500,
    height: 400,
    modal:true,
    autoShow: true,
    layout: {
        type: 'hbox',
        align: 'stretch'
    },
    padding: 10,
    server: null,
    selectedRole: null,
    resizable:false,
    draggable: false,
    selectRole: function(role) {
        this.down('#roleHeader').update({roleName: role.get('name')});
        this.selectedRole = role;
        this.down('servicetypelist').reload();
    },

    createRole: function() {
        try {
            var selection = this.down('rolelist').getSelectionModel().getSelection();
            if (selection.length < 1)
                Ext.Error.raise("No Role selected.");
            var role = selection[0].get('role');
            var server = this.down('configitempicker').getItem();
            
            if (!server)
                Ext.Error.raise("No Server selected.");
            server.shouldBeA('Server');
            var node = server.spawnNode(ConfigManager.getNextAvailableName(role.get('name')), null, '',null);
            if (node) {
                node.applyRole(role);
                SM.reloadAll();
            }
            this.close();
        } catch (err) {
            Functions.errorMsg(err.message);
        }
    },

    initComponent: function(config) {    
        var me = this;
        this.buttons = [{
            text: 'Add Role',
            scope: this,
            handler: this.createRole
        }, {
            text: 'Close',
            scope: this,
            handler: this.close
        }];

        this.items = [{
            xtype: 'rolelist',
            title: 'Available Roles',
            hideHeaders: true,
            flex: 1,
            margin: 2,
            listeners: {
                select: function(rowModel, record) {
                    me.selectRole(record.get('role'));
                }
            }
        }, {
            xtype : 'container',
            border: false,
            flex  : 1,
            margin: 2,
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            defaults: {margin: '4 0 0 0'},
            items : [{
                xtype: 'configitempicker',
                searchCriteria: {type: 'Server'}
            }, {
                xtype : 'component',
                itemId: 'roleHeader',
                data  : {roleName: 'None'},
                tpl   : '<h3>Selected Role: {roleName}</h3>'
            }, {
                xtype: 'servicetypelist',
                title: 'Services in Role',
                hideHeaders: true,
                flex: 1,
                tbar: null,
                getData: function() {
                    var role = me.selectedRole;
                    if (!role)
                        return [];
                    else
                        return Ext.Array.map(role.getServiceTypes(), function(type) {
                            return {
                                name   : type.get('name'),
                                version: type.get('version'),
                                object : type
                            };
                        });
                }
            }]
        }];

        this.callParent(arguments);
        if (this.server){
            this.down('configitempicker').setItem(this.server);
        }
    }
});


