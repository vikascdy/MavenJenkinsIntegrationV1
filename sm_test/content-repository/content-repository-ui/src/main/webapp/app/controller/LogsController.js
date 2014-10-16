
// CONTROLLER: Logs Controller
// Manager error reports and log files.
// ----------------------------------------------------------------------------

Ext.define('SM.controller.LogsController', {
    extend: 'Ext.app.Controller',
    
    stores: ['ServerErrorStore', 'FilteredErrorStore'],
    models: ['ErrorLog', 'LogFile'],
    
    views: [
        'log.ErrorList',
        'log.LogFileList',
        'log.LogViewerPane',
        'log.ServiceErrorList'
    ],

    init: function() {
        var controller = this;
        this.control({
            'logfilelist > gridview': {
                cellclick: function (view, cell, cellIndex, record, row, rowIndex, e) {
                    var linkClicked = (e.target.tagName == 'A');
                    var clickedDataIndex =
                        view.panel.headerCt.getHeaderAtIndex(cellIndex).dataIndex;
                    
                    if (linkClicked && clickedDataIndex == 'name') {
                        controller.showLogFile(record);
                        e.stopEvent();
                        return false;
                    }
                }
            },
            'logviewerpane': {
                refresh: function(pane, logFile) {
                    controller.showLogFile(logFile);
                }
            }
        });
    },

    showLogFile: function(logFile) {
        var loadingWindow = Ext.widget('progresswindow', {text: 'Retrieving log file...'});
        Functions.jsonCommand("UI Service", "logs.text", {
            id:       logFile.get('id'),
            filename: logFile.get('name')
        }, {
            success: function(response) {
                loadingWindow.destroy();
                var infoPaneCtr = SM.viewport.down('#infopane');
                if (infoPaneCtr) {
                    infoPaneCtr.removeAll();
                    infoPaneCtr.add(Ext.create('SM.view.log.LogViewerPane', {
                        logFile: logFile,
                        logText: response
                    }));
                }
            },
            failure: function(response) {
                loadingWindow.destroy();
                Functions.errorMsg('Failed to load log file text.<br /><br />' +
                    response.errorClass + ': ' + response.error);
            }
        });
    }
});

