Ext.onReady(function () {

    Ext.QuickTips.init();
    Ext.Loader.setConfig({enabled:true});
    Ext.require([
        'Ext.grid.*',
        'Ext.data.*',
        'Ext.util.*',
        'Ext.tip.*',
        'Ext.Action'
    ]);

    var states = Ext.create('Ext.data.Store', {
        fields:['abbr', 'name'],
        data:[
            {"abbr":"AL", "name":"Alabama"},
            {"abbr":"AK", "name":"Alaska"},
            {"abbr":"AZ", "name":"Arizona"}
        ]
    });

    Ext.grid.dummyData = [
        ['3m Co', 71.72, 0.02, 0.03, '9/1 12:00am'],
        ['Alcoa Inc', 29.01, 0.42, 1.47, '9/1 12:00am'],
        ['Altria Group Inc', 83.81, 0.28, 0.34, '9/1 12:00am'],
        ['American Express Company', 52.55, 0.01, 0.02, '9/1 12:00am'],
        ['American International Group, Inc.', 64.13, 0.31, 0.49, '9/1 12:00am'],
        ['AT&T Inc.', 31.61, -0.48, -1.54, '9/1 12:00am'],
        ['Boeing Co.', 75.43, 0.53, 0.71, '9/1 12:00am'],
        ['Caterpillar Inc.', 67.27, 0.92, 1.39, '9/1 12:00am'],
        ['Citigroup, Inc.', 49.37, 0.02, 0.04, '9/1 12:00am'],
        ['E.I. du Pont de Nemours and Company', 40.48, 0.51, 1.28, '9/1 12:00am'],
        ['Exxon Mobil Corp', 68.1, -0.43, -0.64, '9/1 12:00am'],
        ['General Electric Company', 34.14, -0.08, -0.23, '9/1 12:00am'],
        ['General Motors Corporation', 30.27, 1.09, 3.74, '9/1 12:00am'],
        ['Hewlett-Packard Co.', 36.53, -0.03, -0.08, '9/1 12:00am'],
        ['Honeywell Intl Inc', 38.77, 0.05, 0.13, '9/1 12:00am'],
        ['Intel Corporation', 19.88, 0.31, 1.58, '9/1 12:00am'],
        ['International Business Machines', 81.41, 0.44, 0.54, '9/1 12:00am'],
        ['Johnson & Johnson', 64.72, 0.06, 0.09, '9/1 12:00am'],
        ['JP Morgan & Chase & Co', 45.73, 0.07, 0.15, '9/1 12:00am'],
        ['McDonald\'s Corporation', 36.76, 0.86, 2.40, '9/1 12:00am'],
        ['Merck & Co., Inc.', 40.96, 0.41, 1.01, '9/1 12:00am'],
        ['Microsoft Corporation', 25.84, 0.14, 0.54, '9/1 12:00am'],
        ['Pfizer Inc', 27.96, 0.4, 1.45, '9/1 12:00am'],
        ['The Coca-Cola Company', 45.07, 0.26, 0.58, '9/1 12:00am'],
        ['The Home Depot, Inc.', 34.64, 0.35, 1.02, '9/1 12:00am'],
        ['The Procter & Gamble Company', 61.91, 0.01, 0.02, '9/1 12:00am'],
        ['United Technologies Corporation', 63.26, 0.55, 0.88, '9/1 12:00am'],
        ['Verizon Communications', 35.57, 0.39, 1.11, '9/1 12:00am'],
        ['Wal-Mart Stores, Inc.', 45.45, 0.73, 1.63, '9/1 12:00am']
    ];

    var store = Ext.create('Ext.data.Store', {
        fields:['name', 'email', 'phone', 'active'],
        data:{
            items:[
                { name:'Lisa', email:'lisa@simpsons.com', phone:'555-111-1224', active:true  },
                { name:'Bart', email:'bart@simpsons.com', phone:'555-222-1234', active:true  },
                { name:'Homer', email:'home@simpsons.com', phone:'555-222-1244', active:false },
                { name:'Marge', email:'marge@simpsons.com', phone:'555-222-1254', active:true  }
            ]
        },
        proxy:{
            type:'memory',
            reader:{
                type:'json',
                root:'items'
            }
        }
    });

    Ext.define('Ext.edifecs.SearchField', {
        extend:'Ext.form.field.ComboBox',
        alias:'widget.searchfield',
        triggerCls:Ext.baseCSSPrefix + 'form-search-trigger',
        store:store,
        emptyCls:'searchEmptyField',
        queryMode:'local',
        listConfig:{
            loadingText:'Searching...',
            emptyText:'No matching posts found.'
        },
        typeAhead:true,
        displayField:'name',
        valueField:'email',
        forceSelection:true,
        matchFieldWidth:true
    });

    var required = '<span style="color:#af3a3a;font-weight:bold" data-qtip="Required">*</span>';


    function change(val) {
        if (val > 0) {
            return '<span style="color:green;">' + val + '</span>';
        } else if (val < 0) {
            return '<span style="color:red;">' + val + '</span>';
        }
        return val;
    }


    function pctChange(val) {
        if (val > 0) {
            return '<span style="color:green;">' + val + '%</span>';
        } else if (val < 0) {
            return '<span style="color:red;">' + val + '%</span>';
        }
        return val;
    }

    // add in some dummy descriptions
    for (var i = 0; i < Ext.grid.dummyData.length; i++) {
        Ext.grid.dummyData[i].push('Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Sed metus nibh, sodales a, porta at, vulputate eget, dui. Pellentesque ut nisl. ');
    }

    // create the data store
    var companyStore = Ext.create('Ext.data.ArrayStore', {
        fields:[
            {name:'company'},
            {name:'price', type:'float'},
            {name:'change', type:'float'},
            {name:'pctChange', type:'float'},
            {name:'lastChange', type:'date', dateFormat:'n/j h:ia'},
            {name:'desc'}
        ],
        data:Ext.grid.dummyData
    });


    var sellAction = Ext.create('Ext.Action', {
        iconCls:'delete', // Use a URL in the icon config
        text:'Sell stock',
        handler:function (widget, event) {

        }
    });

    var buyAction = Ext.create('Ext.Action', {
        iconCls:'add', // Use a URL in the icon config
        text:'Buy stock',
        handler:function (widget, event) {

        }
    });

    var selModel = Ext.create('Ext.selection.CheckboxModel', {
        listeners:{
            selectionchange:function (sm, selections) {
                grid4.down('#removeButton').setDisabled(selections.length === 0);
            }
        }
    });

    var editAction = Ext.create('Ext.Action', {
        iconCls:'edit', // Use a URL in the icon config
        text:'Edit stock'
    });

    var printAction = Ext.create('Ext.Action', {
        iconCls:'print', // Use a URL in the icon config
        text:'Print stock'
    });

    <!-- Context Menu -->
    var contextMenu = Ext.create('Ext.menu.Menu', {
        items:[
            buyAction,
            sellAction,
            editAction,
            printAction
        ]
    });

    <!-- Tooltips -->
    var tooltips = [
        {
            target:'tip1',
            html:'A very simple tooltip'
        },
        {
            target:'ajax-tip',
            width:200,
            autoLoad:{url:'ajax-tip.html'},
            dismissDelay:15000 // auto hide after 15 seconds
        },
        {
            target:'tip2',
            title:'My Tip Title',
            html:'Click the X to close me',
            autoHide:false,
            closable:true,
            draggable:true
        },
        {
            target:'track-tip',
            title:'Mouse Track',
            ui:'red-tooltip',
            width:200,
            html:'This tip will follow the mouse while it is over the element',
            trackMouse:true
        },
        {
            title:'<span>Rich Content Tooltip</span>',
            id:'content-anchor-tip',
            target:'leftCallout',
            anchor:'left',
            ui:'preview-box',
            html:null,
            width:415,
            autoHide:false,
            closable:false,
            contentEl:'content-tip', // load content from the page
            listeners:{
                'render':function () {
                    this.header.on('click', function (header, e) {
                        debugger;
                        e.stopEvent();
                        Ext.Msg.alert('Link', 'Link to something interesting.');
                        Ext.getCmp('content-anchor-tip').hide();
                    }, this, {delegate:'a'});
                }
            }
        },
        {
            target:'bottomCallout',
            anchor:'top',
            ui:'red-tooltip',
            anchorOffset:85, // center the anchor on the tooltip
            html:'This tip\'s anchor is centered'
        },
        {
            target:'trackCallout',
            anchor:'right',
            ui:'white-tooltip',
            trackMouse:true,
            html:'Tracking while you move the mouse'
        }
    ];


    Ext.create('Ext.container.Container',
        {
            title:'Guidlines',
            width:900,
            renderTo:Ext.getBody(),
            defaults:{
                margin:'10 0 0 20'
            },
            items:[
                <!-- Basic Inputs -->
                {
                    xtype:'container',
                    html:'<h2>1. Basic Inputs</h2>'
                },
                {
                    xtype:'fieldcontainer',
                    layout:{
                        type:'table',
                        columns:3,
                        tdAttrs:{
                            style:{
                                'vertical-align':'top'
                            }
                        }
                    },
                    fieldDefaults:{
                        labelAlign:'top',
                        msgTarget:'side'
                    },
                    defaults:{
                        margin:'0 10 10 0'
                    },
                    items:[
                        {
                            xtype:'textfield',
                            fieldLabel:'Textbox normal',
                            value:'Value'
                        },
                        {
                            xtype:'combobox',
                            fieldLabel:'Dropdown Normal',
                            store:states,
                            queryMode:'local',
                            displayField:'name',
                            valueField:'abbr',
                            value:'Value'
                        },
                        {
                            xtype:'numberfield',
                            fieldLabel:'Number Field',
                            value:25,
                            minValue:0,
                            maxValue:100
                        },
                        {
                            xtype:'textfield',
                            fieldLabel:'Password',
                            inputType:'password',
                            allowBlank:false,
                            value:'Value',
                            minLength:8
                        },
                        {
                            xtype:'form',
                            layout:'form',
                            border:false,
                            width:150,
                            defaultType:'textfield',
                            items:[
                                {
                                    fieldLabel:'DOB',
                                    name:'dob',
                                    xtype:'datefield',
                                    tooltip:'Enter your date of birth'
                                }
                            ]
                        },
                        {
                            xtype:'container',
                            items:[
                                {
                                    xtype:'textfield',
                                    fieldLabel:'Input Disabled',
                                    disabled:true,
                                    value:'textbox disabled'
                                }
                            ]
                        },
                        {
                            xtype:'textfield',
                            fieldLabel:'Field with Error',
                            afterLabelTextTpl:required,
                            allowBlank:false,
                            tooltip:'This is a error message example'
                        },
                        {
                            xtype:'searchfield',
                            fieldLabel:'Search Field',
                            emptyText:'Type to search'
                        },
                        {
                            xtype:'textareafield',
                            fieldLabel:'Text Area',
                            grow:true,
                            anchor:'100%',
                            width:300,
                            colspan:2,
                            height:200,
                            value:'Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry\'s standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book.'
                        }
                    ]
                },
                <!-- Checkboxes and Radiobuttons -->
                {
                    xtype:'container',
                    html:'<h2>2. Checkboxes and Radiobuttons</h2>'
                },
                {
                    xtype:'fieldcontainer',
                    layout:{type:'table', columns:3},
                    fieldDefaults:{
                        labelAlign:'top',
                        msgTarget:'side'
                    },
                    defaults:{
                        margin:'0 10 10 0'
                    },
                    items:[
                        {
                            xtype:'checkboxgroup',
                            fieldLabel:'Two Columns',
                            labelWidth:75,
                            columns:1,
                            vertical:true,
                            fieldDefaults:{
                                labelAlign:'left'
                            },
                            items:[
                                {
                                    boxLabel:'Anchovies',
                                    name:'topping',
                                    inputValue:'1',
                                    id:'checkbox1'
                                },
                                {
                                    boxLabel:'Artichoke Hearts',
                                    name:'topping',
                                    inputValue:'2',
                                    checked:true,
                                    id:'checkbox2'
                                },
                                {
                                    boxLabel:'Check Disabled',
                                    name:'topping',
                                    inputValue:'3',
                                    id:'checkbox3',
                                    checked:true,
                                    disabled:true
                                }
                            ]
                        },
                        {
                            xtype:'radiogroup',
                            fieldLabel:'Size',
                            labelWidth:30,
                            columns:1,
                            vertical:true,
                            fieldDefaults:{
                                labelAlign:'left'
                            },
                            items:[
                                {
                                    boxLabel:'Radio Button',
                                    name:'size',
                                    inputValue:'m',
                                    id:'radio1'
                                },
                                {
                                    boxLabel:'Radio Button',
                                    name:'size',
                                    inputValue:'l',
                                    id:'radio2'
                                },
                                {
                                    boxLabel:'Radio Disabled',
                                    name:'size',
                                    inputValue:'xl',
                                    id:'radio3',
                                    checked:true,
                                    disabled:true
                                }
                            ]
                        }
                    ]
                },
                <!-- Date Picker, Progress Bar, Slider, Tooltip, Preview Box -->
                {
                    xtype:'container',
                    html:'<h2>3. Date Picker, Progress Bar, Slider, Tooltip, Preview Box</h2>'
                },
                {
                    xtype:'container',
                    width:'100%',
                    layout:{
                        type:'table',
                        columns:3,
                        tableAttrs:{
                            style:{
                                width:'100%'
                            }
                        },
                        tdAttrs:{
                            style:{
                                'vertical-align':'top'
                            }
                        }
                    },
                    defaults:{
                        margin:'0 30 10 0'
                    },
                    items:[
                        {
                            xtype:'progressbar',
                            width:300,
                            id:'progressBarId'
                        },
                        {
                            xtype:'datepicker'
                        },
                        {
                            xtype:'container',
                            layout:'vbox',
                            items:[
                                {
                                    xtype:'slider',
                                    width:200,
                                    value:50,
                                    increment:10,
                                    minValue:0,
                                    maxValue:100
                                },
                                {
                                    xtype:'slider',
                                    width:200,
                                    value:50,
                                    increment:10,
                                    minValue:0,
                                    maxValue:100
                                },
                                {
                                    xtype:'slider',
                                    width:200,
                                    value:50,
                                    increment:10,
                                    minValue:0,
                                    maxValue:100
                                }
                            ]
                        },
                        {
                            xtype:'container',
                            colspan:2,
                            layout:'hbox',
                            items:[
                                {
                                    xtype:'container',
                                    colspan:3,
                                    layout:{type:'table', columns:3},
                                    defaults:{
                                        xtype:'button',
                                        scale:'medium',
                                        style:{
                                            "margin":"0px 5px 10px 0px"
                                        }
                                    },
                                    items:[
                                        {
                                            id:'tip1',
                                            text:'Basic ToolTip'
                                        },
                                        {
                                            id:'tip2',
                                            text:'autoHide disabled'
                                        },
                                        {
                                            id:'ajax-tip',
                                            text:'Ajax ToolTip'
                                        },
                                        {
                                            id:'track-tip',
                                            text:'Mouse Track'
                                        },
                                        {
                                            id:'leftCallout',
                                            text:'Anchor right, rich content'
                                        },
                                        {
                                            id:'bottomCallout',
                                            text:'Anchor below',
                                            width:200
                                        },
                                        {
                                            id:'trackCallout',
                                            text:'Anchor with tracking'
                                        }
                                    ]
                                }
                            ]
                        },
                        {
                            xtype:'container',
                            width:'100%',
                            layout:'hbox',
                            items:[
                                {
                                    xtype:'slider',
                                    hideLabel:true,
                                    height:214,
                                    vertical:true,
                                    minValue:0,
                                    maxValue:100,
                                    tipText:function (thumb) {
                                        return Ext.String.format('<b>{0}% complete</b>', thumb.value);
                                    }
                                },
                                {
                                    xtype:'slider',
                                    hideLabel:true,
                                    height:214,
                                    vertical:true,
                                    minValue:0,
                                    maxValue:100,
                                    tipText:function (thumb) {
                                        return Ext.String.format('<b>{0}% complete</b>', thumb.value);
                                    }
                                },
                                {
                                    xtype:'slider',
                                    hideLabel:true,
                                    height:214,
                                    vertical:true,
                                    minValue:0,
                                    maxValue:100,
                                    tipText:function (thumb) {
                                        return Ext.String.format('<b>{0}% complete</b>', thumb.value);
                                    }
                                }
                            ]
                        }
                    ]
                },
                <!-- Buttons, Toolbars and Tabbar -->
                {
                    xtype:'container',
                    html:'<h2>4. Buttons, Toolbars and Tabbar</h2>'
                },
                {
                    xtype:'container',
                    layout:'hbox',
                    defaults:{
                        margin:'0 5 30 0',
                        xtype:'button'
                    },
                    items:[
                        {
                            text:'Normal'
                        },
                        {
                            text:'Toggle',
                            enableToggle:true
                        },
                        {
                            text:'Normal',
                            iconCls:'add'
                        },
                        {
                            text:'Toggle',
                            enableToggle:true,
                            iconCls:'upload'
                        },
                        {
                            text:'Disabled',
                            iconCls:'delete',
                            disabled:true
                        }
                    ]
                },
                {
                    xtype:'container',
                    layout:'hbox',
                    defaults:{
                        margin:'0 5 30 0',
                        xtype:'button',
                        arrowAlign:'right'
                    },
                    items:[
                        {
                            text:'Normal'
                        },
                        {
                            text:'Menu',
                            menu:[
                                {text:'Item One'},
                                {text:'Item Two'},
                                {text:'Item Three'},
                                {text:'Item Four'}
                            ]
                        },
                        {
                            text:'Edit',
                            iconCls:'edit',
                            menu:[
                                {text:'Item One'},
                                {text:'Item Two'},
                                {text:'Item Three'},
                                {text:'Item Four'}
                            ]
                        }
                    ]
                },
                {
                    xtype:'container',
                    layout:'hbox',
                    defaults:{
                        margin:'0 5 30 0',
                        xtype:'splitbutton'
                    },
                    items:[
                        {
                            text:'Button'
                        },
                        {
                            text:'Split Button',
                            menu:new Ext.menu.Menu({
                                items:[
                                    {text:'Item 1'},
                                    {text:'Item 2'}
                                ]
                            })
                        },
                        {
                            text:'Publish',
                            iconCls:'publish',
                            menu:new Ext.menu.Menu({
                                items:[
                                    {text:'Item 1'},
                                    {text:'Item 2'}
                                ]
                            })
                        }
                    ]
                },
                {
                    xtype:'container',
                    layout:'hbox',
                    defaults:{
                        margin:'0 5 30 0',
                        scale:'medium',
                        xtype:'button'
                    },
                    items:[
                        {
                            text:'Normal'
                        },
                        {
                            text:'Toggle',
                            enableToggle:true
                        },
                        {
                            text:'Menu',
                            menu:new Ext.menu.Menu({
                                items:[
                                    {text:'Item 1'},
                                    {text:'Item 2'}
                                ]
                            })
                        },
                        {
                            text:'Menu',
                            iconCls:'report24',
                            menu:new Ext.menu.Menu({
                                items:[
                                    {text:'Item 1'},
                                    {text:'Item 2'}
                                ]
                            })
                        },
                        {
                            text:'Split Button',
                            xtype:'splitbutton',
                            menu:new Ext.menu.Menu({
                                items:[
                                    {text:'Item 1'},
                                    {text:'Item 2'}
                                ]
                            })
                        }
                    ]
                },
                {
                    xtype:'container',
                    layout:'hbox',
                    defaults:{
                        margin:'0 5 30 0',
                        scale:'large',
                        xtype:'button'
                    },
                    items:[
                        {
                            text:'Normal'
                        },
                        {
                            text:'Toggle',
                            enableToggle:true
                        },
                        {
                            text:'Menu',
                            menu:new Ext.menu.Menu({
                                items:[
                                    {text:'Item 1'},
                                    {text:'Item 2'}
                                ]
                            })
                        },
                        {
                            text:'Menu',
                            iconCls:'copy32',
                            menu:new Ext.menu.Menu({
                                items:[
                                    {text:'Item 1'},
                                    {text:'Item 2'}
                                ]
                            })
                        },
                        {
                            text:'Split Button',
                            xtype:'splitbutton',
                            menu:new Ext.menu.Menu({
                                items:[
                                    {text:'Item 1'},
                                    {text:'Item 2'}
                                ]
                            })
                        }
                    ]
                },
                {
                    xtype:'container',
                    layout:'vbox',
                    defaults:{
                        margin:'0 20 30 0'
                    },
                    items:[
                        {
                            xtype:'buttongroup',
                            shadow:false,
                            items:[
                                {
                                    iconCls:'add',
                                    tooltip:'Add a new item'
                                },
                                {
                                    text:'Upload',
                                    iconCls:'upload',
                                    tooltip:'Upload item',
                                    xtype:'button'
                                },
                                {
                                    text:'Toggle',
                                    iconCls:'delete',
                                    tooltip:'Delete item',
                                    xtype:'button',
                                    enableToggle:true
                                },
                                {
                                    text:'Edit',
                                    iconCls:'edit',
                                    tooltip:'Edit item',
                                    xtype:'button',
                                    disabled:true
                                },
                                {
                                    xtype:'button',
                                    iconCls:'partner',
                                    text:'Partner Menu',
                                    arrowAlign:'right',
                                    menu:[
                                        {text:'Item One'},
                                        {text:'Item Two'},
                                        {text:'Item Three'},
                                        {text:'Item Four'}
                                    ]
                                },
                                {
                                    text:'Report',
                                    xtype:'button',
                                    iconCls:'report',
                                    arrowAlign:'right',
                                    menu:[
                                        {text:'Item One'},
                                        {text:'Item Two'},
                                        {text:'Item Three'},
                                        {text:'Item Four'}
                                    ]
                                }
                            ]
                        },
                        {
                            xtype:'toolbar',
                            items:[
                                {
                                    iconCls:'add',
                                    tooltip:'Add a new item'
                                },
                                {
                                    text:'Upload',
                                    iconCls:'upload',
                                    tooltip:'Upload item',
                                    xtype:'button'
                                },
                                {
                                    text:'Toggle',
                                    iconCls:'delete',
                                    tooltip:'Delete item',
                                    xtype:'button',
                                    enableToggle:true
                                },
                                {
                                    text:'Edit',
                                    iconCls:'edit',
                                    tooltip:'Edit item',
                                    xtype:'button',
                                    disabled:true
                                },
                                {
                                    xtype:'button',
                                    iconCls:'partner',
                                    text:'Partner Menu',
                                    arrowAlign:'right',
                                    menu:[
                                        {text:'Item One'},
                                        {text:'Item Two'},
                                        {text:'Item Three'},
                                        {text:'Item Four'}
                                    ]
                                },
                                {
                                    text:'Report',
                                    xtype:'button',
                                    iconCls:'report',
                                    arrowAlign:'right',
                                    menu:[
                                        {text:'Item One'},
                                        {text:'Item Two'},
                                        {text:'Item Three'},
                                        {text:'Item Four'}
                                    ]
                                }
                            ]
                        },
                        {
                            xtype:'CustomButtonGroup',
                            buttonItems:[
                                {
                                    iconCls:'add',
                                    tooltip:'Add',
                                    itemId:'addId'
                                },
                                {
                                    text:'Edit',
                                    tooltip:'Edit',
                                    itemId:'editId'
                                },
                                {
                                    iconCls:'delete',
                                    text:'Delete',
                                    tooltip:'Delete',
                                    iconAlign:'right',
                                    itemId:'deleteId'
                                },
                                {
                                    iconCls:'email',
                                    tooltip:'Email',
                                    text:'Email',
                                    itemId:'emailId'
                                },
                                {
                                    text:'Export',
                                    tooltip:'Export',
                                    itemId:'exportId',
                                    hidden:false,
                                    disabled:true
                                },
                                {
                                    iconCls:'download',
                                    text:'Link',
                                    tooltip:'Link',
                                    iconAlign:'right',
                                    itemId:'linkId'
                                }
                            ]
                        },
                        {
                            xtype:'tabpanel',
                            width:600,
                            activeTab:0,
                            plain:true,
                            items:[
                                {
                                    xtype:'gridpanel',
                                    store:companyStore,
                                    columnLines:true,
                                    columns:[
                                        {
                                            text:'Company',
                                            flex:1,
                                            sortable:false,
                                            dataIndex:'company'
                                        },
                                        {
                                            text:'Price',
                                            width:75,
                                            sortable:true,
                                            renderer:'usMoney',
                                            dataIndex:'price'
                                        },
                                        {
                                            text:'Change',
                                            width:75,
                                            sortable:true,
                                            renderer:change,
                                            dataIndex:'change'
                                        },
                                        {
                                            text:'% Change',
                                            width:75,
                                            sortable:true,
                                            renderer:pctChange,
                                            dataIndex:'pctChange'
                                        },
                                        {
                                            text:'Last Updated',
                                            width:85,
                                            sortable:true,
                                            renderer:Ext.util.Format.dateRenderer('m/d/Y'),
                                            dataIndex:'lastChange'
                                        }
                                    ],
                                    bbar:Ext.create('Ext.PagingToolbar', {
                                        store:companyStore,
                                        displayInfo:true,
                                        displayMsg:'Displaying topics {0} - {1} of {2}',
                                        emptyMsg:"No topics to display",
                                        items:[
                                            '-', {
                                                text:'Show Preview',
                                                enableToggle:true
                                            }]
                                    }),
                                    dockedItems:[
                                        {
                                            xtype:'toolbar',
                                            items:[
                                                buyAction, sellAction
                                            ]
                                        }
                                    ],
                                    viewConfig:{
                                        stripeRows:true,
                                        listeners:{
                                            itemcontextmenu:function (view, rec, node, index, e) {
                                                e.stopEvent();
                                                contextMenu.showAt(e.getXY());
                                                return false;
                                            }
                                        }
                                    },
                                    height:350,
                                    width:600,
                                    title:'Action Grid',
                                    stateful:false
                                },
                                {
                                    title:'Business View'
                                },
                                {
                                    title:'CMS-1500',
                                    disabled:true
                                },
                                {
                                    title:'Data View',
                                    closable:true
                                },
                                {
                                    title:'Inbox',
                                    iconCls:'print'
                                },
                                {
                                    title:'Business View'
                                },
                                {
                                    title:'CMS-1500',
                                    disabled:true
                                },
                                {
                                    title:'Data View',
                                    closable:true
                                },
                                {
                                    title:'Inbox',
                                    iconCls:'print'
                                }
                            ]
                        }
                    ]
                },
                <!-- Grid and Scrollbar -->
                {
                    xtype:'container',
                    html:'<h2>5. Grid and Scrollbar</h2>'
                },
                {
                    xtype:'gridpanel',
                    store:companyStore,
                    plugins:[
                        {
                            ptype:'rowexpander',
                            rowBodyTpl:new Ext.XTemplate(
                                '<p><b>Company:</b> {company}</p>',
                                '<p><b>Change:</b> {change:this.formatChange}</p><br>',
                                '<p><b>Summary:</b> {desc}</p>',
                                {
                                    formatChange:function (v) {
                                        var color = v >= 0 ? 'green' : 'red';
                                        return '<span style="color: ' + color + ';">' + Ext.util.Format.usMoney(v) + '</span>';
                                    }
                                })
                        }
                    ],
                    columns:[
                        {text:"Company", flex:1, sortable:true, dataIndex:'company'},
                        {text:"Price", width:120, sortable:true, renderer:Ext.util.Format.usMoney, dataIndex:'price'},
                        {text:"Change", width:120, sortable:true, dataIndex:'change'},
                        {text:"% Change", width:120, sortable:true, dataIndex:'pctChange'},
                        {text:"Last Updated", width:120, sortable:true, renderer:Ext.util.Format.dateRenderer('m/d/Y'), dataIndex:'lastChange'}
                    ],
                    collapsible:true,
                    selModel:selModel,
                    dockedItems:[
                        {
                            xtype:'toolbar',
                            dock:'bottom',
                            ui:'footer',
                            layout:{
                                pack:'center'
                            },
                            items:[
                                {
                                    minWidth:80,
                                    text:'Save'
                                },
                                {
                                    minWidth:80,
                                    text:'Cancel'
                                }
                            ]
                        },
                        {
                            xtype:'toolbar',
                            items:[
                                {
                                    text:'Add Something',
                                    tooltip:'Add a new row',
                                    iconCls:'add'
                                },
                                '-',
                                {
                                    text:'Options',
                                    tooltip:'Set options',
                                    iconCls:'edit'
                                },
                                '-',
                                {
                                    itemId:'removeButton',
                                    text:'Remove Something',
                                    tooltip:'Remove the selected item',
                                    iconCls:'delete',
                                    disabled:true
                                }
                            ]
                        }
                    ],
                    width:800,
                    height:400,
                    frame:true,
                    title:'Support for standard Panel features such as framing, buttons and toolbars'
                },
                <!-- Panel, Splitter -->
                {
                    xtype:'container',
                    html:'<h2>6. Panel, Splitter</h2>'
                },
                {
                    xtype:'container',
                    layout:'hbox',
                    defaults:{
                        margin:'0 20 30 0',
                        xtype:'panel',
                        flex:1,
                        title:'Masked Panel with a really long title'
                    },
                    items:[
                        {
                            width:500,
                            height:200,
                            collapsible:true,
                            autoScroll:true,
                            collapseDirection:Ext.Component.DIRECTION_LEFT,
                            items:[
                                {
                                    xtype:'gridpanel',
                                    width:399,
                                    store:companyStore,
                                    columns:[
                                        {text:"Company", sortable:true, dataIndex:'company'},
                                        {text:"Price", sortable:true, renderer:Ext.util.Format.usMoney, dataIndex:'price'},
                                        {text:"Change", sortable:true, dataIndex:'change'},
                                        {text:"% Change", sortable:true, dataIndex:'pctChange'},
                                        {text:"Last Updated", sortable:true, renderer:Ext.util.Format.dateRenderer('m/d/Y'), dataIndex:'lastChange'}
                                    ]
                                }
                            ]
                        },
                        {
                            width:500,
                            height:200,
                            collapsible:true,
                            collapsed:true
                        }
                    ]
                },
                {
                    xtype:'container',
                    layout:'hbox',
                    defaults:{
                        margin:'0 20 30 0',
                        xtype:'panel',
                        flex:1,
                        title:'Frame Panel collapsed/without collapsed'
                    },
                    items:[
                        {
                            frame:true,
                            height:200,
                            collapsible:true,
                            autoScroll:true,
                            collapseDirection:Ext.Component.DIRECTION_LEFT,
                            items:[
                                {
                                    xtype:'gridpanel',
                                    width:399,
                                    store:companyStore,
                                    columns:[
                                        {text:"Company", sortable:true, dataIndex:'company'},
                                        {text:"Price", sortable:true, renderer:Ext.util.Format.usMoney, dataIndex:'price'},
                                        {text:"Change", sortable:true, dataIndex:'change'},
                                        {text:"% Change", sortable:true, dataIndex:'pctChange'},
                                        {text:"Last Updated", sortable:true, renderer:Ext.util.Format.dateRenderer('m/d/Y'), dataIndex:'lastChange'}
                                    ]
                                }
                            ]
                        },
                        {
                            width:500,
                            frame:true,
                            height:200,
                            collapsible:true,
                            collapsed:true
                        }
                    ]
                },
                <!-- Tree, Tree Grid -->
                {
                    xtype:'container',
                    html:'<h2>7. Tree, Tree Grid</h2>'
                },
                {
                    xtype:'container',
                    layout:{type:'table', columns:2},
                    defaults:{margin:'10 0 10 10'},
                    items:[
                        {
                            xtype:'treepanel',
                            rootVisible:false,
                            useArrows:true,
                            frame:true,
                            title:'Check Tree',
                            width:250,
                            height:300,
                            store:new Ext.data.TreeStore({
                                proxy:{
                                    type:'ajax',
                                    url:'./json/check-nodes.json'
                                },
                                sorters:[
                                    {
                                        property:'leaf',
                                        direction:'ASC'
                                    },
                                    {
                                        property:'text',
                                        direction:'ASC'
                                    }
                                ]
                            })
                        },
                        {
                            xtype:'treepanel',
                            title:'Simple Tree',
                            frame:true,
                            width:250,
                            height:300,
                            rootVisible:false,
                            store:Ext.create('Ext.data.TreeStore', {
                                root:{
                                    expanded:true,
                                    children:[
                                        {text:"detention", leaf:true},
                                        {text:"homework", expanded:true, children:[
                                            {text:"book report", leaf:true},
                                            {text:"algebra", leaf:true}
                                        ]},
                                        {text:"buy lottery tickets", leaf:true}
                                    ]
                                }})
                        },
                        {
                            xtype:'treepanel',
                            title:'Core Team Projects',
                            height:300,
                            width:550,
                            colspan:2,
                            useArrows:true,
                            rootVisible:false,
                            multiSelect:true,
                            singleExpand:true,
                            store:new Ext.data.TreeStore({
                                fields:[
                                    {
                                        name:'task',
                                        type:'string'
                                    },
                                    {
                                        name:'user',
                                        type:'string'
                                    },
                                    {
                                        name:'duration',
                                        type:'float'
                                    },
                                    {
                                        name:'done',
                                        type:'boolean'
                                    }
                                ],
                                proxy:{
                                    type:'ajax',
                                    url:'./json/treegrid.json'
                                },
                                folderSort:true
                            }),
                            columns:[
                                {
                                    xtype:'treecolumn', //this is so we know which column will show the tree
                                    text:'Task',
                                    flex:2,
                                    sortable:true,
                                    dataIndex:'task'
                                },
                                {
                                    xtype:'templatecolumn',
                                    text:'Duration',
                                    flex:1,
                                    sortable:true,
                                    dataIndex:'duration',
                                    align:'center',
                                    tpl:Ext.create('Ext.XTemplate', '{duration:this.formatHours}', {
                                        formatHours:function (v) {
                                            if (v < 1) {
                                                return Math.round(v * 60) + ' mins';
                                            } else if (Math.floor(v) !== v) {
                                                var min = v - Math.floor(v);
                                                return Math.floor(v) + 'h ' + Math.round(min * 60) + 'm';
                                            } else {
                                                return v + ' hour' + (v === 1 ? '' : 's');
                                            }
                                        }
                                    })
                                },
                                {
                                    text:'Assigned To',
                                    flex:1,
                                    dataIndex:'user',
                                    sortable:true
                                },
                                {
                                    xtype:'checkcolumn',
                                    header:'Done',
                                    dataIndex:'done',
                                    width:55,
                                    stopSelection:false,
                                    menuDisabled:true
                                }
                            ]
                        }
                    ]
                },
                <!-- Dialog Window -->
                {
                    xtype:'container',
                    html:'<h2>8. Dialog Window</h2>'
                },
                {
                    xtype:'container',
                    width:600,
                    height:400,
                    layout:'hbox',
                    border:true,
                    items:[
                        {
                            xtype:'button',
                            text:'Dialog Box',
                            margin:'10',
                            handler:function () {
                                //if(!win)
                                win.show();

                            }
                        },
                        {
                            xtype:'button',
                            text:'Message Box',
                            margin:'10',
                            handler:function () {
                                Ext.MessageBox.show({
                                    title:'Address',
                                    msg:'Please enter your address:',
                                    width:300,
                                    buttons:Ext.MessageBox.OKCANCEL,
                                    animateTarget:'mb4',
                                    icon:Ext.MessageBox.QUESTION
                                });
                            }
                        }
                    ]
                }
            ]
        });

    Ext.getCmp("progressBarId").wait({
        interval:500,
        duration:50000,
        increment:15,
        text:'Updating...',
        scope:this
    });

    Ext.each(tooltips, function (config) {
        Ext.create('Ext.tip.ToolTip', config);
    });

    win = Ext.create('Ext.window.Window', {
        height:300,
        width:550,
        modal:true,
        layout:'hbox',
        title:'Add or Edit Widget',
        closable:true,
        items:[
            {
                xtype:'gridpanel',
                margin:10,
                flex:1,
                store:store,
                columns:[
                    { text:'Name', dataIndex:'name' },
                    { text:'Email', dataIndex:'email', flex:1 },
                    { text:'Phone', dataIndex:'phone' }
                ],
                height:240,
                width:400,
                dockedItems:[
                    {
                        xtype:'toolbar',
                        dock:'bottom',
                        padding:'6 0 0 0',
                        ui:'footer',
                        items:[
                            '->',
                            {
                                xtype:'button',
                                text:'Cancel',
                                iconCls:'close'
                            },
                            {
                                xtype:'button',
                                text:'Ok'
                            }
                        ]
                    }
                ]
            }
        ]
    });

});