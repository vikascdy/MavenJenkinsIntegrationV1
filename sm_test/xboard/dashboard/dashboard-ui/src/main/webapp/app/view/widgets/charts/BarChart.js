Ext.define('DD.view.widgets.charts.BarChart', {
    extend:'Ext.chart.Chart',
    alias :'widget.barchart',
    animate: {
        easing: 'bounceOut',
        duration: 750
    },
    isDrillingAllowed:true,
    useAggregateData:true,
    style:{
        font:'14px Helvetica, sans-serif'
    },
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
                position: 'bottom',
                fields: config.Xfields,
                label: {
                    font:'10px Helvetica, sans-serif',
                    renderer: Ext.util.Format.numberRenderer('0,0')
                },
                title: config.XfieldLabel,
                grid: true,
                minimum: config.minimum
            },
            {
                type: 'Category',
                position: 'left',
                fields: config.Yfields,
                title: config.YfieldLabel,
                label: {
                    font:'10px Helvetica, sans-serif'
                }
            }
        ];
        this.series = [
            {
                type: 'bar',
                axis: 'bottom',
                highlight: true,
//                label:{
//                    display: 'outside',
//                    field: config.Xfields
//                },
                tips: {
                    trackMouse: true,
                    minWidth: 200,
                    height: 28,
                    renderer: function(storeItem, item) {
                        this.setTitle(storeItem.get(config.Yfields) + ' : ' + storeItem.get(config.Xfields));
                    }
                },
                listeners: {
                    'itemclick': function(attr, e) {

                        if (!this.useSampleStore) {
                            var item = attr.storeItem;

                            var store = me.getStore();
                            store.clearFilter(true);
                            me.fireEvent('drillData', me.up('portlet'), config.Yfields, item.get(config.Yfields));

                            var chartSlices = me.chartSlices;

                            Ext.each(chartSlices, function(slice) {
                                slice.sprite.setAttributes({
                                    fill: 'url(#grayGradient)',
                                    label:''
                                }, true);

                            });
                            attr.sprite.setAttributes({fill: me.seriesColor}, true);
                        }
                    }
                },
                xField: config.Yfields,
                yField: config.Xfields
            }
        ];

        this.initTheme(config.theme);

        this.callParent(arguments);
    } ,

    listeners : {
        boxready:function() {
            this.chartSlices = this.series.items[0].items;
            this.seriesColor = this.series.items[0].seriesStyle.fill;
        }
    }

});
