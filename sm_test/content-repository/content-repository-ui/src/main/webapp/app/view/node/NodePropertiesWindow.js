
// VIEW: Node Properties Window
// A popup window displaying the "Node Overview" panel.
// ----------------------------------------------------------------------------

Ext.define('SM.view.node.NodePropertiesWindow', {
    extend: 'Ext.window.Window',
    alias : 'widget.nodepropertieswindow',

    title : 'Node Properties',
    layout: 'fit',
    width : 400,
    height: 400,
    autoShow: true,
    resizable:false,
    node  : null,
    modal:true,
    draggable: false,
    initComponent: function() {
        this.title = Ext.String.format('Node "{0}" Properties', this.node.get('name'));
        
        this.items = [{
            xtype: 'nodeoverview',
            preventHeader: true,
            border: false,
            bodyPadding: 8,
            node: this.node
        }];

        this.buttons = [{
            text: 'Close',
            scope: this,
            handler: this.close
        }];
        
        this.callParent(arguments);
    }
});

