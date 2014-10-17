Ext.onReady(function() {
    Ext.create('Ext.container.Viewport', {
        layout : 'border',
        renderTo : Ext.getBody(),
        items : [
            {
                region          :    'north',
                xtype           :    'ApplicationBar',
                appBarUrl       :    'json/applicationBar.json',
                border          :    false
            }
        ]
    });
});