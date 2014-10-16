
// VIEW: Edit Config Window 
// A popup window for editing basic attributes of an existing config file.
// ----------------------------------------------------------------------------

Ext.define('SM.view.core.EditConfigWindow', {
    extend: 'Ext.window.Window',
    alias : 'widget.editconfigwindow',

    title   : 'Edit Configuration',
    iconCls : 'ico-edit',
    layout  : 'fit',
    autoShow: true,
    config  : null,
    width   : 460,
    height  : 290,
    resizable:false,
    buttons: [{
        text: "Save",
        formBind:true,
        handler: function(btn) {
            var ecw = btn.up('editconfigwindow');
            var form = ecw.down('configform');
            form.save();
            ecw.close();
        }
    }, {
        text: "Cancel",
        handler: function(btn) {
            var ecw = btn.up('editconfigwindow');
            ecw.close();
        }
    }],

    initComponent: function() {
        this.items = [{
            xtype : 'configform',
            flex  : 1,
            bodyPadding: 20,
            config: this.config
        }];

        this.callParent(arguments);
    }
});

