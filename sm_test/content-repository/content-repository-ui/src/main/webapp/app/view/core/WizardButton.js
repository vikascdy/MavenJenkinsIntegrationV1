// VIEW: Wizard Button
// A labeled button used to proceed to the Step-by-Step Wizard from the Create
// New Configuration page.
// ----------------------------------------------------------------------------


Ext.define('SM.view.core.WizardButton', {
    extend: 'Ext.container.Container',
    alias : 'widget.wizardbutton',

    items : [
        {
            xtype: 'button',
            text : 'Use Step-by-Step Wizard',
            tooltip: {
                text: 'The wizard will guide you through the process of setting up a new configuration by defining Servers, Services, and Resources.',
                anchor: 'bottom',
                anchorOffset: 85,
                width:200
            },
            listeners: {
                click: function (btn) {
                    var parent = btn.up('wizardbutton');
                    parent.fireEvent('click', parent);
                }
            }
        }
    ]
});
