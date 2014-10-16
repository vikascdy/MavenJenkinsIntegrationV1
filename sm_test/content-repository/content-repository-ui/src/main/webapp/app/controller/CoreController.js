// CONTROLLER: Core Controller
// Manages loading configuration files and displaying the Configuration 
// Overview Tree.
// ----------------------------------------------------------------------------

Ext.define('SM.controller.CoreController', {
    extend: 'Ext.app.Controller',

    stores: ['ConfigTreeStore', 'SavedConfigStore', 'SavedUsersStore', 'TemplateStore', 'ProductStore'],
    models: ['Config', 'SavedConfig', 'SavedUsers', 'Property', 'TreeNode', 'Product'],

    views: [
        'core.Header',
        'core.CreateConfigPage',
        'core.OpenConfigPage',
        'core.ServiceManagerPage',
        'core.SavedConfigList',
        'core.TemplateList',
        'core.ProductList',
        'core.ConfigForm',
        'core.ConfigEditorButton',
        'core.WizardButton',
        'core.DefineConfigWindow',
        'core.EditConfigWindow',
        'core.SaveConfigAsWindow',
        'core.ProgressWindow',
        'core.ConfigTree',
        'core.PropertiesForm',
        'core.ConfigItemPicker',
        'core.InfoPaneHeader',
        'core.InfoPaneSubheader',
        'core.PatchUploadWindow',
        'core.AllConfigsLink',
        'core.DeploymentProgressWindow',
        'core.PatchOverviewWindow',
        'Edifecs.CustomButtonGroup'
    ],

    init: function() {
        var me=this;
        this.control({
            'openconfigpage savedconfiglist gridview': {
                cellclick: function(view, cell, cellIndex, record, row, rowIndex, e) {
                    var me=this;
                    var linkClicked = (e.target.tagName == 'A');
                    var clickedDataIndex =
                        view.panel.headerCt.getHeaderAtIndex(cellIndex).dataIndex;

                    if (linkClicked && clickedDataIndex == 'name') {
                        SM.isConfigLoaded=false;
                            location.href='#!/service/'+record.get('name') +'/'+record.get('version')+'/'+record.get('active');
                        e.stopEvent();
                        return false;
                    }
                }
            },
            'openconfigpage savedconfiglist button': {
                click: this.openConfigListButtonClicked
            },
            'createconfigpage  #back': {
                click: function() {
                	location.href='#!/config';
                }
            },
            'servicemanagerpage  #back': {
                click: function() {
                	location.href='#!/config';
                }
            },
            'servicemanagerpage configtree': {
                itemdblclick: Functions.showPropertiesWindow,
                itemcontextmenu: Functions.showContextMenu,
                itemclick: this.showInfoPane,
                keydown: function(tree, e) {
                    // Pressing Spacebar or Enter selects a tree item.
                    if (e.keyCode == 32 || e.keyCode == 13) {
                        var selection = tree.getSelectionModel().getSelection();
                        if (selection.length > 0)
                            tree.up('servicemanagerpage').showInfoPaneFor(selection[0]);
                    }
                }
            },
            'configtree > treeview': {
                beforedrop: this.handleTreeDrop,
                drop: function() {
                    SM.reloadAll();
                }
            },
            'configtree #newmenu menuitem': {
                click: this.newMenuItemClicked
            },
            'configtree #delete': {
                click: function(btn) {
                    var item = btn.up('configtree').getSelectedItem();
                    if (item) item.askToDelete();
                }
            },
            'configitemlist > gridview': {
                cellclick: function (view, cell, cellIndex, record, row, rowIndex, e) {
                    var linkClicked = (e.target.tagName == 'A');
                    var clickedDataIndex =
                        view.panel.headerCt.getHeaderAtIndex(cellIndex).dataIndex;

                    if (linkClicked && clickedDataIndex == 'name') {
                        SM.viewport.down('servicemanagerpage').showInfoPaneFor(record);
                        e.stopEvent();
                        return false;
                    }
                }
            },
            'propertiesform textfield': {
                blur: this.propertiesFormAutoUpdate
            },
            'propertiesform checkboxfield': {
                change: this.propertiesFormAutoUpdate
            },
            'propertiesform combobox': {
                change: this.propertiesFormAutoUpdate
            },
            'servicemanagerpage #sidebar gridpanel': {
                select: function(rowModel, record, index, e) {
                    SM.viewport.down('servicemanagerpage').showPropertiesFor(record);
                }
            },
            'configheader button': {
                click: this.configHeaderButtonClicked
            },
            // Automagically append asterisks to required fields.
            'field': {
                render: function(field) {
                    if (field.allowBlank === false && !field.noAsterisk) {
                        try {
                            var label = field.getFieldLabel();
                            if (label) field.setFieldLabel(label + "<span class='required-asterisk'>*</span>");
                        } catch (err) {
                        } // We don't want this to throw any errors.
                    }
                }
            }
        });
    },
    
    newMenuItemClicked: function(mitem) {
        try {
            var tree = SM.viewport.down('configtree');
            switch (mitem.getItemId()) {
                case 'newcluster':
                    Ext.widget('newclusterwindow');
                    break;
                case 'newserver':
                    Ext.widget('newserverwindow', {
                        cluster: tree.getSelectedItemOfType('Cluster')
                    });
                    break;
                case 'newresource':
                    Ext.widget('newresourcewindow', {
                        cluster: tree.getSelectedItemOfType('Cluster')
                    });
                    break;
                case 'newrole':
                    Ext.widget('newrolewindow', {
                        server: tree.getSelectedItemOfType('Server')
                    });
                    break;
                case 'newnode':
                    Ext.widget('newnodewindow', {
                        server: tree.getSelectedItemOfType('Server')
                    });
                    break;
                case 'newservice':
                    Ext.widget('installserviceswindow', {
                        node: tree.getSelectedItemOfType('Node')
                    });
            }
        } catch (err) {
            Functions.errorMsg(err);
        }
    },

    openConfigListButtonClicked: function(btn) {
        var me=this;
        var records = btn.up('savedconfiglist').getSelectionModel().getSelection();
        var selected = null;
        if (records.length > 0)
            selected = records[0];
        switch (btn.getItemId()) {
            case 'create':
                location.href='#!/createconfig';
                break;
            case 'open':
                if (selected) {
                    var view = btn.up('savedconfiglist').down('gridview');
                    view.setLoading('Loading ' + selected.get('name') + '...');
                    SM.loadAndViewConfig(selected);
                } else {
                    Functions.errorMsg("You must select a configuration to open.");
                    break;
                }
                break;
            case 'rename':
                if (selected) {
                    if (selected.get('active')) {
                        Functions.errorMsg("Cannot rename the active configuration!");
                        return;
                    }
                    Ext.Msg.prompt(
                        "Rename Config",
                        'Enter a new name for the configuration file "' +
                            selected.get('name') + '":',
                        function(btn, value) {
                            if (btn == 'ok')
                                ConfigManager.renameConfig(selected.get('name'), value, selected.get('version'),
                                    function() {
                                        Ext.Msg.alert("Config Renamed", "Successfully renamed configuration file.");
                                    });
                        }
                    );
                } else {
                    Functions.errorMsg("You must select a configuration to rename.");
                    break;
                }
                break;
            case 'delete':
                if (selected) {
                    if (selected.get('active')) {
                        Functions.errorMsg("Cannot delete the active configuration!");
                        return;
                    }
                    Ext.Msg.confirm(
                        "Delete Config?",
                        'Are you sure you want to delete the configuration file "' +
                            selected.get('name') + '"? This cannot be undone!',
                        function(btn) {
                            if (btn == 'yes')
                                ConfigManager.deleteConfig(selected.get('name'), selected.get('version'),
                                    function() {
                                        Ext.Msg.alert("Config Deleted", "Successfully deleted configuration file.");
                                        ConfigManager.checkConfigList();
                                    });
                        }
                    );
                } else {
                    Functions.errorMsg("You must select a configuration to delete.");
                    break;
                }
                break;
            case 'import':
            		location.href='#!/createconfig/import';                   
                break;
            case 'export':
                if (!selected) {
                    Functions.errorMsg("You must select a configuration to export.");
                    break;
                }
                var loadingWindow = Ext.widget('progresswindow', {
                    text: 'Loading ' + selected.get('name') + ' for export...'
                });
                var callback = function() {
                    loadingWindow.destroy();
                    ConfigManager.exportConfig(function() {
                        ConfigManager.clearConfig();
                    });
                };
                if (selected.get('active'))
                    ConfigManager.loadDefaultConfig(callback);
                else
                    ConfigManager.loadSavedConfig(selected.get('name'), selected.get('version'), callback);
                break;
        }
    },

    configHeaderButtonClicked: function(btn) {
        switch (btn.getItemId()) {
            case 'save':
                ConfigManager.saveConfig('');
                break;
            case 'saveAs':
                Ext.widget('saveconfigaswindow', {
                    onSuccessfulSave: function(name, version) {
                        ConfigManager.loadSavedConfig(name, version, function() {
                            SM.reloadAll();
                        });
                    }
                });
                break;
            case 'validate':
                ConfigManager.checkRequiredServiceTypes(true,function(){
                    ConfigManager.validateConfig(function() {
                      Ext.Msg.alert("Validation Successful", "Validation was successful; no errors returned.");
                    });
                });
                break;
            case 'deploy':
                Ext.Msg.confirm(
                    "Deploy Config?",
                    "Are you sure you want to deploy the currently open configuration?" +
                        " This will replace the current active configuration!\n\n" +
                        "(The current configuration must be validated before it will be deployed)",
                    function(btn) {
                        if (btn == 'yes') {
                            ConfigManager.checkRequiredServiceTypes(true,function(){
                                ConfigManager.validateConfig(function() {
                                    ConfigManager.applyConfig();
                                });
                            });                            
                        }
                    }
                );
                break;
            case 'refresh':
                SM.reloadAllWithStatuses();
                break;
            case 'export':
                ConfigManager.exportConfig();
                break;
            case 'patch':
                var patchUpload = Ext.widget('patchuploadwindow');
                break;
           
        }
    },

    showInfoPane: function(rowModel, record, index, e) {
        if (SM.viewport.down('servicemanagerpage')){
            SM.viewport.down('servicemanagerpage').showInfoPaneFor(record);
        }
    },

    handleTreeDrop: function(element, data, overModel, dropPos, dropFunc, e) {
        if (data.alreadyDropped) return false;
        try {
            var dropped = data.records[0];
            if (dropped instanceof SM.model.TreeNode)
                dropped = dropped.get('object');
           
            var dtype = dropped.getType();
            var target = overModel.get('object');
            var ttype = target.getType();
            if (ttype == 'Cluster' && (dtype == 'Server' || dtype == 'Resource')) {
                return dropped.moveToCluster(target) ? 0 : false;
            } else if (ttype == 'Server' && dtype == 'Node') {
                return dropped.moveToServer(target) ? 0 : false;
            } else if (ttype == 'Node' && dtype == 'Service') {
                if (dropped.isServiceUnmovable()) {
                    Ext.Msg.confirm(
                        "Move Service?",
                        "Moving this service can cause data loss !\n\n"+
                        "Are you sure you want to continue?",
                        function(btn) {
                            if (btn == 'yes')
                                return dropped.moveToNode(target) ? 0 : false;
                            else
                                return false;
                        }
                    );
                } else {
                    return dropped.moveToNode(target) ? 0 : false;
                }
               
            } else if (ttype == 'Node' && dtype == 'ServiceType') {
                return target.addServiceFromType(dropped) ? 0 : false;
            } else {
                Functions.fmerr("Cannot drop a {0} on a {1}.", dtype, ttype);
            }
        } catch (ex) {
            Functions.errorMsg(ex);
            return false;
        }
    },

    propertiesFormAutoUpdate: function(field) {
        var formpanel = field.up("propertiesform");
        var obj = formpanel.object;
        if (formpanel.shouldAutoSave() &&
            formpanel.getForm().isValid() &&
            field.getValue() != obj.getProperty(field.getName()))
            formpanel.save();
    }
});

