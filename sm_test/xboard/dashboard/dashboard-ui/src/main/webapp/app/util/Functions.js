Ext.define('Util.Functions', {});

window.Functions = {

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
    errorMsg: function(title, msg) {
        var message = msg;
        // Check if 'msg' is an error object.
        if (!msg) {
            message = "(null message)";
        } else if (msg.message) {
            try {
                if (!title) title = msg.constructor.name;
            }
            catch (e) {
            }
            message = msg.message;
        }
        title = title || "Error";
        Ext.MessageBox.show({
            title: title,
            msg: message,
            buttons: Ext.MessageBox.OK,
            icon: Ext.MessageBox.ERROR
        });
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

    cloneStore : function(source) {

        var target = Ext.create('Ext.data.Store', {
            model: source.model
        });

        Ext.each(source.getRange(), function (record) {
            var newRecordData = Ext.clone(record.copy().data);
            var model = new source.model(newRecordData, newRecordData.id);

            target.add(model);
        });

        return target;
    }


};

