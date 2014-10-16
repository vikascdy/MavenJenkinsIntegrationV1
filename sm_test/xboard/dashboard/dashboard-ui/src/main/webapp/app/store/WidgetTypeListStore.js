Ext.define('DD.store.WidgetTypeListStore', {
    extend  :'Ext.data.Store',
    model   :'DD.model.WidgetTypeListModel',
    autoLoad:true,
    proxy:{
        type:'ajax',
        url:JSON_SERVLET_PATH + 'getWidgetTypes'
    },
    listeners : {
        load : function(store, records) {
            Ext.each(records, function(rec) {
                var image = null;
                var name = rec.get('name');
                rec.set('type', name);
                switch (name) {
                    case 'chart':
                        image = 'resources/images/widgetTypes/charts.png';
                        break;
                    case 'grid':
                        image = 'resources/images/widgetTypes/grid.png';
                        break;
                    case 'image':
                        image = 'resources/images/widgetTypes/image.png';
                        break;
                    case 'text':
                        image = 'resources/images/widgetTypes/text.png';
                        break;
                    case 'shape':
                        image = 'resources/images/widgetTypes/shapes.png';
                        break;
                     case 'embedded':
                        image = 'resources/images/widgetTypes/embedded.png';
                        break;
                }
                rec.set('imageUrl', image);
                rec.commit();
            });
        }
    }
});

