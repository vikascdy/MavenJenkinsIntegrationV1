Ext.define('Security.view.common.EditPermissionsTabPanel', {
    extend: 'Ext.tab.Panel',
    alias: 'widget.editpermissionstabpanel',
    plain:true,
    activeTab:0,
    flex:1,
    permissionsOfRole:null,
    overflowX: 'hidden',
    createPermissionCategory : function(category, readOnly, grayed) {

        var treeData = Functions.generateCategoryData(category);

        var store = Ext.create('Ext.data.Store', {
            fields:['name','items'],
            autoLoad:true,
            data:treeData,
            groupField:'name'
        });

        var groupingFeature = Ext.create('Ext.grid.feature.GroupingSummary', {
            groupHeaderTpl:  [
                '{name}'
            ],
            startCollapsed:false,
            showSummaryRow:false,
            hideGroupedHeader: true,
            enableGroupingMenu: false
        });


        return Ext.widget({
            xtype:'permissiongroups',
            readOnly:readOnly,
            grayed:grayed,
            features:[groupingFeature],
            store:store

        });
    },
    initComponent:     function() {
        var me = this;
        var items = [];
        Ext.each(me.productGroups, function(group, index) {
        	if(group.name!="*")
	            items.push({
	                title:group.name,
	                items:me.createPermissionCategory(group.children, me.readOnly, me.grayed),
	                layout:'fit',
	                overflowX:'hidden'
	
	            });
        });
        me.items = items;


        me.listeners = {
            render : function() {
               // if (!me.readOnly && me.grayed)
                    RoleManager.getPermissionsForRole(me.record, function(permissionsOfRole) {
                        RoleManager.RolePermissionMap = permissionsOfRole;
                        me.fireEvent('tabchange', me, me.getActiveTab());
                    });
                
               if(me.getLayout().getLayoutItems()==0){
            	   me.down('#refreshPermissionForRole').hide();
               }

            },
            tabchange : function(tabPanel, card) {
               // if (!me.readOnly)
                    me.setPermissionsForRole(tabPanel, card);
            }
        };

        this.callParent(arguments);
    },


    setPermissionsForRole : function(tabPanel, card) {
		var me=this;
		me.setLoading("Loading Permissions...");
        var permissionsChecked = new Ext.util.HashMap();
        var inputBoxes=document.getElementsByTagName('input');
            Ext.each(inputBoxes,function(box){
               box.checked=false;
            });
        Ext.defer(function() {
//            var productTabs = me.getLayout().getLayoutItems();
//            var productTabsMap = new Ext.util.HashMap();
//            Ext.each(productTabs, function(product) {
//                productTabsMap.add(product.title, product.items);
//            });
			
            Ext.each(RoleManager.RolePermissionMap, function(permission) {
//                var searchId = permission.id + '-' + permission.productCanonicalName + '-' + permission.categoryCanonicalName + '-' + permission.typeCanonicalName + '-' + permission.subTypeCanonicalName + '-' + permission.canonicalName;
                var searchId = 'permission-'+permission.id;
//                var product = productTabsMap.get(permission.productCanonicalName).items[0];
                searchId = searchId.split(' ').join('');
                if (searchId && card.title == permission.productCanonicalName) {
                    var checkBox = document.getElementById(searchId);
                    if (checkBox){
                        checkBox.checked = true;
                        permissionsChecked.add(permission.id,searchId);
                    }
                }
            });
            me.setLoading(false);
        }, 500);
        RoleManager.permissionsForRole=permissionsChecked;
    }


});
