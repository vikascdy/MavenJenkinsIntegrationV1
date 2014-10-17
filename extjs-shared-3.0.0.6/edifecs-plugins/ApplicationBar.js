Ext.define('Edifecs.ApplicationBar', {
    extend: 'Ext.container.Container',
    alias: 'widget.ApplicationBar',
    cls: 'headerbg',
    height: 70,
    border: false,
    layout: {
        type: 'hbox',
        align: 'stretch',
        pack: 'start'
    },
    statics: {
        COMPONENT_IMAGE_PATH: '../packages/ext-theme-edifecs/build/resources/images/edifecs-components/doormat/',
        HOME_IMAGE: 'home.png',
        NAVIGATION_IMAGE: 'navigation.png',
        SEARCH_IMAGE: 'search.png',
        NOTIFICATIONS_IMAGE: 'notifications.png',
        FAVORITES_IMAGE: 'favorites.png',
        HELP_IMAGE: 'help.png',
        USER_MENU_IMAGE: 'user.png',
        HOME: 'HOME',
        NAVIGATION: 'NAVIGATION',
        SEARCH: 'SEARCH',
        NOTIFICATIONS: 'NOTIFICATION',
        FAVORITES: 'FAVOURITE',
        HELP: 'HELP',
        USER_MENU: 'SIMPLEMENU',
        APPBAR_UI: 'edifecs-doormateNavigation-appbar',
        SUBMENU_UI: 'edifecs-doormateNavigation-appbar-submenu'
    },
    appBarUrl: "",
    logoIcon: "",
    initComponent: function () {
        // set tooltip config options
        Ext.tip.QuickTipManager.init();
        Ext.apply(Ext.tip.QuickTipManager.getQuickTip(),
            {
                showDelay: 50,
                minWidth: 10,
                trackMouse: true
            });
        this.callParent(arguments);
    },
    afterRender: function () {

        // create store
        var navigationStore = this.createNavigationStore(this.appBarUrl);
        var me = this;

        navigationStore.on("load", function (store, record) {

            var navigationJSON = record[0].data;
            var menuButton, subMenu, subMenuContainer;
            var navigationLogo = navigationJSON["Logo"];
            var navigationHome = navigationJSON["UserHome"];
            var navigationNavX = navigationJSON["NavX"];
            var navigationSearch = navigationJSON["Search"];
            var navigationHelp = navigationJSON["Help"];
            var navigationAlert = navigationJSON["Alert"];
            var navigationFavourite = navigationJSON["Favourite"];
            var navigationUserSettings = navigationJSON["UserSettings"];

            // add edifecs Logo
            if (navigationLogo) {
                me.add(me.createLogo(navigationLogo.id, navigationLogo.logoIcon));
                me.add(me.createSeparator());
            }

            // Navigation Home
            if (navigationHome) {
                subMenu = me.createSubMenu();
                menuButton = me.createButton(null, navigationHome.id);
                me.setAppModules(navigationHome, menuButton, subMenu, Edifecs.ApplicationBar.HOME, Edifecs.ApplicationBar.HOME_IMAGE, Edifecs.ApplicationBar.APPBAR_UI, me);
            }

            // Doormat Nav Bar
            if (navigationNavX) {
                subMenu = me.createSubMenu();
                if (navigationNavX["sections"] != undefined) {
                    subMenuContainer = new Ext.widget('Navigation', {
                        navigationJson: navigationNavX["sections"]
                    });
                    subMenu.add([subMenuContainer]);
                }
                menuButton = me.createButton(subMenu, navigationNavX.id);
                me.setAppModules(navigationNavX, menuButton, subMenu, Edifecs.ApplicationBar.NAVIGATION, Edifecs.ApplicationBar.NAVIGATION_IMAGE, Edifecs.ApplicationBar.SUBMENU_UI, me);
                me.add(me.createSeparator());
                me.add(me.createSpacer());
            }

            // Navigation search
            if (navigationSearch) {
                subMenu = me.createSubMenu();
                menuButton = me.createButton(null, navigationSearch.id);
                menuButton.on('click', function (e, t) {
                    this.fireEvent('searchClicked', e.id);
                });
                me.setAppModules(navigationSearch, menuButton, subMenu, Edifecs.ApplicationBar.SEARCH, Edifecs.ApplicationBar.SEARCH_IMAGE, Edifecs.ApplicationBar.APPBAR_UI, me);
            }

            // Navigation Alert or Notifications
            if (navigationAlert) {
                subMenu = me.createSubMenu();
                menuButton = me.createButton(subMenu, navigationAlert.id);
                if (navigationAlert["sections"] != undefined) {
                    subMenuContainer = new Ext.widget('notifications', {
                        notificationJson: navigationAlert["sections"]
                    });
                    menuButton.on('afterrender', function (sender, event) {
                        if (navigationAlert["sections"] != undefined) {
                            if (navigationAlert["sections"].length > 0)
                                Ext.DomHelper.append(sender.el, {tag: 'div', cls: 'notificationNo', id: 'new-div-id', html: navigationAlert["sections"].length});
                            else
                                Ext.DomHelper.append(sender.el, {tag: 'div', cls: 'notificationNo', id: 'new-div-id', html: "0"});
                        }
                    });
                    subMenu.add([subMenuContainer]);
                }
                me.setAppModules(navigationAlert, menuButton, subMenu, Edifecs.ApplicationBar.NOTIFICATIONS, Edifecs.ApplicationBar.NOTIFICATIONS_IMAGE, Edifecs.ApplicationBar.SUBMENU_UI, me);
            }

            // Navigation Favorites
            if (navigationFavourite) {
                subMenu = me.createSubMenu();
                menuButton = me.createButton(subMenu, navigationFavourite.id);
                if (navigationFavourite["sections"] != "") {
                    subMenuContainer = new Ext.widget('Favorites', {
                        favoritesjson: navigationFavourite["sections"]
                    });
                }
                subMenu.add([subMenuContainer]);
                me.setAppModules(navigationFavourite, menuButton, subMenu, Edifecs.ApplicationBar.FAVORITES, Edifecs.ApplicationBar.FAVORITES_IMAGE, Edifecs.ApplicationBar.SUBMENU_UI, me);
            }

            // Navigation Help
            if (navigationHelp) {
                subMenu = me.createSubMenu();
                menuButton = me.createButton(null, navigationHelp.id);
                me.setAppModules(navigationHelp, menuButton, subMenu, Edifecs.ApplicationBar.HELP, Edifecs.ApplicationBar.HELP_IMAGE, Edifecs.ApplicationBar.APPBAR_UI, me);
            }

            // Navigation User or User settings
            if (navigationUserSettings) {
                subMenu = me.createSubMenu();
                subMenu.addCls("subMenu");
                Ext.each(navigationUserSettings, function (subItem, index) {
                    subMenu.add({
                        text: (subItem.description != undefined && subItem.description != "") ? "<ul><li>" + subItem.text + "</li><li class='description'>" + subItem.description + "</li></ul>" : subItem.text,
                        itemId: subItem.id,
                        href: (subItem.href != undefined && subItem.href != "") ? subItem.href : "#",
                        hrefTarget: (subItem.hrefTarget != undefined && subItem.hrefTarget != "") ? subItem.hrefTarget : "_self",
                        cls: (index == 0) ? 'firstMenuItem' : ''
                    });
                });

                menuButton = me.createButton(subMenu, navigationUserSettings.id);
                me.setAppModules(navigationUserSettings, menuButton, subMenu, Edifecs.ApplicationBar.USER_MENU, Edifecs.ApplicationBar.USER_MENU_IMAGE, Edifecs.ApplicationBar.SUBMENU_UI, me);
            }
        });

        Edifecs.ApplicationBar.superclass.afterRender.apply(this, arguments);
        return;
    },

    setAppModules: function (appModule, menuButton, subMenu, appModuleType, appModuleIcon, setUICls, me) {
        // id
        if (appModule.id != undefined)
            menuButton.itemId = appModule.id;

        // disabled
        if (appModule.disable)
            menuButton.setDisabled(true);

        // hidden

        if (appModule.hidden && appModuleType != Edifecs.ApplicationBar.USER_MENU)
            menuButton.hidden = true;
        else
            me.add(me.createSeparator());

        // tooltip
        if (appModule.tooltip != undefined && appModule.tooltip != null)
            menuButton.setTooltip(navigationHome.tooltip);

        // text Or Icon
        if (appModule.text != undefined && appModule.text != "")
            menuButton.setText(appModule.text);
        else
            me.setDoormatCoreImages(subMenu, menuButton, appModule.icon, appModuleIcon);

        menuButton.setUI(setUICls);
        me.add(menuButton);


        menuButton.on("click", function (sender, event) {

            if (appModuleType != "" && appModuleType != Edifecs.ApplicationBar.HOME) {
                me.setSubMenuPosition(menuButton);
            }
            else if (appModuleType == Edifecs.ApplicationBar.HOME) {
                window.location.href = appModule.linkUrl;
            }

            if (appModuleType == Edifecs.ApplicationBar.HELP) {
                if (appModule.hrefTarget != undefined && appModule.hrefTarget != "")
                    window.open(appModule.linkUrl, appModule.hrefTarget);
                else
                    window.open(appModule.linkUrl, "_blank");
            }

            // set the position of menu button after resizing window
            Ext.EventManager.onWindowResize(function () {
                if (menuButton.hasVisibleMenu())
                    me.setSubMenuPosition(menuButton);
            });
        });
    },

    setSubMenuPosition: function (menuButton) {
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


    setIconImage: function (imageName, eventString) {
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

    setDoormatIcons: function (menuButton, overrideImageIcon, imagePath, orignalImageIcon) {
        if (overrideImageIcon != "" && overrideImageIcon)
            menuButton.setIcon(overrideImageIcon);
        else
            menuButton.setIcon(imagePath + orignalImageIcon);
    },

    setDoormatIconsHover: function (menuButton, overrideImageIcon, imagePath, orignalImageIcon) {
        if (overrideImageIcon != "" && overrideImageIcon)
            menuButton.setIcon(this.setIconImage(overrideImageIcon, "-hover"));
        else
            menuButton.setIcon(this.setIconImage(imagePath + orignalImageIcon, "-hover"));
    },


    setDoormatCoreImages: function (subMenu, menuButton, overrideImageIcon, orignalImageIcon) {

        var me = this;
        me.setDoormatIcons(menuButton, overrideImageIcon, Edifecs.ApplicationBar.COMPONENT_IMAGE_PATH, orignalImageIcon);

        menuButton.on('mouseout', function (sender, event) {
            if (!this.hasVisibleMenu()) {
                me.setDoormatIcons(menuButton, overrideImageIcon, Edifecs.ApplicationBar.COMPONENT_IMAGE_PATH, orignalImageIcon);
            }
            this.blur();
        });

        menuButton.on('mouseover', function (sender, event) {
            me.setDoormatIconsHover(menuButton, overrideImageIcon, Edifecs.ApplicationBar.COMPONENT_IMAGE_PATH, orignalImageIcon);
        });


        subMenu.on('mouseover', function (menu, menuItem, event) {
            me.setDoormatIconsHover(menuButton, overrideImageIcon, Edifecs.ApplicationBar.COMPONENT_IMAGE_PATH, orignalImageIcon);
        });

        subMenu.on('mouseleave', function (menu, menuItem, event) {
            if (menuButton.hasVisibleMenu()) {
                menuButton.hideMenu();
                me.setDoormatIcons(menuButton, overrideImageIcon, Edifecs.ApplicationBar.COMPONENT_IMAGE_PATH, orignalImageIcon);
                menuButton.blur();
            }
        });

        subMenu.on('click', function () {
            menuButton.blur();
        });

        subMenu.on('hide', function (sender, e) {
            me.setDoormatIcons(menuButton, overrideImageIcon, Edifecs.ApplicationBar.COMPONENT_IMAGE_PATH, orignalImageIcon);
        });
    },

    // create navigation store
    createNavigationStore: function (appBarUrl) {
        var store = new Ext.data.JsonStore({
            autoLoad: true,
            autoSync: true,
            storeId: 'navigationStore',
            proxy: {
                type: 'ajax',
                url: appBarUrl,
                reader: {
                    type: 'json'
                }
            },
            fields: [
                {
                    name: 'Logo'
                },
                {
                    name: 'UserHome'
                },
                {
                    name: 'NavX'
                },
                {
                    name: 'Search'
                },
                {
                    name: 'Help'
                },
                {
                    name: 'Alert'
                },
                {
                    name: 'Favourite'
                },
                {
                    name: 'UserSettings'
                }
            ]
        });
        return store;
    },

    // create edifecs logo
    createLogo: function (id, logoIcon) {
        var edifecsLogo = new Ext.Component({
            border: false,
            cls: 'edifecslogo',
            itemId: id,
            autoEl: 'div',
            html: '<img src="' + logoIcon + '" />'
        });
        return edifecsLogo;
    },

    // create menu items
    createButton: function (subMenu, menuId) {
        var menuButton = new Ext.button.Button({
            scale: 'medium',
            itemId: menuId,
            border: false,
            arrowCls: 'removeArrowCls',
            menu: (subMenu != "" || subMenu != null) ? subMenu : null
        });
        return menuButton
    },

    // create subMenu items
    createSubMenu: function createSubMenu() {
        var subMenu = Ext.create('Ext.menu.Menu', {
            plain: true,
            titleAlign: 'left',
            floating: true,
            shadow: 'sides',
            shadowOffset: 18,
            bodyStyle: 'border:0px',
            style: {
                borderColor: '#E6EBED',
                borderStyle: 'solid',
                borderWidth: '0px',
                borderTop: '0px',
                borderRadius: '0px 0px 2px 2px'
            },
            bodyPadding: 0
        });
        return subMenu;
    },

    // create separator between items
    createSeparator: function () {
        var separator = Ext.create('Ext.toolbar.Separator', {
            cls: "tbseprator",
            border: true
        });
        return separator;
    },

    // create tab spacer
    createSpacer: function () {
        var tbspacer = Ext.create('Ext.toolbar.Spacer', {
            flex: 1
        });
        return tbspacer;
    },

    // private, clean up
    onDestroy: function () {
        this.removeAll();
        Edifecs.ApplicationBar.superclass.onDestroy.apply(this, arguments);
    }
});