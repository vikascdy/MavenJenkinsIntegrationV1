// MIXIN: Status Control Mixin
// Used for objects with a controllable status--that is, objects with "start"
// and "stop" actions. In order for this to work, the `startUrl` and `stopUrl`
// properties MUST be defined for the implementing object!
// ----------------------------------------------------------------------------

Ext.define('SM.mixin.StatusControlMixin', {
    startUrl : null, // Define me!
    stopUrl : null, // Define me!

    _successPopup : function(action) {
        var me = this;
        Ext.MessageBox.show({
            title : Ext.String.format('{0} {1}', me.getType(), action),
            msg : Ext.String.format("Successfully {0} {1} '{2}'.", action.toLowerCase(), me.getType(), me.get('name')),
            buttons : Ext.MessageBox.OK,
            icon : 'ext-mb-info'
        });
    },

    _failurePopup : function(action) {
        var me = this;
        Ext.MessageBox.show({
            title : Ext.String.format('{0} {1} Failed', me.getType(), action),
            msg : Ext.String.format("Failed to {0} {1} '{2}'.", action.toLowerCase(), me.getType(), me.get('name')),
            buttons : Ext.MessageBox.OK,
            icon : 'ext-mb-error'
        });
    },

    _redundantPopup : function(action) {
        var me = this;
        Ext.MessageBox.show({
            title : Ext.String.format('{0} Aleady {1}', me.getType(), action),
            msg : Ext.String.format(" The {1} '{2}' is already {0}.", action.toLowerCase(), me.getType(), me.get('name')),
            buttons : Ext.MessageBox.OK,
            icon : 'ext-mb-warning'
        });
    },

    start : function(callback, scope) {
        if (this.get('status') == 'active') {
            this._redundantPopup('Started');
            return false;
        }
        var me = this;
        Ext.Ajax.request({
            url : me.startUrl || Ext.Error.raise("startUrl must be defined for type " + me.getType() + "!"),
            params : {
                data : Ext.encode({
                    'id' : me.getId()
                })
            },
            success : function(response) {
                var json = Ext.decode(response.responseText);
                if (json.success === true) {
                    me._successPopup('Started');
                } else {
                    me._failurePopup('Start');
                }
                SM.reloadAllWithStatuses();
                Ext.callback(callback, scope);
            },
            failure : function(response) {
                me._failurePopup('Start');
            }
        });
        return true;
    },

    stop : function(callback, scope) {
        if (this.get('status') == 'offline') {
            this._redundantPopup('Stopped');
            return false;
        }
        var me = this;
        Ext.Ajax.request({
            url : me.stopUrl || Ext.Error.raise("stopUrl must be defined for type " + me.getType() + "!"),
            params : {
                data : Ext.encode({
                    'id' : me.getId()
                })
            },
            success : function(response) {
                var json = Ext.decode(response.responseText);
                if (json.success === true)
                    me._successPopup('Stopped');
                else
                    me._failurePopup('Stop');
                SM.reloadAllWithStatuses();
                Ext.callback(callback, scope);
            },
            failure : function(response) {
                me._failurePopup('Stop');
            }
        });
        return true;
    }
});
