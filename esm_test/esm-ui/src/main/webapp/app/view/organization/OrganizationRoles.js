Ext.define('Security.view.organization.OrganizationRoles', {
    extend:'Ext.grid.Panel',
    alias:'widget.organizationroles',
    selType: 'checkboxmodel',

    initComponent : function(){
    	var me=this;
    	
    	var store = Ext.StoreManager.lookup('OrganizationRoles');
    	store.getProxy().setExtraParam('organizationId',me.organization.get('id'));
    	store.load();
    	this.store=store;
    	
    	this.tbar = Ext.create('Ext.toolbar.Toolbar', {
             padding:5,
             items:[
						{
						    xtype:'component',
						    margin:'5 0 0 10',
						    html:'<a href="#" id="assignOrganizationRoles-link" class="addRole quickLinks">Add Role</a>',
						    listeners : {
						        'afterrender':function () {
						            this.getEl().on('click', function(e, t, opts) {		

										if(Security.viewport.down('organizationroles').autoGenId==true)
											me.fireEvent('addRole', me, me.organization);
						                e.stopEvent();
										Security.viewport.down('organizationroles').autoGenId=false;
						            }, null, {delegate: '.addRole'});
						
						        }
						    }
						},
						{
						    xtype:'component',
						    disabled:true,
						    itemId:'removeRole',
						    margin:'5 0 0 10',
						    html:'<a href="#" id="removeOrganizationRoles-link"  class="removeRole quickLinks">Remove Role</a>',
						    listeners : {
						        'afterrender':function () {
						            this.getEl().on('click', function(e, t, opts) {
						                e.stopEvent();
						                me.fireEvent('removeRole', me, me.organization);
						            }, null, {delegate: '.removeRole'});
						
						        }
						    }
						}
                    ]

         });
    	
    	this.callParent(arguments);
    },
    

    columns : [
            {
                header:'Role Name',
                dataIndex:'canonicalName',
                flex:1
            },
            {
                header:'Description',
                dataIndex:'description',
                flex:1
            }
        ],
        viewConfig : {
            emptyText: 'There are no assigned roles.'
        }
});