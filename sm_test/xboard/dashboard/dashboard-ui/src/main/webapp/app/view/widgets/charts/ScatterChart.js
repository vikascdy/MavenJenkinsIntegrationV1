Ext.define('DD.view.widgets.charts.ScatterChart', {
    extend:'Ext.chart.Chart',
    alias :'widget.scatterchart',
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
                grid:true,
                minimum: config.minimum,
                label: {
                    font:'10px Helvetica, sans-serif'
                }
            },
            {
                type: 'Category',
                position: 'bottom',
                fields: config.Xfields,
                title: config.XfieldLabel,
                label: {
                    font:'10px Helvetica, sans-serif',
                    rotate: {
                        degrees: 315
                    }
                }
            }
        ];
        var seriesObj = [];
        var markerConfig = {
            radius: 5,
            size: 5
        };

        var i = 0;
        Ext.each(config.Yfields, function(field) {
            seriesObj.push({
                type: 'scatter',
                axis: 'left',
                xField: config.Xfields,
                yField: field,
                markerConfig: markerConfig,
                listeners: {
                    'itemclick': function(attr, e) {
                        if (!this.useSampleStore) {
                            var item = attr.storeItem;
                            var store = me.getStore();
                            store.clearFilter(true);
                            me.fireEvent('drillData', me.up('portlet'), config.Xfields, item.get(config.Xfields));

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
                }
            });
            i++;
        });
        this.series = seriesObj;

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
