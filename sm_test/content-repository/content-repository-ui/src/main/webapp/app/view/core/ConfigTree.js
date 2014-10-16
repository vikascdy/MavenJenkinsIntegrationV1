// VIEW: Config Tree
// The Tree widget that displays the Configuration Overview.
// ----------------------------------------------------------------------------

Ext.define('SM.view.core.ConfigTree', {
    extend: 'Ext.tree.Panel',
    mixins: ['SM.mixin.PersistentTreeReloadMixin'],
    alias : 'widget.configtree',

    title : 'Configuration',
    store : 'ConfigTreeStore',
    rootVisible: false,
    enableDragDrop: true,
    columnView: false,
    bodyPadding:'0',
    scroll: 'vertical',

    viewConfig: {
        // The drag/drop plugin has been moved to initComponent().
        listeners: {
            'viewready' : function() {
                this.getSelectionModel().select(0);
            },
            'afterrender' : function(view) {
                // Pass key events from the view element to the tree, to allow
                // CoreController to see key events from the tree.
                var tree = view.up('configtree');
                view.getEl().on('keydown', function(e) {
                    tree.fireEvent('keydown', tree, e);
                });
                view.getEl().on('keyup', function(e) {
                    tree.fireEvent('keyup', tree, e);
                });
            }
        }
    },

    tools: [{
        type: 'expand',
        tooltip:'Expand All',
        handler: function(e, target, owner, tool) {
            owner.up('configtree').getRootNode().collapse(true);
            owner.up('configtree').getRootNode().expand(true);
        }
    }, {
        type: 'collapse',
        tooltip:'Collapse All',
        handler: function(e, target, owner, tool) {
            Ext.each(owner.up('configtree').getRootNode().childNodes, function(n) {
                n.collapse(true);
            });
        }
    }, {
        type: 'refresh',
        tooltip:'Refresh',
        handler: function(e, target, owner, tool) {
            owner.up('configtree').reload();
        }
    }],

    extraColumns: [{
        xtype:     'treecolumn',
        text:      'Item',
        dataIndex: 'text',
        flex:       1,
        renderer:  function(value, metadata, record) {
            var tooltip = record.data.type;
            if (tooltip == 'Cluster')
                tooltip = 'Configuration';
            metadata.tdAttr = 'data-qtip=' + Functions.capitalize(tooltip);
            return value;
        }
    }, {
        text:      'Status',
        dataIndex: 'status',
        width:      55,
        sortable:false,
        renderer:  function(value, metadata, record) {
            metadata.tdAttr = 'data-qtip=' + Functions.capitalize(value);
            if (!value) return "";
            return Ext.String.format("<div class='icon {0}'>",
                record.get('object').getStatusIconCls());
        }
    }, {
        text:     'Errors',
        width:     55,
        sortable:false,
        renderer: function(value, metadata, record) {
            try {
                var errors = record.get('object').getAllErrors();
                if (errors.length > 0) {
                    var level = 'warning';
                    Ext.each(errors, function(err) {
                        var sev = err.get('severity');
                        if ((level == 'warning' && sev == 'error' || sev == 'fatal') ||
                            (level == 'error' && sev == 'fatal'))
                            level = sev;
                    });
                    var iconTable = {
                        warning: 'ico-warning',
                        error:   'ico-error',
                        fatal:   'ico-fatal'
                    };
                    var iconCls = iconTable[level] || 'ico-unknown';
                    return Ext.String.format("<div class='icon {0}' style='position: absolute;' title='{1}'>&nbsp;</div> <div style='padding-left: 18px;'>{2}</div>",
                        iconCls, Functions.capitalize(level), errors.length);
                } else {
                    return '';
                }
            } catch (e) {
                return "<div class='icon ico-unknown'>&nbsp;</div>";
            }
        }
    }],

    initComponent: function() {
        // Only allow drag-and-drop if the user is an administrator and the
        // `enableDragDrop` config is set to true.
        // TODO: Perform an actual permissions test here.
        if (/*UserManager.admin && */this.enableDragDrop) {
            this.viewConfig.plugins = [
                {
                    ptype: 'treeviewdragdrop',
                    pluginId: 'draganddrop',
                    ddGroup: 'tree',
                    appendOnly: true
                }
            ];
        }
        // Only show extra columns if the `columnView` config is set to true.
        if (this.columnView)
            this.columns = this.extraColumns;
        this.callParent(arguments);
        /*if (!UserManager.admin && this.down('toolbar'))
            this.down('toolbar').hide();*/
        this.getStore().load({
            callback: function() {
            	this.getRootNode().collapse(true);
                this.getRootNode().expand(true);
            },
            scope: this
        });
    },

    getSelectedItem: function() {
        var records = this.getSelectionModel().getSelection();
        if (records.length > 0)
            return records[0].get('object');
    },

    getSelectedItemOfType: function(type) {
        var selected = this.getSelectedItem();
        while (selected && selected.getType() != type)
            selected = selected.getParent();
        return selected || null;
    }
});

