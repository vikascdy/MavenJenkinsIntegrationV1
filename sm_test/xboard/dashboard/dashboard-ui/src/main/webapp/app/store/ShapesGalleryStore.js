Ext.define('DD.store.ShapesGalleryStore', {
    extend  :'Ext.data.Store',
    fields:['name','id','type','url'],
    data:[
        {name:'Circle',id:'circle','type':'shape',url:'resources/images/widgets/shapes/circle.png'},
        {name:'Triangle',id:'triangle','type':'shape',url:'resources/images/widgets/shapes/triangle.png'},
        {name:'Rectangle',id:'rectangle','type':'shape',url:'resources/images/widgets/shapes/rectangle.png'},
        {name:'Arrow',id:'arrow','type':'arrow',url:'resources/images/widgets/shapes/arrowline.png'},
        {name:'Line',id:'line','type':'line',url:'resources/images/widgets/shapes/line.png'}
    ]
});

