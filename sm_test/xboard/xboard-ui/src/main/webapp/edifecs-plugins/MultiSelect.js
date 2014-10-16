Ext.define('Edifecs.MultiSelect', {
    requires: [
        'Edifecs.CustomButtonGroup',
        'Edifecs.ToolTip'
    ],
    extend: 'Ext.panel.Panel',
    alias: 'widget.multiselect',
    buttonGroup: "",
    tooltip: null,
    dataURL: "",
    itemValues: null,
    dataStore: null,
    cls: 'multi-select',
    bodyCls: 'multi-selectBody',
    itemTitle: '',
    bodyPadding: '8 10 5 23',
    bodyBorder: false,
    border: false,
    showTooltip:true,
    initComponent: function () {
        // set tooltip config options
        Ext.tip.QuickTipManager.init();

        var me = this;
        me.addEvents('checkedManually', 'uncheckedManually');

        me.stateCheckBoxes = {
            checked: [],
            unchecked: []
        };
        me.checkedCount = 0;
        me.tooltip = null;

        me.items = [{
                xtype: 'CustomButtonGroup',
                buttonItems: me.buttonGroup,
                listeners: {
                    itemsAfterrender: function() {
                        if(me.showTooltip) {
                            // attach tooltip with button
                            me.dataStore = me.createStore(me.dataURL, me.itemValues);
                        }
                    }
                }
            }, {
                xtype: 'container',
                minHeight: 0,
                maxHeight: 150,
                autoScroll: true,
                layout: 'vbox',
                itemId: 'list',
                autoEl: 'ul',
                cls: 'tags'
            }
        ];

        this.callParent(arguments);
    },/*
    afterRender: function () {
        var me = this;
        this.collapseTool.addCls("collapseCls");

        this.callParent(arguments);
    },*/
    createTooltip: function (targetEl, anchorAlignment, anchorOffset) {
        var me = this;
        var itemsConfig = me.createProviderStateList(me.stateCheckBoxes);
        var footerText = '<span>' + me.checkedCount + ' items Selected</span>';
        return Ext.widget('tooltip', {
            target: targetEl,
            showDelay: 500,
            padding: 5,
            bodyPadding: 10,
            closeable: true,
            shadow: true,
            closeAction: 'hide',
            //title: "<h4 style='padding:3px 0px 0px 7px;'>Providers</h4>",
            title: "<h4 style='padding:3px 0px 0px 7px;'>" + me.itemTitle + "</h4>",
            anchorOffset: anchorOffset,
            anchor: anchorAlignment,
            itemsConfig: itemsConfig,
            /*
            tools: [{
                    type: 'close',
                    handler: function (event, toolEl, owner, tool) {
                        me.checkEmptyList();
                        owner.ownerCt.close();
                    }
                }
            ],
            */
            listeners: {
                hide: function () {
                    me.checkEmptyList();
                }
            },
            fbar: [
                {
                    xtype: 'component',
                    itemId: 'tooltipFooter',
                    html: footerText,
                    width: '100%',
                    padding: '0 0 0 5',
                    style: "float:left;font-weight:normal;"
                }
            ]
        });
    },

    createStore: function (dataURL, itemValues) {
        var me = this;


        var store = {
            fields: [
                {name: 'id', type: 'string'},
                {name: 'description', type: 'string'},
                {name: 'checked'}
            ],
            autoLoad: true,
            listeners: {
                load: function(store, records, successful, eOpts ) {
                    if (successful) {
                        var listeners =  {
                            change: function (checkbox, newValue, oldValue) {
                                var tooltip = me.tooltip;
                                var tooltipFooter = tooltip.down('component[itemId=tooltipFooter]');

                                var btn = me.down("CustomButtonGroup");
                                var btnCustom = btn.items.items[btn.items.items.length - 1];


                                var checked = tooltip.down('panel[itemId=checked]');
                                var unchecked = tooltip.down('panel[itemId=unchecked]');
                                var list = me.down('container[itemId=list]');

                                if (newValue) {
                                    checked.add(checkbox);
                                    unchecked.remove(checkbox);

                                    list.add({
                                        xtype: 'component',
                                        autoEl: 'li',
                                        listItemDescription: checkbox.boxLabel,
                                        listItemId: checkbox.inputValue,
                                        html: '<span class="text">' + checkbox.boxLabel + '</span><span class="closetag"></span>',
                                        listeners: {
                                            afterrender: function (c) {
                                                c.getEl().on('click', function (e, t) {
                                                    me.closeItem([{id: c.listItemId, item: c}]);
                                                }, null, {delegate: '.closetag'});
                                            }
                                        }
                                    });

                                    me.checkedCount++;
                                    btnCustom.toggle('pressed');
                                }
                                else {
                                    unchecked.add(checkbox);
                                    checked.remove(checkbox);
                                    me.checkedCount--;
                                    var listItem = list.down('component[listItemId=' + checkbox.inputValue + ']');
                                    list.remove(listItem);
                                    me.checkEmptyList();
                                }
                                tooltipFooter.update('<span>' + me.checkedCount + ' items Selected</span>');
                                btnCustom.setText("Custom (" + me.checkedCount + ")..");
                            }
                        };
                        var list = me.down('container[itemId=list]');

                        var hasChecked = false;

                        Ext.each(records, function (record, index) {
                            var itemChecked = (record.data['checked'] == true);
                            if (hasChecked == false) {
                                hasChecked = (itemChecked == true);
                            }
                            var item = {
                                boxLabel: record.data['description'],
                                checked: itemChecked,
                                inputValue: record.data['id'],
                                listeners: {
                                    change: me.onChangeTooltipCheckbox,
                                    scope: me
                                }
                            };

                            if (itemChecked) {
                                me.stateCheckBoxes.checked.push(item);
                                me.checkedCount++;
                                list.add({
                                    xtype: 'component',
                                    autoEl: 'li',
                                    listItemDescription: item.boxLabel,
                                    listItemId: item.inputValue,
                                    html: '<span class="text">' + item.boxLabel + '</span><span class="closetag"></span>',
                                    listeners: {
                                        afterrender: function (c) {
                                            c.getEl().on('click', function (e, t) {
                                                me.closeItem([{id: c.listItemId, item: c}]);
                                            }, null, {delegate: '.closetag'});
                                        }
                                    }
                                });
                            } else {
                                me.stateCheckBoxes.unchecked.push(item);
                            }
                        });

                        var btn = me.down("CustomButtonGroup");
                        var btnAll = btn.items.items[0];

                        var btnCustom = btn.items.items[btn.items.items.length - 1];
                        btnCustom.setText("Custom (" + me.checkedCount + ")..");

                        if (hasChecked) {
                            btnCustom.toggle('pressed');
                        } else {
                            btnAll.toggle('pressed');
                        }

                        me.tooltip = me.createTooltip(btnCustom.getEl(), "left", 5);
                        me.tooltip.setUI("white-tooltip");

                        btnCustom.on("click", function () {
                            me.tooltip.show();
                        });
                    }
                }
            }
        };
        if (itemValues == null) {
            store.proxy = {
                type: 'ajax',
                url: dataURL,
                reader: {
                    type: 'json',
                    root: 'items'
                }
            }
        } else {
            store.data = itemValues;
        }
        return Ext.create('Ext.data.Store', store);
    },

    createProviderStateList: function (stateCheckBoxes) {
        var me = this;
        return Ext.create('Ext.form.Panel', {
            itemId: 'statelist',
            bodyPadding: '5 10 5 10',
            layout: 'anchor',
            style: {
                border: '1px solid #CAD4DD'
            },
            minHeight: 0,
            maxHeight: 330,
            minWidth: 230,
            maxWidth: 300,
            border: false,
            bodyBorder: false,
            items: [{
                    xtype: 'panel',
                    itemId: 'checked',
                    autoScroll: true,
                    border: false,
                    minHeight: 0,
                    maxHeight: 150,
                    defaultType: 'checkboxfield',
                    style: 'border-bottom:1px dotted #CAD4DD;margin-bottom:5px;margin-top:7px;',
                    items: stateCheckBoxes.checked
                }, {
                    xtype: 'panel',
                    itemId: 'unchecked',
                    autoScroll: true,
                    border: false,
                    minHeight: 0,
                    maxHeight: 150,
                    defaultType: 'checkboxfield',
                    items: stateCheckBoxes.unchecked
                }
            ]
        });
    },
    checkEmptyList: function() {
        var me = this;
        if (me.checkedCount == 0) {
            var btn = me.down("CustomButtonGroup");
            var btnAll = btn.items.items[0];
            btnAll.toggle('pressed');
        }
    },
    getFilterList: function(checked) {
        var me = this;
        var filterList = [];
        var tooltipList = me.tooltip.down('panel[itemId=statelist]').down('panel[itemId=' + (checked ? 'checked' : 'unchecked') + ']').items.items;

        Ext.each(tooltipList, function (record, index) {
            if (record.checked == checked) {
                filterList.push({
                    id: record['inputValue'],
                    description: record['boxLabel'],
                    checked: checked
                });
            }
        });

        return filterList;
    },
    getValue: function() {
        var me = this;
        var valuesList = [];

        var btn = me.down("CustomButtonGroup");
        var btnCustom = btn.items.items[btn.items.items.length - 1];
        if (btnCustom.pressed) {
            var checkedList = me.getFilterList(true);
            Ext.each(checkedList, function (record, index) {
                valuesList.push(record['id']);
            });
        }

        return valuesList;
    },
    closeItem: function(itemList, remove) {
        var me = this;
        var statelist = me.tooltip.down('panel[itemId=statelist]');

        var checkedPanel = statelist.down('panel[itemId=checked]');
        var uncheckedPanel = statelist.down('panel[itemId=unchecked]');
        var list = me.down('container[itemId=list]');

        Ext.each(itemList, function (record, index) {
            var checkbox = checkedPanel.down('checkboxfield[inputValue=' + record['id'] + ']');
            if (checkbox != null) {
                checkbox.suspendEvents(false);
                checkbox.setValue(false);

                if (remove != true) {
                    uncheckedPanel.add(checkbox);
                }

                checkedPanel.remove(checkbox);
                checkbox.resumeEvents();
                me.checkedCount--;
            }

            var item = record['item'];
            if (item == null) {
                item = list.down('component[listItemId=' + record['id'] + ']');
            }
            if (item != null) {
                list.remove(item);
            }
        });

        var tooltipFooter = me.tooltip.down('component[itemId=tooltipFooter]');
        tooltipFooter.update('<span>' + me.checkedCount + ' items Selected</span>');

        var btn = me.down("CustomButtonGroup");
        var btnCustom = btn.items.items[btn.items.items.length - 1];
        btnCustom.setText("Custom (" + me.checkedCount + ")..");

        me.checkEmptyList();
        if ((itemList.length == 1) && (!remove)) {
            me.fireEvent('uncheckedManually', itemList[0]);
        }
    },
    removeItem: function(id) {
        var me = this;
        me.closeItem([{id: id}], true);
    },
    promoteToMaster: function(master) {
        var me = this;
        var v = me.getFilterList(false);

        master.closeItem(v);
    },
    addItemUnchecked: function(item) {
        var me = this;
        var unchecked = me.tooltip.down('panel[itemId=statelist]').down('panel[itemId=unchecked]');
        unchecked.add({
            boxLabel: item['description'],
            checked: false,
            inputValue: item['id'],
            listeners: {
                change: me.onChangeTooltipCheckbox,
                scope: me
            }
        });
    },
    onChangeTooltipCheckbox: function (checkbox, newValue, oldValue) {
        var me = this;
        var tooltip = me.tooltip;
        var tooltipFooter = tooltip.down('component[itemId=tooltipFooter]');

        var btn = me.down("CustomButtonGroup");
        var btnCustom = btn.items.items[btn.items.items.length - 1];


        var checked = tooltip.down('panel[itemId=checked]');
        var unchecked = tooltip.down('panel[itemId=unchecked]');
        var list = me.down('container[itemId=list]');

        if (newValue) {
            checked.add(checkbox);
            unchecked.remove(checkbox);

            list.add({
                xtype: 'component',
                autoEl: 'li',
                listItemDescription: checkbox.boxLabel,
                listItemId: checkbox.inputValue,
                html: '<span class="text">' + checkbox.boxLabel + '</span><span class="closetag"></span>',
                listeners: {
                    afterrender: function (c) {
                        c.getEl().on('click', function (e, t) {
                            me.closeItem([{id: c.listItemId, item: c}]);
                        }, null, {delegate: '.closetag'});
                    }
                }
            });

            me.checkedCount++;
            btnCustom.toggle('pressed');
            me.fireEvent('checkedManually', {id: checkbox.inputValue, description: checkbox.boxLabel});
        }
        else {
            unchecked.add(checkbox);
            checked.remove(checkbox);
            me.checkedCount--;
            var listItem = list.down('component[listItemId=' + checkbox.inputValue + ']');
            list.remove(listItem);
            me.checkEmptyList();
            me.fireEvent('uncheckedManually', {id: checkbox.inputValue, description: checkbox.boxLabel});
        }
        tooltipFooter.update('<span>' + me.checkedCount + ' items Selected</span>');
        btnCustom.setText("Custom (" + me.checkedCount + ")..");
    },
    // private, clean up
    onDestroy: function () {
        this.removeAll();
        Edifecs.CustomButtonGroup.superclass.onDestroy.apply(this, arguments);
    }
});