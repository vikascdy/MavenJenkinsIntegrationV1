Ext.define('Edifecs.MiniGrid', {
    extend          :'Ext.panel.Panel',
    alias           :'widget.MiniGrid',
    border          :false,
    url             :"",
    rootValue       :"",
    headingText     :"",
    tbarItems       :"",
	flex			:1,
    totalVisibleRows:6,
    headerIcon      :true,
	headerIconCls	:"",
	hideHeaders		:false,
    cls             :'topBar',
    dockedToolbar   :false,
    // Overriding superclass template method
    afterRender     :function () {

        //Top Header Panel
        var topPanel = this.miniTopBar();
        if (this.headerIcon) {
            topPanel.add({xtype:"component", cls:this.headerIconCls});
            topPanel.add({xtype:"tbtext", text:this.headingText, cls:"gridHeading", padding:0});
        }
        else {
            topPanel.add({xtype:"tbtext", text:this.headingText, cls:"gridHeading"});
        }
        if (this.dockedToolbar) {
            topPanel.add([
                {xtype:'tbspacer', flex:1}
            ]);

            var customButtonGroup = new Edifecs.CustomButtonGroup({buttonItems:this.tbarItems});
            topPanel.add(customButtonGroup);
        }
		
		var me = this;
		this.createAjaxHit(this.url, function(miniGridJson) {
			var columnArray=[];
			var modelArray=[];
			
			Ext.each(miniGridJson.gridColumn, function(column,index) {
				
				var gridProperties={};
				var modelProperties={};
				
				// define model
				modelProperties['name']=column.name;
				modelProperties['type']=column.type;
				modelArray.push(modelProperties);
				
				// grid structure
				gridProperties['text']=column.columnHeader;
				gridProperties['dataIndex']=column.name;
				gridProperties['hidden']=column.hidden;
				if(column.width)
					gridProperties['width']=column.width;
				else
					gridProperties['flex']=column.flex ? column.flex : 1;
				switch(column.type)
				{
					case "number":
					{
						gridProperties['xtype']='numbercolumn';
						columnArray.push(gridProperties);
						break;
					}
					case "boolean":
					{
						gridProperties['xtype']='booleancolumn';
						columnArray.push(gridProperties);
						break;
					}
					case "date":
					{
						gridProperties['xtype']='datecolumn';
						gridProperties['format']=column.dateFormat;
						columnArray.push(gridProperties);
						break;
					}
					default:
					{
						columnArray.push(gridProperties);
					}
				}
			});
			
			Ext.each(miniGridJson.actionColumn, function(actionColumn,index) 
			{
				var gridProperties={};
				gridProperties['xtype']	=	'actioncolumn';
				gridProperties['width']	=	actionColumn.width;
				gridProperties['menuDisabled']	=	true;
				gridProperties['sortable']	=	false;
				gridProperties['align']	=	'center';				
				
				var actionItemsArray = [];
				Ext.each(actionColumn.items, function(item,index) {
					var actionItemProperties = {};
					actionItemProperties['id']		=	item.id;
					actionItemProperties['icon']	=	item.icon;
					actionItemProperties['iconCls']	=	item.iconCls;
					actionItemProperties['tooltip'] =	item.tooltip;
					actionItemsArray.push(actionItemProperties);
				});
				
				gridProperties['items'] = actionItemsArray;
				columnArray.push(gridProperties);
			});
			
			//Set Store
			var miniStore = me.miniStore(modelArray,miniGridJson.data,"data");
			
			//Grid Panel
			var miniGrid = me.miniGrid(miniStore,columnArray,me.hideHeaders);
			var miniGridPanel = me.miniGridPanel();
			
			if (me.totalVisibleRows != "" || me.totalVisibleRows != 0)
				miniGridPanel.add(miniGrid.setHeight((me.totalVisibleRows * 30) + 10));
			else
				miniGridPanel.add(miniGrid.setHeight(190));
				
			 me.add(topPanel, miniGridPanel);
		});
		
        // Call superclass
        Edifecs.MiniGrid.superclass.afterRender.apply(this, arguments);
        return;
    },

    //private Methods
    miniStore:function (fieldData,gridData,rootValue) {	
        var miniStore = Ext.create('Ext.data.Store',
            {
				fields		:	fieldData,
				data 		:	gridData
            });
        return miniStore;
    },

    miniGrid:function (miniStore,columnArray,hideHeaders) {
        var miniGrid = Ext.create('Ext.grid.Panel', {
            store      :	miniStore,
            border     :	!hideHeaders,
			hideHeaders:	hideHeaders,
            bodyBorder :	!hideHeaders,
            cls        :	'cellColor',
            columns    :	columnArray
        });
        return miniGrid;
    },

    miniTopBar:function () {
        var topbar = new Ext.create('Ext.toolbar.Toolbar',
            {
                layout :"hbox",
                padding:'0 0 5 0',
                height :30,
                border :false,
                style  :"background:#F1F3F5;"
            });
        return topbar;
    },

    miniGridPanel:function () {
        var miniPanel = new Ext.create('Ext.panel.Panel',
            {
                bodyCls	:	"gridPanel",
				style  :"background:#F1F3F5"
            });
        return miniPanel;
    },
	
	createAjaxHit : function(url, callbackfn) {
		Ext.Ajax.request({
					url : url,
					success : function(response) {
						var json = Ext.JSON.decode(response.responseText);
						Ext.callback(callbackfn(json));
					}
				});
	},

    // private, clean up 
    onDestroy    :function () {
        this.removeAll();
        Edifecs.MiniGrid.superclass.onDestroy.apply(this, arguments);
    }
});

