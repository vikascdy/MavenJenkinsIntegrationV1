Ext.define('Edifecs.DoormatNavigation', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.DoormatNavigation',
    flex: 1,
    minWidth: 500,
    minHeight: 300,
    padding: 0,
    border: false,
    layout: {
        type: 'hbox'
    },
    // user defined variables
    prevButton: "",
    style: {margin: '0px'},
    doormatUrl: "",
    columnPanelHeight: 0,
    columnPanelWidth: 0,
    taskWidth: 0,
    taskStatus: true,
    dockedToolbarWidth: 0,
    widthMap: null,
    heightMap: null,
    columnPanelPadding: 0,
    statics: {
        COMPONENT_IMAGE_PATH: '../packages/ext-theme-edifecs/build/resources/images/edifecs-components/doormat/',
        ABOUT_PRODUCT: 'ABOUTPRODUCT',
        ABOUT_PRODUCT_IMAGE: 'about-selected.png',
        SETTINGS: 'SETTINGS',
        SETTINGS_IMAGE: 'settings-selected.png'
    },
    initComponent: function () {

        this.widthMap = new Ext.util.HashMap();
        this.heightMap = new Ext.util.HashMap();
        this.callParent(arguments);
    },
    // overriding superclass template method
    afterRender: function () {

        // define variables
        var me, dockedToolbar, doormatMenu, columnPanel, taskTemplate, taskList;
        var transactionTypePanel;

        var columnData = [], columnPanelItem, listMenuTemplate, columnHeaderHeight = 0;

        me = this;
        // create docked toolbar
        dockedToolbar = me.createDockedToolbar();

        // create doormat with card layout
        doormatMenu = me.createDoormatMenu('card');

        this.createAjaxHit(me.doormatUrl, function (menuJson) {
            //detect if old or new style json
            if(typeof menuJson.success === "boolean" ){
                menuJson = menuJson.data;
            }
            Ext.each(menuJson, function (menuItem, index) {

                // add icons in the docked toolbar
                if (menuItem.type != undefined)
                    dockedToolbar.add(me.addDockedToolbarIcon(menuItem.icon, dockedToolbar.id + menuItem.id, menuItem.active, menuItem.type, menuItem.defaultLinkUrl, menuItem.tooltip));
                else
                    dockedToolbar.add(me.addDockedToolbarIcon(menuItem.icon, dockedToolbar.id + menuItem.id, menuItem.active, menuItem.type, menuItem.defaultLinkUrl, menuItem.tooltip));

                // set title for each doormat item
                columnPanel = me.columnPanel(menuItem.name);

                // create template for column panel
                listMenuTemplate = me.listMenuTemplate();

                Ext.each(menuItem.menu, function (columnItem, index) {
                    var temp = null;
                    for (var prop in columnItem) {
                        temp = prop;
                    }

                    Ext.each(columnItem[temp], function (item, index) {
                        columnData.push(item);
                    });

                    if (menuItem.type != undefined) {
                        if (menuItem.type.toUpperCase() == Edifecs.DoormatNavigation.ABOUT_PRODUCT) {
                            columnPanel.add(me.createAboutContainer(columnItem.text, columnItem.image, columnItem.linkName, columnItem.linkUrl, columnItem.hrefTarget));
                        }
                        else if (menuItem.type.toUpperCase() == Edifecs.DoormatNavigation.SETTINGS) {
                            columnPanelItem = me.addColumnPanelItems(listMenuTemplate, columnData);
                            columnPanel.add(columnPanelItem);
                        }
                    }
                    else {
                        columnPanelItem = me.addColumnPanelItems(listMenuTemplate, columnData);
                        columnPanel.add(columnPanelItem);
                    }

                    columnPanelItem.addListener('afterrender', function () {
                        // set height
                        columnHeaderHeight = this.up("panel").getHeader().getHeight();
                        if (me.columnPanelHeight <= this.getHeight()) {
                            columnHeaderHeight = columnHeaderHeight + this.up("panel").getHeader().getEl().getPadding('tb') + this.up("panel").getEl().getPadding('tb');
                            me.columnPanelHeight = this.getHeight();
                            me.heightMap.add(dockedToolbar.id + menuItem.id, me.columnPanelHeight + columnHeaderHeight);
                        }
                        me.columnPanelPadding = this.up("panel").getEl().getPadding('lr');
                    });
                    // set width
                    me.columnPanelWidth = me.columnPanelWidth + columnPanelItem.width;
                    me.widthMap.add(dockedToolbar.id + menuItem.id, me.columnPanelWidth);
                    columnData = [];
                });

                transactionTypePanel = me.createDoormatMenu('hbox', dockedToolbar.id + menuItem.id);
                transactionTypePanel.add(columnPanel);

                me.taskStatus = (menuItem.tasks != undefined) ? true : false;

                if (me.taskStatus) {
                    // task panel & template
                    taskTemplate = me.taskTemplate();
                    taskList = me.createTasks(taskTemplate, menuItem.tasks, menuItem.taskheading, menuItem.contextHelp, dockedToolbar.id + menuItem.id);
                    transactionTypePanel.add(taskList);
                }

                // add toolbar & task list in the doormat menu
                doormatMenu.addDocked(dockedToolbar);
                doormatMenu.add(transactionTypePanel);

                //reset the column panel width to zero
                me.columnPanelWidth = 0;
                me.columnPanelHeight = 0;

                // add doormat menu in a container
                me.add(doormatMenu);
            });
            me.setComponentWidth(me);
            me.setComponentHeight(me);
        });

        Edifecs.DoormatNavigation.superclass.afterRender.apply(this, arguments);
        return;
    },


    setComponentHeight: function (me) {
        me.setHeight(Ext.Array.max(me.heightMap.getValues()));
        me.updateLayout();
    },

    setComponentWidth: function (me) {
        me.setWidth(me.taskWidth + me.dockedToolbarWidth + Ext.Array.max(me.widthMap.getValues()) + me.columnPanelPadding);
        me.updateLayout();
    },

    addColumnPanelItems: function (tpl, columnData) {
        var colPanelItem = Ext.create('Ext.container.Container', {
            html: tpl.apply(columnData),
            width: 210,
            padding: '0px 12px 0px 12px',
            listeners: {
                'afterrender': function (c) {
                    c.getEl().on('click', function (e, t) {
                        c.up('DoormatApplicationBar').fireEvent('menuItemClicked', t.id);
                    }, null, {delegate: '.menuItem'});
                }
            }
        });
        return colPanelItem;
    },

    createDockedToolbar: function () {
        var me = this;
        var dockedtoolbar = Ext.create('Ext.toolbar.Toolbar', {
            padding: 0,
            ui: 'edifecs-doormatnavigation-toolbar',
            border: false,
            height: '100%',
            width: 58,
            dock: 'left',
            listeners: {
                'afterrender': function (dockedToolbar) {
                    me.dockedToolbarWidth = dockedToolbar.width;
                }
            }
        });
        return dockedtoolbar;
    },

    addDockedToolbarIcon: function (tabIcon, tabId, activeItem, menuType, defaultLinkUrl, tooltipText) {
        var me = this;
        var dockedToolbarIcon = Ext.create('Ext.Button', {
            ui: 'edifecs-doormatnavigation-lbar',
            scale: 'medium',
            tooltip: tooltipText,
            cls:tabId,
            border: false,
            handler: function () {
                this.up("panel").getLayout().setActiveItem(tabId);

                if (menuType != undefined && menuType.toUpperCase() == Edifecs.DoormatNavigation.ABOUT_PRODUCT)
                    if (tabIcon != undefined)
                        me.tabImageSelection(me, this, false, tabIcon);
                    else
                        me.tabImageSelection(me, this, true, Edifecs.DoormatNavigation.ABOUT_PRODUCT_IMAGE);
                else if (menuType != undefined && menuType.toUpperCase() == Edifecs.DoormatNavigation.SETTINGS)
                    if (tabIcon != undefined)
                        me.tabImageSelection(me, this, false, tabIcon);
                    else
                        me.tabImageSelection(me, this, true, Edifecs.DoormatNavigation.SETTINGS_IMAGE);
                else
                    me.tabImageSelection(me, this, false, tabIcon);
            },
            listeners: {
                'afterrender': function () {
                    if (menuType != undefined && menuType.toUpperCase() == Edifecs.DoormatNavigation.ABOUT_PRODUCT) {
                        me.onloadtabImage(me, this, "about", tabIcon, activeItem);
                    }
                    else if (menuType != undefined && menuType.toUpperCase() == Edifecs.DoormatNavigation.SETTINGS) {
                        me.onloadtabImage(me, this, "settings", tabIcon, activeItem);
                    }
                    else {
                        if (activeItem && tabIcon != undefined) {
                            this.addCls('rightArrow');
                            this.setIcon(tabIcon.substring(0, tabIcon.lastIndexOf(".")) + "-selected.png");
                            me.prevButton = this;
                        }
                        else {
                            this.setIcon(tabIcon);
                        }
                    }
                }
            }
        });
        return dockedToolbarIcon;
    },

    onloadtabImage: function (me, currentButton, staticImageName, tabIcon, activeItem) {

        if (activeItem && tabIcon == undefined) {
            currentButton.setIcon(Edifecs.DoormatNavigation.COMPONENT_IMAGE_PATH + staticImageName + "-selected.png");
            currentButton.addCls('rightArrow');
            me.prevButton = currentButton;
        }
        else if (activeItem && tabIcon != undefined) {
            currentButton.setIcon(tabIcon.substring(0, tabIcon.lastIndexOf(".")) + "-selected.png");
            currentButton.addCls('rightArrow');
            me.prevButton = currentButton;
        }
        else if (tabIcon != undefined) {
            currentButton.setIcon(tabIcon);
        }
        else {
            currentButton.setIcon(Edifecs.DoormatNavigation.COMPONENT_IMAGE_PATH + staticImageName + ".png");
        }
    },

    tabImageSelection: function (me, currentButton, status, tabIcon) {

        if (me.prevButton && me.prevButton.icon != undefined) {
            me.prevButton.setIcon(me.prevButton.icon.substring(0, me.prevButton.icon.lastIndexOf("-")) + ".png");
            me.prevButton.removeCls('rightArrow');
        }
        if (status)
            currentButton.setIcon(Edifecs.DoormatNavigation.COMPONENT_IMAGE_PATH + tabIcon);
        else if (tabIcon != undefined)
            currentButton.setIcon(tabIcon.substring(0, tabIcon.lastIndexOf(".")) + "-selected.png");
        currentButton.addCls('rightArrow');
        me.prevButton = currentButton;
    },

    createDoormatMenu: function (layoutType, panelId) {
        var doormatMenu = Ext.create('Ext.panel.Panel', {
            id: panelId,
            flex: 1,
            height: '100%',
            border: false,
            bodyBorder: false,
            layout: layoutType
        });
        return doormatMenu;
    },

    columnPanel: function (tabName) {
        var columnPanel = Ext.create('Ext.panel.Panel', {
            title: tabName.toUpperCase(),
            flex: 1,
            layout: {
                type: 'table',
                tdAttrs: {
                    style: {
                        'vertical-align': 'top'
                    }
                }
            },
            border: false,
            ui: 'edifecs-doormatnavigation-columnpanel',
            bodyStyle: {
                background: '#FFFFFF'
            },
            listeners: {
                'afterrender': function () {
                    var length = this.getEl().select("table td").elements.length;
                    Ext.each(this.getEl().select("table td").elements,
                        function (item, index) {
                            if (index < length - 1)
                                Ext.DomHelper.applyStyles(item, 'border-right:1px solid #E6EBED');
                        });
                }
            }
        });
        return columnPanel;
    },

    // task panel for doormat
    createTasks: function (tpl, taskData, taskheading, contextHelp, contextId) {
        var me = this;
        var tasks = Ext.create('Ext.panel.Panel', {
            title: taskheading.toUpperCase(),
            width: 185,
            height: '100%',
            autoScroll: true,
            bodyPadding: '10 10 0 10',
            html: tpl.apply(taskData),
            ui: 'edifecs-doormatnavigation-tasks',
            style: {
                borderLeft: '1px solid #E6EBED !important'
            },
            border: false,
            bbar: {
                xtype: 'component',
                padding: '0 0 0 25',
                hidden: !contextHelp,
                style: {
                    backgroundColor: '#FAFBFC'
                },
                cls: 'contextHelp',
                height: 30,
                cls: 'context',
                html: '<a href="#" id="Context' + contextId + '">CONTEXT HELP</a>',
                listeners: {
                    'afterrender': function (c) {
                        c.getEl().on('click', function (e, t) {
                            c.up('DoormatApplicationBar').fireEvent('contextHelpClicked', t.id);
                        }, c);
                    }
                }
            },
            listeners: {
                'afterrender': function (taskPanel) {
                    me.taskWidth = taskPanel.width;
                    taskPanel.getEl().on('click', function (e, t) {
                        taskPanel.up('DoormatApplicationBar').fireEvent('taskItemClicked', t.id);
                    }, null, {delegate: '.taskItems'});
                }
            }
        });
        return tasks;
    },

    createAboutContainer: function (data, image, linkName, linkUrl, hrefTarget) {
        var productContainer = Ext.create('Ext.container.Container', {
            xtype: 'container',
            width: '100%',
            height: '100%',
            padding: '0 20 0 20',
            cls: 'aboutProduct',
            html: data,
            listeners: {
                'afterrender': function (sender) {
                    Ext.DomHelper.insertBefore(sender.el, {tag: 'div', cls: 'productLogo', id: 'productLogo', html: "<img src='" + image + "'/><br/><a target='" + hrefTarget + "' href='" + linkUrl + "'>" + linkName + "</a>"});
                }
            }
        });
        return productContainer;
    },

    taskTemplate: function () {
        var tpl = new Ext.XTemplate('<div class="tasks">', '<ul>',
            '<tpl for=".">',
            '<li><a href="{linkUrl}" class="taskItems" id="{id}">{text}</a></li>', '</tpl>',
            '</ul>', '</div>');
        return tpl;
    },

    createAjaxHit: function (url, callbackfn) {
        Ext.Ajax.request({
            url: url,
            success: function (response) {
                var json = Ext.JSON.decode(response.responseText);
                Ext.callback(callbackfn(json));
            }
        });
    },

    listMenuTemplate: function () {
        var me = this;
        var listMenutemplate = new Ext.XTemplate('<tpl for=".">',
            '<tpl if="text != \'\'">',
            '<div class="thumb-wrap">',
            '<a href="{linkUrl}" id="{id}" target="{[this.isTarget(values.hrefTarget)]}" onclick="{[this.isJavaScriptCode(values.javascript)]}" class="menuItem">{text}</a>',
            '<tpl for="subMenu">',
            '<div class="subItems">',
            '<a href="{linkUrl}" id="{id}" target="{[this.isTarget(values.hrefTarget)]}" onclick="{[this.isJavaScriptCode(values.javascript)]}" class="menuItem">{text}</a>',
            '<tpl if="xindex &lt; xcount">',
            '<span>|</span>',
            '</tpl>',
            '</div>',
            '</tpl>',
            '</div>',
            '<tpl else>',
            '<div class="thumb-wrap-blank">',
            '<span>&nbsp;</span>',
            '</div>',
            '</tpl>',
            '</tpl>',
            {
                isTarget: function(target){
                    return (target!="" && target!=undefined && target=="_blank")?target:"_self";
                },
                isJavaScriptCode:function(javaScriptCode)
                {
                    if(javaScriptCode!=undefined)
                       return 'callJavaScript('+javaScriptCode+');';
                }
            }
        );
        return listMenutemplate;
    },

    // private, clean up
    onDestroy: function () {
        this.removeAll();
        Edifecs.DoormatNavigation.superclass.onDestroy.apply(this, arguments);
    }

});

function callJavaScript(javaScriptCode)
{
   eval(javaScriptCode);
}