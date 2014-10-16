Ext.define('DD.view.widgets.charts.PieChart', {
    extend:'Ext.chart.Chart',
    alias :'widget.piechart',
    animate: {
        easing: 'bounceOut',
        duration: 750
    },
    isDrillingAllowed:true,
    useAggregateData:true,
    chartSlices:null,
    initComponent : function() {
        var me = this;

        var config = this.configObj;
        if (this.useSampleStore)
            this.store = ChartManager.createStoreInstanceUsingConfig(config.storeConfig, config.storeData);
        else
            this.store = this.storeInstance;

        if (config.legend == "true") {
            this.legend = {
                position: config.legendPosition,
                boxStroke:null
            };
        }

        this.gradients = [
            {
                id: 'grayGradient',
                angle: 20,
                stops: {
                    0: {
                        color: '#817f7f'
                    },
                    100: {
                        color: '#F0F0F0'
                    }
                }
            }
        ];
        this.series = [
            {
                type: 'pie',
                donut:config.donut,
                animate:true,
                insetPadding:20,
                angleField: config.Xfields,
                showInLegend: true,
                highlight: {
                    segment: {
                        margin: 20
                    }
                },
                draggable:true,
                label: {
                    field: config.Yfields,
                    display: 'rotate',
                    contrast: true,
                    font:'10px Helvetica, sans-serif',
                    renderer: function(label, storeItem, item, i, display, animate, index) {
                        var total = 0;
                        me.store.each(function(rec) {
                            total += rec.get(config.Xfields);
                        });
                        return( Math.round(item.get(config.Xfields) / total * 100) + '%');
                    }
                },
                tips: {
                    trackMouse: true,
                    width: 200,
                    height: 28,
                    renderer: function(storeItem, item) {
                        this.setTitle(storeItem.get(config.Yfields) + ' : ' + storeItem.get(config.Xfields));
                    }
                },
                listeners: {
                    'itemclick': function(attr, e) {
                        if (!me.useSampleStore) {

                            var item = attr.storeItem;
                            var store = me.getStore();
                            store.clearFilter(true);
                            me.fireEvent('drillData', me.up('portlet'), config.Yfields, item.get(config.Yfields));


                            var originalColor = null;
                            var chartSlices = me.chartSlices;

                            Ext.each(chartSlices, function(slice) {
                                slice.sprite[0].setAttributes({
                                    fill: 'url(#grayGradient)',
                                    label:''
                                }, true);
                                if (slice.sprite[0].id == attr.sprite.id)
                                    originalColor = slice.sprite[0].fill;
                            });
                            attr.sprite.setAttributes({fill: originalColor}, true);
                        }

                    }
                }
            }
        ];


        this.initTheme(config.theme);

        this.callParent(arguments);
    },

    listeners : {
        boxready:function() {
            this.chartSlices = this.series.items[0].slices;
        }
    }
});
