Ext.require([
    'Ext.grid.plugin.BufferedRenderer',
    'Ext.ux.form.SearchField'
]);


Ext.define('Security.view.common.CommonManagePage', {
    extend: 'Ext.container.Container',
    alias: 'widget.commonmanagepage',
    layout:'border',
    config:{
        minHeight:700
    },
    defaults : {
        style:{
            backgroundColor:'#FFF!important'
        }
    },
    initComponent  :    function () {
                var me=this;
                var detailPage = {};
                 if (me.detailPage) {
                                 detailPage=me.detailPage;
         }
                
        this.items = [
            {
                xtype:'component',
                itemId:'pageHeader',
                region:'north',
                padding:'20 0 0 20',
                height:60
            },
            {
                xtype:'container',
                layout:'fit',
                itemId:'gridHolder',
                region:'center',
                padding:'0 10 20 20',
                split:true,
                width:'40%'
            },
            {
                xtype:'container',
                layout:'fit',
                itemId:'detailHolder',
                margin:'0 10 20 0',
                region:'east',
                width:'60%',
                hidden: me.detailPage ? false :true,
                                                items:[detailPage]
            }
        ];
        this.callParent(arguments);
    },
    afterRender: function () {
        var me = this;
        // create store
        var pageConfigStore = this.getPageConfiguration(this.configurationUrl);


        pageConfigStore.on("load", function (store, records) {
            var config = records[0];
            me.createPageHeader(config.get('heading'));
            me.createStoreForGrid(config, function(store) {
                me.createItemsGrid(config, store, function() {
//                    if (me.detailPage) {
//                        me.down('#detailHolder').removeAll();
//                        me.down('#detailHolder').add(me.detailPage);
//                    }
//                    else
//                            me.down('#detailHolder').hide();
                });
            });
        });

        Security.view.common.CommonManagePage.superclass.afterRender.apply(this, arguments);
        return;
    },

    createPageHeader :  function(header) {
        if (this.down('#pageHeader'))
            this.down('#pageHeader').update('<h1>' + header + '</h1>');
        return;
    },

    createStoreForGrid : function(config, callback) {

        var storeConfigObj = {
            model:config.get('model'),
            storeId:config.get('ref') + 'StoreId',
            autoLoad:false
        };
        
        if(config.get('grouping').enabled)
                storeConfigObj['groupField'] = config.get('grouping').groupField;
        
        
        if (config.get('pagination').enabled) {
            storeConfigObj['pageSize'] = 20;
            storeConfigObj['remoteFilter'] = true;
                                                storeConfigObj['remoteSort'] = true;

                                                if(!config.get('pagination').toolbar){
                                                                storeConfigObj['buffered'] = true;
                                                                storeConfigObj['leadingBufferZone'] = 100;
                                                                }
        }

        if(!config.get('usingREST'))
                {
                                var tempModel = Ext.create(config.get('model'));
                                storeConfigObj['proxy'] = tempModel.proxy;
                }
       
        var store = Ext.create('Ext.data.Store', storeConfigObj);

        Ext.callback(callback, this, [store]);
    },

    createItemsGrid : function(config, store, callback) {
        var me = this;
        var gridHolder = this.down('#gridHolder');
        
        if (gridHolder) {
                                var columnsList = [{xtype:'rownumberer',width:50, align:'center'}];
           /* var columnsList = [{dataIndex: 'id', width: 30, menuDisabled:true, sortable : false,
                                                                                renderer: function(value, m, record) {
                                                                                                var id = Ext.id();
                                                            Ext.defer(function () {
                                                                Ext.widget({
                                                                                xtype:'checkbox',
                                                                                renderTo:id,
                                                                                hideLabel:true,
                                                                                cls:'checkField',
                                                                                record:record
                                                                });
                                                            }, 100);
                                                            return Ext.String.format('<div id="{0}"></div>', id);
                                                                                }
                                                                                                }];*/
            
            Ext.each(config.get('columns'), function(column) {
                columnsList.push({
                    header:column.header,
                    dataIndex:column.field,
                    menuDisabled:true,
                    sortable : false,
                    flex:1,
                    renderer : function(v,m,r){
                                if(column.linkable)
                                                return Ext.String.format('<a id="'+config.get('ref')+'Detail'+r.get('id')+'" href="#">{0}</a>', v);
                                else
                                                return v;
                    }
                })
            });

            var features = [];

            if (config.get('grouping').enabled)
                features.push({ftype:'grouping'});

            var grid = Ext.create('Ext.grid.Panel', {
                itemId:config.get('ref') + 'Grid',
                columns:columnsList,
                                                                viewConfig: {
                                                                                getRowClass: function(record, rowIndex, rowParams, store){
                                                                                                return "select-grid-row";
                                                                                }
                                                                },
                store:store,
                                                                selModel : Ext.create('Ext.selection.RowModel', {
                     enableKeyNav : false,
                                                                                toggleOnClick : false
               }),
//                selModel : Ext.create('Ext.selection.CheckboxModel', {
//                            showHeaderCheckbox:false,
//                            mode:'SINGLE'
//                }),
                loadMask:true,
                features: features,
                tbar:Ext.create('Ext.toolbar.Toolbar', {
                    padding:5,
                    itemId:'gridToolbar'

                }),
                                                                bbar: (config.get('pagination').enabled && config.get('pagination').toolbar) ? {
                                                                                xtype:'pagingtoolbar',
                    store: store,   // same store GridPanel is using
                                                                                displayInfo: true,
                                                                                listeners : {
                                                                                                'change' : function(toolbar, pageData) {
                                                                                                                var record =
                                    store.getAt(0);
                                if (record)
                                    grid.getSelectionModel().select(record);
                                                                                                }
                                                                                }              
                } : {},                                                      
                plugins: (config.get('pagination').enabled && !config.get('pagination').toolbar) ? [
                    {
                        ptype: 'bufferedrenderer',
                        trailingBufferZone: 20,  // Keep 25 rows rendered in the table behind scroll
                        leadingBufferZone: 20   // Keep 50 rows rendered in the table ahead of scroll
                    }
                ] : [],
                listeners : {
                    'render' : function(grid) {
                                                                                if(this.down('#gridToolbar'))
                        this.down('#gridToolbar').add(config.get('useButtonToolbar') ? me.getButtonToolbar(grid, config) : me.getHyperLinkToolbar(grid, config));
                        var store = this.getStore();
                        
                        
                        if(!config.get('usingREST') && me.getLoadingParams()){
                            var paramObj =     me.getLoadingParams();
                            for (var i in paramObj) {
                                store.getProxy().setExtraParam(i,paramObj[i]);                            
                            }

                        }
                        
                        
                        store.load({
                            callback : function() {
                                var record =
                                    store.getAt(0);
                                if (record)
                                    grid.getSelectionModel().select(record);
                                grid.updateLayout();
                                grid.setLoading(false);
                            }
                        });
                    },
                    'selectionchange' : function(selectionModel, selected) {
                        if (selected.length > 0) {
                            grid.down('#editItem').setDisabled(selected.length === 0);
                            grid.down('#deleteItem').setDisabled(selected.length === 0);
                        }
                        else {
                            grid.down('#editItem').setDisabled(true);
                            grid.down('#deleteItem').setDisabled(true);
                        }
                    }
                }
            });
            gridHolder.removeAll();
            gridHolder.add(grid);
            Ext.callback(callback, this, [grid]);
        }
    },


    // get configuration for rendering
    getPageConfiguration: function (configurationUrl) {
        var store = new Ext.create('Ext.data.Store',
            {
                autoLoad: true,
                autoSync: true,
                storeId: 'configStore',
                proxy: {
                    type: 'ajax',
                    url: configurationUrl,
                    reader: {
                        type: 'json'
                    }
                },
                fields: [
                    {name: 'heading', type: 'string'},
                    {name: 'ref', type: 'string'},
                    {name: 'alias', type: 'string'},
                    {name: 'usingREST', type: 'boolean', defaultValue: false},
                    {name: 'params', type: 'auto'},
                    {name: 'model', type: 'string'},
                    {name: 'grouping', type: 'auto'},
                    {name: 'searching', type: 'auto'},
                    {name: 'columns', type: 'auto'},
                    {name: 'operations', type: 'auto'},
                    {name: 'useButtonToolbar', type: 'boolean', defaultValue: true},
                    {name: 'pagination', type: 'auto'}
                ]
            });
        return store;
    },

    getButtonToolbar : function(grid, config) {
        var store = grid.getStore();
        return [
            {
                text:'New',
                action:'new' + config.get('ref'),
                id:'new' + config.get('ref')+'-link',
                tooltip:'Create New ' + config.get('alias'),
                hidden:!config.get('operations').create,
                handler : function() {
                    grid.fireEvent('newItem', this);
                }
            },
            {
                text:'Edit',
                action:'edit' + config.get('ref'),
                id:'edit' + config.get('ref')+'-link',
                tooltip:'Edit ' + config.get('alias'),
                itemId:'editItem',
                disabled:true,
                hidden:!config.get('operations').edit
            },
            {
                text:'Delete',
                action:'delete' + config.get('ref'),
                id:'delete' + config.get('ref')+'-link',
                tooltip:'Delete ' + config.get('alias'),
                itemId:'deleteItem',
                disabled:true,
                hidden:!config.get('operations').delete,
                handler : function() {
                    grid.fireEvent('deleteItem', this);
                }
            },
            {
                text:'Import',
                action:'import' + config.get('ref'),
                id:'import' + config.get('ref')+'-link',
                tooltip:'Import ' + config.get('alias'),
                itemId:'importItem',
                disabled:true,
                hidden:!config.get('operations').import,
                handler : function() {
                    grid.fireEvent('importItem', this);
                }
            },
                                                {
                text:'Export',
                action:'export' + config.get('ref'),
                id:'export' + config.get('ref')+'-link',
                tooltip:'Export ' + config.get('alias'),
                itemId:'exportItem',
                disabled:true,
                hidden:!config.get('operations').export,
                handler : function() {
                    grid.fireEvent('exportItem', this);
                }
            },
            '->',
            {
                xtype:'textfield',
                emptyText:'Search ' + config.get('alias'),
                itemId:'search' + config.get('ref'),
                id:'search' + config.get('ref')+'-field',
                hidden:!config.get('searching').enabled,
                enableKeyEvents:true,
                listeners : {
                    'keyup' : function(field) {
                        if (config.get('searching').enabled) {
                            store.clearFilter(true);
                            store.filter(config.get('searching').searchField, field.getValue());
                            var record =
                                store.getAt(0);
                            if (record)
                                grid.getSelectionModel().select(record);
                            field.focus();
                        }
                    }
                }
            }
        ];
    },

    getHyperLinkToolbar : function(grid, config) {
                var me=this;
        var store = grid.getStore();
        return [
            {
                xtype:'component',
                hidden:!config.get('operations').create,
                margin:'5 0 0 10',
                html:'<a href="#" id="new' + config.get('ref')+'-link" class="newItem quickLinks">New</a>',
                listeners : {
                    'afterrender':function () {
                        this.getEl().on('click', function(e, t, opts) {
                            e.stopEvent();
                            grid.fireEvent('newItem', grid, me);
                        }, null, {delegate: '.newItem'});

                    }
                }
            },
            {
                xtype:'component',
                hidden:!config.get('operations').edit,
                itemId:'editItem',
                margin:'5 0 0 10',
                html:'<a href="#" id="edit' + config.get('ref')+'-link" class="editItem quickLinks">Edit</a>',
                listeners : {
                    'afterrender':function () {
                        this.getEl().on('click', function(e, t, opts) {
                            e.stopEvent();
                            grid.fireEvent('editItem', grid, me);
                        }, null, {delegate: '.editItem'});

                    }
                }
            },
            {
                xtype:'component',
                hidden:!config.get('operations').delete,
                itemId:'deleteItem',
                margin:'5 0 0 10',
                html:'<a href="#" id="delete' + config.get('ref')+'-link" class="deleteItem quickLinks">Delete</a>',
                listeners : {
                    'afterrender':function () {
                        this.getEl().on('click', function(e, t, opts) {
                            e.stopEvent();
                            grid.fireEvent('deleteItem', grid, me);
                        }, null, {delegate: '.deleteItem'});

                    }
                }
            },
                                                {
                xtype:'component',
                hidden:!config.get('operations').import,
                itemId:'importItem',
                margin:'5 0 0 10',
                html:'<a href="#" id="import' + config.get('ref')+'-link" class="importItem quickLinks">Import</a>',
                listeners : {
                    'afterrender':function () {
                        this.getEl().on('click', function(e, t, opts) {
                            e.stopEvent();
                            grid.fireEvent('importItem', grid, me);
                        }, null, {delegate: '.importItem'});

                    }
                }
            },
                                                {
                xtype:'component',
                hidden:!config.get('operations').export,
                itemId:'exportItem',
                margin:'5 0 0 10',
                html:'<a href="#" id="export' + config.get('ref')+'-link" class="exportItem quickLinks">Export</a>',
                listeners : {
                    'afterrender':function () {
                        this.getEl().on('click', function(e, t, opts) {
                            e.stopEvent();
                            grid.fireEvent('exportItem', grid, me);
                        }, null, {delegate: '.exportItem'});

                    }
                }
            },
            '->',
//            {
//                xtype:'textfield',
//                emptyText:'Search ' + config.get('alias'),
//                itemId:'search' + config.get('ref'),
//                hidden:!config.get('searching').enabled,
//                enableKeyEvents:true,
//                listeners : {
//                    'keyup' : function(field) {
//                        if (config.get('searching').enabled) {
//                            store.clearFilter(true);
//                            store.filter(config.get('searching').searchField, field.getValue());
//                            var record =
//                                store.getAt(0);
//                            if (record)
//                                grid.getSelectionModel().select(record);
//                            field.focus();
//                        }
//                    }
//                }
//            }
            {
                xtype: 'searchfield',
                flex:1,
                itemId:'search' + config.get('ref'),
                id:'search' + config.get('ref')+'-field',
                emptyText:'Search ' + config.get('alias'),
                labelWidth: 50,
                hidden:!config.get('searching').enabled,
                store: grid.getStore()
            }
        ];
    },


    // private, clean up
    onDestroy: function () {
        this.removeAll();
        Security.view.common.CommonManagePage.superclass.onDestroy.apply(this, arguments);
    }
});
