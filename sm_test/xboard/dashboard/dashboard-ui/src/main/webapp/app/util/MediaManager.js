Ext.define('Util.MediaManager', {});

window.MediaManager = {

    showTextEditor : function(portlet, e, callback) {
        var textEditorWindow = Ext.widget({xtype:'texteditorwidnow',portlet:portlet});
        textEditorWindow.show(e);
        Ext.callback(callback, this, []);
    },

    showImageGallery : function(portlet, e, callback) {
        var newImageWindow = Ext.widget({xtype:'newimagewindow',portlet:portlet});
        newImageWindow.show(e);
        Ext.callback(callback, this, []);
    },

    showCodeEmbeddedWindow : function(portlet, e, callback) {
        var embeddedCodeWindow = Ext.widget({xtype:'embeddedcodewindow',portlet:portlet});
        embeddedCodeWindow.show(e);
        Ext.callback(callback, this, []);
    }


};