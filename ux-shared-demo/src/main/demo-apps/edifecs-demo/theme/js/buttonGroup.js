Ext.onReady(function() {
Ext.create('Ext.toolbar.Toolbar', {
    renderTo: document.getElementById('buttonGroup'),
    padding: 2,
    margin: 0,
    width:125,
    componentCls: 'roundAll',
    items: [
        {
            // xtype: 'button', // default for Toolbars
            text: '',
            iconCls: 'toolBarSprite profile'
        },
        
        {
            // xtype: 'button', // default for Toolbars
            text: '',
            iconCls: 'toolBarSprite newFile'
        },
       
        {
            // xtype: 'button', // default for Toolbars
            text: '',
            iconCls: 'toolBarSprite edit'
        },
       
        {
            // xtype: 'button', // default for Toolbars
            text: '',
            iconCls: 'toolBarSprite close'
        },
       
        {
            // xtype: 'button', // default for Toolbars
            text: '',
            iconCls: 'toolBarSprite addNew',
            disabled: true
        }
    ]
});

Ext.create('Ext.toolbar.Toolbar', {
    renderTo: document.getElementById('buttonGroup2'),
    padding: 2,
    margin: 0,
    width:76,
    componentCls: 'roundAll',
    items: [
        {
            // xtype: 'button', // default for Toolbars
            text: '',
            iconCls: 'toolBarSprite edit'
        },
        
        {
            // xtype: 'button', // default for Toolbars
            text: '',
            iconCls: 'toolBarSprite close'
        },
        
        {
            // xtype: 'button', // default for Toolbars
            text: '',
            iconCls: 'toolBarSprite addNew'
        }
    ]
});

Ext.create('Ext.toolbar.Toolbar', {
    renderTo: document.getElementById('buttonGroup3'),
    padding: 2,
    margin: 0,
    width:28,
    componentCls: 'roundAll',
    items: [
        {
            // xtype: 'button', // default for Toolbars
            text: '',
            iconCls: 'toolBarSprite edit'
        }
    ]
});

});