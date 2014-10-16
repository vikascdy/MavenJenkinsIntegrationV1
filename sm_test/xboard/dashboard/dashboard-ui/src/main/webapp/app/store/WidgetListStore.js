Ext.define('DD.store.WidgetListStore', {
    extend  :'Ext.data.Store',
    model   :'DD.model.WidgetListModel',
    autoLoad:false,
    proxy:{
        type:'ajax',
        url:JSON_SERVLET_PATH + 'getWidgets'
    },
    listeners : {
        load : function(store, records) {

            var filter = null;
            Ext.each(records, function(rec) {
                var imageUrl = null;

                Ext.each(rec.get('parameters'), function(param) {
                    if (param.parameterDef.name == 'Name')
                        rec.set('name', param.value);
                    else
                    if (param.parameterDef.name == 'Description')
                        rec.set('description', param.value);
                    else
                    if (param.parameterDef.name == 'Configuration') {
                        var widgetXtype = Ext.Object.fromQueryString(param.value).widgetXtype;
                        rec.set('widgetXtype', widgetXtype);
                        switch (widgetXtype) {
                            case 'columnchart':
                                imageUrl = 'resources/images/widgets/charts/chart-column.png';
                                break;
                            case 'barchart':
                                imageUrl = 'resources/images/widgets/charts/chart-bar.png';
                                break;
                            case 'areachart':
                                imageUrl = 'resources/images/widgets/charts/chart-area.png';
                                break;
                            case 'linechart':
                                imageUrl = 'resources/images/widgets/charts/chart-line.png';
                                break;
                            case 'scatterchart':
                                imageUrl = 'resources/images/widgets/charts/chart-scatter.png';
                                break;
                            case 'piechart':
                                imageUrl = 'resources/images/widgets/charts/chart-pie.png';
                                break;
                             case 'gridpanel':
                                imageUrl = 'resources/images/widgets/grid.png';
                                break;
                        }
                        rec.set('imageUrl', imageUrl);
                    }
                });
                rec.commit();
            });

//            switch (WidgetManager.activePortlet.widgetType) {
//                case 'chart' :
//                    filter = 'Chart:widget';
//                    break;
//                case 'grid' :
//                    filter = 'Grid:widget';
//                    break;
//            }
//            store.clearFilter();
//            store.filter('widgetCategory', filter);
        }
    }
});


