// Utility functions that Ext JS does not provide, for use throughout the
// application.

Ext.define('Util.Functions', {}); // Placeholder, ignore this.

window.Functions = {


    // Displays an Ext JS error message popup.
    errorMsg:function (msg, title) {
        var message = msg;
        // Check if 'msg' is an error object.
        if (msg.message) {
            try {
                if (!title) title = msg.constructor.name;
            }
            catch (e) {
            }
            message = msg.message;
        }
        title = title || "Error";
        Ext.MessageBox.show({
            title  :title,
            msg    :message,
            buttons:Ext.MessageBox.OK,
            icon   :Ext.MessageBox.ERROR
        });
    }


};

