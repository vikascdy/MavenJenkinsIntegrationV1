
// VIEW: Server Properties Window
// A popup window displaying the "Server Overview" panel.
// ----------------------------------------------------------------------------

Ext.define('SM.view.server.ServerPropertiesWindow', {
    extend: 'Ext.window.Window',
    alias : 'widget.serverpropertieswindow',

    title : 'Server Properties',
    layout: 'fit',
    width : 400,
    height: 400,
    autoShow: true,
    resizable:false,
    server: null,
    draggable: false,
    modal:true,
    initComponent: function() {
        this.title = Ext.String.format('Server "{0}" Properties', this.server.get('name'));
        
        this.items = [{
            xtype: 'serveroverview',
            preventHeader: true,
            border: false,
            bodyPadding: 8,
            server: this.server
        }];

        this.buttons = [{
            text: 'Close',
            scope: this,
            handler: this.close
        }];
        
        this.callParent(arguments);
    }
});

