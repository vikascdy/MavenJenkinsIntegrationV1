Ext.define('Security.controller.SiteController', {
    extend: 'Ext.app.Controller',
    model: ['Sites','SubSitesListModel','SiteTenants'],
    stores: ['SitesListStore','SubSitesListStore','SearchAppsListStore','TenantAppsListStore','AppsListStore','SiteTenant'],
    views: [
        'site.SiteConfiguration',
        'site.SiteOverview',
        'site.CreateSite',
        'site.ManageSiteTenants',
        'site.AddressDataView',
        'site.AdminDetailDataView',
		'site.SiteAppBrowse',
		'site.SiteAppsList',
		'site.SiteAppList',
		'site.BrowseByTenant'
		
    ],
    refs: [ 
        {
	        ref: 'siteConfiguration',
	        selector: 'siteconfiguration'
	    },
        {
            ref: 'siteOverview',
            selector: 'siteoverview'
        }
    ],

    init: function() {
        this.control({
            '#manageSiteMenu':{
                'itemclick' : this.handleTreeOptions
            },
			'#westLeftMenuId':{
			 'itemclick' : this.handleAppTreeOptions
            },
            'siteconfiguration':{
                statechange:this.showSiteInfo
            },
            'siteoverview': {
            	'showDefaultSite' : this.showDefaultSite
            },
            'managesites #siteGrid gridview' :{
           	 'cellclick': this.showSiteDetails
            },
            'createsite button[action="createSite"]':{
            	'click' : this.createSite
            },
            'siteoverview button[action="updateSite"]':{
                'click' : this.updateSite
            }
           
        });
    },
    

    showDefaultSite: function(form) {
    	Security.loadingWindow = Ext.widget('progresswindow', {text: 'Loading Site Overview...'});
    	SiteManager.getDefaultSiteRecord(function(record){
    		form.loadRecord(record);
    		Security.removeLoadingWindow(function(){
        	});
    	});
    },
    
    showSiteInfo:function () {
        var me = this;
        SiteManager.getDefaultSiteRecord(function(siteRecord){                	
            var siteConfiguration = me.getSiteConfiguration();
            siteConfiguration.setSiteId(siteRecord.get('id'));
            siteConfiguration.setSiteRecord(siteRecord);
        });
    },

    

    handleTreeOptions : function(treeView, record, item, index) {
    	var me=this;
        if (record) {
        	
//        	SiteManager.getDefaultSiteRecord(function(defaultSite){

			            treeView.getSelectionModel().select(record);
			            
			            if (record.childNodes.length == 0) {
			                var page = Ext.widget('siteoverview');
			                switch (record.internalId) {
			                    case 'overview' :
			                    	window.location="#!/Site/Overview";
			                        break;
			                    case 'manageTenants' :
			                    	window.location="#!/Site/ManageTenants";
			                        break;
			                    case 'site-apps' :
								    window.location="#!/Site/ManageApps";
			                        break;
			                   
								default :
			                        page = Ext.widget('siteoverview');
			
			                }
//			                SiteManager.loadSitePages(page);
			            }
        	
//        		});
        }
    },
 handleAppTreeOptions : function(treeView, record, item, index) {
    	var me=this;
        if (record) {
		var siteapps= Ext.getCmp('siteapps');
        	//        	SiteManager.getDefaultSiteRecord(function(defaultSite){

			            treeView.getSelectionModel().select(record);
			            
			            if (record.childNodes.length == 0) {
			                var page = Ext.widget('siteoverview');
			                switch (record.data.id) {
			                    case 'all' :
								    window.location="#!/Site/ManageApps";
									break;
								case 'browsebytenant' :
									siteapps.removeAll();
									var page = Ext.widget('browsebytenant');									
								    siteapps.add(page);			                    	
			                        break;			                    
			                    default :
			                        page = Ext.widget('siteoverview');
			
			                }
			            }
        }
    },
    showSiteDetails : function(grid, selected) {
        var selectedRecord = selected[0];
        var subSiteDetailPane = this.getSubSiteDetailPane();
        var subSiteDetailPaneHeader = subSiteDetailPane.down('subsitedetailpaneheader');
        subSiteDetailPaneHeader.loadSiteDetail(selectedRecord, function() {

        });

    },

    
    createSite : function(btn){
    	var form=btn.up('form');
    	var formBasic=form.getForm();

    	var siteObj=formBasic.getValues();
    	
    	var siteRecord = Ext.create('Security.model.Sites',siteObj);
    	siteRecord.save();
    	
    	Functions.errorMsg("Site created successfully",'Success',null,'INFO');
    	var ctr = Ext.getCmp('Home-page-container');
        ctr.removeAll();
        ctr.add(Ext.widget('managesites'));
    },

    updateSite : function(btn){
        var form=btn.up('form');
        var formBasic=form.getForm();
		
        if(form.isValid())
        {
            var siteObj=formBasic.getValues();
            var record= formBasic.getRecord();
            siteObj['id']=record.get('id');
            SiteManager.updateSite(siteObj,function(response){
                if(response)
                    Functions.errorMsg("Site updated successfully.",'Success',null,'INFO');
                else
                    Functions.errorMsg("Site updation failed.",'Failure',null,'ERROR');
            },this);
        }
        else
            Functions.errorMsg("Site information incomplete.",'Failure',null,'ERROR');
    }
});