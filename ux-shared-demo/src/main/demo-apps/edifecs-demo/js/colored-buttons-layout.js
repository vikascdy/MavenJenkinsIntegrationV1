Ext.onReady(function () {
    Ext.tip.QuickTipManager.init();
    var buttonPanel = Ext.create('Ext.panel.Panel', {
        bodyPadding:5,
        title      :'Colored Buttons',
        layout     :{
            type   :'table',
            columns:5,
            tdAttrs:{
                style:{
                    padding:'5px'
                }
            }
        },
        defaults   :{
            // scale   :'small',
            xtype   :'button'
        },
        items      :[
            <!-- Green Button -->
            {
                ui             :    'greenbutton',
                text        :    'Button',
                plain        :    true,
                menu          :     [
                    {text: 'Item 1'},
                    {text: 'Item 2'},
                    {text: 'Item 3'},
                    {text: 'Item 4'}
                ]
            },
            {
                ui          :'greenbutton',
                text        :'Toggle Button',
                enableToggle:true
            },
            {
                ui      :'greenbutton',
                text    :'Disabled',
                disabled:true,
                tooltip:"Disabled"
            },
            {
                xtype:'splitbutton',
                ui   :'greenbutton',
                text :'Split Button'
            },
            {
                ui       :'greenbutton',
                text     :'Icon Button',
                iconCls  :'add-White',
                iconAlign:'right'
            },
            <!-- Blue Button -->
            {
                ui  :'bluebutton',
                text:'Button',
                plain        :    true,
                menu          :     [
                    {text: 'Item 1'},
                    {text: 'Item 2'},
                    {text: 'Item 3'},
                    {text: 'Item 4'}
                ]
            },
            {
                ui          :'bluebutton',
                text        :'Toggle Button',
                enableToggle:true
            },
            {
                ui      :'bluebutton',
                text    :'Disabled',
                disabled:true,
                tooltip:"Disabled"
            },
            {
                xtype:'splitbutton',
                ui   :'bluebutton',
                text :'Split Button'
            },
            {
                ui       :'bluebutton',
                text     :'Icon Button',
                iconCls  :'upload-White',
                iconAlign:'right'
            },
            <!-- Orange Button -->
            {
                ui  :'orangebutton',
                text:'Button',
                plain        :    true,
                menu          :     [
                    {text: 'Item 1'},
                    {text: 'Item 2'},
                    {text: 'Item 3'},
                    {text: 'Item 4'}
                ]
            },
            {
                ui          :'orangebutton',
                text        :'Toggle Button',
                enableToggle:true
            },
            {
                ui      :'orangebutton',
                text    :'Disabled',
                disabled:true,
                tooltip:"Disabled"
            },
            {
                xtype:'splitbutton',
                ui   :'orangebutton',
                text :'Split Button'
            },
            {
                ui       :'orangebutton',
                text     :'Icon Button',
                iconCls  :'delete-White',
                iconAlign:'right'
            },
            <!-- Gray Button -->
            {
                ui  :'graybutton',
                text:'Button',
                plain        :    true,
                menu          :     [
                    {text: 'Item 1'},
                    {text: 'Item 2'},
                    {text: 'Item 3'},
                    {text: 'Item 4'}
                ]
            },
            {
                ui          :'graybutton',
                text        :'Toggle Button',
                enableToggle:true
            },
            {
                ui      :'graybutton',
                text    :'Disabled',
                disabled:true,
                tooltip:"Disabled"
            },
            {
                xtype:'splitbutton',
                ui   :'graybutton',
                text :'Split Button'
            },
            {
                ui       :'graybutton',
                text     :'Icon Button',
                iconCls  :'edit-White',
                iconAlign:'left'
            },
            <!-- Red Button -->
            {
                ui  :'redbutton',
                text:'Button',
                plain        :    true,
                menu          :     [
                    {text: 'Item 1'},
                    {text: 'Item 2'},
                    {text: 'Item 3'},
                    {text: 'Item 4'}
                ]
            },
            {
                ui          :'redbutton',
                text        :'Toggle Button',
                enableToggle:true
            },
            {
                ui      :'redbutton',
                text    :'Disabled',
                disabled:true,
                tooltip:"Disabled"
            },
            {
                xtype:'splitbutton',
                ui   :'redbutton',
                text :'Split Button'
            },
            {
                ui       :'redbutton',
                text     :'Icon Button',
                iconCls  :'publish-White',
                iconAlign:'left'
            }
        ],
        renderTo   :"buttons"
    });
});