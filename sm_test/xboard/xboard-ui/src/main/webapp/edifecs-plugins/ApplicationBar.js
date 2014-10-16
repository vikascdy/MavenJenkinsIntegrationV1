Ext.define('Edifecs.ApplicationBar', {
    extend: 'Ext.container.Container',
    alias: 'widget.ApplicationBar',
    cls: 'headerbg',
    height: 70,
    border: false,
    layout: {type: 'hbox', align: 'stretch', pack: 'start'},
    url: "",
    logoIcon: "",
    activeFlag: false,
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
    afterRender: function () {

        // add edifecs Logo
        this.add(this.createLogo(this.logoIcon));
        // create store
        var navigationStore = this.createNavigationStore(this.url);
        // assign current object
        var me = this, previousButton = null, firstButton;

        navigationStore.on("load", function (store, record) {
            navigationStore.each(function (item, index) {
                var menuButton, submenu;
                if (item.get("type") == "subMenu") {

                    // create sub menu object
                    submenu = me.createSubMenu();
                    submenu.addCls("subMenu-tab");

                    // iterate through sub menu items
                    Ext.each(item.data.subMenu, function (subItem, index) {
                        // add sub menu items
                        submenu.add({
                            text: subItem.text,
                            id: subItem.id
                        });
                    });

                    // create main menu item object with sub menu
                    menuButton = me.createButton(submenu);
                    menuButton.setUI('edifecs-applicationbar-submenu');
                    menuButton.setIconCls('userIconCls');

                    // remove submenu on mouse leave & click events
                    submenu.on('mouseleave', function (sender, event) {
                        if (menuButton.hasVisibleMenu()) {
                            menuButton.hideMenu();
                            menuButton.setIcon(item.get("icon"));
                            menuButton.removeCls("bottomArrowOverCls");
                            menuButton.blur();
                        }
                    });

                    submenu.on('mouseenter', function (sender, event) {
                        if (menuButton.hasVisibleMenu()) {
                            menuButton.setIcon(me.setIconImage(item.get("icon"), "-hover"));
                            menuButton.addCls("bottomArrowOverCls");
                        }
                    });

                    submenu.on('click', function () {
                        menuButton.setIcon(item.get("icon"));
                        menuButton.removeCls("bottomArrowOverCls");
                        menuButton.addCls("bottomArrowCls");
                        menuButton.blur();
                    });

                    submenu.on('hide', function (sender, e) {
                        menuButton.setIcon(item.get("icon"));
                        menuButton.removeCls("bottomArrowOverCls");
                        menuButton.addCls("bottomArrowCls");
                    });

                    menuButton.on('afterrender', function (sender, event) {
                        submenu.minWidth = this.getWidth();
                        Ext.each(submenu.items.items, function (menuItem, index) {
                            menuItem.minWidth = submenu.minWidth;
                        });
                    });
                }
                else {
                    // create menu item
                    menuButton = me.createButton();
                    menuButton.on('afterrender', function (sender, event) {
                        var btn = Ext.get(sender.id);
                        btn.on('mousedown', function (e, t) {
                            if (!sender.pressed) {
                                sender.setUI('edifecs-applicationbar-mousedown');
                            }
                        });
                    });
                }

                menuButton.setText(item.get("text"));
                if(item.get("tooltip")!=null && item.get("tooltip")!=undefined)
                    menuButton.setTooltip(item.get("tooltip"));

                // assign id to each menu item
                if (item.get("id") != undefined)
                    menuButton.id = item.get("id");

                // hide menu item
                if (item.get('hidden'))
                    menuButton.hidden = true;
                else
                    me.add(me.createSeparator());

                if (item.get("icon") && !item.get("active")) {
                    menuButton.setIcon(item.get("icon"));
                    // mouse over and out events defined
                    menuButton.on('mouseover', function (sender, event) {
                        if (!this.pressed) {
                            this.setIcon(me.setIconImage(item.get("icon"), "-hover"));
                            this.addCls("bottomArrowOverCls");
                        }
                    });

                    menuButton.on('mouseout', function (sender, event) {
                        if (!this.pressed) {
                            if (!this.hasVisibleMenu()) {
                                this.setIcon(item.get("icon"));
                                this.removeCls("bottomArrowOverCls");
                            }
                        }
                    });
                }

                // disable menu item
                if (item.get("disable")) {
                    if (item.get("icon"))
                        menuButton.setIcon("./images/appbar/" + item.get("icon") + ".png");
                    menuButton.setDisabled(true);
                }

                if (!me.activeFlag && item.get("active")) {
                    menuButton.toggle(true);
                    if (item.get("icon") != "")
                        menuButton.setIcon(me.setIconImage(item.get("icon"), "-active"));
                    me.activeFlag = true;
                    previousButton = menuButton;
                }

                if (index == 0)
                    firstButton = menuButton;

                // menu selected event defined
                menuButton.on("click", function (sender, event) {
                    if (!(item.get("subMenu") != "")) {
                        menuButton.setUI("edifecs-applicationbar");
                        if (previousButton != null) {
                            previousButton.toggle(false);
                            if (previousButton.icon != undefined)
                                previousButton.setIcon(me.setIconImage(previousButton.icon));
                        }
                        if (item.get("icon"))
                            this.setIcon(me.setIconImage(item.get("icon"), "-active"));
                        this.toggle(true);
                        previousButton = this;
                    }
                    else {
                        if (item.get("icon"))
                            this.setIcon(me.setIconImage(item.get("icon"), "-hover"));
                        me.setSubMenuPosition(menuButton);
                    }

                    // set the position of menu button after resizing window
                    Ext.EventManager.onWindowResize(function () {
                        me.setSubMenuPosition(menuButton);
                    });
                });

                // create tab spacer for left and right items
                if (item.get("text") == "tbspacer")
                    me.add(me.createSpacer());
                else
                    me.add(menuButton);
            });

            if (!me.activeFlag) {
                firstButton.toggle(true);
                if (firstButton.icon != "")
                    firstButton.setIcon(me.setIconImage(firstButton.icon, "-active"));
                previousButton = firstButton;
            }
        });

        Edifecs.ApplicationBar.superclass.afterRender.apply(this, arguments);
        return;
    },

    setIconImage: function (imageName, eventString) {
        var tempImageName, actualImageName;
        if (eventString == undefined) {
            tempImageName = imageName.substring(imageName.lastIndexOf("/") + 1,
                imageName.lastIndexOf("."));
            if (tempImageName.lastIndexOf("-") != -1)
                actualImageName = tempImageName.substring(0, tempImageName.lastIndexOf("-"));
            else
                actualImageName = tempImageName.substring(0, tempImageName.length);
            return imageName.replace(tempImageName, actualImageName);
        } else {
            tempImageName = imageName.substring(imageName.lastIndexOf("/") + 1,
                imageName.lastIndexOf("."));
            return imageName
                .replace(tempImageName, tempImageName + eventString);
        }
    },

    setSubMenuPosition: function (menuButton) {
        if (menuButton.menu != undefined) {
            menuButton.menu.showAt(0, 0);
            var windowWidth = Ext.getBody().getViewSize();
            if (menuButton.menu.el != undefined) {
                var menuWidth = menuButton.menu.el.dom.clientWidth;
                var buttonPosition = menuButton.getPosition();
                if (buttonPosition.length != 0)
                    menuButton.menu.setPosition(menuButton.getWidth() + buttonPosition[0] - menuWidth, 70);
            }
        }
    },

    // create navigation store
    createNavigationStore: function (url) {
        var store = new Ext.create('Ext.data.JsonStore',
            {
                autoLoad: true,
                autoSync: true,
                storeId: 'navigationStore',
                proxy: {
                    type: 'ajax',
                    url: url,
                    reader: {
                        type: 'json',
                        root: 'navigation'
                    }
                },
                fields: [
                    {name: 'id', type: 'string'},
                    {name: 'tooltip', type: 'string'},
                    {name: 'icon', type: 'string'},
                    {name: 'text', type: 'string'},
                    {name: 'type', type: 'string'},
                    {name: 'subMenu'},
                    {name: 'disable', type: 'boolean', defaultValue: false},
                    {name: 'hidden', type: 'boolean', defaultValue: false},
                    {name: 'active', type: 'boolean', defaultValue: false}
                ]
            });
        return store;
    },

    // create edifecs logo
    createLogo: function (logoIcon) {
        var edifecsLogo = new Ext.Component({
            border: false,
            cls: 'edifecslogo',
            autoEl: 'div',
            html: '<img src="' + logoIcon + '" />'
        });
        return edifecsLogo;
    },

    // create menu items
    createButton: function (subMenu) {
        var menuButton = new Ext.button.Button({
            scale: 'large',
            iconAlign: 'left',
            arrowAlign: 'right',
            arrowCls: 'bottomArrowCls',
            border: false,
            ui: "edifecs-applicationbar",
            menu: (subMenu != "" || subMenu != null) ? subMenu : null
        });
        return    menuButton
    },

    // create submenu items
    createSubMenu: function createSubMenu() {
        var subMenu = Ext.create('Ext.menu.Menu',
            {
                plain: true,
                titleAlign: 'left',
                showSeparator: false,
                shadow: false,
                border: false,
                bodyPadding: 0
            });
        return subMenu;
    },

    // create separator between items
    createSeparator: function () {
        var separator = Ext.create('Ext.toolbar.Separator',
            {
                cls: "tbseprator",
                border: true
            });
        return separator;
    },

    // create tab spacer
    createSpacer: function () {
        var tbspacer = Ext.create('Ext.toolbar.Spacer',
            {
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