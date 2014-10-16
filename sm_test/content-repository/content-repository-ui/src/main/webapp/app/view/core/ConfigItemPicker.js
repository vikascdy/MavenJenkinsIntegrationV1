
// VIEW: Config Item Picker
// A SplitButton that works like a ComboBox; it allows the user to pick from an
// arbitrary list of ConfigItems, retrieved using ConfigItem.getChildrenWith
// and the `searchCriteria` and `parentItem` config options.
// ----------------------------------------------------------------------------

Ext.define('SM.view.core.ConfigItemPicker', {
    extend: 'Ext.button.Split',
    alias : 'widget.configitempicker',
    defaultText: "[ No Item Selected ]",
    includeNull: false,

    listeners: {
        click: function(button) {
            button.showMenu();
        }
    },

    showMenu: function() {
        this.callParent(arguments);
        this.menu.setWidth(this.getWidth());
        this.menu.showBy(this);
    },

    setItem: function(item) {
        if (item === null) {
            this.selectedItem = null;
            this.setIconCls('ico-unknown');
            this.setText(this.defaultText);
        } else if (this.hasItem(item)) {
            this.selectedItem = item;
            this.setIconCls(item.getIconCls());
            this.setText(item.get('name'));
        } else {
            Functions.fmerr("Cannot select the item {0}; item is not in ConfigItemPicker.", item);
        }
    },

    getItem: function() {
        return this.selectedItem;
    },

    hasItem: function(item) {
        var found = false;
        Ext.each(this.items, function(i) {
            if (i === item) {
                found = true;
                return false;
            }
        });
        return found;
    },

    resetSelection: function() {
        if (this.items.length > 0 && !this.includeNull)
            this.setItem(this.items[0]);
        else
            this.setItem(null);
    },

    initComponent: function() {
        if (!this.searchCriteria)
            Ext.Error.raise("Cannot create a ConfigItemPicker without a searchCriteria option.");
        this.parentItem = this.parentItem || ConfigManager.config;
        if (!this.parentItem)
            Ext.Error.raise("Cannot create a ConfigItemPicker without a loaded config file.");
        this.callParent(arguments);
        this.reload();
    },

    reload: function() {
        var button = this;
        this.items = this.parentItem.getChildrenWith(this.searchCriteria);
        this.items.sort(function(a, b) {
            var na = a.get('name'), nb = b.get('name');
            return na == nb ? 0 : na < nb ? -1 : 1;
        });
        var menuEntries = Ext.Array.map(this.items, function(item) {
            return {
                text: item.get('name'),
                iconCls: item.getIconCls(),
                width: '100%',
                handler: function() {
                    button.setItem(item);
                    button.fireEvent("itemselect", button, item);
                }
            };
        });
        if (this.includeNull) {
            menuEntries = [{
                text: this.defaultText,
                iconCls: 'ico-unknown',
                width: '100%',
                handler: function() {
                    button.setItem(null);
                    button.fireEvent("itemselect", button, null);
                }
            }].concat(menuEntries);
        }
        this.menu = Ext.create('Ext.menu.Menu', {items: menuEntries});
        if (!this.hasItem(this.selectedItem) && !(this.selectedItem === null && this.includeNull))
            this.resetSelection();
    }
});

