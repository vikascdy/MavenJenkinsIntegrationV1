Ext.define('Portal.view.LandingPage', {
    extend: 'Ext.container.Container',
    alias: 'widget.landingpage',
    layout:'border',
    
    defaults : {
        style:{
            backgroundColor:'#FFF!important'
        }
    },
	minWidth:1080,
	overflowX: 'hidden',
	cls:'landingPage',
	previousPill : null,
    initComponent  :    function () {
    	var me=this;

        this.items = [
            {
                xtype:'component',
                itemId:'pageHeader',
                region:'north',
                padding:'20 0 0 20',
                height:70,
				listeners : {
                    'afterrender':function () {
						
					
                        this.getEl().on('click', function(e, t, opts) {
                            e.stopEvent();
                            
							document.getElementById(me.previousPill).className="pill-button";
							document.getElementById(t.id).className="active pill-button";
							me.previousPill = t.id;
							
							var tagName = t.getAttribute('name').split('pill-')[1];
							me.showApplicationFeaturedItems(tagName);
						
							
                        }, null, {delegate: '.pill-button'});

                    }
                }
            },
            {
                xtype:'container',
                itemId:'itemContainer',
                region:'center',
                padding:'20 0 20 5',
				layout:'fit',
				flex:1
            }
        ];
		
		this.listeners = {
			'resize':function(cont, width){
			
			var cont = document.getElementsByClassName('container');
				/*if(width<=1080)
						Ext.each(cont,function(cont){
							cont.className='container';
						});
					else
						Ext.each(cont,function(cont){
							cont.className='container containerWidth';
						});*/
			}
		};
        this.callParent(arguments);
    },
    afterRender: function () {
        var me = this;
        
        var pageConfigStore = this.getPageConfiguration(this.configurationUrl);
		var pageHeader = me.down('#pageHeader');
		var itemContainer = me.down('#itemContainer');
		
		var navHtml = '<ul class="nav nav-pills">';

        pageConfigStore.on("load", function (store, records) {
			Ext.each(pageConfigStore.getRange(),function(item, index){
				var pillId = Ext.id();
				
				if(item.get('featuredItems').length>0){
					if(index==0)
						{
						navHtml +=  '<li id="pill-'+pillId+'" index='+item.index+' name="pill-'+item.get('name')+'" class="active pill-button"><a href="#">'+item.get('name')+'</a></li>';
						me.previousPill = "pill-"+pillId;
						}
					else	
						navHtml +=  '<li id="pill-'+pillId+'" index='+item.index+' name="pill-'+item.get('name')+'" class="pill-button"><a href="#">'+item.get('name')+'</a></li>';
				}
				
				var itemPanel = Ext.create('Ext.container.Container', {
						store: pageConfigStore,
						id:'featuredItemPanel',
						autoScroll:true,
						border:false,
						listeners : {
							'afterrender' : function(panel){
							
							
									Ext.each(pageConfigStore.getGroups(),function(group, index){
									
										try{
											 var id = Ext.id();
											 var targetDiv = document.createElement('div');
											 targetDiv.setAttribute('style', 'overflow-x:hidden; margin-bottom:25px;');
											 if(index==0)													
												targetDiv.setAttribute('class', 'showContainer featuredItem');
											 else
												targetDiv.setAttribute('class', 'hideContainer featuredItem');
												
											 targetDiv.setAttribute('id', id);
											 targetDiv.setAttribute('name', group.name);
											 Ext.get('featuredItemPanel-innerCt').appendChild(targetDiv);
												
												me.createAppLinks(group.children[0].get('featuredItems'), id, function(menuPage){
																		
												});

											
										}
										catch(e){
											console.log(e);
										}
									});									
							
							}
						},
						flex:1
					});
				itemContainer.removeAll();
				itemContainer.add(itemPanel);					
				
			});
			pageHeader.update(navHtml);
			
			if(records.length<2)
				pageHeader.hide();
			else
				pageHeader.show();
        });

        Portal.view.LandingPage.superclass.afterRender.apply(this, arguments);
        return;
    },
	
	showApplicationFeaturedItems : function(groupName){	

	var currentCard = document.getElementsByClassName('showContainer')[0];
	var newCard = document.getElementsByName(groupName)[0];
	
	currentCard.className = 'hideContainer featuredItem';
	newCard.className = 'showContainer featuredItem';

	},
	
	scrollPage : function (position) {
	   var landingPageCt = Ext.getCmp('featuredItemPanel')
	   landingPageCt.body.scrollBy(0,position-160,true);
	   Ext.getCmp('featuredItemPanel').doLayout();
	},
	
	cropString : function(text, noOfCharacters){
			var short = text.substr(0, noOfCharacters);
			if (/^\S/.test(text.substr(noOfCharacters)))
				return short.replace(/\s+\S*$/, "");
			return short;
	},
	
	createAppLinks : function(menu, id, callback){
	var me=this;
	Ext.define('subMenu', {
			extend: 'Ext.data.Model',
			fields: [
				{ name:'title', type:'string' },
				{ name:'description', type:'string',convert : function(v, r){
					if(v.length>256)
						return me.cropString(v, 256)+'...';
					else
						return v;
				}},
				{ name:'icon', type:'string' },
				{ name:'permission', type:'string' },
				{ name:'links', type:'auto' }
			]
		});

		var store = Ext.create('Ext.data.Store', {
			model: 'subMenu'
		});
		store.removeAll();
		Ext.each(menu, function(items, index){
			if(items.links.length==0)
				menu.splice(index,1)
		});
		store.add(menu);		
		
	
		var tpl = new Ext.XTemplate(
			'<tpl for=".">',
				'<div class="container containerWidth">',
					'<div class="left">',
						'<img src="{icon}" width="40px" height="40px" />',
					'</div>',
					'<div class="right">',
						'<h3 class="featureItem-heading"><a style="text-decoration:none; white-space:normal;">{title}</a></h3>',
						'<p style="white-space:normal; color:gray;">{description}</p>',
						'<div style="white-space:normal;">',
						'<tpl for="links">',
							'<a href="{url}" target="{hrefTarget}">{caption}</a>',
							'<tpl if="xindex &lt; xcount">',
								'<span style="padding-left:10px; padding-right:10px;">|</span>',
							'</tpl>',
						'</tpl>',
						'</div>',
					'</div>',
				'</div>',
			'</tpl>'
		);	


		var menuPage = Ext.create('Ext.view.View',{
			tpl: tpl,
			store : store,
			itemSelector: 'div.container',
			renderTo:id
		});
		

		Ext.callback(callback,this,[menuPage]);
	},


    // get configuration for rendering
    getPageConfiguration: function (configurationUrl) {
        var store = new Ext.create('Ext.data.Store',
            {
                autoLoad: true,
                autoSync: true,
                storeId: 'gridConfigStore',
                proxy: {
                    type: 'ajax',
                    url: configurationUrl,
                    reader: {
                        type: 'json',
						root:'data'
                    }					
                },
				groupField:'name',
                fields: [
                    {name: 'id', type: 'string'},
                    {name: 'name', type: 'string'},
                    {name: 'featuredItems', type: 'auto'}
                ]
            });
        return store;
    },

    // private, clean up
    onDestroy: function () {
        this.removeAll();
        Portal.view.LandingPage.superclass.onDestroy.apply(this, arguments);
    }
});