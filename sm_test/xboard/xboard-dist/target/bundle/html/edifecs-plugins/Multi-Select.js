Ext.define('Edifecs.Multi-Select', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.multiselect',
    buttonGroup: "",
    tooltip: null,
    dataURL: "",
    dataStore: null,
    stateCheckBoxes: [],
    cls: 'multi-select',
    bodyPadding: '5 0 0 23',
    bodyBorder: false,
    border: false,
    collapsible: true,
    showTooltip: true,
    checkedCount: 0,
    initComponent: function () {

        // set tooltip config options
        Ext.tip.QuickTipManager.init();

        var me = this;
        Ext.applyIf(this, {
            items: [
                {
                    xtype: 'CustomButtonGroup',
                    buttonItems: this.buttonGroup,
                    listeners: {
                        afterrender: function () {
                            Ext.DomHelper.insertAfter(me.down("CustomButtonGroup").getEl(), {
                                cls: 'tags',
                                tag: 'ul'
                            });
                        }
                    }
                }
            ]
        });


        this.dataStore = this.createStore(this.dataURL);
        this.dataStore.load();

        this.callParent(arguments);
    },
    afterRender: function () {

        var me = this;
        this.collapseTool.addCls("collapseCls");

        // attach tool tip with button
        this.down("CustomButtonGroup").on('afterrender', function (btn) {

            btn.down("#" + btn.items.keys[0]).on("click", function () {
                this.toggle('pressed');
                me.addAll(me);
            });

            Ext.each(btn.items.items, function (btn, index) {
                btn.setHeight(20);
            });

            if (me.showTooltip) {
                var itemId = btn.items.keys[btn.items.keys.length - 1];
                btn.down("#" + itemId).setText("Custom ...");
                btn.down("#" + itemId).on("click", function () {
                    this.toggle('pressed');
                    if (me.tooltip == null) {
                        me.tooltip = me.createTooltip(btn.down("#" + itemId).getEl(), "left", 5);
                        me.tooltip.setUI("white-tooltip");                        
                        me.tooltip.showAt([this.getBox().right + 20, this.getBox().top - 5]);
                    }
                    else
                        me.tooltip.showAt([this.getBox().right + 20, this.getBox().top - 5]);
                });
            }
        });

        if (me.showTooltip) {

            // create check-box list
            this.dataStore.load(function (records, operation, success) {
                var totalLength = records.length;
                me.stateCheckBoxes = [];
                Ext.each(records, function (record, index) {
                    me.stateCheckBoxes.push(
                        {
                            boxLabel: record.data.name,
                            name: 'rb',
                            inputValue: record.data.value,
                            listeners: {
                                //'change': me.addtags
                                'change': function (checkbox, newValue, oldValue) {

                                    var tooltip = Ext.getCmp(me.tooltip.el.id);
                                    var Checked = tooltip.down("panel").down("#Checked");
                                    var unChecked = tooltip.down("panel").down("#unChecked");
                                    var tags = me.body.el.select('.tags').elements;

                                    var tooltipFooter = Ext.getCmp(tooltip.dockedItems.items[1].id).down("component");
                                    var btngroup = me.down("CustomButtonGroup");
                                    var itemId = btngroup.items.keys[btngroup.items.keys.length - 1];


                                    if (newValue) {

                                        // add or remove check-box value
                                        Checked.add(checkbox);
                                        unChecked.remove(checkbox);

                                        // tag Dom structure
                                        var spec = {
                                            tag: 'li',
                                            cls: checkbox.inputValue,
                                            children: [
                                                {
                                                    tag: 'span',
                                                    cls: 'text',
                                                    html: checkbox.boxLabel
                                                },
                                                {
                                                    tag: 'span',
                                                    cls: 'closetag'
                                                }
                                            ]
                                        };

                                        // append tag
                                        if (tags.length > 0)
                                            Ext.DomHelper.append(tags[0], spec);

                                        me.checkedCount++;
                                        Ext.select("." + checkbox.inputValue + "> .closetag").on('click', function (element, e) {

                                            // suspend check-box event
                                            checkbox.suspendEvents(false);

                                            // add or remove check-box value
                                            checkbox.setValue(false);
                                            unChecked.add(checkbox);
                                            Checked.remove(checkbox);

                                            // resume check-box event
                                            checkbox.resumeEvents();

                                            //update text values
                                            me.checkedCount--;
                                            tooltipFooter.update('<span>' + me.checkedCount + ' items Selected</span>');
                                            if (me.checkedCount != 0)
                                                btngroup.down("#" + itemId).setText("Custom (" + me.checkedCount + ")...");
                                            else
                                                btngroup.down("#" + itemId).setText("Custom ...");

                                            if (Ext.get(this.id) != null)
                                                Ext.get(this.id).parent().remove();

                                            if (me.checkedCount == 0) {
                                                Ext.DomHelper.applyStyles(Checked.getEl(), 'border-bottom:0px dotted #CAD4DD;margin-bottom:0px;margin-top:0px;');
                                                btngroup.down("#" + itemId).setText("Custom ...");
                                                itemId = btngroup.items.keys[0];
                                                tooltipFooter.update('');
                                                btngroup.down("#" + itemId).toggle('pressed');
                                            }
											me.doLayout();
                                        });
                                    }
                                    else {

                                        // add or remove check-box value
                                        unChecked.add(checkbox);
                                        Checked.remove(checkbox);

                                        //update text values
                                        me.checkedCount--;
                                        Ext.select("." + checkbox.inputValue).remove();
                                    }

                                    tooltipFooter.update('<span>' + me.checkedCount + ' items Selected</span>');
                                    if (me.checkedCount != 0)
                                        btngroup.down("#" + itemId).setText("Custom (" + me.checkedCount + ")...");
                                    else
                                        btngroup.down("#" + itemId).setText("Custom ...");

                                    if (records.length == me.checkedCount)
                                        me.addAll(me);
                                }
                            }
                        });
                });
            });
        }

        this.callParent(arguments);
    },

    addAll: function (me) {

        var tooltip = Ext.getCmp(me.tooltip.el.id);
        var Checked = tooltip.down("panel").down("#Checked");
        var unChecked = tooltip.down("panel").down("#unChecked");
        var tags = me.body.el.select('.tags').elements;

        var tooltipFooter = Ext.getCmp(tooltip.dockedItems.items[1].id).down("component");
        var btngroup = me.down("CustomButtonGroup");
        var itemId = btngroup.items.keys[btngroup.items.keys.length - 1];

        Ext.each(Checked.items.items, function (checkbox, index) {
            checkbox.suspendEvent('change');
            checkbox.setValue(false);
            checkbox.resumeEvent('change');
        });

        if (Checked.items.length > 0) {
            unChecked.add(Checked.items.items);
            Checked.removeAll();
        }

        Ext.DomHelper.applyStyles(Checked.getEl(), 'border-bottom:0px dotted #CAD4DD;margin-bottom:0px;margin-top:0px;');

        me.checkedCount = 0;
        Ext.DomHelper.overwrite(tags[0], '');
        btngroup.down("#" + itemId).setText("Custom ...");
        itemId = btngroup.items.keys[0];
        tooltipFooter.update('');
        btngroup.down("#" + itemId).toggle('pressed');
        tooltip.close();
    },

    createTooltip: function (targetEl, anchorAlignment, anchorOffset) {
        var me = this;
        var btngroup = me.down("CustomButtonGroup");
        var itemId = btngroup.items.keys[0];
        var tooltip = Ext.widget('tooltip',
            {
                //target: targetEl,
                showDelay: 500,
                padding: 5,
                bodyPadding: 10,
                closeable: true,
                shadow: true,

                closeAction: 'hide',
                title: "<h4 style='padding:3px 0px 0px 7px;'>Providers</h4>",
                anchorOffset: anchorOffset,
                anchor: anchorAlignment,
                tools: [
                    {
                        type: 'close',
                        handler: function (event, toolEl, owner, tool) {
                            if (me.checkedCount == 0) {
                                btngroup.down("#" + itemId).toggle('pressed');
                            }
                            owner.ownerCt.close();
                        }
                    }
                ],
                listeners: {
                    'hide': function () {
                        if (me.checkedCount == 0) {
                            btngroup.down("#" + itemId).toggle('pressed');
                        }
                    }
                },
                itemsConfig: me.createProviderStateList(me.stateCheckBoxes),
                fbar: [
                    {
                        xtype: 'component',
                        itemId: 'tooltipFooter',
                        width: '100%',
                        padding: '0 0 0 5',
                        cls: 'toolttipTitle'
                    }
                ]
            });
        return tooltip;
    },

    createStore: function (dataURL) {
        var me = this;
        var store = new Ext.create('Ext.data.JsonStore',
            {
                fields: [
                    {name: 'name', type: 'string'},
                    {name: 'value', type: 'string'}
                ],
                autoLoad: true,
                proxy: {
                    type: 'ajax',
                    url: dataURL,
                    reader: {
                        type: 'json',
                        root: 'providers'
                    }
                }
            });
        return store;

    },

    createProviderStateList: function (stateCheckBoxes) {
        var statelist = new Ext.create('Ext.form.Panel',
            {
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
                items: [
                    {
                        xtype: 'panel',
                        itemId: "Checked",
                        autoScroll: true,
                        border: false,
                        minHeight: 0,
                        maxHeight: 150,
                        border: false,
                        defaultType: 'checkboxfield',
                        listeners: {
                            'afterrender': function () {
                                setPanelScroller(this);
                            },
                            'add': function () {
                                if (this.items.length == 1) {
                                    Ext.DomHelper.applyStyles(this.el, 'border-bottom:1px dotted #CAD4DD;margin-bottom:5px;margin-top:7px;');
                                }
                            }
                        }
                    },
                    {
                        xtype: 'panel',
                        itemId: "unChecked",
                        border: false,
                        minHeight: 0,
                        maxHeight: 150,
                        defaultType: 'checkboxfield',
                        listeners: {
                            'afterrender': function () {
                                setPanelScroller(this);
                            }
                        },
                        items: stateCheckBoxes
                    }
                ]
            });
        return statelist;
    },

    // private, clean up
    onDestroy: function () {
        this.removeAll();
        Edifecs.CustomButtonGroup.superclass.onDestroy.apply(this, arguments);
    }
});

