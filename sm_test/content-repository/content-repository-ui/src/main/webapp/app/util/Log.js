// LOG.JS
// Handles logging to the console. Can have a "log level" set to suppress
// unnecessary log information, and can prevent logging in incompatible
// browsers (such as IE).
//
// All logging methods take arguments in the same format as Ext.String.Format.
// This can be used to make logging statements much more concise.
// ----------------------------------------------------------------------------

Ext.define('Util.Log', {}); // Placeholder, ignore this.

window.Log = {
    NONE   : 0,
    ERROR  : 1,
    WARNING: 2,
    INFO   : 3,
    DEBUG  : 4,
    level  : 3,

    error: function(message) {
        if (Log.level < Log.ERROR) return false;
        if (console && !Ext.isIE) {

            if (arguments.length > 1)
                console.error(Ext.String.format.apply(this, arguments));
            else {
                // Check if 'message' is an error object
                if (message.message) {
                    // If it is, try to print a stacktrace.
                    if (console.exception) {
                        // Only Firebug supports this method, but it can give us a 'real'
                        // stacktrace.
                        console.exception(message);
                    } else if (message.stack) {
                        // Chrome keeps a 'stack' object on real JavaScript errors.
                        // It doesn't provide all of the data of a real stacktrace, but
                        // it's close enough.
                        console.error(message.message);
                        console.error(message.stack);
                    } else {
                        if (message.sourceClass && message.sourceMethod) {
                            // If we can't get a stack trace, Ext JS errors will at least
                            // give us the class and method where they occurred.
                            console.error("Error occurred in class '" + message.sourceClass +
                                "', method '" + message.sourceMethod + "':\n" + message.message);
                        } else {
                            console.error("Error occurred, but could not get stack trace:");
                            console.error(message.message);
                            console.dir(message);
                        }
                    }
                } else {
                    console.error(message);
                }
            }
        }
        return true;
    },

    warn: function(message) {
        if (Log.level < Log.WARNING) return false;
        if (console && !Ext.isIE) {
            if (arguments.length > 1)
                console.warn(Ext.String.format.apply(this, arguments));
            else
                console.warn(message);
        }
        return true;
    },

    info: function(message) {

        if (Log.level < Log.INFO) return false;
        if (console && !Ext.isIE) {
            if (arguments.length > 1)
                console.info(Ext.String.format.apply(this, arguments));
            else
                console.info(message);

        }
        return true;
    },

    debug: function(message) {
        if (Log.level < Log.DEBUG) return false;

        if (console && !Ext.isIE) {
            if (arguments.length > 1)
                console.debug(Ext.String.format.apply(this, arguments));
            else
                console.debug(message);
        }
        return true;
    }
};

