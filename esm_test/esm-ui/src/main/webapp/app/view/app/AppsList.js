Ext.define('Security.view.app.AppsList', {
    extend:'Ext.view.View',
    alias:'widget.appslist',
	initComponent : function(){
	
	var me = this;
	this.store = this.storeInstance;
	this.store.load();
	
	this.listeners = {

				itemclick : function(field,record)
				{  
				
					var appWindow = Ext.widget({
						xtype:'appinfowindow',
						appRecord : record,
						installApp:me.installApp,
						storeParams:me.store.getProxy().extraParams
					});
					appWindow.update(record,function(){
						appWindow.show();
					});
					
					
			  }
							
			};
	
	this.callParent(arguments);
	},
	
	
       frame: true,
       collapsible: true,
       
       
        tpl: 
		[
                '<tpl for=".">',
				'<div class ="flip">',
				'<div class = "card">',
				   '<div class="face_front"  >',
				   '<div class = " image_front" style="height:64px;width:80px;float:left;border : 5px solid transparent;background-size: 80px 65px;"><img src = "resources/images/app_icon.png" height = "60px"; width = "60px"></img></div>',
					'<div class = " desc_front" style="background-color:#F5F5DC;height:64px;width:120px;float:left;border : solid transparent;"><font color = "black"><b>&nbsp{name}</b><br/><br/>&nbspVersion : {version}</font></div>',				
				   '</div>',
                     
				//	'<div class="face back" style="border:1px solid ; padding:5px;height:64px;width:200px;"> {description} </div> ',
					'</div>',
					'</div>',
                '</tpl>',
            ],
            multiSelect: true,
            trackOver: true,           
            itemSelector: 'div.card',
            emptyText: 'No images to display'
    
			
         });
		
