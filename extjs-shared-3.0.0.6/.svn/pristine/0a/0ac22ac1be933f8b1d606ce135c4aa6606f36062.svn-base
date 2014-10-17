var jscrollerScript = new Array();
jscrollerScript[0] = "../jquery/jscroller/js/jquery.jscrollpane.min.js";
jscrollerScript[1] = "../jquery/jscroller/js/jquery.mousewheel.js";

var jscrollerCSS = new Array();
jscrollerCSS[0] = "../jquery/jscroller/css/jquery.jscrollpane.css";
jscrollerCSS[1] = "../jquery/jscroller/css/jquery.jscrollpane.lozenge.css";

for (var i = 0; i < jscrollerScript.length; i++) {
    var scripttag = document.createElement('script');
    scripttag.setAttribute("type", "text/javascript");
    scripttag.setAttribute("src", jscrollerScript[i]);
    document.getElementsByTagName("head")[0].appendChild(scripttag);
}

for (var i = 0; i < jscrollerCSS.length; i++) {
    var csstag = document.createElement('link');
    csstag.setAttribute("rel", 'stylesheet');
    csstag.setAttribute("type", "text/css");
    csstag.setAttribute("href", jscrollerCSS[i]);
    document.getElementsByTagName("head")[0].appendChild(csstag);
}

setPanelScroller = function (me)
{
    var panelId = me.getId();
    me.addBodyCls("scrollerPane");
    if (window.jQuery) {
        $(function () {
            var settings = {
                showArrows: true,
                autoReinitialise: true,
                mouseWheelSpeed: 50,
                maintainPosition: true,
                stickToBottom: true
            };

            if (!me.scrollPane) {
                me.scrollPane = $('#' + panelId + ' > div.scrollerPane');
                me.scrollPane.jScrollPane(settings);
            }
        });
    }
}
