
// VIEW: Rename Node Window 
// A popup window for renaming Nodes.
// ----------------------------------------------------------------------------

Ext.define('EdifecsInstaller.view.node.RenameNodeWindow', {
    extend: 'Ext.window.Window',
    alias : 'widget.renamenodewindow',

    title   : 'Rename Node',
    layout  : 'fit',
    autoShow: true,
    node    : null,
    bodyPadding: 5,
    resizable:false,
    draggable: false,
    modal:true,
    items: [{
        xtype : 'form',
        layout: 'anchor',
        width : 360,
        bodyPadding: 5,
        items : [{
            xtype: 'displayfield',
            name: 'oldName',
            fieldLabel: 'Old Name'
        }, {
            xtype: 'textfield',
            name: 'newName',
            fieldLabel: 'New Name',
            maskRe: /[^:]/i,
            allowBlank: false
        }],

        buttons: [{
            text: "Rename Node",
            formBind: true,
            disabled: true,
            handler: function(btn) {
                var rnw = btn.up('renamenodewindow');
                var values = btn.up('form').getForm().getFieldValues();
                if (rnw.node.rename(values.newName))
                    EdifecsInstaller.reloadAll();
                rnw.close();
            }
        }, {
            text: "Cancel",
            handler: function(btn) {
                var rnw = btn.up('renamenodewindow');
                rnw.close();
            }
        }]
    }],

    initComponent: function() {
        this.callParent(arguments);
        this.down('form').down('displayfield').setValue(this.node.get('name'));
    }
});


