Ext.define('Util.LayoutManager', {});

window.LayoutManager = {

    previousTarget:null,
    currentTarget:null,
    noOfRows:null,
    noOfCols:null,

    neighbourArray:null,

    initializeGridSize : function(noOfRows, noOfCols, callback) {
        LayoutManager.noOfRows = noOfRows;
        LayoutManager.noOfCols = noOfCols;
        Ext.callback(callback, this, []);
    },


    generateCanvasGrid : function() {
        var me = this;

        var canvasGrid = DashboardManager.isEditMode ? '<table width="100%" height="100%" class="canvasGrid" id="canvasGrid" >' : '<table width="100%" height="100%" class="canvasGridBkd" id="canvasGrid" >';

        for (var i = 0; i < LayoutManager.noOfRows; i++) {
            var canvasGridRow = DashboardManager.isEditMode ? '<tr class="canvasRow">' : '<tr>';
            for (var j = 0; j < LayoutManager.noOfCols; j++) {
                if (DashboardManager.isEditMode)
                    canvasGridRow += '<td class="canvasCol" id="canvasGrid-cell-' + i + '-' + j + '"></td>';
                else
                    canvasGridRow += '<td id="canvasGrid-cell-' + i + '-' + j + '"></td>';

            }
            canvasGridRow += '</tr>';
            canvasGrid += canvasGridRow;
        }

        canvasGrid += '</table>';

        return canvasGrid;
    },

    alignPortlet : function(freeFormLayout, portlet, x, y, showAlignmentLines) {


        var noOfCols = LayoutManager.noOfCols;
        var noOfRows = LayoutManager.noOfRows;


        var actualCanvasWidth = freeFormLayout.getEl().dom.clientWidth;
        var actualCanvasHeight = freeFormLayout.getEl().dom.clientHeight;

        var actualCellWidth = actualCanvasWidth / noOfCols;
        var actualCellHeight = actualCanvasHeight / noOfRows;

        var requiredCellWidth = parseFloat((actualCanvasWidth / noOfCols).toString().split(".")[0]);
        var requiredCellHeight = parseFloat((actualCanvasHeight / noOfRows).toString().split(".")[0]);


        var requiredCanvasWidth = requiredCellWidth * noOfCols;
        var requiredCanvasHeight = requiredCellHeight * noOfRows;


        var leftRightMargin = Math.abs(actualCanvasWidth - requiredCanvasWidth);
        var topBottomMargin = Math.abs(actualCanvasHeight - requiredCanvasHeight);


        freeFormLayout.setSize(requiredCanvasWidth, requiredCanvasHeight);

//        console.log(leftRightMargin, topBottomMargin, requiredCellWidth, requiredCellHeight);


        var portletDOM = portlet.getEl().dom;


        var portletOffsetLeft = portletDOM.offsetLeft;
        var portletOffsetTop = portletDOM.offsetTop;

        var portletOffsetRight = portletOffsetLeft + portlet.getWidth();
        var portletOffsetBottom = portletOffsetTop + portlet.getHeight();


        var startCol = Math.floor(portletOffsetLeft / actualCellWidth);
        var startRow = Math.floor(portletOffsetTop / actualCellHeight);
        var endCol = Math.floor(portletOffsetRight / actualCellWidth);
        var endRow = Math.floor(portletOffsetBottom / actualCellHeight);


        var moveLeft = Math.abs((startCol * actualCellWidth) - portletOffsetLeft);
        var moveTop = Math.abs((startRow * actualCellHeight) - portletOffsetTop);

        var newHeight = (endRow - startRow) * actualCellHeight;
        var newHWidth = (endCol - startCol) * actualCellWidth;

//        console.log(portletOffsetLeft, portletOffsetTop);

//        console.log(actualCanvasWidth, actualCanvasHeight, startRow, startCol, endRow, endCol);


//        var line = Ext.create('Ext.draw.Sprite', {
//            type: 'path',
//            path: 'M200 207 L180 207 L180 205 L200 205 Z',
//            fill: 'green',
//            rotate:{
//                x:0,
//                y:0,
//                degrees:'right'
//            }
//        });
//        var widget = {
//            xtype:'drawcomponent',
//            items:[line]
//        };

        portlet.setGridDimensions({
            startRow:startRow,
            startCol:startCol,
            endRow:endRow - 1,
            endCol:endCol - 1
        });

        portlet.setSize(newHWidth, newHeight);
//        portlet.setPosition(x - moveLeft, y - moveTop);
        portlet.setLocalX(x - moveLeft - 1);
        portlet.setLocalY(y - moveTop - 1);

    },



    addPortlet : function(freeFormLayout, x, y, widget, type, width, height, callback) {

//        console.log(x, y, widget, type, width, height);

        var defaultHeight = 250;
        var defaultWidth = 350;

        switch (type) {
            case 'chart' :
                defaultHeight = 250;
                defaultWidth = 400;
                break;
            case 'text' :
                defaultHeight = 70;
                defaultWidth = 300;
                break;
            case 'image' :
                defaultHeight = 300;
                defaultWidth = 400;
                break;
            case 'grid' :
                defaultHeight = 300;
                defaultWidth = 400;
                break;
            case 'shape' :
                defaultHeight = 200;
                defaultWidth = 200;
                break;
            case 'embedded' :
                defaultHeight = 300;
                defaultWidth = 400;
                break;
        }


        var portlet = Ext.widget({
            xtype:'portlet',
            height:height ? height : defaultHeight,
            width:width ? width : defaultWidth,
            x:x,
            y:y,
            widget:widget,
            widgetType:type,
            listeners :{
                'move':function(cont, x, y, e) {
//                    console.log(cont.getMovement());
//                    cont.setMovement('move');
//                    if (cont.getMovement() != 'resize')
                        LayoutManager.alignPortlet(freeFormLayout, this, x, y, true);
                },
                'resize':function(cont, width, height, oldWidth, oldHeight) {
//                    console.log(cont.getMovement());
//                    if (cont.getMovement() != 'move')
//                        cont.setMovement('resize');
//                    if (portlet.getWidget()) {
//                        var widgetCoordinates = portlet.getGridDimensions();
//                        console.log("resize");
//                        LayoutManager.alignPortlet(freeFormLayout, this, cont.getX(), cont.getY(), true);
//                    }
                }
            }
        });

        freeFormLayout.add(portlet);
        Ext.callback(callback, this, [portlet]);

    },


    addParameterControl : function(freeFormLayout, x, y, controlInfo, width, height, callback) {

        var defaultHeight = 50;
        var defaultWidth = 150;
        var name = '';

        switch (controlInfo.controlType) {
            case 'singleslidercontrol' :
                name = 'Single Slider';
                defaultHeight = 70;
                defaultWidth = 300;
                break;
            case 'singleverticalslidercontrol' :
                name = 'Single Vertical Slider';
                defaultHeight = 300;
                defaultWidth = 80;
                break;
            case 'multislidercontrol' :
                name = 'Multi Slider';
                defaultHeight = 70;
                defaultWidth = 300;
                break;
            case 'multiverticalslidercontrol' :
                name = 'Multi Vertical Slider';
                defaultHeight = 300;
                defaultWidth = 80;
                break;
            case 'numberfieldcontrol' :
                name = 'Numberfield';
                defaultHeight = 50;
                defaultWidth = 200;
                break;
            case 'comboboxcontrol' :
                name = 'Combobox';
                defaultHeight = 80;
                defaultWidth = 250;
                break;
            case 'checkboxgroupcontrol' :
                name = 'Checkbox';
                defaultHeight = 100;
                defaultWidth = 200;
                break;

        }


        var parameterControl = Ext.widget({
            xtype:'parametercontrol',
            height:height ? height : defaultHeight,
            width:width ? width : defaultWidth,
            x:x ? x : null,
            y:y ? y : null,
            parameterControl:controlInfo,
            listeners :{
                'move':function(cont, x, y, e) {
                    LayoutManager.alignPortlet(freeFormLayout, this, x, y, true);
                },
                'resize':function(cont, width, height, oldWidth, oldHeight) {
//                    LayoutManager.alignPortlet(freeFormLayout, cont, cont.getX(), cont.getY());
//                    cont.setPosition(cont.getX() - 1, cont.getY() - 1);

                }
            }
        });

        freeFormLayout.add(parameterControl);

//        var node = {
//            widgetType:'parameter',
//            text:'Parameter - ' + name,
//            id:parameterControl.id + '-node',
//            holderId:parameterControl.id,
//            leaf:true,
//            checked:true
//        };
//        LayoutManager.addWidgetNodeToTree(node);

        Ext.callback(callback, this, [parameterControl,name]);

    },


    updatePortletProperties : function(freeFormLayout, portlet, widget, type, x, y, callback) {

        LayoutManager.alignPortlet(freeFormLayout, portlet, x, y, false);
        LayoutManager.addWidgetNodeToTree({
            widgetType : type,
            type : type,
            id : portlet.id + '-node',
            holderId : portlet.id,
            leaf : true,
            text : 'Widget - ' + type,
            checked : true
        });
        Ext.callback(callback, this);

    },

    updateParameterControlProperties : function(freeFormLayout, parameterControl, parameterName, x, y, callback) {

        LayoutManager.alignPortlet(freeFormLayout, parameterControl, x, y, false);

        LayoutManager.addWidgetNodeToTree({
            widgetType:'parameter',
            text:'Parameter - ' + parameterName,
            id:parameterControl.id + '-node',
            holderId:parameterControl.id,
            leaf:true,
            checked:true
        });

        Ext.callback(callback, this);


    },

    addWidgetNodeToTree : function(widget) {
        var dashboardElementsTreeStore = Ext.StoreManager.lookup('DashboardElementsTreeStore');
        var rootNode = dashboardElementsTreeStore.getRootNode();
        rootNode.appendChild(widget);
    },

    getCellWidthHeight : function(freeFormLayout, callback) {
        var noOfCols = DashboardManager.noOfCols;
        var noOfRows = DashboardManager.noOfRows;
        var actualCanvasWidth = freeFormLayout.getEl().dom.clientWidth;
        var actualCanvasHeight = freeFormLayout.getEl().dom.clientHeight;

        var cellWidth = Ext.util.Format.round(actualCanvasWidth / noOfCols, 0);
        var cellHeight = Ext.util.Format.round(actualCanvasHeight / noOfRows, 0);

        Ext.callback(callback, this, [cellWidth,cellHeight]);
    },

    showTargetPane : function(freeFormLayout, x, y) {

        var noOfCols = LayoutManager.noOfCols;
        var noOfRows = LayoutManager.noOfRows;
        var actualCanvasWidth = freeFormLayout.getEl().dom.clientWidth;
        var actualCanvasHeight = freeFormLayout.getEl().dom.clientHeight;

        var cellWidth = Ext.util.Format.round(actualCanvasWidth / noOfCols, 0);
        var cellHeight = Ext.util.Format.round(actualCanvasHeight / noOfRows, 0);

        if (x != undefined && y != undefined) {
            var targetElement = document.elementFromPoint(x, y);

            if (targetElement.tagName.toUpperCase() == 'TD') {
                var target = Ext.get(targetElement.id);
                if (LayoutManager.previousTarget && LayoutManager.currentTarget && (LayoutManager.previousTarget.id != LayoutManager.currentTarget.id || LayoutManager.currentTarget.id == 'canvasGrid-cell-0-0' )) {
                    LayoutManager.clearNeighbourCells(function() {
                        LayoutManager.highlightNeighbourCells(LayoutManager.currentTarget);
                    });
                }
                LayoutManager.previousTarget = LayoutManager.currentTarget;
                LayoutManager.currentTarget = target;
            }

        }

    },

    clearNeighbourCells : function(callback) {
        if (LayoutManager.neighbourArray) {
            for (var r = 0; r < DashboardManager.targetGridCellCount; r++) {
                for (var c = 0; c < DashboardManager.targetGridCellCount; c++) {
                    var cellRef = Ext.get(LayoutManager.neighbourArray[r][c].id);
                    if (cellRef) {
                        cellRef.dom.className = 'canvasCol';
//                        Ext.DomHelper.applyStyles(cellRef, {'backgroundColor':'#E0E8EC'});
                    }
                }

            }
        }
        Ext.callback(callback, this, []);

    },

    highlightNeighbourCells : function(cell) {
        var id = cell.id;
        var idArray = id.split('-');
        var cellNamePartOne = idArray[0];
        var cellNamePartTwo = idArray[1];
        var cellX = idArray[2];
        var cellY = idArray[3];

        LayoutManager.neighbourArray = new Array();
        for (var i = 0; i < DashboardManager.targetGridCellCount; i++) {
            var colArray = new Array();
            for (var j = 0; j < DashboardManager.targetGridCellCount; j++) {
                var newX = parseInt(cellX) + i,newY = parseInt(cellY) + j;
                var cellProp = {
                    id:cellNamePartOne + '-' + cellNamePartTwo + '-' + newX + '-' + newY
//                    cls:'canvasGrid-cell-highlight-' + i + '-' + j
                };
                colArray[j] = cellProp;
            }
            LayoutManager.neighbourArray[i] = colArray;
        }
//         console.log(neighbourArray);
        for (var r = 0; r < DashboardManager.targetGridCellCount; r++) {
            for (var c = 0; c < DashboardManager.targetGridCellCount; c++) {
                var className = LayoutManager.neighbourArray[r][c].cls + ' canvasGrid-cell-highlight-bkd';
                var cellRef = Ext.get(LayoutManager.neighbourArray[r][c].id);
                if (cellRef) {
                    cellRef.dom.className = className;
//                    Ext.DomHelper.applyStyles(cellRef, {'backgroundColor':'#ced5d9'});
                }
            }

        }


    }
};

