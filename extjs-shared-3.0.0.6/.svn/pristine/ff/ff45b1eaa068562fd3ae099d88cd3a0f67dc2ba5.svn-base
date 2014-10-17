Ext.define('Edifecs.Navigation', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.Navigation',
    flex: 1,
    minHeight: 300,
    padding: 0,
    border: false,
    layout: {
        type: 'hbox'
    },
    // user defined variables
    prevButton: "",
    style: {margin: '0px'},
    navigationJson: "",
    columnPanelHeight: 0,
    columnPanelWidth: 0,
    taskWidth: 0,
    taskStatus: false,
    taskMap: null,
    widthMap: null,
    heightMap: null,
    columnPanelPadding: 20,
    activeCardId: null,
    activeTabIcon: null,
    statics: {
        COMPONENT_IMAGE_PATH: '../packages/ext-theme-edifecs/build/resources/images/edifecs-components/doormat/',
        NO_OF_ITEMS_COL_LEVEL: 10,
        TASKS: 'TASKS'
    },
    initComponent: function () {
        this.taskMap = new Ext.util.HashMap();
        this.widthMap = new Ext.util.HashMap();
        this.heightMap = new Ext.util.HashMap();
        this.callParent(arguments);
    },
    // overriding superclass template method
    afterRender: function () {

        // define variables
        var me, dockedToolbar, navigationMenu, columnPanel, taskTemplate, taskList, transactionTypePanel;
        var columnData, columnDataOne, columnDataTasks, columnPanelItem, listMenuTemplate;

        //current instance refernce
        me = this;

        // create docked toolbar
        dockedToolbar = me.createDockedToolbar();

        // create navigation with card layout
        navigationMenu = me.createDoormatMenu('card');

        //iterate through json loop
        Ext.each(me.navigationJson, function (menuItem, index) {

            // initialize array to empty
            columnData = [];
            columnDataOne = [];
            columnDataTasks = [];

            //add docked toolbar
            dockedToolbar.add(me.addDockedToolbarIcon(menuItem.icon, dockedToolbar.id + menuItem.id, menuItem.active, menuItem.defaultLinkUrl, menuItem.tooltip));

            // check for active toolbar
            if (menuItem.active) {
                me.activeCardId = dockedToolbar.id + menuItem.id;
                me.activeTabIcon = menuItem.icon;
            }

            // set title for each navigation item
            columnPanel = me.columnPanel(menuItem.name, dockedToolbar.id + menuItem.id);

            // create template for column panel
            listMenuTemplate = me.listMenuTemplate();

            // divide the groups into two column layout
            Ext.each(menuItem.groups, function (columnItem, index) {
                if (index >= Math.round(menuItem.groups.length / 2) && menuItem.groups.length >= Edifecs.Navigation.NO_OF_ITEMS_COL_LEVEL) {
                    Ext.each(columnItem, function (item, index) {
                        if (item.text.toUpperCase() == Edifecs.Navigation.TASKS)
                            columnDataTasks.push(columnItem);
                        else
                            columnDataOne.push(item);
                    });
                }
                else {
                    Ext.each(columnItem, function (item, index) {
                        if (item.text.toUpperCase() == Edifecs.Navigation.TASKS)
                            columnDataTasks.push(columnItem);
                        else
                            columnData.push(item);
                    });
                }
            });

            // add items into column layout
            columnPanelItem = me.addColumnPanelItems(listMenuTemplate, columnData);
            columnPanel.add(columnPanelItem);

            //check for second column array exist or not
            if (columnDataOne.length > 0) {
                columnPanelItem = me.addColumnPanelItems(listMenuTemplate, columnDataOne);
                columnPanel.add(columnPanelItem);
                columnPanelItem.width = columnPanelItem.width * 2;
            }

            // calculate total navigation width exclude task width
            me.columnPanelWidth = me.columnPanelWidth + dockedToolbar.width + columnPanelItem.width + me.columnPanelPadding;

            transactionTypePanel = me.createDoormatMenu('hbox', dockedToolbar.id + menuItem.id);
            transactionTypePanel.add(columnPanel);

            // check for tasks exist or not
            me.taskStatus = (columnDataTasks.length > 0) ? true : false;
            if (me.taskStatus) {
                taskTemplate = me.taskTemplate();
                if (columnDataTasks[0].navItems != undefined) {
                    taskList = me.createTasks(taskTemplate, columnDataTasks[0].navItems, columnDataTasks[0].text, dockedToolbar.id + menuItem.id);
                    transactionTypePanel.add(taskList);
                }
                me.taskMap.add(dockedToolbar.id + menuItem.id, me.taskStatus);
                me.taskWidth = taskList.width;
            }

            // add width of each navigation panel into hashmap
            me.widthMap.add(dockedToolbar.id + menuItem.id, me.columnPanelWidth);

            // add toolbar & task list in the navigation menu
            navigationMenu.addDocked(dockedToolbar);
            navigationMenu.add(transactionTypePanel);

            // add navigation menu in a container
            me.add(navigationMenu);

            //reset the column panel width to zero
            me.columnPanelWidth = 0;

        });

        //set active navigation
        dockedToolbar.up("panel").getLayout().setActiveItem(me.activeCardId);
        dockedToolbar.down("#dockIcon" + me.activeCardId).addCls('rightArrow');
        dockedToolbar.down("#dockIcon" + me.activeCardId).setIcon(me.activeTabIcon.substring(0, me.activeTabIcon.lastIndexOf(".")) + "-selected.png");
        me.prevButton = dockedToolbar.down("#dockIcon" + me.activeCardId);

        // check task list exist and set its width in navigation
        if (me.taskMap.get(me.activeCardId))
            me.setWidth(Ext.Array.max(me.widthMap.getValues()) + me.taskWidth);
        else
            me.setWidth(Ext.Array.max(me.widthMap.getValues()));
        me.updateLayout();

        me.columnPanelHeight = 0;

        Edifecs.Navigation.superclass.afterRender.apply(this, arguments);
        return;
    },

    addColumnPanelItems: function (tpl, columnData) {
        var colPanelItem = Ext.create('Ext.container.Container', {
            html: tpl.apply(columnData),
            width: 210,
            padding: '0px 12px 0px 12px',
            listeners: {
                'afterrender': function (c) {
                    c.getEl().on('click', function (e, t) {
                        c.up('ApplicationBar').fireEvent('menuItemClicked', t.id);
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
            dock: 'left'
        });
        return dockedtoolbar;
    },

    addDockedToolbarIcon: function (tabIcon, tabId, activeItem, defaultLinkUrl, tooltipText) {
        var me = this;
        var dockedToolbarIcon = Ext.create('Ext.Button', {
            ui: 'edifecs-doormatnavigation-lbar',
            scale: 'medium',
            itemId: "dockIcon" + tabId,
            tooltip: tooltipText,
            cls: tabId,
            border: false,
            handler: function () {
                this.up("panel").getLayout().setActiveItem(tabId);
                if (me.taskMap.get(tabId))
                    me.setWidth(Ext.Array.max(me.widthMap.getValues()) + me.taskWidth);
                else
                    me.setWidth(Ext.Array.max(me.widthMap.getValues()));
                me.updateLayout();
                me.tabImageSelection(me, this, false, tabIcon);
            },
            listeners: {
                'afterrender': function () {
                    if (me.activeCardId != tabId)
                        this.setIcon(tabIcon);
                }
            }
        });
        return dockedToolbarIcon;
    },

    onloadtabImage: function (me, currentButton, staticImageName, tabIcon, activeItem) {

        if (activeItem && tabIcon == undefined) {
            currentButton.setIcon(Edifecs.Navigation.COMPONENT_IMAGE_PATH + staticImageName + "-selected.png");
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
            currentButton.setIcon(Edifecs.Navigation.COMPONENT_IMAGE_PATH + staticImageName + ".png");
        }
    },

    tabImageSelection: function (me, currentButton, status, tabIcon) {

        if (me.prevButton && me.prevButton.icon != undefined) {
            me.prevButton.setIcon(me.prevButton.icon.substring(0, me.prevButton.icon.lastIndexOf("-")) + ".png");
            me.prevButton.removeCls('rightArrow');
        }
        if (status)
            currentButton.setIcon(Edifecs.Navigation.COMPONENT_IMAGE_PATH + tabIcon);
        else if (tabIcon != undefined)
            currentButton.setIcon(tabIcon.substring(0, tabIcon.lastIndexOf(".")) + "-selected.png");
        currentButton.addCls('rightArrow');
        me.prevButton = currentButton;
    },

    createDoormatMenu: function (layoutType, panelId) {
        var navigationMenu = Ext.create('Ext.panel.Panel', {
            id: panelId,
            flex: 1,
            height: '100%',
            border: false,
            bodyBorder: false,
            layout: layoutType
        });
        return navigationMenu;
    },

    columnPanel: function (tabName, tabId) {
        var me = this;
        var columnPanel = Ext.create('Ext.panel.Panel', {
            title: tabName.toUpperCase(),
            flex: 1,
            layout: {
                type: 'table',
                columns: 2,
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
                    var el = this.getEl();
                    var length = el.select("table td").elements.length;
                    Ext.each(this.getEl().select("table td").elements,
                        function (item, index) {
                            if (length > 1 && index % 2 == 1) {
                                Ext.DomHelper.applyStyles(item, 'border-left:1px solid #E6EBED');
                            }
                        });

                    me.columnPanelHeight = this.getHeader().getHeight() + this.getHeader().getEl().getPadding('tb') + this.body.getHeight() + this.getEl().getPadding('tb');
                    me.heightMap.add(tabId, me.columnPanelHeight);
                    me.setHeight(Ext.Array.max(me.heightMap.getValues()));
                    me.updateLayout();
                }
            }
        });
        return columnPanel;
    },

    // task panel for Navigation
    createTasks: function (tpl, taskData, taskheading) {
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
            listeners: {
                'afterrender': function (taskPanel) {
                    taskPanel.getEl().on('click', function (e, t) {
                        taskPanel.up('ApplicationBar').fireEvent('taskItemClicked', t.id);
                    }, null, {delegate: '.taskItems'});
                }
            }
        });
        return tasks;
    },

    taskTemplate: function () {
        var tpl = new Ext.XTemplate(
            '<div class="tasks">',
                '<ul>',
                    '<tpl for=".">',
                        '<tpl if="linkUrl != \'\' && linkUrl!=undefined">',
                            '<li><a href="{linkUrl}" class="taskItems" id="{id}">{text}</a></li>',
                        '<tpl else>',
                            '<li><span class="taskItems" id="{id}">{text}</span></li>',
                        '</tpl>',
                    '</tpl>',
                '</ul>',
            '</div>');
        return tpl;
    },

    listMenuTemplate: function () {
        var me = this;
        var listMenutemplate = new Ext.XTemplate(
            '<tpl for=".">',
                '<tpl if="text != \'\'">',
                    '<div class="thumb-wrap">',
                        '<tpl if="linkUrl != \'\' && linkUrl!=undefined">',
                            '<a href="{linkUrl}" id="{id}" target="{[this.isTarget(values.hrefTarget)]}" onclick="{[this.isJavaScriptCode(values.javascript)]}" class="menuItem">{text}</a>',
                        '<tpl else>',
                            '<span id="{id}" class="menuItem">{text}</span>',
                        '</tpl>',
                        '<tpl for="navItems">',
                            '<div class="subItems">',
                                '<tpl if="linkUrl != \'\' && linkUrl!=undefined">',
                                    '<a href="{linkUrl}" id="{id}" target="{[this.isTarget(values.hrefTarget)]}" onclick="{[this.isJavaScriptCode(values.javascript)]}" class="menuItem">{text}</a>',
                                '<tpl else>',
                                    '<span id="{id}" class="menuItem">{text}</span>',
                                '</tpl>',
                                '<tpl if="xindex &lt; xcount">',
                                    '<span>|</span>',
                                '</tpl>',
                            '</div>',
                        '</tpl>',
                    '</div>',
                '<tpl else>',
                    '<div class="thumb-wrap thumb-wrap-blank">',
                        '<span>&nbsp;</span>',
                    '</div>',
                '</tpl>',
            '</tpl>',
            {
                isTarget: function (target) {
                    return (target != "" && target != undefined && target == "_blank") ? target : "_self";
                },
                isJavaScriptCode: function (javaScriptCode) {
                    if (javaScriptCode != undefined)
                        return 'callJavaScript(' + javaScriptCode + ');';
                }
            }
        );
        return listMenutemplate;
    },

    // private, clean up
    onDestroy: function () {
        this.removeAll();
        Edifecs.Navigation.superclass.onDestroy.apply(this, arguments);
    }

});

function callJavaScript(javaScriptCode) {
    eval(javaScriptCode);
}