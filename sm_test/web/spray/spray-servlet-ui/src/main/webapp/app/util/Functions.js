
// FUNCTIONS.JS
// Utility functions that Ext JS does not provide, for use throughout the
// application.
// ----------------------------------------------------------------------------

Ext.define('Util.Functions', {}); // Placeholder, ignore this.

window.Functions = {
    
    // Waits for the function `predicate` to evaluate to true, and, when it
    // does, calls the function `action`. The wait time between checks is
    // configurable, but defaults to 25ms.
    waitFor: function(predicate, action, checkInterval) {
        checkInterval = checkInterval || 25;
        var waitFunc = function() {
            if (predicate())
                action();
            else
                setTimeout(waitFunc, checkInterval);
        };
        waitFunc();
    },

    // Capitalizes the first letter of a string.
    capitalize: function(str) {
        return str.charAt(0).toUpperCase() + str.slice(1);
    },

    // Clones an object or array.
    clone: function(o) {
        if (!o || 'object' !== typeof o) {
            return o;
        }
        if ('function' === typeof o.clone) {
            return o.clone();
        }
        var c = '[object Array]' === Object.prototype.toString.call(o) ? [] : {};
        var p, v;
        for (p in o) {
            if (o.hasOwnProperty(p)) {
                v = o[p];
                if (v && 'object' === typeof v) {
                    c[p] = Functions.clone(v);
                }
                else {
                    c[p] = v;
                }
            }
        }
        return c;
    },

    // Returns a new object that is a combination of two existing objects'
    // members. Members of the second object will override members of the
    // first.
    merge: function(o1, o2) {
        if (!o2 || 'object' !== typeof o2) {
            return o1;
        }
        if (!o1 || 'object' !== typeof o1) {
            return o2;
        }
        var p, result = {};
        for (p in o1) {
            if (o1.hasOwnProperty(p))
                result[p] = o1[p];
        }
        for (p in o2) {
            if (o2.hasOwnProperty(p))
                result[p] = o2[p];
        }
        return result;
    },

    // Displays an Ext JS error message popup.
    errorMsg: function(msg, title) {
        var message = msg;
        // Check if 'msg' is an error object.
        if (!msg) {
            message = "(null message)";
        } else if (msg.message) {
            try {if (!title) title = msg.constructor.name;}
            catch (e) {}
            message = msg.message;
        }
        title = title || "Error";
        Ext.MessageBox.show({
            title: title,
            msg: message,
            buttons: Ext.MessageBox.OK,
            icon: Ext.MessageBox.ERROR
        });
        if (Log) Log.error(msg);
    },

    // Displays an error message on the loading screen, or an Ext JS error
    // message popup if the loading screen is no longer visible.
    loadingErr: function(err) {
        var msg = err.message;
        var loadingEl = document.getElementById('site-loading');
        if (loadingEl) {
            loadingEl.innerHTML = "<h2 class='loading-error-header'>An Error has Occurred</h2>" +
                                  "<p class='loading-error-message'>" + msg + "</p>";
        } else {
            Functions.errorMsg(err, 'Loading Error');
        }
    },

    // Used for functions that can take either a model or its name as an
    // argument. If `obj` is a string, returns `obj`; otherwise, returns the
    // `name` property of `obj`.
    getStringOrName: function(obj) {
        if ("string" == typeof obj)
            return obj;
        else
            return obj.get('name');
    },


    // Sorter function that sorts ConfigItems by their names.
    nameSorter: function(a, b) {
        var an = a.name||a.get('name'), bn = b.name||b.get('name');
        return an == bn ? 0 : (an < bn ? -1 : 1);
    },

    // Creates an Ext.data.Model field type for an array of child models of a
    // given class. The type will automatically convert all of the items in an
    // array into the given class.
    childArrayType: function(childClassName) {
        return {
            convert: function(v, data) {
                if (!v) return [];
                var arr = Ext.Array.map(Ext.Array.from(v),
                function(item) {
                    if (item.self && item.self.getName() == childClassName)
                        return item;
                    else
                        return Ext.create(childClassName, item);
                }, this);
                arr.sort(Functions.nameSorter);
                return arr;
            },
            sortType: function(v) {
                return v;
            },
            type: 'childArray'
        };
    },

    // Creates an Ext.data.Model field type that converts timestamps into Date
    // objects.
    timestampType: function() {
        return {
            convert: function(v, data) {
                return new Date(parseInt(v,10));
            },
            sortType: function(v) {
                return v;
            },
            type: 'timestamp'
        };
    },

    // Shorthand for Ext.Error.Raise(Ext.String.format(...)).
    fmerr: function() {
        Ext.Error.raise(Ext.String.format.apply(this, arguments));
    },

    ajax: function(url, message, params, successCallback) {
        if (message) var loadingWindow = Ext.widget('progresswindow', {text: message});
        Ext.Ajax.request({
            url: url,
            params: params,
            success: function(response) {
                if (message) loadingWindow.destroy();
                if (successCallback) successCallback(response);
            },
            failure: function(response) {
                if (message) loadingWindow.destroy();
                Functions.errorMsg("Failed to connect to the server.", "Connection Error");
            }
        });
    },

    download: function(url) {
        try { // Destroy old download frames.
            Ext.destroy(Ext.get('download-frame'));
        } catch(e) {}
        Ext.DomHelper.append(document.body, {
            tag: 'iframe',
            id :'download-frame',
            frameBorder: 0,
            width : 0,
            height: 0,
            css: 'display:none;visibility:hidden;height:0px;',
            src: url
        });
    },

    mergePanelHeaders: function(panel) {
        panel.preventHeader = true;
        panel.tbar = [{
            xtype: 'component',
            data: {iconCls: panel.iconCls},
            tpl: "<div class='icon {iconCls}'>&nbsp;</div>",
            border: false
        }, {
            xtype: 'component',
            data: {title: panel.title},
            tpl: "<h2 class='panel-merged-header'>{title}</h2>",
            border: false
        }, '->'].concat(panel.tbar||[]);
    },

    statusIconHeader1: {
        name: 'iconCls',
        dataFn: function(item) {
            return item.getStatusIconCls();
        },
        visible: false
    },

    statusIconHeader2: {
        name: 'status',
        dataFn: function(item) {
            return Functions.capitalize(item.get('status'));
        },
        tpl: "<div class='icon {iconCls}' style='position: absolute;'>&nbsp;</div> <div style='margin-left: 20px;'>{status}</div>"
    }
};

