Ext.onReady(function () {
    Ext.create('Edifecs.TriStateTree', {
        ALL_ID: 1,
        width: 400,
        height: 300,
        data: {
            text: 'SELECT ALL',
            id: '21',
            expanded: true,
            checked: false,
            children: [
                {
                    text: "id2 homework ",
                    checked: false,
                    expanded: true,
                    id: 2,
                    children: [
                        {
                            id: 3,
                            checked: false,
                            text: "id3 book report ",
                            leaf: true
                        },
                        {
                            id: 4,
                            checked: false,
                            text: "id4 algebra ",
                            leaf: true
                        }
                    ]
                },
                {
                    id: 5,
                    checked: false,
                    text: "id5 food ",
                    expanded: true,
                    children: [
                        {
                            id: 6,
                            checked: false,
                            text: "id6 meat",
                            leaf: true
                        },
                        {
                            id: 7,
                            checked: false,
                            text: "id7 milk ",
                            leaf: true
                        }
                    ]
                },
                {
                    id: 8,
                    checked: false,
                    text: "id8 plans",
                    expanded: true,
                    children: [
                        {
                            id: 9,
                            checked: false,
                            text: "id9 vacation ",
                            leaf: true
                        },
                        {
                            id: 10,
                            checked: false,
                            text: "id10 rule the world ",
                            leaf: true
                        }
                    ]
                }
            ]
        },
        /*dataURL :   'json/triStateTree.json',*/
        tbar: [
            {   xtype: 'button',
                text: 'Set selected  3,4,7,10',
                handler: function () {
                    this.up('panel').setSelections([ 3, 4, 7, 10])
                }
            },
            {   xtype: 'button',
                text: 'Get selected',
                handler: function () {
                    Ext.MessageBox.show({
                        title: 'Selected Nodes',
                        msg: JSON.stringify(this.up('panel').getSelections(), null, 4),
                        icon: Ext.MessageBox.INFO
                    });
                }
            },
            {     xtype: 'button',
                text: 'Get selected id_only ',
                handler: function () {
                    Ext.MessageBox.show({
                        title: 'Selected Nodes',
                        msg: JSON.stringify(this.up('panel').getSelections(true), null, 4),
                        icon: Ext.MessageBox.INFO
                    });
                }}
        ],
        renderTo: Ext.getBody()
    });
});