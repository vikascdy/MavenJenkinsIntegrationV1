// CONTROLLER: Content Controller
// Manages the Content Repository page and its associated views.
// ----------------------------------------------------------------------------

Ext.define('SM.controller.ContentController', {
    extend: 'Ext.app.Controller',

    models: ['ContentNode'],

    views: [ 
        'content.ContentRepositoryPage',
        'content.ContentRepositoryTree',
        'content.RepositoryToolbar',
        'content.FileInfoBar',
        'content.FileContextMenu',
        'content.FolderContextMenu',
        'content.FilePropertiesWindow',
        'content.FileHistoryList',
        'content.FileUploadWindow',
        'content.CopyOrMoveWindow',
        'content.TextEditorWindow' 
    ],

    init: function() {
        var controller = this;
        this.control({
            'repositorytree': {
                select: function(rowModel, record) {
                    var infoBar = SM.viewport.down(
                        'contentrepositorypage')
                        .down('fileinfobar');
                    infoBar.loadInfoFor(record);
                },
                itemcontextmenu: Functions.showContextMenu
            },
            'repositorytoolbar button': {
                click: function(btn) {
                    var tree = btn.up('contentrepositorypage').down('repositorytree');
                    var selection = tree.getSelectionModel().getSelection();
                    var node = (selection.length > 0) ? selection[0]: null;
                    switch (btn.getItemId()) {
                        case 'newFolder':
                            if (!node || !node.get('directory')) {
                                controller.promptToCreateFolder(new SM.model.ContentNode({id: '/'}));
                                // Functions.errorMsg(
                                //     "You must select a parent folder before creating a new folder.");
                            } else {
                                console.log(node);
                                controller.promptToCreateFolder(node);
                            }
                            break;
                        case 'upload':
                            if (!node || !node.get('directory'))
                                Functions.errorMsg(
                                    "You must select a parent folder to upload a file into.");
                            else
                                controller.showUploadWindow(node);
                            break;
                        case 'copy':
                            if (!node)
                                Functions.errorMsg(
                                    "You must select a file or folder to copy/move.");
                            else
                                controller.showCopyWindow(node);
                            break;
                        case 'rename':
                            if (!node)
                                Functions.errorMsg(
                                    "You must select a file or folder to rename.");
                            else
                                controller.promptToRename(node);
                            break;
                        case 'delete':
                            if (!node)
                                Functions.errorMsg(
                                    "You must select a file or folder to delete.");
                            else
                                controller.askToDelete(node);
                            break;
                        case 'download':
                            if (!node)
                                Functions.errorMsg(
                                    "You must select a file to download.");
                            else
                                controller.downloadFile(node);
                            break;
                        case 'properties':
                            if (!node)
                                Functions.errorMsg(
                                    "You must select a file or folder to display the properties of.");
                            else
                                controller.showPropertiesWindow(node);
                            break;
                        case 'edit':
                            controller.showEditWindow(node);
                            break;
                        case 'refresh':
                            SM.reloadAll();
                            tree.getSelectionModel().deselectAll();
                            break;
                    }
                }
            },
            'filecontextmenu menuitem, foldercontextmenu menuitem': {
                click: function(mitem) {
                    var node = mitem.up('menu').node;
                    switch (mitem.getItemId()) {
                        case 'download':
                            controller.downloadFile(node);
                            break;
                        case 'edit':
                            controller.showEditWindow(node);
                            break;
                        case 'expand':
                            node.expand(true);
                            break;
                        case 'collapse':
                            node.collapse(true);
                            break;
                        case 'upload':
                            controller.showUploadWindow(node);
                            break;
                        case 'newFolder':
                            controller.promptToCreateFolder(node);
                            break;
                        case 'copy':
                            controller.showCopyWindow(node);
                            break;
                        case 'delete':
                            controller.askToDelete(node);
                            break;
                        case 'rename':
                            controller.promptToRename(node);
                            break;
                        case 'properties':
                            controller.showPropertiesWindow(node);
                            break;
                    }
                }
            },
            'fileuploadwindow #uploadButton': {
                click: function(btn) {
                    controller.uploadFileFromForm(
                        btn.up('#uploadForm'), btn.up('window').parentDir);
                }
            },
            'filehistorylist gridview': {
                downloadIcon: function(node, version) {
                    controller.downloadFile(node, version);
                },
                editIcon: function(node, version) {
                    controller.showEditWindow(node, version);
                }
            },
            'copyormovewindow #copy, copyormovewindow #move': {
                click: function(btn) {
                    try {
                        var action = btn.getItemId();
                        var win = btn.up('window');
                        var oldNode = win.node;
                        var form = win.down('form').getForm();
                        var tree = win.down('#folders');
                        var selection = tree.getSelectionModel().getSelection();
                        if (selection.length === 0)
                            Functions.fmerr("You must select a directory to {0} to.", action);
                        var selNode = selection[0];
                        if (!selNode.get('directory'))
                            Functions.fmerr("You must select a directory, not a file, to {0} to.", action);
                        var filename = form.getValues().filename;
                        if (!filename)
                            Functions.fmerr("You must enter a filename for the new file.", action);
                        if (action == 'move')
                            controller.moveNode(
                                oldNode.get('id'),
                                selNode.get('id') + '/' + filename,
                                function() { win.close(); }
                            );
                        else
                            controller.copyNode(
                                oldNode.get('id'),
                                selNode.get('id') + '/' + filename,
                                function() { win.close(); }
                            );
                    } catch (err) {
                        Functions.errorMsg(err.message);
                    }
                }
            },
            'texteditorwindow': {
                beforeclose: function(win) {
                    if (win.down('#textarea').getValue() != win.filedata && !win.confirmClose) {
                        Ext.Msg.confirm("Close Without Saving?",
                            "This editor has unsaved changes. If you close it, you will lose those changes.\n<br />" +
                            "Are you sure you want to close this editor?",
                            function(btn) {
                                if (btn == 'yes') {
                                    win.confirmClose = true;
                                    win.close();
                                }
                            }
                        );
                        return false;
                    }
                }
            },
            'texteditorwindow #save': {
                click: function(btn) {
                    var win = btn.up('window');
                    Ext.Msg.confirm("Save File?",
                        "Are you sure you want to save this file?",
                        function(dbtn) {
                            if (dbtn == 'yes') {
                                win.filedata = win.down('#textarea').getValue();
                                controller.updateFile(win.filepath, win.filedata);
                            }
                        }
                    );
                }
            },
            'texteditorwindow #revert': {
                click: function(btn) {
                    var win = btn.up('window');
                    Ext.Msg.confirm("Revert File?",
                        "This will revert the contents of this editor to the current contents of the open file." +
                        "All unsaved changes will be lost.\n<br />" +
                        "Are you sure you want to revert the contents of this editor?",
                        function(dbtn) {
                            if (dbtn == 'yes') {
                                win.down('#textarea').setValue(win.filedata);
                            }
                        }
                    );
                }
            }
        });
    },

    askToDelete: function(node) {
        var me = this;
        Ext.Msg.confirm("Delete " + (node.get('directory') ? 'Folder': 'File') + "?",
            'Are you sure you want to delete the ' + (node.get('directory') ? 'folder': 'file') +
            ' "' + node.get('name') + '"?',
            function(btn) {
                if (btn == 'yes')
                    me.deleteNode(node);
            }
        );
    },

    deleteNode: function(node) {
        Functions.jsonCommand("UI Service",
            node.get('directory') ? 'content.deleteFolder' : 'content.deleteFile', {
                path: node.get('id')
        }, {
            success: function(response) {
                Ext.Msg.alert("Deleted",
                    (node.get('directory') ? 'Folder' : 'File') + " '" + node.get('name') +
                    "' successfully deleted.");
                SM.reloadAll();
                var tree = SM.viewport.down('repositorytree');
                tree.getSelectionModel().deselectAll();
                Log.info(node);
            }
        });
    },

    promptToRename: function(node) {
        var me = this;
        Ext.Msg.prompt("Rename " + (node.get('directory') ? 'Folder': 'File'),
            'Enter a new name for "' + node.get('name') + '":',
            function(btn, value) {
                if (btn == 'ok')
                    me.renameNode(node, value);
            }
        );
    },

    renameNode: function(node, newName) {
        var parts = node.get('id').split('/');
        parts.pop();
        parts.push(Ext.String.trim(newName));
        var newPath = parts.join('/');
        Functions.jsonCommand("UI Service", "content.move", {
            oldPath: node.get('id'),
            newPath: newPath,
            username: "unknown" // TODO: Load username properly.
        }, {
            success: function(response) {
                Ext.Msg.alert("Renamed",
                    "Successfully renamed '" + node.get('name') + "' to '" + newName + "'.");
                SM.reloadAll();
                var tree = SM.viewport.down('repositorytree');
                tree.getSelectionModel().deselectAll();
            }
        });
    },

    moveNode: function(oldPath, newPath, callback, scope) {
        Functions.jsonCommand("UI Service", "content.move", {
            oldPath: oldPath,
            newPath: newPath,
            username: "unknown" // TODO: Load username properly.
        }, {
            success: function(response) {
                Ext.Msg.alert("Moved",
                    "Successfully moved '" + oldPath + "' to '" + newPath + "'.");
                Ext.callback(callback, scope);
                SM.reloadAll();
                var tree = SM.viewport.down('repositorytree');
                tree.getSelectionModel().deselectAll();
            }
        });
    },

    copyNode: function(oldPath, newPath, callback, scope) {
        Functions.jsonCommand("UI Service", "content.copy", {
            oldPath: oldPath,
            newPath: newPath,
            username: "unknown" // TODO: Load username properly.
        }, {
            success: function(response) {
                Ext.Msg.alert("Copied",
                    "Successfully copied '" + oldPath + "' to '" + newPath + "'.");
                Ext.callback(callback, scope);
                SM.reloadAll();
                var tree = SM.viewport.down('repositorytree');
                tree.getSelectionModel().deselectAll();
            }
        });
    },

    promptToCreateFolder: function(node) {
        var me = this;
        Ext.Msg.prompt(
            "Create New Folder",
            "You are creating a new folder under '" + node.get('id') + "'.\n<br />" +
            "Enter the new folder's name:",
            function(btn, value) {
                if (btn == 'ok') {
                    if (value.length > 255) {
                        Functions.errorMsg('Folder name cannot exceed 255 characters.');
                    } else
                        me.createFolder(node, value);
                }
            }
        );
    },

    createFolder: function(parentNode, folderName) {
        Functions.jsonCommand("UI Service", "content.createFolder", {
            path: parentNode.get('id') + '/' + Ext.String.trim(folderName),
            username: "unknown" // TODO: Load username properly.
        }, {
            success: function(response) {
                Ext.Msg.alert("Folder Created",
                    "New folder successfully created.");
                SM.reloadAll();
                var tree = SM.viewport.down('repositorytree');
                tree.getSelectionModel().deselectAll();
            }
        });
    },

    updateFile: function(filepath, filedata) {
        Functions.jsonCommand("UI Service", "content.updateFile", {
            path: filepath,
            data: filedata,
            username: "unknown" // TODO: Load username properly.
        }, {
            success: function(response) {
                Ext.Msg.alert("File Updated",
                    "Saved file '" + filepath.split('/').pop() +
                    "', version " + response + ".");
                SM.reloadAll();
                var tree = SM.viewport.down('repositorytree');
                tree.getSelectionModel().deselectAll();
            }
        });
    },

    downloadFile: function(node, version) {
        if (node.get('typeName') == 'folder') {
            Functions.errorMsg("Cannot download a folder.");
            return;
        } else if (node.get('typeName') != 'file') {
            Functions.errorMsg("Cannot download a non-file node.");
            return;
        }
        Functions.download(JSON_URL + "/content.downloadFile?" + Ext.urlEncode({
            path: node.get('id'),
            version: version
        }));
        /*Functions.jsonCommand("UI Service", "content.downloadFile", {
            path: node.get('id'),
            version: version
        }, {
            success: function(response) {
                // TODO: Restore download functionality.
                Ext.Msg.alert("Downloading files has not yet been reimplemented.");
            }
        });*/
    },

    showUploadWindow: function(parentNode) {
        Ext.widget('fileuploadwindow', {
            parentDir: parentNode.get('id')
        });
    },

    uploadFileFromForm: function(formPanel, parentDir) {
        var filename = formPanel.down('filefield').getValue()
            .split('/').pop().split('\\').pop();
        var loadingWindow = Ext.widget('progresswindow', {
            text: 'Uploading file "' + filename + '"...'
        });
        formPanel.getForm().submit({
            url: JSON_UPLOAD_URL + '/content.uploadFile',
            params: {
                data: Ext.encode({
                    path: parentDir + '/' + filename
                })
            },
            success: function(form, action) {
                loadingWindow.destroy();
                formPanel.up('window').close();
                Ext.Msg.alert("File Uploaded",
                    'File "' + filename + '" successfully uploaded.');
                SM.reloadAll();
                var tree = SM.viewport.down('repositorytree');
                tree.getSelectionModel().deselectAll();
            },
            failure: function(form, action) {
                loadingWindow.destroy();
                formPanel.up('window').close();
                switch (action.failureType) {
                    case Ext.form.action.Action.CLIENT_INVALID:
                        Functions.errorMsg(
                            'You must provide a file to upload.',
                            'Upload Failed');
                        break;
                    case Ext.form.action.Action.CONNECT_FAILURE:
                        Functions.errorMsg(
                            'Could not connect to server.',
                            'Upload Failed');
                        break;
                    case Ext.form.action.Action.SERVER_INVALID:
                        Functions.errorMsg(
                            action.result.error,
                            'Upload Failed');
                }
            }
        });
    },

    showCopyWindow: function(node) {
        Ext.widget('copyormovewindow', {
            node: node
        });
    },

    showEditWindow: function(node, version, force) {
        if (node.get('directory')) {
            Functions.errorMsg("Select a file.");
            return;
        }
        var me = this;
        var loadingWindow = Ext.widget('progresswindow', {
            text: 'Opening file in Text Editor...'
        });
        Functions.jsonCommand("UI Service", "content.fileText", {
            path: node.get('id'),
            version: version,
            force: force
        }, {
            success: function(response) {
                loadingWindow.destroy();
                if (response.warning) {
                    Ext.Msg.show({
                        title: "File Warning",
                        msg: response.message +
                            "\n<br />Are you sure you want to edit this file?",
                        buttons: Ext.MessageBox.YESNO,
                        icon: Ext.MessageBox.WARNING,
                        fn: function(btn) {
                            if (btn == 'yes')
                                me.showEditWindow(node, version, true);
                        }
                    });
                } else {
                    Ext.widget('texteditorwindow', {
                        filepath: node.get('id'),
                        filedata: response.data
                    });
                }
            }
        });
    },

    showPropertiesWindow: function(node) {
        Ext.widget('filepropertieswindow', {
            node: node
        });
    }
});

