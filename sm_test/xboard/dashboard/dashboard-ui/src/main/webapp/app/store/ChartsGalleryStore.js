Ext.define('DD.store.ChartsGalleryStore', {
    extend  :'Ext.data.Store',
    fields:['name','id','type','url'],
    data:[
        {name:'Area Chart',id:'areaChart','type':'chart',url:'resources/images/widgets/charts/chart-area.png'},
        {name:'Bar Chart',id:'barChart','type':'chart',url:'resources/images/widgets/charts/chart-bar.png'},
        {name:'Column Chart',id:'columnChart','type':'chart',url:'resources/images/widgets/charts/chart-column.png'},
        {name:'Line Chart',id:'lineChart','type':'chart',url:'resources/images/widgets/charts/chart-line.png'},
        {name:'Pie Chart',id:'pieChart','type':'chart',url:'resources/images/widgets/charts/chart-pie.png'},
        {name:'Pie Chart',id:'pieChart','type':'chart',url:'resources/images/widgets/charts/chart-pie.png'},
        {name:'Scatter Chart',id:'scatterChart','type':'chart',url:'resources/images/widgets/charts/chart-scatter.png'}
//        {name:'Gauge Chart',id:'','type':'chart',url:'resources/images/widgets/charts/chart-gauge.png'}
    ]
});

