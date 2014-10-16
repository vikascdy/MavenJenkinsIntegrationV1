Ext.define('DD.view.widgets.charts.RadarChart', {
    extend:'Ext.chart.Chart',
    alias :'widget.radarchart',
     animate: {
                easing: 'bounceOut',
                duration: 750
            },
    isDrillingAllowed:true,
    useAggregateData:true,
    initComponent : function() {

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
                type: 'Radial',
                position: 'radial',
                label: {
                    font:'10px Helvetica, sans-serif',
                    display: true
                }
            }
        ];
        var seriesObj = [];
        Ext.each(config.Yfields, function(field) {
            seriesObj.push({
                type: 'radar',
                xField: config.Xfields,
                yField: field,
                showInLegend: true,
                showMarkers: true,
                markerConfig: {
                    radius: 5,
                    size: 5
                },
                style: {
                    'stroke-width': 2,
                    fill: 'none'
                },
                listeners: {
                    'itemclick': function(attr, e) {
                        var item = attr.storeItem;
                        var store = me.getStore();
                        store.clearFilter(true);
                        me.fireEvent('drillData', me.up('portlet'), config.Yfields, item.get(config.Yfields));
                    }
                }
            });
        });

        this.series = seriesObj;

        this.initTheme(config.theme);

        this.callParent(arguments);
    }
});
