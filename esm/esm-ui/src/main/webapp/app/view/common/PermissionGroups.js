var arr = [];
var i =0;
function persistState(e)
	{
	
	if(e.checked == true)
	{
		var flag=1;
		if(flag == 1) 
		{
			for(i;i<arr.length;i++)
			{
				if(e.id!= arr[i])
				arr.push(e.id);
			}
		}
	}
	else 
		flag=0;
	for(i;i<arr.length;i++)
	{
		if(arr[i]==e.id)
		{
			if(e.checked == false)
				delete arr[i];
		}
	}
}(this);





Ext.define('Security.view.common.PermissionGroups', {
    extend: 'Ext.grid.Panel',
    alias: "widget.permissiongroups",
    overflowX: 'hidden',
    hideHeaders: true,
    bodyBorder: false,
    rowLines: false,
    layout: {
        type: 'fit'
    },
    border: 0,
    cls: 'no-border-group',
    sortableColumns:false,
	stateful : true,
	listeners: {
			'groupclick': function () {
				Ext.defer(function () {
				 
					 Ext.each(RoleManager.permissionsForRole.getValues(),function(searchId){
						 arr.push(searchId);
						 searchId = searchId.split(' ').join('');
							var checkBox = document.getElementById(searchId);
							if (checkBox){
								checkBox.checked = true;
							}				
						});
						
				}, 1500);
			},
			'beforecellmouseenter' : function(){
				return false;
			}

		},
    initComponent: function() {
	
        var me = this;
        this.columns = [
            {
                dataIndex: '.',
                flex: 1,
                renderer: function(v, m, r, ro, co, s, view) {                	
                	try{
                		return me.renderParentPermissionGrid(v, m, r, ro, co, s, view, me);
                	}
                	catch(e){
                		console.log(e);
                	}
                	
                }
            }
        ];
		
		
		
		
        this.callParent(arguments);
    },
    
    renderParentPermissionGrid : function(v, m, r, ro, co, s, view, me){    	
   	
        var subTypeArray = [];

        Ext.each(r.get('items'), function(type) {
            var subTypes = type.children;
            Ext.each(subTypes, function(subType) {
                subTypeArray.push(subType.data);
            });
        });
        
        var groupingFeature = Ext.create('Ext.grid.feature.Grouping', {
            groupHeaderTpl:  [
                '{name}'
            ],
            startCollapsed: false,
            showSummaryRow: false,
            hideGroupedHeader: true,
            enableGroupingMenu: false,
            collapsible:false
        });
        
        
        var typeStore = Ext.create('Ext.data.Store', {
            model: 'Security.model.Permission',
            autoLoad: true,
            data: subTypeArray,
            groupField: 'typeCanonicalName'
        });
        
        var data = [];
        
        Ext.each(typeStore.getGroups(), function(group) {
            var type = group.name;
            var subType = group.children;
            var subTypeStore = Ext.create('Ext.data.Store', {
                model: 'Security.model.Permission',
                groupField: 'subTypeCanonicalName'
            });
            subTypeStore.loadRecords(subType);
            
            Ext.each(subTypeStore.getGroups(), function(subGroup) {

                var subType = subGroup.name;
                var canNameString = "";
                var productCanonicalName = '';
                var categoryCanonicalName = '';
                var canId = null;
                Ext.each(subGroup.children, function(canName, index) {
                    if (index == subGroup.children.length - 1)
                        canNameString += canName.get('canonicalName') + ': ' + canName.get('id');
                    else
                        canNameString += canName.get('canonicalName') + ': ' + canName.get('id') + ',';

                    productCanonicalName = canName.get('productCanonicalName');
                    categoryCanonicalName = canName.get('categoryCanonicalName');
                    canId = canName.get('id');
                });
                
                data.push({
                    'id': canId,
                    'productCanonicalName': productCanonicalName,
                    'categoryCanonicalName': categoryCanonicalName,
                    'typeCanonicalName': type,
                    'subTypeCanonicalName': subType,
                    'canonicalName': canNameString,
                    'permissions': canNameString
                });
                
            });
        });
        
        typeStore.loadData(data);
        
        var id = Ext.id();
        
        try{
        Ext.defer(function (id) {
        	
            Ext.widget('grid', {
                store:typeStore,
                cls:'no-border-group',
                sortableColumns:false,
                features:[groupingFeature],
                bodyBorder:false,
                rowLines:true,
                border:0,
                renderTo: id,
                autoScroll: false,
                layout : {
                    type : 'fit'
                },
				listeners : {
						'beforecellmouseenter' : function(){
							return false;
						},
						'itemmouseenter' : function(){
							return false;
						},
						'itemmouseleave' : function(){
							return false;
						}
				},
                columns:[
                    {
                        dataIndex:'subTypeCanonicalName',
                        menuDisabled:true,
                        header:'Name',
                        flex:1,
                        renderer : function(v) {
                            return Ext.String.format('<div style="padding-left:30px;">{0}</div>', v);
                        }
                    },
                    {
                        dataIndex:'permissions',
                        menuDisabled:true,
                        header:'Permissions',
                        flex:3,
                        renderer: function(v, m, r) {
                            try {
                            	var nestedGridHolderId = Ext.id();
                        		return me.renderNestedPermissionGrid(v,m,r,me,nestedGridHolderId);
                            } catch (e) {
                            	console.log(e);
                            	debugger;
                            	location.reload();
                                return Ext.String.format('<div id="{0}">Error</div>', nestedGridHolderId);
                            }
                        }
                    }
                ]
            });
       		}, 50,this,[id]);
        	return Ext.String.format('<div style="overflow-x:hidden" id="{0}"></div>', id);
        }
        catch(e){
    		console.log(" ~~~ Error in creating parent grid ~~~ ");
    		console.log(e);
    		location.reload();
    		return Ext.String.format('<div id="{0}"></div>', id);
    	}
        
    },
    
    renderNestedPermissionGrid : function(v, m, r,me,nestedGridHolderId){
    	
    		
	    	var dataParam = null;
	        var names = v.split(',');
	        var items = '';
	        
	        Ext.each(names, function(name, index) {        	
	        	
					
	            var nameInfo = name.split(':');
	            var nameString = nameInfo[0];
	            var nameId = nameInfo[1];
//	            var nameId = nameInfo[1] + '-' + r.get('id') + nameString;
	            nameId = nameId.split(' ').join('');
	            
	            r.data['canonicalName'] = nameString;
	            r.data['id'] = parseInt(nameId);
	            
	            delete r.data['permissions'];
	            
	            dataParam = Ext.util.Format.htmlEncode(Ext.encode(r.data));
	            var name = r.get('productCanonicalName')+':'+r.get('categoryCanonicalName')+':'+r.get('typeCanonicalName')+':'+r.get('subTypeCanonicalName')+':'+r.get('canonicalName');
	            var html;
	            if (me.grayed)
	                html = Ext.String.format('<input disabled type="checkbox" name= "{3}" id="permission-{0}" data-permission="{1}" style="margin-right:10px;" "/>' +
		                    '<span style="margin-right:10px">{2}</span>',
		                    nameId, dataParam, Ext.util.Format.htmlEncode(nameString),name);
	            else 
				if (me.readOnly)
	            	html = '<span style="margin-right:20px;">' + Ext.util.Format.htmlEncode(nameString) + '</span>';
	            else
	                html = Ext.String.format('<input type="checkbox" name= "{3}" id="permission-{0}" data-permission="{1}" style="margin-right:10px;" onclick="persistState(this)"/>' +
	                    '<span style="margin-right:10px">{2}</span>',
	                    nameId, dataParam, Ext.util.Format.htmlEncode(nameString),name);
	            items += html;
	            
	            if(index==names.length-1){
	            	
	            }
	        });
	
//	        if(me.readOnly)
	        	items='<div>'+items+'</div>';
//	        else
//	        	item='<form>'+items+'</form>';
	        
	        Ext.defer(function () {
	        	try{
		            Ext.widget('component', {
		                html:items,
		                renderTo:nestedGridHolderId
		            });
	        	}
	        	catch(e){	        		
	        		console.log(" ~~~ Error in creating nested grid ~~~ ");
	        		location.reload();
	        	}
	        }, 200);
	        
	        return Ext.String.format('<div id="{0}"></div>', nestedGridHolderId);    	
    
    }
	


});
