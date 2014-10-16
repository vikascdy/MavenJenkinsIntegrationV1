Ext.define('Security.view.site.SiteAppBrowse',{
	extend : 'Ext.form.field.ComboBox',
	alias : 'widget.tenantappbrowse',
	store : 'SearchAppsListStore',
	emptyText : 'Start typing app name',
	minChars : 0,
	hideTrigger : true,
	hideLabel : true,
	displayField : 'name',
	valueField : 'id',
	typeAhead : true,
	queryMode : 'local',
	matchFieldWidth : true,
	listConfig : {
		loadingText : 'Searching...',
		emptyText : 'No matching app found.',
		getInnerTpl : function() {
			return '<div class="searchAppCont"><div class="searchAppName">{name}</div><div appid={id} class="addButtonToApp"></div></div>';
		}
//		listeners : {
//			boxready : function(field) {
//				var btn = field.getEl().dom
//						.getElementsByClassName('addButtonToApp');
//				Ext.each(btn, function(item) {
//					Ext.widget({
//						xtype : 'button',
//						itemId : 'addButton',
//						renderTo : item,
//						text : 'Install',
//						ui : 'greenbutton',
//						listeners : {
//							click : function() {
//								Ext.widget({
//									xtype:'window',
//									autoScroll:true,									
//									autoShow:true,
//									modal:true,
//									resizable:false,
//									draggable:false,									
//									title:'Application Configuration',
//									items:[{xtype:'appconfiguration',appId:item.getAttribute('appid')}]
//								});
//							}
//						}
//					});
//				});
//
//			},
		// itemmouseenter : function(view, record, item) {
		// var btn=view.el.query('div.addButtonToApp')[0];
		// Ext.get(btn.id).show();
		// btn.classNAme="showButton addButtonToApp";
		// },
		// itemmouseleave : function(view, record, item) {
		// var btn=view.el.query('div.addButtonToApp')[0];
		// // btn.classNAme="hideButton addButtonToApp";
		//
		// }
//		}
	}
});