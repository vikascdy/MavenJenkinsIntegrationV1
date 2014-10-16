
// ABSTRACT VIEW: Wizard Pane
// Abstract base class for form panes that can be displayed in the center of
// the New Configuration Wizard interface.
// ----------------------------------------------------------------------------

Ext.define('SM.view.wizard.WizardPane', {
    extend  : 'Ext.form.Panel',
    alias   : 'widget.wizardpane',
    cls     : 'wizard-pane',
    autoScroll: true,

    defaults: {
        xtype: 'fieldcontainer',
        labelAlign: 'top',
        cls: 'formpage-major-category',
        labelCls: 'formpage-major-category-header ',
        labelSeparator: '',
        anchor: '60%',
        layout: 'form'
    },
    bodyPadding  : 20,
    preventHeader: true,

    initComponent: function() {
        this.items = [{
            xtype : 'component',
            cls   : 'info-pane-header',
            anchor:'100%',
            itemId: 'paneHeader',
            border: false,
            html  : "<div class='large-icon " + this.iconCls + "' style='float: left;'></div><h2 style='margin-left: 34px;'>" + this.title + "</h2>"
        }].concat(this.items);
        this.callParent(arguments);
    },

    saveChanges: function() {
        Functions.errorMsg('Did not override saveChanges()!');
        return false;
    },

    getPrevPane: function() {
        Functions.errorMsg('Did not override getPrevPane()!');
        return null;
    },

    getNextPane: function() {
        Functions.errorMsg('Did not override getNextPane()!');
        return null;
    },

    getNextPaneName: function() {
        Log.warn('Did not override getNextPaneName()!');
        return "Next";
    }
});

