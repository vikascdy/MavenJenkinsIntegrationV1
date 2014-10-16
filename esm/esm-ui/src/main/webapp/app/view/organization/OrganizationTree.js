Ext.define('Security.view.organization.OrganizationTree', {
    extend: 'Ext.tree.Panel',
    alias : 'widget.organizationtree',
    cls:'no-icon-tree',
    itemId:'organizationTree',
    id:'childOrganizationTree',
	rootVisible:true,
    useArrows:true,
    initComponent: function() {
        var me=this;

        
        var oldNode = null,
        currentNode = null;
        
        Ext.apply(this, {
        	
 
            tbar:Ext.create('Ext.toolbar.Toolbar', {
                    padding:5,
                    items: [ {
				                xtype:'component',
				                margin:'5 0 0 10',
				                itemId:'newItem',
				                html:'<a href="javascript: void(0)" id="newChildOrganization-link" class="newItem quickLinks">New Child Organization</a>',
				                listeners : {
				                    'afterrender':function () {
				                    	var btn=this;
				                        this.getEl().on('click', function(e, t, opts) {
				                            e.stopEvent();
				                            me.fireEvent('newItem',btn, me);
				                        }, null, {delegate: '.newItem'});
				
				                    }
				                }
				            },
				            {
				                xtype:'component',
				                itemId:'deleteItem',
				                margin:'5 0 0 10',
				                html:'<a href="javascript: void(0)" id="deleteChildOrganization-link" class="deleteItem quickLinks">Delete Child Organization</a>',
				                listeners : {
				                    'afterrender':function () {
				                    	var btn=this;
				                        this.getEl().on('click', function(e, t, opts) {
				                            e.stopEvent();
				                            me.fireEvent('deleteItem',btn, me);
				                        }, null, {delegate: '.deleteItem'});
				
				                    }
				                }
				            }]
            	}),
            
            columns: [{
                xtype: 'treecolumn', 
                text: 'Organization Name',
                menuDisabled:true,
                flex: 1,
			    dataIndex: 'text',
                renderer : function(v,m,r){
                		return Ext.String.format('<a href="javascript: void(0)" id="ChildOrganizationDetail-link-'+r.get('id')+'">{0}</a>', v);
                }
            }],

		    listeners : {
					 'render' : function(){
								me.setLoading("Fetching Child Organizations...");
								 me.getRootNode().expand(true,function(){
									 me.setLoading(false);
								 });
								 me.getRootNode().cascadeBy(function(node) {
											node.set({checked:false});
								});
								me.doLayout();
				 
					 },
					 'beforeitemdblclick' : function(){
						return false;
					 },
					 'checkchange' : function(node, checked){
						 oldNode = currentNode;
						 currentNode = node;

 						if(oldNode!=currentNode && oldNode!= null)
						{
						 oldNode.set('checked',false);
						}
						
					 }
		    }

        });
        
        this.callParent(arguments);
    }
});