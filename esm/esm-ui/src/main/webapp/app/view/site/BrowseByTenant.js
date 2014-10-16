Ext.define('Security.view.site.BrowseByTenant', {
    extend: 'Ext.grid.Panel',
    alias : 'widget.browsebytenant',
	collapsible: true,
    title: 'Browse By Tenants',
    flex:1,
	store : 'SiteTenant',
	initComponent : function() {
	                
					
					this.columns = [
										{
										 
										  
										  }
						
					];
					
					this.listeners = {
						'afterrender' : function(){
						var me=this;
						SiteManager.getDefaultSiteRecord(function(defaultSite){
							me.getStore().getProxy().setExtraParam({'siteId':defaultSite.get(1)});
							me.getStore().load();
						});
							
						
						}
					
					};
						this. features = [{
												id: 'group',
												ftype: 'grouping',
												groupHeaderTpl: '{name}',
												//hideGroupedHeader: true,
												enableGroupingMenu: false
									}],
	
	this.callParent(arguments);
	
	}
	
});
