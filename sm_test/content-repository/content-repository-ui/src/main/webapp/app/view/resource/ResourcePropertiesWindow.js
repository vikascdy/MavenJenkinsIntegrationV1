
// VIEW: Resource Properties Window
// Wraps several Resource information views in a tabbed window.
// ----------------------------------------------------------------------------

Ext.define('SM.view.resource.ResourcePropertiesWindow', {
    extend: 'Ext.window.Window',
    alias : 'widget.resourcepropertieswindow',

    title : 'Resource Properties',
    layout: 'fit',
    resizable:false,
    width : 400,
    height: 400,
    autoShow: true,
    draggable: false,
    resource: null,
    modal:true,
    initComponent: function() {
        this.title = Ext.String.format('Resource "{0}" Properties', this.resource.get('name'));

        this.items = [{
            xtype: 'propertiesform',
            preventHeader: true,
            border: false,
            bodyPadding: 8,
            object: this.resource
        }];

        this.buttons = [{
            text: 'Close',
            scope: this,
            handler: this.close
        }];
        
        this.callParent(arguments);
    }
});

