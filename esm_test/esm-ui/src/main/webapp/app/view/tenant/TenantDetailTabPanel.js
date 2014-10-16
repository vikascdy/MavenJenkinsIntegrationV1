Ext.define('Security.view.tenant.TenantDetailTabPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.tenantdetailtabpanel',
    border:0,
    bodyPadding:'0 20 15 15',
    autoScroll:true,
    overflowX: 'hidden',
    initComponent : function() {
        var me = this;

        this.items = [
//			{
//			    xtype:'component',
//			    html:'<div class="tabPanelHeader"><h3>No Detail Found</h3></div>'
//			},
			{
		          xtype:'component',
		          html:'<div class="tabPanelHeader"><div class="tabPanelHeading">Domain</div></div>'
		     },
		 	{
		          xtype:'component',
		          itemId:'domainName'
		     },
//			{
//                xtype:'component',
//                html:'<div class="tabPanelHeader"><div class="tabPanelHeading">Primary Organisation</div></div>'
//            },
//            {
//                xtype:'component',
//                html:'<div class="tabPanelSubHeader"><div class="tabPanelSubHeading">Name</div><div class="tabPanelSubHeadingValue">Blue Shield of California</div></div>'
//            },
//            {
//                xtype:'component',
//                html:'<div class="tabPanelSubHeader"><div class="tabPanelSubHeading">Address</div></div>'
//            },
//            {
//                xtype:'addressdataview'
//            },
//            {
//                xtype:'component',
//                html:'<div class="tabPanelHeader"><div class="tabPanelHeading">Administrators</div></div>'
//            },
//            {
//                xtype:'admindetaildataview',
//                margin:'20 0 0 0'
//            },
//            {
//                xtype:'component',
//                html:'<div class="tabPanelHeader"><div class="tabPanelHeading">Apps</div></div>'
//            },
//            {
//                xtype:'appslist',
//                disableAppAdd:true
//            },
            {
                xtype:'component',
                itemId:'heightReference'
            }

        ];


        this.callParent(arguments);
    },
    
    update : function(record){
    	this.down('#domainName').update(record.get('domain'));
    }


});