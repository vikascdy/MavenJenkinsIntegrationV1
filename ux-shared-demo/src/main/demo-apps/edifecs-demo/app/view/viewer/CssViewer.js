//Used to display CSS for the component
Ext.define('PP.view.viewer.CssViewer', {
    extend       :'Ext.panel.Panel',
    alias        :'widget.cssviewer',
    title        :'Component CSS',
    autoScroll   :true,
    bodyPadding  :'0',
    style        :'backgroundColor:#ffffff',
    initComponent:function () {

        this.tools = [
            {
                type  :'maximize',
                qtip  :'Open CSS in new tab',
                itemId:'css'
            }
        ];
        var me = this;

        //Masking viewport until component is loaded
        var loadingWindow = Ext.widget('progresswindow', {
            text:'Loading Component...'
        });

        //AJAX call to get the passed CSS file
        Ext.Ajax.request({
            url    :this.pluginFile + '.css',
            success:function (r) {
                Ext.getCmp(me.id).update('<pre style="padding: 10px;" class="sh_css">' + r.responseText + '</pre>');
                designCode();
                loadingWindow.destroy();

            },
            failure:function (r) {
                Ext.getCmp(me.id).update('<center><p class="no-file-found">No CSS file found for this component !</p></center>'); //If no CSS file is found
                loadingWindow.destroy();
            }
        });
        this.callParent(arguments);
    }

});
