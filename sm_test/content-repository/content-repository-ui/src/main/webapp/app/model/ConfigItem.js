
// ABSTRACT MODEL: Config Item
// Handles various tasks common to all items in a config file (Servers, Nodes,
// etc.) Any model that would be included in a config file should inherit from
// this.
// ----------------------------------------------------------------------------

Ext.define('SM.model.ConfigItem', {
    extend: 'Ext.data.Model',

    parentItem: null,
    
    getType: function() {
        // Override me!
        Functions.fmerr(
            "No type name defined for model '{0}'. Did you forget to override a method?",
            this.self.getName()
        );
    },

    shouldBeA: function(type) {
        if ('string' == typeof type) {
            if (this.getType() != type)
                Functions.fmerr(
                    "Expected a {0}, but got a {1}.", type, this.getType());
        } else if (type instanceof Array) {
            var found = false;
            Ext.each(type, function(t) {
                if (this.getType() == t) {
                    found = true;
                    return false;
                }
            }, this);
            if (!found) {
                var typeStr = type[0];
                for (var i=1; i<type.length; i++) {
                    typeStr += ' or ' + type[i];
                }
                Functions.fmerr(
                    "Expected a {0}, but got a {1}.", typeStr, this.getType());
            }
        } else {
            Ext.Error.raise("ConfigItem.shouldBeA takes either a String or an Array.");
        }
    },

    shouldNotAlreadyHave: function(child) {
        var childAlreadyExists = false;
        this.eachChild(function(ec) {
            if (ec.get('name') == child.get('name')) {
                childAlreadyExists = true;
                return false;
            }
        });
        if (childAlreadyExists) {
            Functions.fmerr(
                'The {0} "{1}" is already present on the {2} "{3}".',
                child.getType(), child.get('name'), this.getType(), this.get('name'));
        }
    },

    getParent: function() {return this.parentItem;},

    getConfig: function() {
        if (this.getType() == 'Config')
            return this;
        else {
            var parent = this.getParent();
            if (!parent) return null;
            return parent.getConfig();
        }
    },

    getIdSegment: function() {return this.get('name');},

    hasAncestor: function(ancestor) {
        if (this === ancestor) return true;
        var parentItem = this.getParent();
        return parentItem ? parentItem.hasAncestor(ancestor) : false;
    },

    appendChild: function(child) {
        // Override me!
        Functions.fmerr(
            "appendChild() undefined for model '{0}'. Did you forget to override a method?",
            this.self.getName()
        );
    },

    removeChild: function(child) {
        // Override me!
        Functions.fmerr(
            "removeChild() undefined for model '{0}'. Did you forget to override a method?",
            this.self.getName()
        );
    },

    getChildren: function() {
        // Override me!
        Functions.fmerr(
            "getChildren() undefined for model '{0}'. Did you forget to override a method?",
            this.self.getName()
        );
    },

    getChildrenRecursively: function() {
        var children = this.getChildren();
        var grandchildren = [];
        Ext.each(children, function(child) {
            grandchildren = grandchildren.concat(child.getChildrenRecursively());
        });
        return children.concat(grandchildren);
    },
    
    eachChild: function(func, scope) {
        Ext.each(this.getChildren(), func, scope);
    },

    getChildrenWith: function(data) {
        var children = [];
        var type = data.type;
        var dataWithoutType = Functions.clone(data);
        dataWithoutType.type = undefined;
        this.eachChild(function(child) {
            var found = true;
            if (type && child.getType() != type)
                found = false;
            else for (var key in dataWithoutType) {
                if (dataWithoutType.hasOwnProperty(key)) {
                    var value, test = dataWithoutType[key];
                    // First check if the key is a model field.
                    if (child.data.hasOwnProperty(key)) {
                        value = child.data[key];
                    }
                    // ...Otherwise, check if it is a property of the object itself.
                    else if (key in child) {
                        value = child[key];
                        // If the property is a function, call it.
                        if ('function' == typeof value)
                            value = value.apply(child);
                    }
                    // If the key doesn't exist at all, it's not a match.
                    if (test != value) {
                        found = false;
                        break;
                    }
                }
            }
            if (found)
                children.push(child);
            else if (!type || child.getType() != type)
                Ext.each(child.getChildrenWith(data), function(c) {children.push(c);});
        });
        return children;
    },

    showPropertiesWindow: function() {
        // Override me!
        Functions.fmerr("No properties window is defined for type '{0}'.", this.getType());
    },

    getInfoPane: function() {
        // Override me!
        return {
            xtype: 'component',
            html: Ext.String.format(
                "No info pane is defined for type '{0}'.", this.getType())
        };
    },

    getContextMenu: function() {
        // Override me!
        return undefined;
    },

    getIconCls: function() {
        // Override me!
        return 'ico-placeholder';
    },

    getStatusIconCls: function() {
        var iconTable = {
            active:  'ico-active',
            offline: 'ico-offline',
            'new':   'ico-New',
            unassociated: 'ico-offline',
            error:   'ico-warning',
            unknown: 'ico-unknown'
        };
        return iconTable[this.get('status')] || 'ico-none';
    },

    getErrors: function() {
        // Override me!
        return [];
    },

    getAllErrors: function() {
        var errors = this.getErrors();
        var store = Ext.getStore('ServerErrorStore');
        store.data.each(function(err) {
            if (err.get('sourceId') == this.getId())
                errors.push(err);
        }, this);
        Ext.each(ConfigManager.getValidationErrors(), function(err) {
            if (err.get('sourceId') == this.getId())
                errors.push(err);
        }, this);
        return errors;
    },

    newError: function(severity, type, message) {
        return Ext.create('SM.model.ErrorLog', {
            sourceId: this.getId(),
            severity: severity,
            type: type,
            message: message
        });
    },

    normalize: function(parentItem) {
        parentItem = parentItem || this.getParent();
        this.parentItem = parentItem;
        if (parentItem) {
            this.set('id', parentItem.getId() + ':' + this.getIdSegment());
            if (this.get('status') !== undefined && (
                    parentItem.get('status') == 'unassociated' ||
                    parentItem.get('status') == 'new' ||
                    (parentItem.get('status') == 'offline' && this.get('status') == 'active')
                )) {
                this.set('status', (parentItem.get('status')));
            }
        } else {
            this.set('id', this.getIdSegment());
        }
        var me = this;
        this.eachChild(function(c) {c.normalize(me);});
    },

    toString: function() {
        return Ext.String.format("[{0}: {1}]", this.getType(), this.get('name'));
    },

    toJSON: function() {
        // Override me!
        Functions.fmerr(
            "toJSON() undefined for model '{0}'. Did you forget to override a function?",
            this.self.getName()
        );
    },

    visualize: function(indent) {
        indent = indent || 0;
        var output = "";
        for (var i=0; i<indent; i++) output += "    ";
        output += this.toString();
        output += "\n";
        this.eachChild(function(c) {output += c.visualize(indent+1);});
        return output;
    },

    askToDelete: function() {
        var me = this;
        Ext.Msg.confirm(
            "Delete " + me.getType() + "?",
            Ext.String.format("Are you sure you want to delete the {0} '{1}'? All configuration data will be lost!",
                me.getType(), me.get('name')),
            function(btn) {
                if (btn == 'yes') {
                    if (me.getParent() && me.getParent().removeChild(me))
                        SM.reloadAll();
                    else
                        Functions.errorMsg("Failed to delete " + me.getType() + ".");
                }
            }
        );
    }
});

