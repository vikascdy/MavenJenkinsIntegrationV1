Ext.define('DD.view.widgets.shapes.Rectangle', {
    extend:'Ext.draw.Component',
    alias:'widget.rectangle',
    viewBox: true,
    padding:10,
    items: [{
        type: 'rect',
        fill: '#79BB3F',
        height:100,
        width:100,
        x: 100,
        y: 100
    }]
});