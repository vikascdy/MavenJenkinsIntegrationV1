Ext.define('Security.view.common.GridDragAndDrop', {
    extend: 'Ext.window.Window',
    alias : 'widget.griddraganddrop',

    requires: [
        'Ext.grid.plugin.DragDrop',
        'Ext.toolbar.Spacer'
    ],

    closable:true,
    constructor: function(config) {
        var me = this;
        Ext.apply(this, config || {});

        Ext.apply(this, {
            items:[
                {
                    xtype: 'grid',
                    hideHeaders: true,
                    itemId: 'leftGrid',
                    viewConfig: {
                        plugins: {
                            ptype: 'gridviewdragdrop',
                            dragGroup: 'firstGridDDGroup',
                            dropGroup: 'secondGridDDGroup'
                        }
                        ,listeners: {
                            beforedrop: function( node, data, overModel,  dropPosition,  dropHandlers){
                            	dropHandlers.wait=true;
                                me.setSrcGrid(data.records, me.down('#rightGrid'),me);
                            }
                        }
                    },
                    selModel:{
                      mode:'MULTI'
                    },
                    border:true,
                    store: config.left.store,
                    columns : config.columns,
                    title: config.left.title,
                    flex:5
                },
                {
                    xtype: 'toolbar',
                    minWidth: 55,
                    maxWidth: 55,
                    vertical:true,
                    style: {
                        border: 0,
                        padding: 0
                    },
                    items: [
                        {
                            xtype: 'tbspacer',
                            padding: '100 0 0 0'
                        },
                        {
                            xtype:'button',
                            icon:'resources/icons/shuttle-right.png',
                            tooltip:'Add selected records',
                            handler: Ext.bind(me.move, me, ['#leftGrid', '#rightGrid']),
                            padding: {top:10, right:10, bottom:10, left:10}
                            ,margin: {top:10, right:10, bottom:10, left:10}
                        },
                        {
                            xtype:'button',
                            icon:'resources/icons/shuttle-left.png',
                            tooltip:'Remove selected records',
                            padding: {top:10, right:10, bottom:10, left:10}
                            ,margin: {top:0, right:10, bottom:10, left:10}
                            ,handler: Ext.bind(me.move, me, ['#rightGrid', '#leftGrid'])
                        }
                    ]
                },
                {
                    xtype: 'grid',
                    hideHeaders: true,
                    itemId: 'rightGrid',
                    viewConfig: {
                        plugins: {
                            ptype: 'gridviewdragdrop',
                            dragGroup: 'secondGridDDGroup',
                            dropGroup: 'firstGridDDGroup',
                            enableDrag:false
                        }
                        ,listeners: {
                            beforedrop: function( node, data, overModel,  dropPosition,  dropHandlers){
                                me.setSrcGrid(data.records, me.down('#leftGrid'),me);
                            }
                        }
                    },
                    selModel:{
                      mode:'MULTI'
                    },
                    border:true,
                    store: config.right.store,
                    columns : config.columns,
                    title: config.right.title,
                    flex:5
                }
            ],
            buttons:[
                {
                    text:'Cancel',
                    handler:function() {
                        me.fireEvent('cancel', me);
                    }
                },
                {
                    text:'Ok',
                    ui:'greenbutton',
                    handler:function() {
                        var toGrid = me.down('#rightGrid');
                        var removedRecords = toGrid.store.getRemovedRecords();
                        toGrid.store.each(function(rec){
                            if (rec.srcGrid != null) {
                                if (rec.srcGrid != toGrid) {
                                    rec.phantom = true;
                                }
                            }
                            Ext.Array.remove( removedRecords, rec);
                        }, this);
                        me.fireEvent('selectionsave', me);
                    }
                }
            ]

        });
        this.callParent();
        return this;
    },

    initComponent: function() {
        var me = this;
        me.callParent();
        me.addEvents(
            'selectionsave',
            'cancel'
        );
    },
    move: function(fromId, toId){
    	var me=this;
        var fromGrid = this.down(fromId);
        var toGrid = this.down(toId);
        var selection = fromGrid.getSelectionModel().getSelection();
        
        
        Ext.each(selection, function(role, index){
        	
        	if(me.originalRecordIds && me.originalRecordIds.containsKey(role.get('id')) )
        		{
            	Functions.errorMsg('You cannot remove the "'+ role.get('canonicalName') +'" here','Move Error');
            	return false;
        		}
        	else
        		{        	
		            if (!role.srcGrid){
		                role.srcGrid = fromGrid;
		            }
		            toGrid.store.add(role);
		            fromGrid.store.remove(role);
        		}
        });
    },
    width:550,
    height:400,
    bodyPadding:10,
    layout       : {
        type: 'hbox',
        align: 'stretch',
        defaultMargins: {top: 0, right: 0, bottom: 0, left: 0},
        padding: 0
    },
    border:false,
    defaults     : { flex : 1 }, //auto stretch
    modal:true,
    
    
    setSrcGrid:function (records, srcGrid, me) {
    	
        Ext.each(records, function (record, index) {
        	if(me.originalRecordIds && me.originalRecordIds.containsKey(record.get('id')) ){  
        		Functions.errorMsg('You cannot remove the "'+ record.get('canonicalName') +'" here','Move Error');
        		return false;            	
        	}
        	else
        	{
	            if (!record.srcGrid) {
	                record.srcGrid = srcGrid;
	            }
	            return true;
        	}
        });
    }

});
