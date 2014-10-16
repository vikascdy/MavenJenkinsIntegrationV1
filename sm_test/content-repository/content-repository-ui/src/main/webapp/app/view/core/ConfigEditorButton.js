// VIEW: Configuration Editor Button
// A labeled button used to proceed to the Configuration Editor from a wizard
// page.
// ----------------------------------------------------------------------------

Ext.define('SM.view.core.ConfigEditorButton', {
    extend: 'Ext.button.Button',
    alias : 'widget.configeditorbutton',
    text : 'Use Configuration Editor',
    tooltip: {
        text : 'For the power users, the config editor helps you to work on complex installation and configurations.',
        anchor: 'bottom',
        anchorOffset: 85,
        width:200
    }
});

