Ext.define('DD.view.widgets.charts.AreaChart', {
    extend:'Ext.chart.Chart',
    alias :'widget.areachart',
    animate: {
        easing: 'bounceOut',
        duration: 750
    },
    isDrillingAllowed:true,
    useAggregateData:true,
    chartSlices:null,
    seriesColor:null,
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

        this.axes = [
            {
                type: 'Numeric',
                position: 'left',
                fields: config.Yfields,
                title: config.YfieldLabel,
                grid: {
                    odd: {
                        opacity: 1,
                        fill: '#ddd',
                        stroke: '#bbb',
                        'stroke-width': 1
                    }
                },
                label:{
                    font:'10px Helvetica, sans-serif'
                },
                minimum: config.minimum,
                adjustMinimumByMajorUnit: 0
            },
            {
                type: 'Category',
                position: 'bottom',
                fields: config.Xfields,
                title: config.XfieldLabel,
                grid: true,
                label: {
                    font:'10px Helvetica, sans-serif',
                    rotate: {
                        degrees: 315
                    }
                }
            }
        ];
        this.series = [
            {
                type: 'area',
                highlight: false,
                axis: 'left',
                xField: config.Xfields,
                yField: config.Yfields,
                style: {
                    opacity: 0.93
                },
                listeners: {
                    'itemclick': function(attr, e) {
                        if (!this.useSampleStore) {
                            var item = attr.storeItem;
                            var store = me.getStore();
                            store.clearFilter(true);
                            me.fireEvent('drillData', me.up('portlet'), config.Xfields, item.get(config.Xfields));

//                            var chartSlices = me.chartSlices;
//
//                            Ext.each(chartSlices, function(slice) {
//                                slice.sprite.setAttributes({
//                                    fill: 'url(#grayGradient)',
//                                    label:''
//                                }, true);
//
//                            });
//                            attr.sprite.setAttributes({fill: me.seriesColor}, true);

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
            this.chartSlices = this.series.items[0].items;
            this.seriesColor = this.series.items[0].seriesStyle.fill;
        }
    }
});
