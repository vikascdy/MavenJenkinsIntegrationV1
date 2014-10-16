Ext.define('Edifecs.DoormatApplicationBar', {
    extend:'Ext.container.Container',
    alias:'widget.DoormatApplicationBar',
    cls:'headerbg',
    height:70,
    border:false,
    layout:{
        type:'hbox',
        align:'stretch',
        pack:'start'
    },
    statics:{
        COMPONENT_IMAGE_PATH:'../packages/ext-theme-edifecs/build/resources/images/edifecs-components/doormat/',
        DOORMATNAVIGATION:'DOORMATNAVIGATION',
        DOORMATNAVIGATION_IMAGE:'navigation.png',
        HOME:'HOME',
        HOME_IMAGE:'home.png',
        SEARCH:'SEARCH',
        SEARCH_IMAGE:'search.png',
		HELP_IMAGE:'helps.png',
        NOTIFICATIONS:'NOTIFICATION',
        NOTIFICATIONS_IMAGE:'notifications.png',
        FAVOURITES:'FAVOURITE',
        FAVOURITES_IMAGE:'favorites.png',
        USER_MENU:'SIMPLEMENU',
        USER_MENU_IMAGE:'user.png'
    },
    appBarUrl:"",
    logoIcon:"",
    doormatUrl:"",
    notificationUrl:"",
    dataString:"",
    helpUrl:"",
	favouritesUrl:"",
    initComponent  :    function () {

        // set tooltip config options
        Ext.tip.QuickTipManager.init();
        Ext.apply(Ext.tip.QuickTipManager.getQuickTip(),
            {
                showDelay :50,
                minWidth  :10,
                trackMouse:true
            });
        this.callParent(arguments);
    },
    afterRender:function () {

        // add edifecs Logo
        this.add(this.createLogo(this.logoIcon));

        // create store
        var navigationStore = this.createNavigationStore(this.appBarUrl);
        var me = this;

        navigationStore.on("load", function (store, record) {
            //me.createAjaxHit(me.notificationUrl, function (notificationJson) {
                navigationStore.each(function (item, index) {
                    var menuButton, submenu, subMenuContainer;

                    // create sub menu instance
                    submenu = me.createSubMenu();

                    if (item.get("type").toUpperCase() == Edifecs.DoormatApplicationBar.DOORMATNAVIGATION) {
                        subMenuContainer = new Ext.widget('DoormatNavigation', {
                            doormatUrl:me.doormatUrl
                        });
                        submenu.add([subMenuContainer]);
                        menuButton = me.createButton(submenu,item.get("id"));
                        menuButton.setUI('edifecs-doormateNavigation-appbar-submenu');
                        me.setDoormatCoreImages(submenu, menuButton, item.get("icon"), Edifecs.DoormatApplicationBar.DOORMATNAVIGATION_IMAGE);
                    }
                    else if (item.get("type").toUpperCase() == Edifecs.DoormatApplicationBar.HOME) {
                        menuButton = me.createButton(null,item.get("id"));
                        menuButton.setUI('edifecs-doormateNavigation-appbar');
                        me.setDoormatCoreImages(submenu, menuButton, item.get("icon"), Edifecs.DoormatApplicationBar.HOME_IMAGE);
                    }
                    else if (item.get("type").toUpperCase() == Edifecs.DoormatApplicationBar.SEARCH) {
                        menuButton = me.createButton(null,item.get("id"));
                        menuButton.setUI('edifecs-doormateNavigation-appbar');
                        me.setDoormatCoreImages(submenu, menuButton, item.get("icon"), Edifecs.DoormatApplicationBar.SEARCH_IMAGE);
                    }
                    else if (item.get("type").toUpperCase() == Edifecs.DoormatApplicationBar.NOTIFICATIONS) {
                        menuButton = me.createButton(submenu,item.get("id"));
                        if(me.notificationUrl!="")
                        {
                            subMenuContainer = new Ext.widget('notifications', {
                                notificationUrl:me.notificationUrl
                            });
                            menuButton.on('afterrender', function (sender, event) {
                                me.createAjaxHit(me.notificationUrl, function (notificationJson) {
                                    if(notificationJson.length!=0)
                                        Ext.DomHelper.append(sender.el, {tag:'div', cls:'notificationNo', id:'new-div-id', html:notificationJson.length});
                                });
                            });
                        }
                        submenu.add([subMenuContainer]);
                        menuButton.setUI('edifecs-doormateNavigation-appbar-submenu');
                        me.setDoormatCoreImages(submenu, menuButton, item.get("icon"), Edifecs.DoormatApplicationBar.NOTIFICATIONS_IMAGE);
                    }
                    else if (item.get("type").toUpperCase() == Edifecs.DoormatApplicationBar.FAVOURITES) {
                        if(me.favouritesUrl!="")
                        {
                            subMenuContainer = new Ext.widget('favourites', {
                                favouritesUrl:me.favouritesUrl
                            });
                        }
                        submenu.add([subMenuContainer]);
                        menuButton = me.createButton(submenu,item.get("id"));
                        menuButton.setUI('edifecs-doormateNavigation-appbar-submenu');
                        me.setDoormatCoreImages(submenu, menuButton, item.get("icon"), Edifecs.DoormatApplicationBar.FAVOURITES_IMAGE);
                    }
                    else if (item.get("type").toUpperCase() == Edifecs.DoormatApplicationBar.USER_MENU) {
                        submenu.addCls("subMenu");
                        Ext.each(item.data.subMenu, function (subItem, index) {
                            submenu.add({
                                text: (subItem.description != undefined && subItem.description != "") ? "<ul><li>" + subItem.text + "</li><li class='description'>" + subItem.description + "</li></ul>" : subItem.text,
                                id: subItem.id,
                                href: (subItem.linkUrl != undefined && subItem.linkUrl != "") ? subItem.linkUrl : "#",
                                hrefTarget: (subItem.hrefTarget != undefined && subItem.hrefTarget != "") ? subItem.hrefTarget : "_self",
                                cls: (index == 0) ? 'firstMenuItem' : ''
                            });
                        });

                        menuButton = me.createButton(submenu,item.get("id"));
                        menuButton.setUI('edifecs-doormateNavigation-appbar-submenu');
                        me.setDoormatCoreImages(submenu, menuButton, item.get("icon"), Edifecs.DoormatApplicationBar.USER_MENU_IMAGE);
                    }
                    else {
                        menuButton = me.createButton(null,item.get("id"));
                        menuButton.setUI('edifecs-doormateNavigation-appbar');
                        if (item.get("icon") != "" && item.get("icon"))
                            me.setDoormatCoreImages(submenu, menuButton, item.get("icon"), "");
                    }

                    if (item.get("text"))
                        menuButton.setText(item.get("text"));

                    if(item.get("tooltip")!=null && item.get("tooltip")!=undefined)
                        menuButton.setTooltip(item.get("tooltip"));

                    // assign id to each menu item
                    if (item.get("id") != undefined)
                        menuButton.itemId = item.get("id");

                    // hide menu item
                    if (item.get('hidden'))
                        menuButton.hidden = true;
                    else
                        me.add(me.createSeparator());

                    // disable menu item
                    if (item.get("disable"))
                        menuButton.setDisabled(true);

                    // menu selected event defined
                    menuButton.on("click", function (sender, event) {
	
                        if (item.get("type") != "" && item.get("type").toUpperCase() != Edifecs.DoormatApplicationBar.HOME)
                        {
                            me.setSubMenuPosition(menuButton);
                        }
                        else if(item.get("type").toUpperCase() == Edifecs.DoormatApplicationBar.HOME)
                        {
                            window.location.href=item.get("linkUrl");
                        }
						
                        // set the position of menu button after resizing window
                        Ext.EventManager.onWindowResize(function () {
                            if(menuButton.hasVisibleMenu())
                                me.setSubMenuPosition(menuButton);
                        });
                    });

                    // create tab spacer for left and right items
                    if (item.get("text") == "tbspacer")
                        {
						me.add(me.createSpacer());
						
						if(me.helpUrl){
								var menuButton = me.createButton(null,'helpId');
								menuButton.on("click", function (sender, event) {												 
									window.open(
										  me.helpUrl,
										  '_blank'
										);
								});
								
								menuButton.setUI('edifecs-doormateNavigation-appbar');
								me.setDoormatCoreImages(submenu, menuButton, item.get("icon"), Edifecs.DoormatApplicationBar.HELP_IMAGE);
								me.add(menuButton);
								menuButton.getEl().setStyle('border-left','1px solid #465159');						
							}
						}
                    else
                        me.add(menuButton);
                });
            //});
        });

        Edifecs.DoormatApplicationBar.superclass.afterRender.apply(this, arguments);
        return;
    },

    setSubMenuPosition:function (menuButton) {
        if (menuButton.menu != undefined) {
            menuButton.menu.showAt(0, 70);
            var windowWidth = Ext.getBody().getViewSize();
            if (menuButton.menu.el != undefined) {
                var menuWidth = menuButton.menu.el.dom.clientWidth;
                var buttonPosition = menuButton.getPosition();
                if (buttonPosition.length != 0)
                    if (windowWidth.width - buttonPosition[0] < menuWidth)
                        menuButton.menu.setPosition(menuButton.getWidth() + buttonPosition[0] - menuWidth, 70);
                    else
                        menuButton.menu.setPosition(buttonPosition[0], 70);
            }
        }
    },


    setIconImage:function (imageName, eventString) {
        var tempImageName, actualImageName;
        if (eventString == undefined) {
            tempImageName = imageName.substring(imageName.lastIndexOf("/") + 1,
                imageName.lastIndexOf("."));
            actualImageName = tempImageName.substring(0, tempImageName
                .lastIndexOf("-"));
            return imageName.replace(tempImageName, actualImageName);
        } else {
            tempImageName = imageName.substring(imageName.lastIndexOf("/") + 1,
                imageName.lastIndexOf("."));
            return imageName
                .replace(tempImageName, tempImageName + eventString);
        }
    },

    setDoormatIcons : function(menuButton, overrideImageIcon, imagePath, orignalImageIcon) {
        if (overrideImageIcon != "" && overrideImageIcon)
            menuButton.setIcon(overrideImageIcon);
        else
            menuButton.setIcon(imagePath + orignalImageIcon);
    },

    setDoormatIconsHover : function(menuButton, overrideImageIcon, imagePath, orignalImageIcon) {
        if (overrideImageIcon != "" && overrideImageIcon)
            menuButton.setIcon(this.setIconImage(overrideImageIcon, "-hover"));
        else
            menuButton.setIcon(this.setIconImage(imagePath + orignalImageIcon, "-hover"));
    },


    setDoormatCoreImages : function(submenu, menuButton, overrideImageIcon, orignalImageIcon) {

        var me = this;
        me.setDoormatIcons(menuButton, overrideImageIcon, Edifecs.DoormatApplicationBar.COMPONENT_IMAGE_PATH, orignalImageIcon);

        menuButton.on('mouseout', function (sender, event) {
            if (!this.hasVisibleMenu()) {
                me.setDoormatIcons(menuButton, overrideImageIcon, Edifecs.DoormatApplicationBar.COMPONENT_IMAGE_PATH, orignalImageIcon);
            }
            this.blur();
        });

        menuButton.on('mouseover', function (sender, event) {
            me.setDoormatIconsHover(menuButton, overrideImageIcon, Edifecs.DoormatApplicationBar.COMPONENT_IMAGE_PATH, orignalImageIcon);
        });


        submenu.on('mouseover', function (menu, menuItem, event) {
            me.setDoormatIconsHover(menuButton, overrideImageIcon, Edifecs.DoormatApplicationBar.COMPONENT_IMAGE_PATH, orignalImageIcon);
        });

        submenu.on('mouseleave', function (menu, menuItem, event) {
            if (menuButton.hasVisibleMenu()) {
                menuButton.hideMenu();
                me.setDoormatIcons(menuButton, overrideImageIcon, Edifecs.DoormatApplicationBar.COMPONENT_IMAGE_PATH, orignalImageIcon);
                menuButton.blur();
            }
        });

        submenu.on('click', function () {
            menuButton.blur();
        });

        submenu.on('hide', function (sender, e) {
            me.setDoormatIcons(menuButton, overrideImageIcon, Edifecs.DoormatApplicationBar.COMPONENT_IMAGE_PATH, orignalImageIcon);
        });
    },

    // create navigation store
    createNavigationStore:function (appBarUrl) {
        var store;
        var dataString=this.dataString;

        if(this.dataString && this.dataString.length < 0){
            store = new Ext.data.JsonStore({
                autoLoad:false,
                autoSync:false,
                storeId:'navigationStore',

                data:dataString,
                fields:[
                    {
                        name:'id',
                        type:'string'
                    },
                    {
                        name:'tooltip',
                        type:'string'
                    },
                    {
                        name:'icon',
                        type:'string'
                    },
                    {
                        name:'text',
                        type:'string'
                    },
                    {
                        name:'linkUrl',
                        type:'string'
                    },
                    {
                        name:'type',
                        type:'string'
                    },
                    {
                        name:'subMenu'
                    },
                    {
                        name:'disable',
                        type:'boolean',
                        defaultValue:false
                    },
                    {
                        name:'hidden',
                        type:'boolean',
                        defaultValue:false
                    }
                ]
            });
        }
        else {

            store = new Ext.data.JsonStore({
                autoLoad:true,
                autoSync:true,
                storeId:'navigationStore',
                callback: function(a,b,c){debugger;},
                proxy:{
                    type:'ajax',
                    url:appBarUrl,
                    reader:{
                        type:'json',
                        root:'data.navigation'
                    }
                },
                fields:[
                    {
                        name:'id',
                        type:'string'
                    },
                    {
                        name:'tooltip',
                        type:'string'
                    },
                    {
                        name:'icon',
                        type:'string'
                    },
                    {
                        name:'text',
                        type:'string'
                    },
                    {
                        name:'linkUrl',
                        type:'string'
                    },
                    {
                        name:'type',
                        type:'string'
                    },
                    {
                        name:'subMenu'
                    },
                    {
                        name:'disable',
                        type:'boolean',
                        defaultValue:false
                    },
                    {
                        name:'hidden',
                        type:'boolean',
                        defaultValue:false
                    }
                ]
            });

        }

        return store;
    },

    // create edifecs logo
    createLogo:function (logoIcon) {
        var edifecsLogo = new Ext.Component({
            border:false,
            cls:'edifecslogo',
            autoEl:'div',
            html:'<img src="'+logoIcon+'" />'
        });
        return edifecsLogo;
    },

    // create menu items
    createButton:function (subMenu,menuId) {
        var menuButton = new Ext.button.Button({
            scale:'medium',
            id:menuId,
            border:false,
            arrowCls:'removeArrowCls',
            menu:(subMenu != "" || subMenu != null) ? subMenu : null
        });
        return menuButton
    },

    // create submenu items
    createSubMenu:function createSubMenu() {
        var subMenu = Ext.create('Ext.menu.Menu', {
            plain:true,
            titleAlign:'left',
            floating:true,
            shadow:'sides',
            shadowOffset:18,
            bodyStyle:'border:0px',
            style:{
                borderColor:'#E6EBED',
                borderStyle:'solid',
                borderWidth:'0px',
                borderTop:'0px',
                borderRadius:'0px 0px 2px 2px'
            },
            bodyPadding:0
        });
        return subMenu;
    },

    // create separator between items
    createSeparator:function () {
        var separator = Ext.create('Ext.toolbar.Separator', {
            cls:"tbseprator",
            border:true
        });
        return separator;
    },

    // create tab spacer
    createSpacer:function () {
        var tbspacer = Ext.create('Ext.toolbar.Spacer', {
            flex:1
        });
        return tbspacer;
    },

    createAjaxHit:function (url, callbackfn) {
        Ext.Ajax.request({
            url:url,
            success:function (response) {
                if(response.responseText!="")
                {
                    var json = Ext.JSON.decode(response.responseText);
                    Ext.callback(callbackfn(json));
                }
            }
        });
    },

    // private, clean up
    onDestroy:function () {
        this.removeAll();
        Edifecs.DoormatApplicationBar.superclass.onDestroy.apply(this, arguments);
    }
});