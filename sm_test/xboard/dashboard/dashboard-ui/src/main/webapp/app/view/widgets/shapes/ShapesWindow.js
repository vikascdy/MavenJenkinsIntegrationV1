var ShapesStore = Ext.create('Ext.data.Store', {
    fields:['name'],
    data:[
        {"name":"circle"},
        {"name":"triangle"},
        {"name":"rectangle"},
        {"name":"text"},
        {"name":"ellipse"}
    ]
});


Ext.define('DD.view.widgets.shapes.ShapesWindow', {
    extend:'Ext.window.Window',
    alias:'widget.shapeswindow',
    modal:true,
    title: 'Select a shape',
    height: 400,
    width: 600,
    layout:'fit',
    dockedItems:[
        {
            xtype:'shapesgallery',
            dock:'left'
        }
    ],
    items: [
        {
            xtype:'container',
            layout:{
                type:'hbox',
                align:'stretch'
            },
            items:[
                {
                    xtype:'container',
                    width:200,
                    flex:1,
                    bodyPadding:8,
                    padding:'4 6 0 6',
                    layout:{
                        type:'vbox',
                        align:'stretch'
                    },
                    items:[
                        {
                            xtype:'propertygrid',
                            title: 'Properties',
                            flex:1
                        }
                    ]
                }
            ]
        }
    ],
    buttons : [
        {
            text: 'Cancel',
            iconCls:'cancel',
            itemId:'cancel'
        },
        {
            text: 'Add to Dashboard',
            itemId:'addToDashboard'
        }
    ]
});

//listeners:{
//                'change':function(thisObj, newValue, oldValue, eOpts ){
//                    this.up('window').close();
//                    ShapesManager.handleSelection(newValue);
//                }
//            }