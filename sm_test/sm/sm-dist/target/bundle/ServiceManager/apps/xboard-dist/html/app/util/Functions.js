// FUNCTIONS.JS
// Utility functions that Ext JS does not provide, for use throughout the
// application.
// ----------------------------------------------------------------------------

Ext.define('Util.Functions', {}); // Placeholder, ignore this.

window.Functions = {

    jsonCommand : function(service, command, params, callbacks) {
        callbacks = callbacks || {};
        Ext.Ajax.request({
            url : JSON_SERVICE_SERVLET_PATH + encodeURIComponent(service) + "/" + encodeURIComponent(command),
            params : {
                data : Ext.encode(params)
            },
            success : function(result) {
                var json = Ext.decode(result.responseText);
                if (json.success === false) {
                    if (callbacks.failure) {
                        callbacks.failure(json);
                    } else {
                        Functions.errorMsg("<b>Error occurred in command '" + command + "':</b><br /><br />" + "<code>" + json.errorClass
                                + "</code><br /><br />" + json.error, "Command Error");
                    }
                } else {
                    if (callbacks.success)
                        callbacks.success(json.data);
                }
            },
            failure : function(result) {
                if (callbacks.failure) {
                    callbacks.failure({
                        success : false,
                        error : result.statusText,
                        errorClass : "HTTP " + result.status
                    });
                } else {
                    Functions.errorMsg("<b>Failed to send command '" + command + "':</b><br /><br />" + "<code>HTTP " + result.status + " - "
                            + result.statusText + "</code><br /><br />" + "Command Error");
                }
            }
        });
    },
    
    setupCommand : function(command, params, callbacks) {
        callbacks = callbacks || {};
        Ext.Ajax.request({
            url : JSON_SERVLET_PATH + "setup/" + encodeURIComponent(command),
            params : {
                data : Ext.encode(params)
            },
            success : function(result) {
                var json = Ext.decode(result.responseText);
                if (json.success === false) {
                    if (callbacks.failure) {
                        callbacks.failure(json);
                    } else {
                        Functions.errorMsg("<b>Error occurred in command '" + command + "':</b><br /><br />" + "<code>" + json.errorClass
                                + "</code><br /><br />" + json.error, "Command Error");
                    }
                } else {
                    if (callbacks.success)
                        callbacks.success(json.data);
                }
            },
            failure : function(result) {
                if (callbacks.failure) {
                    callbacks.failure({
                        success : false,
                        error : result.statusText,
                        errorClass : "HTTP " + result.status
                    });
                } else {
                    Functions.errorMsg("<b>Failed to send command '" + command + "':</b><br /><br />" + "<code>HTTP " + result.status + " - "
                            + result.statusText + "</code><br /><br />" + "Command Error");
                }
            }
        });
    },

    redirectToLogin : function() {
        var pathname = window.location.pathname;
        var hashString = window.location.hash;
        window.location.href = '/esm/login/?redirectURL=' + pathname + hashString;
    },

    // Waits for the function `predicate` to evaluate to true, and, when it
    // does, calls the function `action`. The wait time between checks is
    // configurable, but defaults to 25ms.
    waitFor : function(predicate, action, checkInterval) {
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
    capitalize : function(str) {
        return str.charAt(0).toUpperCase() + str.slice(1);
    },

    // Uncapitalizes the first letter of a string.
    uncapitalize : function(str) {
        return str.charAt(0).toLowerCase() + str.slice(1);
    },

    // Clones an object or array.
    clone : function(o) {
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
                } else {
                    c[p] = v;
                }
            }
        }
        return c;
    },

    // Returns a new object that is a combination of two existing objects'
    // members. Members of the second object will override members of the
    // first.
    merge : function(o1, o2) {
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
    errorMsg : function(msg, title) {
        var message = msg;
        // Check if 'msg' is an error object.
        if (!msg) {
            message = "(null message)";
        } else if (msg.message) {
            try {
                if (!title)
                    title = msg.constructor.name;
            } catch (e) {
            }
            message = msg.message;
        }
        title = title || "Error";
        Ext.MessageBox.show({
            title : title,
            msg : message,
            buttons : Ext.MessageBox.OK,
            icon : Ext.MessageBox.ERROR
        });
        if (Log)
            Log.error(msg);
    },

    // Used for functions that can take either a model or its name as an
    // argument. If `obj` is a string, returns `obj`; otherwise, returns the
    // `name` property of `obj`.
    getStringOrName : function(obj) {
        if ("string" == typeof obj)
            return obj;
        else
            return obj.get('name');
    },

    // Sorter function that sorts ConfigItems by their names.
    nameSorter : function(a, b) {
        var an = a.name || a.get('name'), bn = b.name || b.get('name');
        return an == bn ? 0 : (an < bn ? -1 : 1);
    },

    // Creates an Ext.data.Model field type for an array of child models of a
    // given class. The type will automatically convert all of the items in an
    // array into the given class.
    childArrayType : function(childClassName) {
        return {
            convert : function(v, data) {
                if (!v)
                    return [];
                var arr = Ext.Array.map(Ext.Array.from(v), function(item) {
                    if (item.self && item.self.getName() == childClassName)
                        return item;
                    else
                        return Ext.create(childClassName, item);
                }, this);
                arr.sort(Functions.nameSorter);
                return arr;
            },
            sortType : function(v) {
                return v;
            },
            type : 'childArray'
        };
    },

    // Creates an Ext.data.Model field type that converts timestamps into Date
    // objects.
    timestampType : function() {
        return {
            convert : function(v, data) {
                return new Date(parseInt(v, 10));
            },
            sortType : function(v) {
                return v;
            },
            type : 'timestamp'
        };
    },

    // Shorthand for Ext.Error.Raise(Ext.String.format(...)).
    fmerr : function() {
        Ext.Error.raise(Ext.String.format.apply(this, arguments));
    },

    ajax : function(url, message, params, successCallback) {
        if (message)
            var loadingWindow = Ext.widget('progresswindow', {
                text : message
            });
        Ext.Ajax.request({
            url : url,
            params : params,
            success : function(response) {
                if (message)
                    loadingWindow.destroy();
                if (successCallback)
                    successCallback(response);
            },
            failure : function(response) {
                if (message)
                    loadingWindow.destroy();
                Functions.errorMsg("Failed to connect to the server.", "Connection Error");
            }
        });
    },

    download : function(url) {
        try { // Destroy old download frames.
            Ext.destroy(Ext.get('download-frame'));
        } catch (e) {
        }
        Ext.DomHelper.append(document.body, {
            tag : 'iframe',
            id : 'download-frame',
            frameBorder : 0,
            width : 0,
            height : 0,
            css : 'display:none;visibility:hidden;height:0px;',
            src : url
        });
    },

    mergePanelHeaders : function(panel) {
        panel.preventHeader = true;
        panel.tbar = [ {
            xtype : 'component',
            data : {
                iconCls : panel.iconCls
            },
            tpl : "<div class='icon {iconCls}'>&nbsp;</div>",
            border : false
        }, {
            xtype : 'component',
            data : {
                title : panel.title
            },
            tpl : "<h2 class='panel-merged-header'>{title}</h2>",
            border : false
        }, '->' ].concat(panel.tbar || []);
    },

    statusIconHeader1 : {
        name : 'iconCls',
        dataFn : function(item) {
            return item.getStatusIconCls();
        },
        visible : false
    },

    statusIconHeader2 : {
        name : 'status',
        dataFn : function(item) {
            return Functions.capitalize(item.get('status'));
        },
        tpl : "<div class='icon {iconCls}' style='position: absolute;'>&nbsp;</div> <div style='margin-left: 20px;'>{status}</div>"
    },

    // Creates a many-to-many relationship that emulates an Ext JS one-to-many
    // relationship. Note that this depends on the existence of an "association"
    // model that links the two models, and it expects that the association
    // model is accessed in a certain way (through a REST proxy at a given URL,
    // with the parentKey as a parameter).
    createManyToMany : function(cls, options) {
        if (!options.associationName || !options.associationCls || !options.targetName || !options.targetCls || !options.parentForeignKey
                || !options.associationUrl)
            Ext.Error.raise("Missing required parameter(s) for 'createManyToMany()'.");
        var aStoreName = options.associationName + "Store";
        var aFuncName = options.associationName + "s";
        var tStoreName = options.targetName + "Store";
        var tFuncName = options.targetName + "s";
        var parentKey = options.parentForeignKey;
        var targetKey = options.targetForeignKey || (options.targetName + "Id");
        cls[aStoreName] = null;
        cls[tStoreName] = null;
        cls[aFuncName] = function() {
            if (!this[aStoreName]) {
                var extraParams = {};
                extraParams[parentKey] = this.get('id');
                var proxy = {
                    type : 'rest',
                    url : options.associationUrl,
                    extraParams : extraParams,
                    reader : {
                        type : 'json',
                        root : 'results'
                    }
                };
                var config = {
                    model : options.associationCls,
                    proxy : proxy,
                    autoLoad : true
                };
                this[aStoreName] = Ext.create('Ext.data.Store', config);
            }
            return this[aStoreName];
        };
        cls[tFuncName] = function() {
            if (!this[tStoreName]) {
                var associations = this[aFuncName]();
                var filter = {
                    filterFn : function(obj) {
                        var match = false;
                        associations.each(function(a) {
                            if (a.get(targetKey) == obj.get("id")) {
                                match = true;
                                return false;
                            }
                        });
                        return match;
                    }
                };
                var config = {
                    model : options.targetCls,
                    filters : [ filter ],
                    remoteFilter : false,
                    autoLoad : true
                };
                this[tStoreName] = Ext.create('Ext.data.Store', config);
            }
            return this[tStoreName];
        };
        return cls;
    },

    generateCategoryData : function(category) {

        Ext.define('Category', {
            extend : 'Ext.data.Model',
            fields : [ {
                name : 'text',
                type : 'string'
            }, {
                name : 'children',
                type : 'auto'
            }, {
                name : 'leaf',
                type : 'boolean'
            } ]
        });
        var treeData = [];
        var categoryStore = Ext.create('Ext.data.Store', {
            model : 'Security.model.Permission',
            autoLoad : true,
            data : category,
            groupField : 'categoryCanonicalName'
        });

        Ext.each(categoryStore.getGroups(), function(group) {
            var categoryName = group.name;
            var type = group.children;
            var permissionTypeStore = Ext.create('Ext.data.Store', {
                model : 'Security.model.Permission',
                groupField : 'typeCanonicalName',
                data : type
            });
            var typeGroup = permissionTypeStore.getGroups();
            // console.log(typeGroup);
            // var typeArray = [];
            // Ext.each(typeGroup, function(type) {
            // var subTypeArray = [];
            // Ext.each(type.children, function(subType) {
            // subTypeArray.push({
            // 'type':type.name,
            // 'subTypes':subType.get('subTypeCanonicalName'),
            // 'permissions':subType.get('canonicalName')
            // })
            // });
            // // typeArray.push({
            // // 'name':type.name,
            // // 'items':subTypeArray
            // //
            // // })
            // typeArray=subTypeArray;
            // });
            treeData.push({
                'name' : categoryName,
                'items' : typeGroup

            });
        });
        return treeData;
    },
    
    getSiteLogo : function(callback){    	
    	 Functions.jsonCommand("xboard-portal-service", "getLogo", {
         }, {
         success : function(response) {
             Ext.callback(callback, this, [response]);
         },
         failure : function(response) {
        	 Ext.callback(callback, this, [null]);
         }
     });
    	 
    },

    compareStringNoCase: function(a,b){
        if(a.toUpperCase() < b.toUpperCase())  {
            return -1;
        }
        else if (a.toUpperCase()=== b.toUpperCase()){
            return 0;
        }
        else {
            return 1;
        }
    }
    
};
