Ext.define('DD.view.widgets.shapes.Triangle', {
    alias:'widget.triangle',
    extend:'Ext.draw.Component',
    viewBox: false,
    items: [
        {
            type: 'path',
            path: 'M0,0 0, 200,200,40',
            fill: "#333"
        }
    ]
});