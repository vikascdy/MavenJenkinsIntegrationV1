Ext.define('Security.view.core.InvalidPage', {
    extend: 'Ext.container.Container',
    alias : 'widget.invalidpage',
    padding:20,
    items: [
        {
            xtype: 'component',
            html:'<div class="invalid-page"> The requested page is invalid !</div>'
        }
    ]
});
