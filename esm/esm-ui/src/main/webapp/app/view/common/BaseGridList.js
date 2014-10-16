Ext.require([
    'Ext.grid.plugin.BufferedRenderer',
    'Ext.ux.form.SearchField'
]);

Ext.define('Security.view.common.BaseGridList' ,{
    extend: 'Ext.grid.Panel',
    alias: 'widget.basegridlist',
    autoScroll:true,
    loadMask:true,
    plugins: {
        ptype: 'bufferedrenderer',
        trailingBufferZone: 20,  // Keep 20 rows rendered in the table behind scroll
        leadingBufferZone: 50   // Keep 50 rows rendered in the table ahead of scroll
    },

    initComponent: function() {
	
		var me=this;		
		
		me.store = Ext.StoreManager.lookup(me.storeName);		
		
//		me.store.on('load',function(store,records,success){
//
//			if(me.originalRecordIds && success){
//				me.originalRecordIds.each(function(key,value) {
//                    var index = me.store.find('id', key);
//                    if(index!=-1)
//                   	 	me.store.removeAt(index);
//                });
//			}
////			me.down('#status').update({count: store.getCount()});
//		});
		
	
	    this.selModel= me.enableCheckboxSel ? {
	        selType: 'checkboxmodel',
	        mode: 'MULTI',
	        pruneRemoved: false,
			checkOnly:true
	    } : {
	    	selType: 'rowmodel',
	    	mode: 'SINGLE',
	    	pruneRemoved: false
	    };
	    
	    this.features = this.enableGrouping ? [{ftype:'grouping',groupHeaderTpl: (me.groupFieldName ? me.groupFieldName : 'Group') +' : {name}'}] : [];
	    
        this.dockedItems= [{
            dock: 'top',
            xtype: 'toolbar',
            hidden:!me.enableSearch,
            items: [{
//                width: 400,
            	flex:1,
                emptyText: 'Search',
                labelWidth: 50,
                xtype: 'searchfield',
                store: me.store
            }
//            , '->', {
//                xtype: 'component',
//                itemId: 'status',
//                tpl: 'Matching threads: {count}',
//                style: 'margin-right:5px'
//            }
            ]
        }];
        
        this.viewConfig ={
            trackOver: false,
            emptyText: '<h3 style="margin:10px">No records found</h3>',
            getRowClass: function(record, rowIndex, rowParams, store){            	
                return ( me.originalRecordIds && me.originalRecordIds.get(record.get('id')) ) ? "display-false" : "";
            }
        };
        
        this.listeners = {
        		'render' : function(){
        			
        			if(me.extraParam){
                        var paramObj = 	me.extraParam;
                        for (var i in paramObj) {
                        	me.store.getProxy().setExtraParam(i,paramObj[i]);                            
                        }
                    }
        			
        			me.store.load({
        			    scope: this,
        			    callback: function(records, operation, success) {
//        			    	if(me.originalRecordIds && success){
//        						me.originalRecordIds.each(function(key,value) {
//        							var rowIndex = me.store.indexOfId(key);
//        							if(rowIndex)
//        		                    	me.getView().getRowClass(rowIndex).style.display = 'none';
//        							
////        		                    var index = me.store.find('id', key);
////        		                    if(index!=-1)
////        		                   	 	me.store.removeAt(index);
//        		                });
//        					}
        			        me.updateLayout();
        			        me.setLoading(false);
        			    }
        			});
        		}
        };

		

        this.callParent(arguments);
    }

});