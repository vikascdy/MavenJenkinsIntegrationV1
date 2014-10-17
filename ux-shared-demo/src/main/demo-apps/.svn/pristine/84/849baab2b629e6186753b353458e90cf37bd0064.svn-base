//Used to display component JS file
Ext.define('PP.view.viewer.JsCodeViewer', {
    extend          :'Ext.panel.Panel',
    alias           :'widget.jscodeviewer',
    title           :'Layout JS',
    autoScroll      :true,
    bodyPadding     :'0',
    styleHtmlContent:true,
    loadMask        :true,
    initComponent   :function () {
        this.tools = [
            {
                type  :'maximize',
                qtip  :'Open JS in new tab',
                itemId:'js'
            }
        ];

        var me = this;
        //Masking viewport until component is loaded
        var loadingWindow = Ext.widget('progresswindow', {
            text:'Loading Component...'
        });

        //AJAX call to get the passed JS file
        Ext.Ajax.request({
            url    :this.pluginFile + '.js',
            success:function (r) {
                Ext.getCmp(me.id).update('<pre style="padding: 10px;" class="sh_javascript">' + Ext.htmlEncode(r.responseText) + '</pre>');
                designCode();
                loadingWindow.destroy();
            },
            failure:function (r) {
                Ext.getCmp(me.id).update('<center><p class="no-file-found">No JS file found for this component !</p></center>'); //If no JS file found
                loadingWindow.destroy();
            }
        });
        this.callParent(arguments);
    },
    afterRender :	function()
    {
         setPanelScroller(this);
         this.callParent(arguments);
    }
});
