Ext.define('Security.view.organization.ChildOrganizationConfigWindow', {
    extend: 'Ext.window.Window',
    alias : 'widget.childorganizationconfigwindow',
    width:'90%',
    height:'90%',
    minWidth:800,
    minHeight:700,
    modal:true,
    resizable:false,
    draggable:false,
    title:'Sub-Organization Details',
    layout:{type:'vbox',align:'stretch'},
    initComponent : function(){

        var me=this;
        this.items=[
            {
                xtype:'component',
                padding:10,
                itemId:'organizationName',
                height:50

            },
            {
                xtype:'tabpanel',
                flex:1,
                items:[

//                    {
//                        title:'Assign Roles',
//                        xtype:'container',
//                        layout:'fit',
//                        items:[{
//                            xtype:'organizationroles',
//                            organization:me.organization,
//                            padding:10
//                        }]
//                    },
                    {
                        title:'Manage Users',
                        xtype:'container',
                        layout:'fit',
                        items:[{
                            xtype:'manageorganizationusers'
                        }]
                    }
//	        	        {
//	        	        	xtype:'container',
//	               	    	title:'Authentication Provider Settings',
//	               	    	itemId:'realmConfigHolder'
//            	        }
                ]
            }
        ];

        this.callParent(arguments);

    },

    updateChildOrganizationDetail: function(organization,callback) {

        var me=this;

//    		var realmConfigHolder=me.down('#realmConfigHolder');
//
//            realmConfigHolder.removeAll();
//            realmConfigHolder.add({
//    	        xtype:'realmconfigform',
//    	    	organization:organization,
//    	    	securityRealms:organization.get('securityRealms')
//            });
//            realmConfigHolder.down('realmconfigform').updatedRealmConfiguration(organization.get('securityRealms')[0],false);

        me.down('#organizationName').update('<h2>'+organization.get('canonicalName')+'</h2>');
        me.down('manageorganizationusers').setLoadingParams({'organizationId':organization.get('id')});
        me.down('manageorganizationusers').setMinHeight(0);
        me.down('manageorganizationusers').updateLayout();
        Ext.callback(callback,this);
    }
});
