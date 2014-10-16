Ext.define('Security.view.user.UserProfile', {
    extend: 'Ext.container.Container',
    alias : 'widget.userprofile',
    border:0,
    minHeight:700,
    requires: [
        'Ext.layout.container.Border',
        'Security.view.user.UserProfileInfo',
        'Security.view.common.PageHeader'
    ],

    title: 'User Profile',
    layout: 'border',
    style : 'background-color: #FFFFFF !important',
    defaults: {
        collapsible: false,
        split: false
    },
    margins:{top:15, right:15, bottom:15, left:15},
    initComponent: function() {
    	this.items= [
    	             {
    	                 xtype:'userprofileinfo',
    	                 margin:'0 0 0 5',
    	                 region:'center'
    	             }
    	         ];
    	
    	this.callParent(arguments);
    }
    
});