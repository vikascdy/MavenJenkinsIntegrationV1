
// VIEW: Wizard Nav Button
// An item on the left-hand navigation bar of the New Configuration Wizard.
// ----------------------------------------------------------------------------

Ext.define('SM.view.wizard.WizardNavButton', {
    extend : 'Ext.Component',
    alias  : 'widget.wizardnavbutton',
    
    height : 37,
    title  : 'Title',
    paneCls: null,
    minWidth:246,

    cls    : 'wizard-nav-button',
    data   : {
        title   : 'Title',
        cmpId   : 'NULL',
        selected: false
    },
    tpl    : new Ext.XTemplate(
    "<tpl if=\"selected\">",
        "{title}",
    "</tpl>",
    "<tpl if=\"!selected\">",
        "<a href='#' onclick=\"Ext.getCmp('{cmpId}').onClick(); return false;\">",
            "{title}",
        "</a>",
    "</tpl>"),

    initComponent: function() {
        this.callParent(arguments);
        if (!this.paneCls)
            Ext.Error.raise("A WizardNavButton must declare a paneCls property!");
        this.reload();
    },

    onClick: function() {
        try {
            var wizardPage = this.up('wizardpage');
            var pane = this.createPane();
            if (pane) wizardPage.setPane(pane);
        } catch (err) {
            Functions.errorMsg(err, "Wizard Error");
        }
    },

    createPane: function() {
        // Override me!
        Functions.errorMsg("Did not override createPane()!");
        return null;
    },

    reload: function() {
        this.updateHtml();
    },

    updateHtml: function() {
        var wizardPage = this.up('wizardpage');
        var selected = wizardPage ? this.paneCls == wizardPage.getPaneName() : false;
        this.update({
            title   : this.title,
            cmpId   : this.id,
            selected: selected
        });
        if (selected) {
            this.addCls('wizard-nav-button-selected');
            this.removeCls('wizard-nav-button-unselected');
        } else {
            this.addCls('wizard-nav-button-unselected');
            this.removeCls('wizard-nav-button-selected');
        }
    }
});

