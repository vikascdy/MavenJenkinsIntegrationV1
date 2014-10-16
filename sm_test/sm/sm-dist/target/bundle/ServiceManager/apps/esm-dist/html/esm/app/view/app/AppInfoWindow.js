Ext.define('Security.view.app.AppInfoWindow', {
    extend:'Ext.window.Window',
    alias:'widget.appinfowindow',
	modal:true,
	resizable:true,
	draggable:true,
	autoShow:false,
	width:600,
	height:500,
	title:'App Information',	
	layout:'fit',
	initComponent : function(){
	
	var me = this;
	
	me.items = [
		{
			xtype:'tabpanel',
			plain:true,
			items:[
				{
					xtype:'appoverviewpage',
					appRecord : me.appRecord,
					installApp:me.installApp,
					storeParams : me.storeParams
				},
				{
					title:'Components'
				}
			]
			
		}
	];
	
	this.callParent(arguments);
	},
	
	update : function(record, callback){
	
		var me=this;		
		me.down('appoverviewpage').update(record,function(){
		});
	
		Ext.callback(callback,this,[]);
	}
	
});
	
	