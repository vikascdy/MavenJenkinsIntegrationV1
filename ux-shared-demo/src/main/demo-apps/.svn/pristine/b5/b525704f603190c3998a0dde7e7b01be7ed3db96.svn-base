//Used to display HTML+JS for the component
Ext.define('PP.view.viewer.HtmlViewer', {
    extend     :'Ext.panel.Panel',
    alias      :'widget.htmlviewer',
    title      :'View HTML + JS',
    autoScroll :true,
    bodyPadding:'0',
    style      :'backgroundColor:#ffffff',

    initComponent:function () {

        this.tools = [
            {
                type  :'maximize',
                qtip  :'Open HTML + JS in new tab',
                itemId:'html'
            }
        ];
        var me = this;

        //Masking viewport until component is loaded
        var loadingWindow = Ext.widget('progresswindow', {
            text:'Loading Component...'
        });

        //AJAX call to get the passed HTML file
        Ext.Ajax.request({
            url    :this.pluginFile + '.html',
            success:function (r) {
                Ext.getCmp(me.id).update('<pre class="sh_javascript"><pre style="padding: 10px;" class="sh_html">' + Ext.htmlEncode(r.responseText) + '</pre></pre>');
                designCode();
                loadingWindow.destroy();
            }
        });

        this.callParent(arguments);
    }

});
