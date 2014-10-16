Ext.define('Security.view.site.SiteAppList', {
    extend:'Ext.view.View',
    alias:'widget.siteappslist',
    store:'AppsListStore',
 
    frame: true,
        collapsible: true,
      
        
        tpl: 
		[
                '<tpl for=".">',
                    '<div class="thumb-wrap" style="width:auto; input {background-color:White;}div:hover input {background-color:Blue;}" >',
                        '<div style="height:80px;width:100px;float:left;border:solid;background-image : url(resources/images/app_icon.png);background-size: 93px 80px;"></div>',
						'<div style="background-color:#F5F5DC;height:80px;width:150px;float:left;border:solid;"><font color = "black"><b>&nbsp{name}</b><br/><br/>&nbspVersion : {version}<br/>&nbspDisplay Version: {displayVersion}</font></div>',
						                
				   '</div>',
                '</tpl>',
                
            ],
			
            multiSelect: true,
           
            trackOver: true,
            overItemCls: 'x-item-over',
            itemSelector: 'div.thumb-wrap',
            emptyText: 'No images to display',
            
            
            
        });
		
  



