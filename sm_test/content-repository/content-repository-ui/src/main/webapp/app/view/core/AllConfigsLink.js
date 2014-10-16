
// VIEW: All Configs Link
// A button that provides a link back to the 'All Configurations' page
// (OpenConfigPage).
// ----------------------------------------------------------------------------

Ext.define('SM.view.core.AllConfigsLink', {
    extend: 'Ext.Component',
    alias : 'widget.allconfigslink',    
    html:'<img src="resources/images/back-config.png"/><a href="#!/config" class="all-config-link">All Configurations</a>',
    padding: '10 0 0 0',
    margin : '0 0 0 20',
    height:30
});

