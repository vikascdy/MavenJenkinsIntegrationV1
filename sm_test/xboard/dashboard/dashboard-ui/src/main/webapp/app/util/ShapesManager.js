Ext.define('Util.ShapesManager', {});

window.ShapesManager = {
    active:null,
    showShapesGallery : function(callback) {
        var newShapesWindow = Ext.widget({xtype:'shapeswindow'});
        newShapesWindow.show();
        Ext.callback(callback, this, []);
    },
    handleShapeSelection:function(shape, callback) {
        var customColorPickerField = Ext.create('Ext.ux.colorpicker.ColorPickerField');
        var source = new Array();
        switch (shape.id) {
            case 'circle':
                source[0] = {
                    Name:"circle",
                    x:20,
                    y:20,
                    radius:20,
                    fillColor:'#333333'
                };
                source[1] = {
                    fillColor: {
                        displayName:"Fill Color",
                        editor: customColorPickerField

                    },
                    Name:{
                        editable:false
                    }
                };
                Ext.callback(callback, this, [source]);
                break;
            case 'rectangle':
                source[0] = {
                    Name:'Rectangle',
                    x:20,
                    y:20,
                    height:100,
                    width:100,
                    fillColor:'#333333',
                    orientation:'right'
                };
                source[1] = {
                    fillColor: {
                        displayName:"Fill Color",
                        editor: customColorPickerField

                    },
                    orientation: {
                        displayName:"Orientation",
                        editor: new Ext.form.field.ComboBox({
                            editable:false,
                            store: ['right','left','up','down']
                        })
                    }
                };
                Ext.callback(callback, this, [source]);
                break;
            case 'triangle':
                source[0] = {
                    Name:'Triangle',
                    path: 'M200 200 L208 206 L200 212 Z',
                    fillColor:'#333333',
                    orientation:'right'
                };
                source[1] = {
                    fillColor: {
                        displayName:"Fill Color",
                        editor: customColorPickerField
                    },
                    orientation:{
                        editor: new Ext.form.field.ComboBox({
                            editable:false,
                            store: ['right','left','down','up']
                        })
                    }
                };
                Ext.callback(callback, this, [source]);
                break;
            case 'arrow':
                source[0] = {
                    Name:'Arrow',
                    path: "M200 200 L208 206 L200 212 Z M200 207 L180 207 L180 205 L200 205 Z",
                    fillColor:'#333333',
                    orientation:'right'
                };
                source[1] = {
                    fillColor: {
                        displayName:"Fill Color",
                        editor: customColorPickerField

                    },
                    orientation: {
                        displayName:"Orientation",
                        editor: new Ext.form.field.ComboBox({
                            editable:false,
                            store: ['right','left','up','down']
                        })
                    }
                };
                Ext.callback(callback, this, [source]);
                break;
            case 'line':
                source[0] = {
                    Name:'Line',
                    path: "M200 207 L180 207 L180 205 L200 205 Z",
                    fillColor:'#333333',
                    orientation:'right'
                };
                source[1] = {
                    fillColor: {
                        displayName:"Fill Color",
                        editor: customColorPickerField

                    },
                    orientation: {
                        displayName:"Orientation",
                        editor: new Ext.form.field.ComboBox({
                            editable:false,
                            store: ['right','left','up','down']
                        })
                    }
                };
                Ext.callback(callback, this, [source]);
                break;

        }
    },
    handleAddEvent:function() {
        if (ShapesManager.active == null)
            Ext.Msg.alert('No Shape Selected');
        var activeShape = ShapesManager.active;
        var portlet = WidgetManager.activePortlet;
        var x, y, color, radius, height, width = null, path = null, widget;
        var shapesWindow = ShapesManager.active.up('window');
        var orientation = shapesWindow.down('propertygrid').source.orientation;
        console.log(orientation);
        var rotateBy = 0;
        switch (orientation) {
            case 'left':
                rotateBy = 180;
                break;
            case 'down':
                rotateBy = 90;
                break;
            case 'up':
                rotateBy = 270;
                break;
        }
        console.log(rotateBy);
        switch (activeShape.id) {
            case 'circle':
                color = shapesWindow.down('propertygrid').source.fillColor;
                x = shapesWindow.down('propertygrid').source.y;
                y = shapesWindow.down('propertygrid').source.x;
                radius = shapesWindow.down('propertygrid').source.radius;
                var circle = Ext.create('Ext.draw.Sprite', {
                    type:'circle',
                    fill:color,
                    x:x,
                    y:y,
                    radius:radius
                });
                widget = {
                    xtype:'drawcomponent',
                    items:[circle]
                };
                portlet.updateWidget(widget, function() {
//                       console.log(WidgetManager.activePortlet);
                });
                break;
            case 'rectangle':
                color = shapesWindow.down('propertygrid').source.fillColor;
                x = shapesWindow.down('propertygrid').source.y;
                y = shapesWindow.down('propertygrid').source.x;
                height = shapesWindow.down('propertygrid').source.height;
                width = shapesWindow.down('propertygrid').source.width;
                var rectangle = Ext.create('Ext.draw.Sprite', {
                    type: 'rect',
                    fill:  color,
                    height:height,
                    width:width,
                    x: x,
                    y: y,
                    rotate:{
                        x:0,
                        y:0,
                        degrees:rotateBy
                    }

                });
                widget = {
                    xtype:'drawcomponent',
                    items:[rectangle]
                };
                portlet.updateWidget(widget, function() {
                });
//                console.log(portlet.down('draw').surface.rotate(rectangle));
                break;
            case 'triangle':
                color = shapesWindow.down('propertygrid').source.fillColor;
                path = shapesWindow.down('propertygrid').source.path;
                var triangle = Ext.create('Ext.draw.Sprite', {
                    type: 'path',
                    path: path,
                    fill: color,
                    rotate:{
                        x:0,
                        y:0,
                        degrees:rotateBy
                    }
                });
                widget = {
                    xtype:'drawcomponent',
                    items:[triangle]
                };
                portlet.updateWidget(widget, function() {
//                       console.log(WidgetManager.activePortlet);
                });
                break;
            case 'arrow':
                color = shapesWindow.down('propertygrid').source.fillColor;
                path = shapesWindow.down('propertygrid').source.path;
                var arrow = Ext.create('Ext.draw.Sprite', {
                    type: 'path',
                    path: path,
                    fill: color,
                    rotate:{
                        x:0,
                        y:0,
                        degrees:rotateBy
                    }

                });
                widget = {
                    xtype:'drawcomponent',
                    items:[arrow]
                };
                portlet.updateWidget(widget, function() {
//                       console.log(WidgetManager.activePortlet);
                });
                break;
            case 'line':
                color = shapesWindow.down('propertygrid').source.fillColor;
                path = shapesWindow.down('propertygrid').source.path;
                var line = Ext.create('Ext.draw.Sprite', {
                    type: 'path',
                    path: path,
                    fill: color,
                    rotate:{
                        x:0,
                        y:0,
                        degrees:rotateBy
                    }
                });
                widget = {
                    xtype:'drawcomponent',
                    items:[line]
                };
                portlet.updateWidget(widget, function() {

                });
                break;

        }
    }
};