
// VIEW: Log Viewer Pane
// A specialized Info Pane that displays log files for Services.
// ----------------------------------------------------------------------------

Ext.define('SM.view.log.LogViewerPane', {
    extend : 'Ext.container.Container',
    alias  : 'widget.logviewerpane',
    cluster: null,
    ieRange: null,

    layout : {
        type: 'vbox',
        align: 'stretch'
    },
    padding: 16,

    writeToFrame: function() {
        var me = this;
        Functions.waitFor(
        function() {return Ext.get('logframe');},
        function() {
            var frame = Ext.get('logframe').dom.contentWindow;
            frame.document.write("<body style='font-family: Consolas, monospace; font-size: 10pt; white-space: pre'>" +
                me.logText + "</body>");
            frame.scrollTo(0, frame.document.body.scrollHeight);
        });
    },

    doSearch: function(query) {
        if (!query) return;
        var iframeEl = Ext.get('logframe');
        if (!iframeEl) return;
        var frame = iframeEl.dom.contentWindow;
        if (Ext.isIE) {
            // -- Internet Explorer --
            // Incremental search just won't work in IE; it switches the focus
            // to the iframe, and then loses its place in the search if focus
            // is switched back. See "findNext" and "findPrev" for the IE
            // implementation.
        } else {
            // -- Firefox, Chrome --
            // Most non-IE browsers support window.find(), which invokes the
            // browser's native Find functionality.
            var selection = frame.getSelection();
            if (selection) selection.removeAllRanges();
            frame.find(query, false, true); // 2nd arg means case-sensitive, 3rd means search in reverse.
        }
    },

    findNext: function(query) {
        if (!query) return;
        var iframeEl = Ext.get('logframe');
        if (!iframeEl) return;
        var frame = iframeEl.dom.contentWindow;
        if (Ext.isIE) {
            this.ieRange = this.ieRange || frame.document.body.createTextRange();
            var textRange = this.ieRange;
            if (textRange.findText(query, -1, 4)) { // http://msdn.microsoft.com/en-us/library/ms536422(v=vs.85).aspx
                                                    // 2nd arg (-1) means match in reverse, 3rd arg (4) means case-sensitive.
                textRange.select();
                textRange.collapse(true);
            }
        } else {
            frame.find(query, false, true);
        }
    },

    findPrev: function(query) {
        if (!query) return;
        var iframeEl = Ext.get('logframe');
        if (!iframeEl) return;
        var frame = iframeEl.dom.contentWindow;
        if (Ext.isIE) {
            this.ieRange = this.ieRange || frame.document.body.createTextRange();
            var textRange = this.ieRange;
            if (textRange.findText(query, 1, 4)) {
                textRange.select();
                textRange.collapse(false);
            }
        } else {
            frame.find(query, false, false);
        }
    },

    initComponent: function(config) {
        var pane    = this;
        var name    = this.logFile.get('name');
        var parts	= this.logFile.get('id').split(':');
//      var service = ConfigManager.searchConfigById(this.logFile.get('id'));
        var service = SM.viewport.down('servicemanagerpage').currentRecord;
        var text    = this.logText;
        if (!this.logFile || this.logText === undefined)
            Ext.Error.raise("A LogViewerPane requires the logFile and logText params.");
        this.items = [{
            xtype: 'component',
            cls: 'info-pane-header',
            border: false,
            data: {
                name:        name,
                serviceName: parts[parts.length-2]
            },
            tpl: "<h2 style='padding-left:5px;'>Log File: {name} ({serviceName})</h2>"
        }, {
            xtype:  'panel',
            title:  name,
            layout: 'fit',
            tools: [{
                type: 'refresh',
                handler: function(e, target, owner, tool) {
                    pane.fireEvent('refresh', pane, pane.logFile);
                }
            }],
            tbar: [{
                xtype: 'button',
                text: 'Back',
                handler: function() {
                    pane.up('servicemanagerpage').showInfoPaneFor(service);
                }
            }, '->', {
                xtype: 'textfield',
                itemId: 'searchfield',
                emptyText: 'Search',
                enableKeyEvents: true,
                listeners: {
                    keyup: function(field, e) {
                        if (e.isNavKeyPress()) return;
                        var text = field.getValue();
                        if (text) pane.doSearch(text);
                    }
                }
            }, {
                xtype: 'button',
                text: 'Next',
                handler: function(btn) {
                    var text = btn.up('toolbar').down('#searchfield').getValue();
                    if (text) pane.findNext(text);
                }
            }, {
                xtype: 'button',
                text: 'Prev',
                handler: function(btn) {
                    var text = btn.up('toolbar').down('#searchfield').getValue();
                    if (text) pane.findPrev(text);
                }
            }],
            html: "<iframe src='about:blank' id='logframe' style='width:100%;height:100%;border:none'></iframe>",
            flex:   1,
            margin: 8
        }];

        this.callParent(arguments);
        this.writeToFrame();
    }
});

