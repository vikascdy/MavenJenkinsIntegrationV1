
// VIEW: JVM Settings Grid
// Displays an editable ordered list of JVM arguments for a Node.
// ----------------------------------------------------------------------------

Ext.define('SM.view.node.JvmSettingsGrid', {
    extend: 'Ext.grid.Panel',
    alias : 'widget.jvmsettingsgrid',

    title  : 'JVM Arguments',
    iconCls: 'ico-jmsqueue',
    node   : null,
    enableDragDrop: true,
    hideHeaders   : true,
    defaultText   : '[ Double-click to edit. ]',

    columns: [{
        header: 'JVM Arguments',
        dataIndex: 'arg',
        sortable: false,
        hideable: false,
        flex: 1,
        editor: {
            xtype: 'textfield',
            allowBlank: false,
            selectOnFocus: true
        }
    }],

    tbar: [{
        xtype : 'CustomButtonGroup',
        buttonItems : [{
            text   : 'Add',
            itemId : 'add',
            icon: 'resources/images/toolbar-add.png'
        }, {
            text   : 'Remove',
            itemId : 'remove',
            icon: 'resources/images/toolbar-delete.png'
        }]
    }],

    save: function() {
        try {
            var newArgs = [];
            var me = this;
            me.getStore().data.each(function(record) {
                Log.debug("Arg: " + record.get('arg'));
                if (record.get('arg') != me.defaultText)
                    newArgs.push(record.get('arg'));
            });
            me.node.set('jvmProperties', newArgs);
            me.reload();
        } catch (err) {
            Functions.errorMsg(err.message);
        }
    },

    reload: function() {
        this.store.loadData(Ext.Array.map(this.node.get('jvmProperties'), function(p) {
            return {arg: p};
        }));
    },

    initComponent: function() {
        var node = this.node;
        var me = this;
        if (!node) {
            Log.warn("Created a JVM Settings grid with no Node!");
            this.callParent(arguments);
            return;
        }
        this.store = Ext.create('Ext.data.Store', {
            fields: [{name: 'arg', type: 'string'}],

            data: Ext.Array.map(node.get('jvmProperties'), function(p) {
                return {arg: p};
            })
        });
        // TODO: Perform permissions checks here.
        //if (UserManager.admin) {
            this.plugins = [Ext.create('Ext.grid.plugin.CellEditing', {
                clicksToEdit: 2
            })];
            this.viewConfig = {
                plugins: [
                    Ext.create('Ext.grid.plugin.DragDrop', {
                        ddGroup: 'jvmProperties',
                        dragText: 'Drag to rearrange.',
                        enableDrop: true
                    })]
            };
        //}
        Functions.mergePanelHeaders(this);
        this.callParent(arguments);
        //if (UserManager.admin) {
            // Autosave when editing or moving arguments.
            this.on('edit', function() {me.save();});
            this.getView().on('drop', function() {me.save();});
        //} else {
        //    this.down('#add').disable();
        //    this.down('#remove').disable();
        //}
    }
});

