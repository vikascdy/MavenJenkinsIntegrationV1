
// VIEW: File Info Bar
// A sidebar that provides secondary information about the currently-selected
// file in a ContentRepositoryTree.
// ----------------------------------------------------------------------------

Ext.define("SM.view.content.FileInfoBar", {
    extend: 'Ext.panel.Panel',
    alias : 'widget.fileinfobar',

    title : 'File Information',
    iconCls: 'ico-properties',

    defaultType: 'displayfield',
    defaults: {
        labelAlign: 'top',
        margin: '8 0'
    },
    bodyPadding: 16,
    autoScroll : true,

    loadInfoFor: function(node) {
        var me = this;
        me.removeAll();
        Functions.jsonCommand("UI Service", "content.fileProperties", {
            path: node.get('id')
        }, {
            success: function(response) {
                Ext.iterate(response, function(key, value) {
                    me.add({
                        xtype: 'displayfield',
                        name : key,
                        fieldLabel: JCRLocale.fileInfoLabels.get(key) ? JCRLocale.fileInfoLabels.get(key) : key,
                        value: JCRLocale.fileInfoValues.get(value) ? JCRLocale.fileInfoValues.get(value) : value
                    });
                });
            }
        });
    }
});

