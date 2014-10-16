
// VIEW: Service Properties Window
// Wraps several Service information views in a tabbed window.
// ----------------------------------------------------------------------------

Ext.define('SM.view.service.ServicePropertiesWindow', {
    extend: 'Ext.window.Window',
    alias : 'widget.servicepropertieswindow',

    title : 'Service Properties',
    layout: 'fit',
    resizable:false,
    width : 400,
    height: 400,
    autoShow: true,
    draggable: false,
    service: null,
    modal:true,
    initComponent: function() {
        this.title = Ext.String.format('Service "{0}" Properties', this.service.get('name'));
        
        this.items = [{
            xtype: 'propertiesform',
            preventHeader: true,
            border: false,
            bodyPadding: 8,
            object: this.service
        }];

        this.buttons = [{
            text: 'Close',
            scope: this,
            handler: this.close
        }];
        
        this.callParent(arguments);
    }
});

