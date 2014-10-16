
// VIEW: File Properties Window
// A tabbed window that displays general information about a file on one tab,
// and the file's history on another.
// ----------------------------------------------------------------------------

Ext.define('SM.view.content.FilePropertiesWindow', {
    extend: 'Ext.window.Window',
    alias : 'widget.filepropertieswindow',

    title    : 'File Properties',
    layout   : 'fit',
    width    : 400,
    height   : 400,
    autoShow : true,
    node     : null,
    activeTab: 0,
    resizable:false,
    draggable:false,
    modal:true,
    initComponent: function() {
        this.items = [{
            xtype: 'tabpanel',
            activeTab: this.activeTab,
            items: [{
                xtype: 'fileinfobar',
                itemId: 'info',
                bodyPadding: 4,
                fieldDefaults: {
                    labelAlign: 'left',
                    margin: '8 0'
                }
            }, {
                xtype: 'filehistorylist',
                node: this.node.get('directory') ? null : this.node,
                itemId: 'history',
                disabled: this.node.get('directory')
            }]
        }];

        this.buttons = [{
            text: 'Close',
            scope: this,
            handler: this.close
        }];
        
        this.callParent(arguments);

        this.down('#info').loadInfoFor(this.node);
    }
});


