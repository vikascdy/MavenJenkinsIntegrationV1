
// CONTROLLER: Wizard Controller
// Manages the New Configuration Wizard and its associated views.
// ----------------------------------------------------------------------------

Ext.define('SM.controller.WizardController', {
    extend: 'Ext.app.Controller',
    
    stores: ['RoleTreeStore'],

    views: [
        'wizard.WizardPage',
        'wizard.WizardNavButton',
        'wizard.WizardPane',
        'wizard.ConfigPropertiesPane',
        'wizard.DefineServersPane',
        'wizard.DefineServicesPane',
        'wizard.ValidationPane',
        'wizard.DeploymentPane',
        'wizard.RoleTree',
        'wizard.ServiceDependencyField',
        'wizard.ResourceDependencyField'
    ],

    init: function() {
        this.control({
            'wizardpage #wizardNext': {
                click: function(btn) {
                    try {
                        var page = btn.up('wizardpage');
                        var pane = page.getCurrentPane();
                        if (!pane.form.isValid()) {
                            Functions.errorMsg("One or more form values is invalid.");
                            return;
                        }
                        if (!pane.saveChanges())
                            return;
                        var nextPane = pane.getNextPane();
                        if (nextPane) page.setPane(nextPane);
                    } catch (err) {
                        Functions.errorMsg(err, "Wizard Error");
                    }
                }
            },
            'wizardpage #wizardPrev': {
                click: function(btn) {
                    try {
                        var page = btn.up('wizardpage');
                        var pane = page.getCurrentPane();
                        var prevPane = pane.getPrevPane();
                        if (prevPane) page.setPane(prevPane);
                    } catch (err) {
                        Functions.errorMsg(err, "Wizard Error");
                    }
                }
            },
            'wizardpage #cancel': {
                click: function(btn) {
                    btn.up('wizardpage').promptToExit();
                }
            },
            'wizardpage #save': {
                click: function(btn) {
                    Ext.widget('saveconfigaswindow');
                }
            },
            'wizardpage #saveAndClose': {
                click: function(btn) {
                    Ext.widget('saveconfigwindow', {onSuccessfulSave: function() {
                        SM.setPage(Ext.create('SM.view.core.OpenConfigPage'), true);
                    }});
                }
            },
            'wizardpage  #back': {
                click: function() {
                    this.application.fireEvent('changeurl', ['config']);
//                    SM.setPage(Ext.create('SM.view.core.OpenConfigPage'));
                }
            },
            'roletree': {
                checkchange: function(node, checked) {
                    try {
                        if (node.get('type') != 'Role' || !node.get('server'))
                            return;
                        var server = node.get('server');
                        var role = node.get('object');
                        if (checked) {
                            var newNodeName = ConfigManager.getNextAvailableIncrementedName(role.get('name'));
                            var newNode = server.spawnNode(newNodeName, 9100, '',null);
                            newNode.applyRole(role);
                        } else {
                            var existing = server.getChildrenWith({type: 'Node', roleName: role.get('name')});
                            Ext.each(existing, function(node) {
                                server.removeChild(node);
                            });
                        }
                        SM.reloadAll();
                    } catch (err) {
                        Functions.errorMsg(err.message, "Wizard Error");
                    }
                }
            }
        });
    }
});

