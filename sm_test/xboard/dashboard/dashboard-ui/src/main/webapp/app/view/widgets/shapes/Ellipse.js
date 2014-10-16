Ext.create('DD.view.widgets.shapes.Ellipse', {
    extend:'Ext.draw.Component',
    alias:'widget.ellipse',
    viewBox:true,
    padding:10,
    items: [{
        type: "ellipse",
        radiusX: 100,
        radiusY: 50,
        x: 100,
        y: 100,
        fill: 'red'
    }]
});